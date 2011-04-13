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

import org.fqlsource.exec.*;
import org.fqlsource.parser.Lexer.Token;
import org.fqlsource.util.NamedIndex;

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
            left = new ContainerNameNode(source, p.lex.getRow(), p.lex.getCol());
        }
        else
        {
            source = p.getIteratingSource();
            left = new MemberNode(source, symName, p.lex.getRow(), p.lex.getCol());
        }
        left = parseBracket(tok, left);

        while (tok == Token.Dot)
        {
            symName = p.lex.nameVal;
            left = new DotNode(left, symName, source.index, p.lex.getRow(), p.lex.getCol());
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
        {
            return pushBack(left);
        }
    }

    FqlNodeInterface parseQuestion() throws FqlParseException
    {
        FqlNodeInterface conditionNode = parseOr();
        Token atToken = next();
        if (atToken != Token.Question)
        {
            return pushBack(conditionNode);
        }
        FqlNodeInterface trueBranchNode = parseOr();
        atToken = next();
        if (atToken != Token.Colon)
        {
            throw new FqlParseException("Colon expected", p);
        }
        FqlNodeInterface falseBranchNode = parseOr();

        return new QuestionNode(conditionNode, trueBranchNode, falseBranchNode, conditionNode.getRow(), conditionNode.getCol());
    }

    FqlNodeInterface parseOr() throws FqlParseException
    {
        FqlNodeInterface l = parseAnd();
        Token atToken = next();
        if (atToken != Token.Or)
        {
            return pushBack(l);
        }
        FqlNodeInterface r = parseAnd();
        return new OrNode(l, r, l.getRow(), l.getCol());
    }

    FqlNodeInterface parseAnd() throws FqlParseException
    {
        FqlNodeInterface l = parseNot();
        Token atToken = next();
        if (atToken != Token.And)
        {
            return pushBack(l);
        }
        FqlNodeInterface r = parseNot();
        return new AndNode(l, r, p.lex.getRow(), p.lex.getCol());
    }


    FqlNodeInterface parseUnaryMinus() throws FqlParseException
    {
        Token atToken = next();
        if (atToken == Token.Minus)
        {
            return new UnaryMinusNode(parseIs(), p.lex.getRow(), p.lex.getCol());
        }
        else
        {
            p.lex.pushBack();
            return parseAtom();
        }
    }

    FqlNodeInterface parseNot() throws FqlParseException
    {
        Token atToken = next();
        if (atToken == Token.Not)
        {
            return new NotNode(parseCompare(), p.lex.getRow(), p.lex.getCol());
        }
        else
        {
            p.lex.pushBack();
            return parseCompare();
        }
    }


    FqlNodeInterface parseCompare() throws FqlParseException
    {
        FqlNodeInterface l = parsePlus();
        Token t = next();
        if (t != Token.Less && t != Token.LessOrEqual && t != Token.Greater && t != Token.GreaterOrEqual && t != Token.Equal && t != Token.Unequal && t != Token.Like && t != Token.Matches)
        {
            return pushBack(l);
        }
        final int row = p.lex.getRow();
        final int col = p.lex.getCol();
        FqlNodeInterface r = parsePlus();

        if (t == Token.Less)
        {
            return new LessNode(l, r, row, col);
        }
        else if (t == Token.LessOrEqual)
        {
            return new LessOrEqualNode(l, r, row, col);
        }
        else if (t == Token.Greater)
        {
            return new GreaterNode(l, r, row, col);
        }
        else if (t == Token.GreaterOrEqual)
        {
            return new GreaterOrEqualNode(l, r, row, col);
        }
        else if (t == Token.Equal)
        {
            return new EqualsNode(l, r, row, col);
        }
        else if (t == Token.Unequal)
        {
            return new NotEqualNode(l, r, row, col);
        }
        else if (t == Token.Like)
        {
            return new LikeNode(l, r, row, col);
        }
        else // if (t == Token.Matches)
        {
            return new MatchesNode(l, r, row, col);
        }
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
        FqlNodeInterface l = parseUnaryMinus();
        for (; ;)
        {
            Token t = next();
            if (t != Token.Mod && t != Token.Star && t != Token.Slash)
            {
                return pushBack(l);
            }
            final int row = p.lex.getRow();
            final int col = p.lex.getCol();
            FqlNodeInterface r = parseUnaryMinus();
            if (t == Token.Mod)
            {
                l = new ModuloNode(l, r, row, col);
            }
            else if (t == Token.Star)
            {
                l = new MultiplyNode(l, r, row, col);
            }
            else
            {
                l = new DivideNode(l, r, row, col);
            }
        }
    }


    FqlNodeInterface parsePlus() throws FqlParseException
    {
        FqlNodeInterface l = parseMultiply();
        for (; ;)
        {
            Token t = next();
            if (t != Token.Plus && t != Token.Minus)
            {
                return pushBack(l);
            }
            FqlNodeInterface r = parseMultiply();
            if (t == Token.Plus)
            {
                l = new PlusNode(l, r, p.lex.row, p.lex.col);
            }
            else
            {
                l = new MinusNode(l, r, p.lex.row, p.lex.col);
            }
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
            {
                throw new FqlParseException("Missing )", p);
            }
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

        if (t != Token.As)
            return pushBack(left);
        final String className = p.lex.nameVal;
        return new TypeCastNode(left, className, p.lex.getRow(), p.lex.getCol());
    }

    FqlNodeInterface parseAssign() throws FqlParseException
    {
        // parse expression
        FqlNodeInterface left = parseAs();

        Lexer.Token t = next();
        if (t != Token.Assign)
        {
            return pushBack(left);
        }
        if (!(left instanceof MemberNode))
        {
            throw new FqlParseException("left of assignment is not a name but a " + left.getClass().getName(), p);
        }
        MemberNode an = (MemberNode) left;
        String targetName = an.getMemberName();


        final FqlNodeInterface right = parseAs();
        return pushBack(new AssignNode(targetName, right, p.lex.getRow(), p.lex.getCol()));
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
