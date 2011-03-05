package org.fqlsource.parser;

import org.fqlsource.data.FqlConnection;
import org.fqlsource.data.FqlDataException;
import org.fqlsource.util.NamedIndex;
import org.fqlsource.exec.*;

import java.util.*;

import static org.fqlsource.parser.Lexer.Token;

public class FqlParser
{

    public static final String default_provided_connection_name = "provided_connection";

    String txt;
    Lexer lex;
    private final List<FqlStatement> clauses = new ArrayList<FqlStatement>();
    public Map<String, NamedIndex> parameters = new HashMap<String, NamedIndex>();
    public Map<String, FqlBuiltinFunction> functions = new HashMap<String, FqlBuiltinFunction>();
    public Map<String, NamedIndex> sources = new HashMap<String, NamedIndex>();
    protected EntryPointStatement fromStatement;
    protected HashMap<String, NamedIndex> connections = new HashMap<String, NamedIndex>();
    protected int connectionCount;
    protected int entryPointCount;
    protected int parameterCount;
    protected NamedIndex iteratingSource;


    public FqlParser(String txt)
    {
        //scope = new FqlScope();
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

    public static Iterator runQuery(String queryText, Object[] parameterValues, FqlConnection... conn) throws FqlParseException, FqlDataException
    {
        final FqlParser parser = new FqlParser(queryText, conn);
        final List<FqlStatement> fqlStatements = parser.parseClauses();
        final RunEnv runEnv = new RunEnv(parser.connectionCount, parser.entryPointCount, parameterValues);
        Iterator precedent = null;
        for (int i = 0; i < fqlStatements.size(); i++)
        {
            FqlStatement statement = fqlStatements.get(i);
            precedent = statement.execute(runEnv, precedent).iterator();
        }
        return precedent;
    }

    public static List<FqlStatement> parse(String queryText) throws FqlParseException
    {
        final FqlParser parser = new FqlParser(queryText);
        final List<FqlStatement> fqlStatements = parser.parseClauses();
        return fqlStatements;
    }

    public static void runQuery(String queryText) throws FqlParseException, FqlDataException
    {
        runQuery(queryText, null);
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
        else lex.pushBack();
        t = nextToken();
        while (t == Token.Use)
        {
            parseEntryPoint();
            t = nextToken();
        }

        if (t == Token.From)
        {
            fromStatement = parseFrom();
        }
        else
        {
            throw new FqlParseException("expected from", this);
        }
        t = nextToken();
        for (; ;)
        {
            if (t == Token.Where)
            {
                clauses.add(new WhereClause(FqlExpressionParser.parseExpression(this)));

            }
            else if (t == Token.EOF)
                break;
        }
        return clauses;
    }

    private void parseOpen() throws FqlParseException
    {
        Token t;

        t = expect_next(Token.LBrace);
        HashMap<String, String> config = new HashMap<String, String>();
        while (t != Token.RBrace)
        {
            String key = name_or_string("connection key");
            t = expect_next(Token.Equal);
            t = expect_next(Token.String);
            String val = lex.stringVal;
            config.put(key, val);
        }
        if (!config.containsKey("driver"))
        {
            throw new FqlParseException("Connection must contain driver key.", this);
        }
        t = nextToken();
        if (t == Token.As)
        {
            String conn_name = expect_name("connection");
            clauses.add(new ConnectClause(conn_name, connectionCount++, config));
        }
        else
        {
            if (connections.containsKey(default_provided_connection_name))
            {
                throw new FqlParseException("Only one unnamed connection allowed", this);
            }
            clauses.add(new ConnectClause(default_provided_connection_name, connectionCount++, config));
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
                    throw new FqlParseException("Connection named '" + connectionName + "' not found.", this);
                t = nextToken();
            }
            else if (connections.size() == 1)
                connHolder = (NamedIndex) connections.values().toArray()[0];
            else
                throw new FqlParseException("Expected 'in connection_name'", this);



            if (t == Token.As)
            {
                String alias = expect_name("entry point alias");
                clauses.add(new EntryPointStatement(entryPointName, alias, entryPointCount++, connHolder.getIndex()));
                t1 = nextToken();
            }
            else
            {
                if (t1 == Token.String)
                    throw new FqlParseException("If entry point (\"" + entryPointName + "\") is a string, then you must specify an alias.", this);
                clauses.add(new EntryPointStatement(entryPointName, "it", entryPointCount++, connHolder.getIndex()));
            }
            if (t1 != Token.Comma)
            {
                lex.pushBack();
                break;
            }

        }
    }

    protected EntryPointStatement parseFrom() throws FqlParseException
    {
        final Token t1 = nextToken();
        String entryPointName = name_or_string(t1);
        EntryPointStatement entryPointStatement;

        Token t = nextToken();
        NamedIndex connHolder;
        if (t == Token.In)
        {
            String connectionName = expect_name("connection");
            connHolder = connections.get(connectionName);
            if (connHolder == null)
                throw new FqlParseException("Connection named '" + connectionName + "' not found.", this);
            t = nextToken();
        }
        else if (connections.size() == 1)
            connHolder = (NamedIndex) connections.values().toArray()[0];
        else
            throw new FqlParseException("Expected 'in connection_name'", this);

        if (t == Token.As)
        {
            String alias = expect_name("entry point alias");
            entryPointStatement = new EntryPointStatement(entryPointName, alias, entryPointCount++, connHolder.getIndex());
            clauses.add(entryPointStatement);
        }
        else
        {
            if (t1 == Token.String)
                throw new FqlParseException("If entry point (\"" + entryPointName + "\") is a string, then you must specify an alias.", this);
            entryPointStatement = new EntryPointStatement(entryPointName, "it", entryPointCount++, connHolder.getIndex());
            clauses.add(entryPointStatement);
        }
        return entryPointStatement;
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
            throw new FqlParseException("Expected " + "connection, dataset or iterator variable" + " as name or string", this);
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
            throw new FqlParseException("Expected " + msg + " as name or string", this);
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
            throw new FqlParseException("Expected " + msg + "name", this);
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
