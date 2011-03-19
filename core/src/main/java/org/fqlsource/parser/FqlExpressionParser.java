package org.fqlsource.parser;

import org.fqlsource.util.NamedIndex;
import org.fqlsource.exec.*;
import org.fqlsource.parser.Lexer.Token;

import java.util.ArrayList;

public class FqlExpressionParser
{
    FqlParser p; // the parser state

    public FqlExpressionParser(FqlParser p)
    {

        this.p = p;
    }


    protected FqlNode parseParam() throws FqlParseException
    {
        String paramName = p.lex.nameVal;
        if (paramName.length() == 0)
        {
            throw new FqlParseException(Res.str("Parameters must have a name"), p);
        }
        NamedIndex parameter = p.getParameter(paramName);
        return new QueryParameterNode(parameter, p.lex.getRow(), p.lex.getCol());


    }


    FqlNodeInterface parseNav() throws FqlParseException
    {
        Lexer.Token tok;
        NamedIndex source;
        FqlNodeInterface left;
        String symName = p.lex.nameVal;
        tok = next();
        if (tok == Lexer.Token.LParen)// function: start parsing argList
        {
            FqlBuiltinFunction builtin = p.functions.get(symName);
            if (builtin == null)
            {
                throw new FqlParseException("Built-in function not found: " + symName, p);
            }

            ArrayList<FqlNodeInterface> argList = new ArrayList<FqlNodeInterface>();
            Token t1 = next();
            while (t1 != Token.RParen)
            {
                p.lex.pushBack();
                FqlNodeInterface arg = parseAs();
                argList.add(arg);
                t1 = next();
                if (t1 == Lexer.Token.RParen)
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
            return new FunctionNode(builtin, argNodes, p.lex.getRow(), p.lex.getCol());
        }

        source = p.sources.get(symName);
        if (source != null)
        {
            left = new DataSourceNode(source, p.lex.getRow(), p.lex.getCol());
        }
        else
        {
            source = p.getIteratingSource();
            left = new AccessNode(source, symName, p.lex.getRow(), p.lex.getCol());
        }
        left = parseBracket(tok, left);

        while (tok == Token.Dot)
        {
            symName = p.lex.nameVal;
            left = new DotNode(left, symName, p.lex.getRow(), source.index, p.lex.getCol());
            left = parseBracket(next(), left);
            tok = next();
        }
        p.lex.pushBack();
        return left;
    }

    private FqlNodeInterface parseBracket(Token tok, FqlNodeInterface left) throws FqlParseException
    {
        if (tok == Token.LBracket)
        {
            FqlNodeInterface indexNode = parseAs();
            tok = next();
            if (tok == Token.RBracket)
            {
                left = new IndexOpNode(left, indexNode, p.lex.getRow(), p.lex.getCol());
            }
            else if (tok == Token.Elipses)
            {
                tok = next();
                if (tok == Token.RBracket)
                {
                    left = new CollectionSliceNode(left, indexNode, null, p.lex.getRow(), p.lex.getCol());
                }
                else
                {
                    FqlNodeInterface upperBound = parseAs();
                    left = new CollectionSliceNode(left, indexNode, upperBound, p.lex.getRow(), p.lex.getCol());
                    p.expect_next(Token.RBracket);
                }
            }
            return left;
        }
        else
            return pushBack(left);
    }

    FqlNodeInterface parseQuestion() throws FqlParseException
    {
        FqlNodeInterface conditionNode = parseOr();
        Token atToken = next();
        if (atToken != Token.Question)
            return pushBack(conditionNode);
        FqlNodeInterface trueBranchNode = parseOr();
        atToken = next();
        if (atToken != Token.Colon)
            throw new FqlParseException("Colon expected", p);
        FqlNodeInterface falseBranchNode = parseOr();

        return new QuestionNode(conditionNode, trueBranchNode, falseBranchNode, conditionNode.getRow(), conditionNode.getCol());
    }

    FqlNodeInterface parseOr() throws FqlParseException
    {
        FqlNodeInterface l = parseAnd();
        Token atToken = next();
        if (atToken != Token.Or)
            return pushBack(l);
        FqlNodeInterface r = parseAnd();
        return new OrNode(l, r, l.getRow(), l.getCol());
    }

    FqlNodeInterface parseAnd() throws FqlParseException
    {
        FqlNodeInterface l = parseCompare();
        Token atToken = next();
        if (atToken != Token.And)
            return pushBack(l);
        FqlNodeInterface r = parseCompare();
        return new AndNode(l, r, p.lex.getRow(), p.lex.getCol());
    }


    FqlNodeInterface parseNot() throws FqlParseException
    {
        Token atToken = next();
        if (atToken == Token.Not)
        {
            return new NotNode(parseIs(), p.lex.getRow(), p.lex.getCol());
        }
        else if (atToken == Token.Minus)
        {
            return new UnaryMinusNode(parseIs(), p.lex.getRow(), p.lex.getCol());
        }
        else if (atToken == Token.Plus) // unary plus
        {
            return parseIs();
        }
        else
        {
            p.lex.pushBack();
            return parseAtom();
        }
    }


    FqlNodeInterface parseCompare() throws FqlParseException
    {
        FqlNodeInterface l = parsePlus();
        Token t = next();
        if (t != Token.Less && t != Token.LessOrEqual && t != Token.Greater && t != Token.GreaterOrEqual
              && t != Token.Equal && t != Token.Unequal && t != Token.Like && t != Token.Matches)
            return pushBack(l);
        final int row = p.lex.getRow();
        final int col = p.lex.getCol();
        FqlNodeInterface r = parsePlus();

        if (t == Token.Less)
            return new LessNode(l, r, row, col);
        else if (t == Token.LessOrEqual)
            return new LessOrEqualNode(l, r, row, col);
        else if (t == Token.Greater)
            return new GreaterNode(l, r, row, col);
        else if (t == Token.GreaterOrEqual)
            return new GreaterOrEqualNode(l, r, row, col);
        else if (t == Token.Equal)
            return new EqualsNode(l, r, row, col);
        else if (t == Token.Unequal)
            return new NotEqualNode(l, r, row, col);
        else if (t == Token.Like)
            return new LikeNode(l, r, row, col);
        else // if (t == Token.Matches)
            return new MatchesNode(l, r, row, col);
    }

    private FqlNodeInterface parseIs() throws FqlParseException
    {
        FqlNodeInterface left = parseAtom();
        final Lexer.Token t = next();

        if (t == Lexer.Token.Is)
        {
            final String className = p.lex.nameVal;
            return new InstanceofNode(left, className, p.lex.getRow(), p.lex.getCol());
        }
        else
        {
            p.lex.pushBack();
            return left;
        }
    }

    FqlNodeInterface parseMultiply() throws FqlParseException
    {
        FqlNodeInterface l = parseNot();
        for (; ;)
        {
            Token t = next();
            if (t != Token.Mod && t != Token.Star && t != Token.Slash)
                return pushBack(l);
            final int row = p.lex.getRow();
            final int col = p.lex.getCol();
            FqlNodeInterface r = parseNot();
            if (t == Token.Mod)
                l = new ModuloNode(l, r, row, col);
            else if (t == Token.Star)
                l = new MultiplyNode(l, r, row, col);
            else
                l = new DivideNode(l, r, row, col);
        }
    }


    FqlNodeInterface parsePlus() throws FqlParseException
    {
        FqlNodeInterface l = parseMultiply();
        for (; ;)
        {
            Token t = next();
            if (t != Token.Plus && t != Token.Minus)
                return pushBack(l);
            FqlNodeInterface r = parseMultiply();
            if (t == Token.Plus)
                l = new PlusNode(l, r, p.lex.row, p.lex.col);
            else
                l = new MinusNode(l, r, p.lex.row, p.lex.col);
        }
    }

    private FqlNodeInterface pushBack(FqlNodeInterface l)
    {
        p.lex.pushBack();
        return l;
    }

    protected FqlNodeInterface parseAtom() throws FqlParseException
    {
        Token t = next();
        if (t == Lexer.Token.ConstInteger)
        {
            return new ConstIntNode(p.lex.intVal, p.lex.getRow(), p.lex.getCol());
        }
        else if (t == Lexer.Token.ConstFloat)
        {
            return new ConstFloatNode(p.lex.floatVal, p.lex.getRow(), p.lex.getCol());
        }
        else if (t == Lexer.Token.String)
        {
            return new ConstStringNode(p.lex.stringVal, p.lex.getRow(), p.lex.getCol());
        }
        else if (t == Lexer.Token.True)
        {
            return new ConstBooleanNode(true, p.lex.getRow(), p.lex.getCol());
        }
        else if (t == Lexer.Token.False)
        {
            return new ConstBooleanNode(false, p.lex.getRow(), p.lex.getCol());
        }
        else if (t == Lexer.Token.Nil)
        {
            return new NilNode(p.lex.getRow(), p.lex.getCol());
        }
        else if (t == Lexer.Token.LParen)
        {
            final FqlNodeInterface node = parseAs();
            if (next() != Token.RParen)
                throw new FqlParseException("Missing )", p);
            return node;
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
            throw new FqlParseException(Res.str("Missing expression"), p);
        }
        throw new FqlParseException(Res.str("Unexpected token: The character at this position cannot be understood"), p);
    }

    FqlNodeInterface parseAs() throws FqlParseException
    {
        FqlNodeInterface left = parseQuestion();
        final Lexer.Token t = next();

        if (t == Lexer.Token.As)
        {
            final String className = p.lex.nameVal;
            return new TypeCastNode(left, className, p.lex.getRow(), p.lex.getCol());
        }
        else
        {
            p.lex.pushBack();
            return left;
        }
    }
    FqlNodeInterface parseAssign() throws FqlParseException
    {

        Lexer.Token t = next();
        if (t != Token.Name)
            return parseAs();
        String var = p.lex.nameVal;
        t = next();
        if (t != Token.Assign)
        {
            p.lex.pushBackNameAndNext(t, var);
            return parseAs();
        }
        final FqlNodeInterface right = parseAs();
        return new AssignNode(var, right, p.lex.getRow(), p.lex.getCol());
    }


    protected Lexer.Token next() throws FqlParseException
    {
        return p.lex.nextToken();
    }


    static FqlNodeInterface parseExpression(FqlParser p) throws FqlParseException
    {
        FqlExpressionParser fqlExpressionParser = new FqlExpressionParser(p);
        return fqlExpressionParser.parseAs();

    }

    public static FqlNodeInterface parseAssignedValue(FqlParser parser) throws FqlParseException
    {
        FqlExpressionParser fqlExpressionParser = new FqlExpressionParser(parser);
        return fqlExpressionParser.parseAssign();
    }
}
