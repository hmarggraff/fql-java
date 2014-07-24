package org.funql.ri.parser;

/**
 Copyright (C) 2011, Hans Marggraff and other copyright owners as documented in the project's IP log.
 This program and the accompanying materials are made available under the terms of the Eclipse Distribution License
 v1.0 which accompanies this distribution, is reproduced below, and is available at http://www.eclipse
 .org/org/documents/edl-v10.php
 All rights reserved.
 Redistribution and use in source and binary forms, with or without modification,
 are permitted provided that the following conditions are met:
 - Redistributions of source code must retain the above copyright notice, this list of conditions and the following
 disclaimer.
 - Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the
 following disclaimer in the documentation and/or other materials provided with the distribution.
 - Neither the name of the Eclipse Foundation, Inc. nor the names of its contributors may be used to endorse or
 promote products derived from this software without specific prior written permission.

 THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES,
 INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 DISCLAIMED.
 IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY,
 WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 **/

import org.funql.ri.data.FqlDataException;
import org.funql.ri.data.FqlIterator;
import org.funql.ri.data.FunqlConnection;
import org.funql.ri.exec.EntryPointSlot;
import org.funql.ri.exec.FqlStatement;
import org.funql.ri.exec.clause.*;
import org.funql.ri.exec.ProvidedConnection;
import org.funql.ri.exec.RunEnv;
import org.funql.ri.exec.node.*;
import org.funql.ri.util.NamedIndex;

import java.util.*;

import static org.funql.ri.parser.Lexer.Token;

public class FqlParser {

    String txt;
    Lexer lex;
    protected Map<String, NamedIndex> connections = new HashMap<String, NamedIndex>();
    final Map<String, EntryPointSlot> maps = new HashMap<String, EntryPointSlot>();
    public Map<String, NamedIndex> parameters = new HashMap<String, NamedIndex>();
    protected Stack<EntryPointSlot> iteratorStack = new Stack<EntryPointSlot>();
    private final List<FqlStatement> clauses = new ArrayList<FqlStatement>();
    protected int iteratorCount = 0;


    public FqlParser(String txt) {
	this.txt = txt;
	lex = new Lexer(txt);
    }

    public FqlParser(String queryText, Iterable<FunqlConnection> conn) {
	this(queryText);
	if (conn != null) {
	    for (FunqlConnection funqlConnection : conn) {
		connections.put(funqlConnection.getName(), new ProvidedConnection(connections.size(), funqlConnection));
	    }
	}
    }

    public FqlParser(String queryText, FunqlConnection conn) {
	this(queryText);
	connections.put(conn.getName(), new ProvidedConnection(connections.size(), conn));
    }

    public static FqlIterator runQuery(String queryText, Object[] parameterValues,
				       FunqlConnection conn) throws FqlParseException, FqlDataException {
	List<FunqlConnection> arr = new ArrayList<>(1);
	arr.add(conn);
	return runQuery(queryText, parameterValues, arr);
    }

    public static FqlIterator runQuery(String queryText, Object[] parameterValues,
				       List<FunqlConnection> conn) throws FqlParseException, FqlDataException {
	final FqlParser parser = new FqlParser(queryText, conn);
	final List<FqlStatement> fqlStatements = parser.parseClauses();
	final RunEnv runEnv = new RunEnv(parser.connections.size(), parser.maps.size(),
		parameterValues);
	if (conn != null) {
	    for (FunqlConnection c : conn) {
		NamedIndex namedIndex = parser.connections.get(c.getName());
		runEnv.setConnectionAt(namedIndex.index, c);
	    }
	}
	FqlIterator precedent = null;
	for (FqlStatement statement : fqlStatements) {
	    precedent = statement.execute(runEnv, precedent);
	}
	return precedent;
    }

    public static List<FqlStatement> parse(String queryText) throws FqlParseException {
	final FqlParser parser = new FqlParser(queryText);
	return parser.parseClauses();
    }

    public static FqlIterator runQuery(String queryText) throws FqlParseException, FqlDataException {
	return runQuery(queryText, null, (List<FunqlConnection>) null);
    }

    public String getQueryString() {
	return txt;
    }

    public int getPos() {
	return lex.getPos();
    }

    public List<FqlStatement> parseClauses() throws FqlParseException {
	Lexer.Token t = nextToken();
	if (t == Token.Open) {
	    parseOpen();
	} else {
	    lex.pushBack();
	}
	t = nextToken();
	while (t == Token.Link) {
	    parseLink();
	    t = nextToken();
	}
	if (t == Token.From || t == Token.Into) {
	    clauses.add(parseToplevelFromOrInto(t));
	} else {
	    throw new FqlParseException("Expected from, but found " + tokenVal(t), this);
	}

	parseNestableClauses(clauses);
	return clauses;
    }

