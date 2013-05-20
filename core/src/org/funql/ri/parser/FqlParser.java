package org.funql.ri.parser;

/*
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
 */

import org.funql.ri.data.FqlDataException;
import org.funql.ri.data.FqlIterator;
import org.funql.ri.data.FunqlConnection;
import org.funql.ri.exec.FqlBuiltinFunction;
import org.funql.ri.exec.FqlStatement;
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
    public Map<String, FqlBuiltinFunction> functions = new HashMap<String, FqlBuiltinFunction>();
    protected Stack<EntryPointSlot> iteratorStack = new Stack<EntryPointSlot>();
    private final List<FqlStatement> clauses = new ArrayList<FqlStatement>();
    //protected int connectionCount;
    //protected int entryPointCount;
    //protected int parameterCount;
    protected int iteratorCount = 0;


    public FqlParser(String txt) {
        this.txt = txt;
        lex = new Lexer(txt);
    }

    public FqlParser(String queryText, Iterable<FunqlConnection> conn) {
        this(queryText);
        for (FunqlConnection funqlConnection : conn) {
            connections.put(funqlConnection.getName(), new ProvidedConnection(connections.size(), funqlConnection));
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
                                       Iterable<FunqlConnection> conn) throws FqlParseException, FqlDataException {
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
        return runQuery(queryText, null, (Iterable<FunqlConnection>) null);
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
        if (t == Token.From) {
            clauses.add(parseFrom());
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
            if (t == Token.Where) {
                innerClauses.add(new WhereClause(FqlExpressionParser.parseExpression(this)));

            } else if (t == Token.Select) {
                innerClauses.add(parseObject());

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

    private FqlStatement parseObject() throws FqlParseException {
        ArrayList<FqlNodeInterface> fieldList = new ArrayList<FqlNodeInterface>();
        do {
            final FqlNodeInterface fqlNodeInterface = FqlExpressionParser.parseAssignedValue(this);
            fieldList.add(fqlNodeInterface);
        }
        while (nextToken() == Token.Comma);
        lex.pushBack();
        return new SelectStatement(fieldList);
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
            check_token(Token.Equal);
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
            String conn_name = expect_name("connection");
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
        Token t;
        boolean hasString;
        ArrayList<String> path = new ArrayList<String>();
        for (; ; ) {
            Token t1 = nextToken();
            hasString = t1 == Token.String;
            String step = name_or_string(t1);
            path.add(step);

            t = nextToken();
            if (t != Token.Dot)
                break;
        }
        final NamedIndex connectionIndex;

        if (t == Token.In) {
            String connectionName = expect_name("connection");
            connectionIndex = connections.get(connectionName);
            if (connectionIndex == null) {
                throw new FqlParseException("Connection named '" + connectionName + "' not found.", this);
            }
            t = nextToken();
        } else if (connections.size() == 1) {
            connectionIndex = (NamedIndex) connections.values().toArray()[0];
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
            entryPointName = path.get(path.size() - 1);
            lex.pushBack();
        } else
            throw new FqlParseException("If the last path component in a use clause a string, " +
                    "then you must specify an alias with 'as'.", this);

        final EntryPointSlot entryPointSlot = new EntryPointSlot(connectionIndex, entryPointName, maps.size());
        maps.put(entryPointName, entryPointSlot);
        clauses.add(new RefClause(path, entryPointSlot, fieldpath));
    }

    protected FromClause parseFrom() throws FqlParseException {
        if (connections.size() == 0) {
            throw new FqlParseException("No connection specified", this);
        }

        final Token t1 = nextToken();
        return parseOuterFrom(t1);
    }

    private FromClause parseOuterFrom(Token t1) throws FqlParseException {
        String entryPointName = name_or_string(t1);

        final NamedIndex connectionIndex;
        Token t = nextToken();
        if (t == Token.In) {
            String connectionName = expect_name("connection");
            connectionIndex = connections.get(connectionName);
            if (connectionIndex == null) {
                throw new FqlParseException("Connection named '" + connectionName + "' not found.", this);
            }
            t = nextToken();
        } else if (connections.size() == 1) {
            connectionIndex = (NamedIndex) connections.values().toArray()[0];
        } else {
            throw new FqlParseException("Expected 'in connection_name'", this);
        }

        FromClause fromClause;
        EntryPointSlot entryPointSlot = new EntryPointSlot(connectionIndex, entryPointName, iteratorCount);
        if (t == Token.As) {
            String alias = expect_name("entry point alias");
            fromClause = new FromClause(entryPointName, alias, entryPointSlot);
        } else {
            lex.pushBack();
            fromClause = new FromClause(entryPointName, entryPointName, entryPointSlot);
        }
        iteratorStack.push(entryPointSlot);
        return fromClause;
    }


    FqlNodeInterface parseNestedQuery() throws FqlParseException {
        final List<FqlStatement> clauses = new ArrayList<FqlStatement>();
        final Token t1 = nextToken();
        if (t1 == Token.LBrace) {
            clauses.add(new NestedFromClause(FqlExpressionParser.parseQuestion(this)));
            expect_next(Token.RBrace);
        } else
            clauses.add(parseOuterFrom(t1));
        parseNestableClauses(clauses);
        return new NestedQueryNode(clauses, lex.row, lex.col);
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
