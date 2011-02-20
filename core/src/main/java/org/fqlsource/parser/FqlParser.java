package org.fqlsource.parser;

import org.fqlsource.NotYetImplementedError;
import org.fqlsource.data.FqlConnection;
import org.fqlsource.data.FqlDataException;
import org.fqlsource.data.FqlQueryParameter;
import org.fqlsource.exec.*;

import java.util.*;

import static org.fqlsource.parser.Lexer.Token;

public class FqlParser
{

    //ArrayList<String> scope;
    String txt;
    Lexer lex;
    private final List<FqlStatement> clauses = new ArrayList<FqlStatement>();
    Map<String, FqlQueryParameter> parameters;
    public Map<String, FqlBuiltinFunction> functions = new HashMap<String, FqlBuiltinFunction>();
    public HashMap<String, ConnectClause> connections = new HashMap<String, ConnectClause>();
    public HashMap<String, IteratorVar> entryPoints = new HashMap<String, IteratorVar>();
    private final String default_connection_pseudo_name = "*default_connection";


    public FqlParser(String txt)
    {
        //scope = new FqlScope();
        this.txt = txt;
        lex = new Lexer(txt);
    }

    public static Iterator runQuery(String queryText, FqlConnection conn) throws FqlParseException, FqlDataException
    {
        final FqlParser parser = new FqlParser(queryText);
        final List<FqlStatement> fqlStatements = parser.parseClauses();
        final RunEnv runEnv = new RunEnv();
        if (conn != null)
            runEnv.connections.put("connection", conn);
        Iterator precedent = null;
        for (int i = 0; i < fqlStatements.size(); i++)
        {
            FqlStatement statement = fqlStatements.get(i);
            precedent = statement.execute(runEnv, precedent).iterator();
        }
        return precedent;
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
        else if (t == Token.From)
        {
            parseFrom();
        }
        else if (t == Token.Where)
            parseWhere();
        else
            throw new FqlParseException("expected from", this);
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
            throw new FqlParseException("Connection must contain driver key.");
        t = nextToken();
        if (t == Token.As)
        {
            String conn_name = expect_name("connection");
            clauses.add(new ConnectClause(conn_name, config));
        }
        else
        {
            if (connections.containsKey(default_connection_pseudo_name))
                throw new FqlParseException("Only one unnamed connection allowed");
            clauses.add(new ConnectClause(default_connection_pseudo_name, config));
        }


    }

    private Token expect_next(final Token expect) throws FqlParseException
    {
        final Token t;
        t = nextToken();
        if (t != expect)
            throw new FqlParseException("Expected " + expect + " but ", this);
        return nextToken();
    }

    private void parseWhere() throws FqlParseException {
        FqlWhere fqlWhere = new FqlWhere(this);
        fqlWhere.parseAs();

    }

    private void parseFrom()
      throws FqlParseException
    {
        String name1;
        final Token t1 = nextToken();
        if (t1 == Token.String)
            name1 = lex.stringVal;
        else if (t1 == Token.Name)
            name1 = lex.nameVal;
        else
            throw new FqlParseException("Expected " + "connection, dataset or iterator variable" + " as name or string", this);

        FromNode fromNode;

        Token t  = nextToken();
        if (t == Token.In)
        {
            if (t1 == Token.String)
                throw new FqlParseException("Iterator variable (\"" + name1+ "\")must be a name, not a string", this);
            String name2 = name_or_string("dataset or connection");
            t = nextToken();
            if (t == Token.Dot)
            {
                String name3 = name_or_string("dataset");
                fromNode = new FromNode(name3, name2, name1);
            }
            else
            {
                lex.pushBack();
                fromNode = new FromNode("connection", name2, name1);
            }
        }
        else
        {
            fromNode = new FromNode("connection", name1, "it");
        }
        //scope.add(fromNode.getIteratorName());
        clauses.add(fromNode);
    }

    String name_or_string(final String msg) throws FqlParseException
    {
        final Token t;
        final String name1;
        t = nextToken();
        if (t == Token.String)
            name1 = lex.stringVal;
        else if (t == Token.Name)
            name1 = lex.nameVal;
        else
            throw new FqlParseException("Expected " + msg + " as name or string", this);
        return name1;
    }

    String expect_name(final String msg) throws FqlParseException
    {
        final Token t;
        t = nextToken();
        if (t == Token.Name)
            return lex.stringVal;
        else
            throw new FqlParseException("Expected " + msg + "name", this);
    }

    private Token nextToken()
      throws FqlParseException
    {
        return lex.nextToken();
    }

    /**
     * optionally check id member exists
     *
     * @param symName
     * @return
     */
    public String findMember(String symName)
    {
        return symName;
    }
}
