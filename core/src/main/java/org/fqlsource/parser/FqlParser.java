package org.fqlsource.parser;

/*
   Copyright (C) 2011, Hans Marggraff and other copyright owners as documented in the project's IP log.
 This program and the accompanying materials are made available under the terms of the Eclipse Distribution License v1.0 which accompanies this distribution, is reproduced below, and is available at http://www.eclipse.org/org/documents/edl-v10.php
 All rights reserved.
 Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following conditions are met:
 - Redistributions of source code must retain the above copyright notice, this list of conditions and the following disclaimer.
 - Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following disclaimer in the documentation and/or other materials provided with the distribution.
 - Neither the name of the Eclipse Foundation, Inc. nor the names of its contributors may be used to endorse or promote products derived from this software without specific prior written permission.

 THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED.
 IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

import org.fqlsource.data.*;
import org.fqlsource.exec.*;
import org.fqlsource.util.NamedIndex;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.fqlsource.parser.Lexer.Token;

public class FqlParser
{

    String txt;
    Lexer lex;
    private final List<FqlStatement> clauses = new ArrayList<FqlStatement>();
    public Map<String, NamedIndex> parameters = new HashMap<String, NamedIndex>();
    public Map<String, FqlBuiltinFunction> functions = new HashMap<String, FqlBuiltinFunction>();
    public Map<String, NamedIndex> sources = new HashMap<String, NamedIndex>();
    protected HashMap<String, NamedIndex> connections = new HashMap<String, NamedIndex>();
    protected int connectionCount;
    protected int entryPointCount;
    protected int parameterCount;
    protected NamedIndex iteratingSource;
    protected int iteratorNesting = 1;


    public FqlParser(String txt)
    {
        this.txt = txt;
        lex = new Lexer(txt);
    }

    public FqlParser(String queryText, FqlConnection[] conn)
    {
        this(queryText);
        for (FqlConnection fqlConnection : conn)
        {
            connections.put(fqlConnection.getName(), new ProvidedConnection(connectionCount++, fqlConnection));
        }
    }

    public static FqlIterator runQuery(String queryText, Object[] parameterValues, FqlConnection... conn) throws FqlParseException, FqlDataException
    {
        final FqlParser parser = new FqlParser(queryText, conn);
        final List<FqlStatement> fqlStatements = parser.parseClauses();
        final RunEnv runEnv = new RunEnv(parser.connectionCount, parser.iteratorNesting, parser.entryPointCount, parameterValues);
        for (int i = 0; i < conn.length; i++)
        {
            runEnv.setConnectionAt(i, conn[i]);
        }
        FqlIterator precedent = null;
        for (int i = 0; i < fqlStatements.size(); i++)
        {
            FqlStatement statement = fqlStatements.get(i);
            precedent = statement.execute(runEnv, precedent);
        }
        return precedent;
    }

    public static List<FqlStatement> parse(String queryText) throws FqlParseException
    {
        final FqlParser parser = new FqlParser(queryText);
        final List<FqlStatement> fqlStatements = parser.parseClauses();
        return fqlStatements;
    }

    public static FqlIterator runQuery(String queryText) throws FqlParseException, FqlDataException
    {
        return runQuery(queryText, null);
    }

    public String getQueryString()
    {
        return txt;
    }

    public int getPos()
    {
        return lex.getPos();
    }

    List<FqlStatement> parseClauses() throws FqlParseException
    {
        Lexer.Token t = nextToken();
        if (t == Token.Open)
        {
            parseOpen();
        }
        else
        {
            lex.pushBack();
        }
        t = nextToken();
        while (t == Token.Use)
        {
            parseEntryPoint();
            t = nextToken();
        }

        if (t == Token.From)
        {
            parseFrom();
        }
        else
        {
            throw new FqlParseException("Expected from, but found " + t, this);
        }
        while (Token.EOF != (t = nextToken()))
        {
            if (t == Token.Where)
            {
                clauses.add(new WhereClause(FqlExpressionParser.parseExpression(this)));

            }
            else if (t == Token.Select)
            {
                clauses.add(parseObject());

            }
            else
            {
                throw new FqlParseException("Expected keyword, but found " + t, this);
            }
        }
        return clauses;
    }

    private FqlStatement parseObject() throws FqlParseException
    {
        ArrayList<FqlNodeInterface> fieldList = new ArrayList<FqlNodeInterface>();
        do
        {
            final FqlNodeInterface fqlNodeInterface = FqlExpressionParser.parseAssignedValue(this);
            fieldList.add(fqlNodeInterface);
        }
        while (nextToken() == Token.Comma);
        return new SelectStatement(fieldList);
    }

    private void parseOpen() throws FqlParseException
    {
        Token t;

        check_token(Token.LBrace);
        HashMap<String, String> config = new HashMap<String, String>();
        while (Token.RBrace != (t = nextToken()))
        {
            final String key;
            if (t == Token.String)
            {
                key = lex.stringVal;
            }
            else if (t == Token.Name)
            {
                key = lex.nameVal;
            }
            else
            {
                throw new FqlParseException("Expected " + "driver configuration" + " as name or string, but found " + t, this);
            }
            check_token(Token.Equal);
            check_token(Token.String);
            String val = lex.stringVal;
            config.put(key, val);
        }
        if (!config.containsKey("driver"))
        {
            throw new FqlParseException("Connection must specify a driver. (driver=\"driverclass\")", this);
        }
        t = nextToken();
        if (t == Token.As)
        {
            String conn_name = expect_name("connection");
            clauses.add(new ConnectClause(conn_name, connectionCount++, config, lex.getRow(), lex.getCol()));
        }
        else
        {
            if (connections.containsKey(RunEnv.default_provided_connection_name))
            {
                throw new FqlParseException("Only one unnamed connection allowed", this);
            }
            final ConnectClause connectClause = new ConnectClause(RunEnv.default_provided_connection_name, connectionCount++, config, lex.getRow(), lex.getCol());
            clauses.add(connectClause);
            connections.put(RunEnv.default_provided_connection_name, connectClause);
            lex.pushBack();
        }


    }

    protected void parseEntryPoint() throws FqlParseException
    {
        for (; ;)
        {
            Token t1 = nextToken();
            String entryPointName = name_or_string(t1);

            Token t = nextToken();
            NamedIndex connHolder;
            if (t == Token.In)
            {
                String connectionName = expect_name("connection");
                connHolder = connections.get(connectionName);
                if (connHolder == null)
                {
                    throw new FqlParseException("Connection named '" + connectionName + "' not found.", this);
                }
                t = nextToken();
            }
            else if (connections.size() == 1)
            {
                connHolder = (NamedIndex) connections.values().toArray()[0];
            }
            else
            {
                throw new FqlParseException("Expected 'in connection_name'", this);
            }


            if (t == Token.As)
            {
                String alias = expect_name("entry point alias");
                clauses.add(new UseClause(entryPointName, alias, entryPointCount++, connHolder.getIndex()));
                sources.put(alias, new NamedIndex(alias, sources.size()));
                t1 = nextToken();
            }
            else
            {
                if (t1 == Token.String)
                {
                    throw new FqlParseException("If entry point (\"" + entryPointName + "\") is a string, then you must specify an alias.", this);
                }
                clauses.add(new UseClause(entryPointName, entryPointName, entryPointCount++, connHolder.getIndex()));
                sources.put(entryPointName, new NamedIndex(entryPointName, sources.size()));
            }
            if (t1 != Token.Comma)
            {
                lex.pushBack();
                break;
            }
        }
    }

    protected void parseFrom() throws FqlParseException
    {
        if (connections.size() == 0)
        {
            throw new FqlParseException("No connection specified", this);
        }

        final Token t1 = nextToken();
        String entryPointName = name_or_string(t1);

        NamedIndex connectionIndex;
        Token t = nextToken();
        if (t == Token.In)
        {
            String connectionName = expect_name("connection");
            connectionIndex = connections.get(connectionName);
            if (connectionIndex == null)
            {
                throw new FqlParseException("Connection named '" + connectionName + "' not found.", this);
            }
            t = nextToken();
        }
        else if (connections.size() == 1)
        {
            connectionIndex = (NamedIndex) connections.values().toArray()[0];
        }
        else
        {
            throw new FqlParseException("Expected 'in connection_name'", this);
        }

        if (t == Token.As)
        {
            String alias = expect_name("entry point alias");
            FromClause fromClause = new FromClause(entryPointName, alias, connectionIndex.getIndex());
            sources.put(alias, new NamedIndex(alias, sources.size()));
            clauses.add(fromClause);
        }
        else
        {
            if (t1 == Token.String)
            {
                throw new FqlParseException("If entry point (\"" + entryPointName + "\") is a string, then you must specify an alias.", this);
            }
            lex.pushBack();
            FromClause fromClause = new FromClause(entryPointName, entryPointName, connectionIndex.getIndex());
            sources.put(entryPointName, new NamedIndex(entryPointName, sources.size()));

            clauses.add(fromClause);
        }
        iteratorNesting++;
    }

    Token expect_next(final Token expect) throws FqlParseException
    {
        final Token t;
        t = nextToken();
        if (t != expect)
        {
            throw new FqlParseException("Expected \"" + expect + "\" but found \"" + t + '"', this);
        }
        return nextToken();
    }

    void check_token(final Token expect) throws FqlParseException
    {
        final Token t;
        t = nextToken();
        if (t != expect)
        {
            throw new FqlParseException("Expected \"" + expect + "\" but found \"" + t + '"', this);
        }
    }

    protected String name_or_string(Token t1) throws FqlParseException
    {
        final String entryPointName;
        if (t1 == Token.String)
        {
            entryPointName = lex.stringVal;
        }
        else if (t1 == Token.Name)
        {
            entryPointName = lex.nameVal;
        }
        else
        {
            throw new FqlParseException("Expected connection, dataset or iterator variable as name or string, but found " + t1, this);
        }
        return entryPointName;
    }

    String name_or_string(final String msg) throws FqlParseException
    {
        final Token t;
        final String name1;
        t = nextToken();
        if (t == Token.String)
        {
            name1 = lex.stringVal;
        }
        else if (t == Token.Name)
        {
            name1 = lex.nameVal;
        }
        else
        {
            throw new FqlParseException("Expected " + msg + " as name or string, but found " + t, this);
        }
        return name1;
    }

    String expect_name(final String msg) throws FqlParseException
    {
        final Token t;
        t = nextToken();
        if (t == Token.Name)
        {
            return lex.nameVal;
        }
        else
        {
            throw new FqlParseException("Expected " + msg + "name, but found " + t, this);
        }
    }

    private Token nextToken() throws FqlParseException
    {
        return lex.nextToken();
    }

    NamedIndex getParameter(String paramName)
    {
        NamedIndex parameter = parameters.get(paramName);
        if (parameter == null)
        {
            parameter = new NamedIndex(paramName, parameterCount++);
            parameters.put(paramName, parameter);
        }
        return parameter;
    }


    public NamedIndex getIteratingSource()
    {
        return iteratingSource;
    }
}