    private String tokenVal(Token t) {
	if (t == Token.Name)
	    return lex.nameVal;
	else if (t == Token.String) return '"' + lex.stringVal + '"';
	else return t.toString();
    }

    private void parseNestableClauses(List<FqlStatement> innerClauses) throws FqlParseException {
	Token t;
	while (Token.EOF != (t = nextToken())) {
	    if (t == Token.Join || t == Token.Left || t == Token.Right)
            {
                final FromClause lastStatement = (FromClause) innerClauses.get(innerClauses.size() - 1);
                innerClauses.add(parseJoin(t, lastStatement.getConnectionSlot()));
            } else if (t == Token.Where) {
		innerClauses.add(new WhereClause(FqlExpressionParser.parseExpression(this)));

	    } else if (t == Token.Select) {
		innerClauses.add(parseObject());

	    } else if (t == Token.Limit) {
		innerClauses.add(parseLimit());

	    } else if (t == Token.End) {
		{
		    innerClauses.add(new EndClause());
		    iteratorStack.pop();
		    break;
		}

	    } else {
		throw new FqlParseException("Expected keyword, but found " + tokenVal(t), this);
	    }
	}
    }

    private FqlStatement parseJoin(Token t0, NamedIndex upstreamSource) throws FqlParseException
    {
        boolean outerLeft = false;
        boolean outerRight = false;

        if (t0 == Token.Left || t0 == Token.Right){
            expect_next(Token.Outer);
            outerLeft = t0 == Token.Left;
            outerRight = t0 == Token.Right;
        }

        final Token t1 = nextToken();
        final String firstname = name_or_string(t1);
        final String root;

        final NamedIndex connectionIndex;
        Token t = nextToken();
        if (t == Token.Dot)
        {
            root = expect_name("root");
            connectionIndex = connections.get(firstname);
            if (connectionIndex == null)
            {
                throw new FqlParseException("Connection named '" + firstname + "' not found.", this);
            }
            t = nextToken();
        }
        else if (connections.size() == 1)
        {
            {
                connectionIndex = (NamedIndex) connections.values().toArray()[0];
                root = firstname;
            }
        }
        else
        {
            throw new FqlParseException("Expected 'connection_name.root'", this);
        }
        EntryPointSlot entryPointSlot = new EntryPointSlot(connectionIndex, root, iteratorCount);

       	if (t != Token.On)
            throw new FqlParseException("Expected 'on'", this);
        final FqlNodeInterface joinExpression = FqlExpressionParser.parseExpression(this);

        t = nextToken();
        JoinClause ret;
        if (t == Token.As) {
       	    String alias = expect_name("entry point alias");
       	    ret = new JoinClause(root, alias, entryPointSlot, joinExpression,upstreamSource, outerLeft, outerRight);
       	} else {
       	    lex.pushBack();
       	    ret = new JoinClause(root, root, entryPointSlot, joinExpression,upstreamSource, outerLeft, outerRight);
       	}
        return ret;
    }

    private FqlStatement parseLimit() throws FqlParseException {
	Token t = nextToken();
	if (t == Token.ConstInteger) {
	    long intVal = lex.intVal;
	    if (intVal < 0)
		throw new FqlParseException("Limit must be greater or equal to 0.", this);
	    return new LimitClause(new ConstIntNode(intVal, lex.getRow(), lex.getCol()));
	} else if (t == Token.Param) {
	    return new LimitClause(parseParam());
	} else
	    throw new FqlParseException("limit must be literal number or a parameter", this);
    }

    protected FqlNode parseParam() throws FqlParseException {
	String paramName = lex.nameVal;
	if (paramName.length() == 0) {
	    throw new FqlParseException(Res.str("Parameters must have a name"), this);
	}
	NamedIndex parameter = getParameter(paramName);
	return new QueryParameterNode(parameter, lex.getRow(), lex.getCol());


    }


    private FqlStatement parseObject() throws FqlParseException {
	ArrayList<FqlNodeInterface> fieldList = parseFieldList();
	return new SelectStatement(fieldList);
    }

    private ArrayList<FqlNodeInterface> parseFieldList() throws FqlParseException {
	ArrayList<FqlNodeInterface> fieldList = new ArrayList<FqlNodeInterface>();
	do {
	    final FqlNodeInterface fqlNodeInterface = FqlExpressionParser.parseAssignedValue(this);
	    fieldList.add(fqlNodeInterface);
	}
	while (nextToken() == Token.Comma);
	lex.pushBack();
	return fieldList;
    }

