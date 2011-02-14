package org.fqlsource.parser;

import org.fqlsource.data.FqlQueryParameter;
import org.fqlsource.exec.*;

import java.util.ArrayList;

public class FqlWhere
{
    FqlParser p; // the parser state


    protected FqlNode parseParam() throws FqlParseException
    {
        String paramName = p.lex.nameVal;
        if (paramName.length() == 0)
        {
            throw new FqlParseException(Res.str("Parameters must have a name"), p);
        }
        FqlQueryParameter parameter = p.parameters.get(paramName);
        if (parameter == null)
        {
            parameter = new FqlQueryParameter(paramName);
        }
        return new QueryParameterNode(parameter);


    }

    FqlNodeInterface parseNav() throws FqlParseException
    {
        Lexer.Token tok;
        FqlNodeInterface left = null;
        do
        {
            String symName = p.lex.nameVal;
            tok = next();
            if (tok == Lexer.Token.LBrace)// start argList
            {
                ArrayList<FqlNodeInterface> argList = new ArrayList<FqlNodeInterface>();
                Lexer.Token t1 = next();
                while (t1 != Lexer.Token.RBrace)
                {
                    FqlNodeInterface arg = parseQuestion(t1);
                    argList.add(arg);
                    t1 = next();
                    if (t1 == Lexer.Token.RBrace)
                    {
                        break;
                    }
                    else if (t1 == Lexer.Token.Comma)
                    {
                        t1 = next();
                    }
                    else
                    {
                        throw new FqlParseException("expected right brace or comma", p);
                    }
                }
                if (left == null)// level 0: function call
                {
                    FqlBuiltinFunction builtin = p.functions.get(symName);
                    if (builtin == null)
                    {
                        throw new FqlParseException("Built-in function not found: " + symName, p);
                    }
                    final FqlNodeInterface[] argNodes;
                    if (argList.size() > 0)
                    {
                        argNodes = new FqlNodeInterface[argList.size()];
                        argList.toArray(argNodes);
                    }
                    else
                    {
                        argNodes = null;
                    }
                    left = new FunctionNode(builtin, argNodes);
                }
                else
                {
                    throw new FqlParseException("Method syntax not supported", p);
                }
                tok = next();
            }
            else if (left == null)
            {
                if (p.connections.containsKey(symName))
                {
                    left = new ConnectionVarNode(symName);
                }
                else
                {
                    final IteratorVar it = p.entryPoints.get(symName);
                    if (it != null)
                    {
                        left = new IteratorVarNode(it);
                    }
                    else
                    {
                        left = new AccessNode(p.findMember(symName));
                    }
                }
            }
            else
            {
                left = new DotNode(left, symName);
            }
            //after the symbol: check next operator
            if (tok == Lexer.Token.LBracket)
            {
                FqlNodeInterface indexNode = parseQuestion(next());
                tok = next();
                if (tok == Lexer.Token.RBracket)
                {
                    left = new IndexOpNode(left, indexNode);
                }
                else if (tok == Lexer.Token.Elipses)
                {
                    tok = next();
                    if (tok == Lexer.Token.RBracket)
                    {
                        left = new CollectionSliceNode(left, indexNode, null);
                    }
                    else
                    {
                        FqlNodeInterface upperBound = parseQuestion(tok);
                        left = new CollectionSliceNode(left, indexNode, upperBound);
                        tok = next();
                        if (tok != Lexer.Token.RBracket)
                        {
                            throw new FqlParseException(Res.str("Missing_right_bracket after index expression"), p);
                        }
                    }
                    next(); // read next token, so it can be pushed back after the break in the loop
                    break; // no dot allowed after Slice
                }
                tok = next();
            }
            if (tok == Lexer.Token.Dot)
            {
                tok = next();
            }
        }
        while (tok == Lexer.Token.Name);
        p.lex.pushBack();
        return left;
    }

    private FqlNodeInterface parseQuestion(Lexer.Token t) throws FqlParseException
    {
        return parseAs();
    }


    protected FqlNodeInterface parseAtom() throws FqlParseException
    {
        final Lexer.Token t = next();
        if (t == Lexer.Token.ConstInteger)
        {
            return new ConstIntNode(p.lex.intVal);
        }
        else if (t == Lexer.Token.ConstFloat)
        {
            return new ConstFloatNode(p.lex.floatVal);
        }
        else if (t == Lexer.Token.String)
        {
            return new ConstStringNode(p.lex.stringVal);
        }
        else if (t == Lexer.Token.True)
        {
            return ConstBooleanNode.TRUE;
        }
        else if (t == Lexer.Token.False)
        {
            return ConstBooleanNode.FALSE;
        }
        else if (t == Lexer.Token.Nil)
        {
            return new NilNode();
        }
        else if (t == Lexer.Token.LBrace)
        {
            return parseQuestion(next());
        }
        else if (t == Lexer.Token.Name)
        {
            return parseNav();
        }
        else if (t == Lexer.Token.Param)
        {
            return parseParam();
        }
        else if (t == Lexer.Token.EOFComment || t == Lexer.Token.EOF)
        {
            throw new FqlParseException(Res.str("Missing filter condition after where"), p);
        }
        throw new FqlParseException(Res.str("Unexpected token: The character at this position cannot be understood"), p);
    }

    FqlNodeInterface parseAs() throws FqlParseException
    {
        FqlNodeInterface left = parseAtom();
        final Lexer.Token t = next();

        if (t == Lexer.Token.As)
        {
            final String className = p.lex.nameVal;
            return new TypeCastNode(left, className);
        }
        return left;
    }


    protected Lexer.Token next() throws FqlParseException
    {
        return p.lex.nextToken();
    }


}