    private void parseOpen() throws FqlParseException {
	check_token(Token.LBrace);
	HashMap<String, String> config = new HashMap<String, String>();
	Token t = nextToken();
	for (; ; ) {
	    final String key;
	    if (t == Token.String) {
		key = lex.stringVal;
	    } else if (t == Token.Name) {
		key = lex.nameVal;
	    } else {
		throw new FqlParseException("Expected " + "driver configuration" + " as name or string, " +
			"but found " + t, this);
	    }
	    check_token(Token.Colon);
	    check_token(Token.String);
	    String val = lex.stringVal;
	    config.put(key, val);
	    t = nextToken();
	    if (t == Token.RBrace)
		break;
	    if (t == Token.Comma) {
		t = nextToken();
		if (t == Token.RBrace)
		    break;
	    } else
		throw new FqlParseException("Expected comma or right brace (,}), but found " + tokenVal(t), this);
	}
	if (!config.containsKey("driver")) {
	    throw new FqlParseException("Connection must specify a driver. (driver=\"driverclass\")", this);
	}
	t = nextToken();
	if (t == Token.As) {
	    String conn_name = expect_name("as");
	    final ConnectClause connectClause = new ConnectClause(conn_name, connections.size(), config, lex.getRow(),
		    lex.getCol());
	    clauses.add(connectClause);
	    connections.put(conn_name, connectClause);
	} else {
	    if (connections.containsKey(RunEnv.default_provided_connection_name)) {
		throw new FqlParseException("Only one unnamed connection allowed", this);
	    }
	    final ConnectClause connectClause = new ConnectClause(RunEnv.default_provided_connection_name,
		    connections.size(), config, lex.getRow(), lex.getCol());
	    clauses.add(connectClause);
	    connections.put(RunEnv.default_provided_connection_name, connectClause);
	    lex.pushBack();
	}


    }

    protected void parseLink() throws FqlParseException {
	Token t = nextToken();
	boolean single = false;
	if (t == Token.Single) {
	    single = true;
	    t = nextToken();
	}

	boolean hasString = t == Token.String;
	String targetName = name_or_string(t);

	final NamedIndex connectionIndex;
	t = nextToken();
	if (t == Token.In) {
	    String connectionName = expect_name("connection");
	    connectionIndex = connections.get(connectionName);
	    if (connectionIndex == null) {
		throw new FqlParseException("Connection named '" + connectionName + "' not found.", this);
	    }
	    t = nextToken();
	} else if (connections.size() == 1) {
	    connectionIndex = (NamedIndex) connections.values().toArray()[0]; // use the only existing connection
	} else {
	    throw new FqlParseException("Expected 'in connection_name'", this);
	}
	ArrayList<String> fieldpath = null;
	if (t == Token.By) {
	    fieldpath = new ArrayList<String>();
	    for (; ; ) {
		Token t1 = nextToken();
		String step = name_or_string(t1);
		fieldpath.add(step);

		t = nextToken();
		if (t != Token.Dot)
		    break;
	    }

	}

	final String entryPointName;

	if (t == Token.As) {
	    Token t1 = nextToken();
	    entryPointName = name_or_string(t1);
	} else if (!hasString) {
	    entryPointName = targetName;
	    lex.pushBack();
	} else
	    throw new FqlParseException("If the name of the link target is a string, " +
		    "then you must specify an alias with 'as'.", this);

	final EntryPointSlot entryPointSlot = new EntryPointSlot(connectionIndex, entryPointName, maps.size());
	maps.put(entryPointName, entryPointSlot);
	clauses.add(new RefClause(targetName, entryPointSlot, fieldpath, single));
    }

    protected FqlStatement parseToplevelFromOrInto(Token which) throws FqlParseException {
	if (connections.size() == 0) {
	    throw new FqlParseException("No connection specified", this);
	}

	final Token t1 = nextToken();
	final String firstname = name_or_string(t1);
	final String root;

	final NamedIndex connectionIndex;
	Token t = nextToken();
	if (t == Token.Dot) {
	    root = expect_name("root");
	    connectionIndex = connections.get(firstname);
	    if (connectionIndex == null) {
		throw new FqlParseException("Connection named '" + firstname + "' not found.", this);
	    }
	    t = nextToken();
	} else if (connections.size() == 1) {
	    {
		connectionIndex = (NamedIndex) connections.values().toArray()[0];
		root = firstname;
	    }
	} else {
	    throw new FqlParseException("Expected 'connection_name.root'", this);
	}
	if (which == Token.From) {

	    FromClause fromClause = buildFromClause(root, connectionIndex, t);
	    return fromClause;
	} else {
	    IntoStatement ret = buildIntoClause(root, connectionIndex, t);
	    return ret;
	}

    }

    private IntoStatement buildIntoClause(String root, NamedIndex connectionIndex, Token t) throws FqlParseException {
	EntryPointSlot entryPointSlot = new EntryPointSlot(connectionIndex, root, iteratorCount);
	iteratorStack.push(entryPointSlot);
	ArrayList<String> names;
	if (lex.currToken == Token.As) {
	    names = parseNameList();
	}
	if (lex.currToken != Token.Put)
	    throw new FqlParseException("Expected put.", this);
	ArrayList<FqlNodeInterface> fieldList = parseFieldList();
	IntoStatement intoStatement = new IntoStatement(root, entryPointSlot, fieldList);
	return intoStatement;
    }

    ArrayList<String> parseNameList() throws FqlParseException {
	ArrayList<String> names = new ArrayList<>();

	for (; ; ) {
	    Token t = lex.nextToken();
	    if (t != Token.Name)
		throw new FqlParseException("Expected a name not a " + tokenVal(t), this);
	    names.add(lex.nameVal);
	    t = nextToken();
	    if (t != Token.Comma)
		return names;
	}
    }

    private FromClause buildFromClause(String root, NamedIndex connectionIndex, Token t) throws FqlParseException {
	FromClause fromClause;
	EntryPointSlot entryPointSlot = new EntryPointSlot(connectionIndex, root, iteratorCount);
	if (t == Token.As) {
	    String alias = expect_name("entry point alias");
	    fromClause = new FromClause(root, alias, entryPointSlot);
	} else {
	    lex.pushBack();
	    fromClause = new FromClause(root, root, entryPointSlot);
	}
	iteratorStack.push(entryPointSlot);
	return fromClause;
    }


    FqlNodeInterface parseNestedQuery() throws FqlParseException {
	final FqlNodeInterface fromNode = FqlExpressionParser.parseQuestion(this);
	List<FqlStatement> innerClauses = new ArrayList<FqlStatement>();
	innerClauses.add(parseNestedFrom(fromNode));

        /*
	final List<FqlStatement> clauses = new ArrayList<FqlStatement>();
        final Token t1 = nextToken();
        if (t1 == Token.LBrace) {
            clauses.add(new NestedFromClause(FqlExpressionParser.parseQuestion(this)));
            expect_next(Token.RBrace);
        } else
            clauses.add(parseFrom(t1));
        */
	parseNestableClauses(innerClauses);
	return new NestedQueryNode(innerClauses, lex.row, lex.col);

    }

    private FqlStatement parseNestedFrom(FqlNodeInterface fromNode) throws FqlParseException {
	if (fromNode instanceof DotNode) {
	    DotNode dn = (DotNode) fromNode;
	    final FqlNodeInterface operand = dn.getOperand();

	    if (operand instanceof MemberNode) {
		MemberNode mn = (MemberNode) operand;
		NamedIndex connectionIndex = connections.get(mn.getMemberName());
		if (connectionIndex != null)
		    return buildFromClause(dn.getMemberName(), connectionIndex, nextToken());
	    }
	}
	return new NestedFromClause(fromNode);
    }


    Token expect_next(final Token expect) throws FqlParseException {
	final Token t;
	t = nextToken();
	if (t != expect) {
	    throw new FqlParseException("Expected \"" + expect + "\" but found \"" + tokenVal(t) + '"', this);
	}
	return nextToken();
    }

    void check_token(final Token expect) throws FqlParseException {
	final Token t;
	t = nextToken();
	if (t != expect) {
	    throw new FqlParseException("Expected \"" + expect + "\" but found \"" + tokenVal(t) + '"', this);
	}
    }

    protected String name_or_string(Token t) throws FqlParseException {
	final String entryPointName;
	if (t == Token.String) {
	    entryPointName = lex.stringVal;
	} else if (t == Token.Name) {
	    entryPointName = lex.nameVal;
	} else {
	    throw new FqlParseException("Expected connection, dataset or iterator variable as name or string, " +
		    "but found " + tokenVal(t), this);
	}
	return entryPointName;
    }

    String name_or_string(final String msg) throws FqlParseException {
	final Token t;
	final String name1;
	t = nextToken();
	if (t == Token.String) {
	    name1 = lex.stringVal;
	} else if (t == Token.Name) {
	    name1 = lex.nameVal;
	} else {
	    throw new FqlParseException("Expected " + msg + " as name or string, but found " + tokenVal(t), this);
	}
	return name1;
    }

    String expect_name(final String msg) throws FqlParseException {
	final Token t;
	t = nextToken();
	if (t == Token.Name) {
	    return lex.nameVal;
	} else {
	    throw new FqlParseException("Expected " + msg + " name, but found " + tokenVal(t), this);
	}
    }

    private Token nextToken() throws FqlParseException {
	return lex.nextToken();
    }

    NamedIndex getParameter(String paramName) {
	NamedIndex parameter = parameters.get(paramName);
	if (parameter == null) {
	    parameter = new NamedIndex(paramName, parameters.size());
	    parameters.put(paramName, parameter);
	}
	return parameter;
    }

}
