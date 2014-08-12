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

import org.funql.ri.exec.BuiltIns;
import org.funql.ri.exec.ContainerSlot;
import org.funql.ri.exec.node.*;
import org.funql.ri.parser.Lexer.Token;

import java.util.ArrayList;

public class FqlExpressionParser {
    FqlParser p; // the parser state

    public FqlExpressionParser(FqlParser p) {

        this.p = p;
    }


    private FqlNodeInterface parseFunctionCall(String symName, ContainerSlot source) throws FqlParseException {
        final BuiltIns func = BuiltIns.get(symName);
        if (func == null) {
            throw new FqlParseException("Built-in function not found: " + symName, p);
        }

        ArrayList<FqlNodeInterface> argList = new ArrayList<FqlNodeInterface>();
        Token t1 = next();
        while (t1 != Token.RParen) {
            p.lex.pushBack();
            FqlNodeInterface arg = parseQuestion();
            argList.add(arg);
            t1 = next();
            if (t1 == Token.RParen) {
                break;
            } else if (t1 == Token.Comma) {
                t1 = next();
            } else {
                throw new FqlParseException("expected right brace or comma", p);
            }
        }
        final FqlNodeInterface[] argNodes;
        if (argList.size() > 0) {
            argNodes = new FqlNodeInterface[argList.size()];
            argList.toArray(argNodes);
        } else {
            argNodes = null;
        }
        return new FunctionNode(func, source, argNodes, p.lex.getRow(), p.lex.getCol());
    }

    private FqlNodeInterface parseBracket(FqlNodeInterface left) throws FqlParseException {
        FqlNodeInterface indexNode = parseQuestion();
        Token tok = next();
        final FqlNodeInterface ret;
        if (tok == Token.RBracket) {
            ret = new IndexOpNode(left, indexNode, p.lex.getRow(), p.lex.getCol());
        } else if (tok == Token.Elipses) {
            tok = next();
            if (tok == Token.RBracket) {
                ret = new CollectionSliceNode(left, indexNode, null, p.lex.getRow(), p.lex.getCol());
            } else {
                FqlNodeInterface upperBound = parseQuestion();
                ret = new CollectionSliceNode(left, indexNode, upperBound, p.lex.getRow(), p.lex.getCol());
                p.expect_next(Token.RBracket);
            }
        } else
            throw new FqlParseException("Expected right bracket ']' or ellipses '..'", p);

        return ret;
    }

    FqlNodeInterface parseQuestion() throws FqlParseException {
        FqlNodeInterface conditionNode = parseOr();
        Token atToken = next();
        if (atToken != Token.Question) {
            return pushBack(conditionNode);
        }
        FqlNodeInterface trueBranchNode = parseOr();
        atToken = next();
        if (atToken != Token.Colon) {
            throw new FqlParseException("Colon expected", p);
        }
        FqlNodeInterface falseBranchNode = parseOr();

        return new QuestionNode(conditionNode, trueBranchNode, falseBranchNode, conditionNode.getRow(),
                conditionNode.getCol());
    }

    FqlNodeInterface parseOr() throws FqlParseException {
        FqlNodeInterface l = parseAnd();
        Token atToken = next();
        if (atToken != Token.Or) {
            return pushBack(l);
        }
        FqlNodeInterface r = parseAnd();
        return new OrNode(l, r, l.getRow(), l.getCol());
    }

    FqlNodeInterface parseAnd() throws FqlParseException {
        FqlNodeInterface l = parseNot();
        Token atToken = next();
        if (atToken != Token.And) {
            return pushBack(l);
        }
        FqlNodeInterface r = parseNot();
        return new AndNode(l, r, p.lex.getRow(), p.lex.getCol());
    }


    FqlNodeInterface parseUnaryMinus() throws FqlParseException {
        Token atToken = next();
        if (atToken == Token.Minus) {
            return new UnaryMinusNode(parseIs(), p.lex.getRow(), p.lex.getCol());
        } else {
            p.lex.pushBack();
            return parseAtom();
        }
    }

    FqlNodeInterface parseNot() throws FqlParseException {
        Token atToken = next();
        if (atToken == Token.Not) {
            return new NotNode(parseEquals(), p.lex.getRow(), p.lex.getCol());
        } else {
            p.lex.pushBack();
            return parseEquals();
        }
    }


    FqlNodeInterface parseEquals() throws FqlParseException {
        FqlNodeInterface l = parseCompare();
        for (; ; ) {
            Token t = next();

            if (t != Token.Equal && t != Token.Unequal && t != Token.Like && t != Token.Matches) {
                return pushBack(l);
            }
            final int row = p.lex.getRow();
            final int col = p.lex.getCol();

            FqlNodeInterface r = parseCompare();
            if (t == Token.Equal) {
                l = new EqualsNode(l, r, row, col);
            } else if (t == Token.Unequal) {
                l = new NotEqualNode(l, r, row, col);
            } else if (t == Token.Like) {
                l = new LikeNode(l, r, row, col);
            } else // if (t == Token.Matches)
            {
                l = new MatchesNode(l, r, row, col);
            }
        }


    }

    FqlNodeInterface parseCompare() throws FqlParseException {
        FqlNodeInterface l = parsePlus();
        Token t = next();
        if (t != Token.Less && t != Token.LessOrEqual && t != Token.Greater && t != Token.GreaterOrEqual) {
            return pushBack(l);
        }
        final int row = p.lex.getRow();
        final int col = p.lex.getCol();
        FqlNodeInterface r = parsePlus();

        if (t == Token.Less) {
            return new LessNode(l, r, row, col);
        } else if (t == Token.LessOrEqual) {
            return new LessOrEqualNode(l, r, row, col);
        } else if (t == Token.Greater) {
            return new GreaterNode(l, r, row, col);
        } else //if (t == Token.GreaterOrEqual)
        {
            return new GreaterOrEqualNode(l, r, row, col);
        }
    }

    private FqlNodeInterface parseIs() throws FqlParseException {
        FqlNodeInterface left = parseAtom();
        final Lexer.Token t = next();

        if (t == Lexer.Token.Is) {
            final String className = p.lex.nameVal;
            return new InstanceofNode(left, className, p.lex.getRow(), p.lex.getCol());
        } else {
            p.lex.pushBack();
            return left;
        }
    }

    FqlNodeInterface parseMultiply() throws FqlParseException {
        FqlNodeInterface l = parseUnaryMinus();
        for (; ; ) {
            Token t = next();
            if (t != Token.Mod && t != Token.Star && t != Token.Slash) {
                return pushBack(l);
            }
            final int row = p.lex.getRow();
            final int col = p.lex.getCol();
            FqlNodeInterface r = parseUnaryMinus();
            if (t == Token.Mod) {
                l = new ModuloNode(l, r, row, col);
            } else if (t == Token.Star) {
                l = new MultiplyNode(l, r, row, col);
            } else {
                l = new DivideNode(l, r, row, col);
            }
        }
    }


    FqlNodeInterface parsePlus() throws FqlParseException {
        FqlNodeInterface l = parseMultiply();
        for (; ; ) {
            Token t = next();
            if (t != Token.Plus && t != Token.Minus) {
                return pushBack(l);
            }
            FqlNodeInterface r = parseMultiply();
            if (t == Token.Plus) {
                l = new PlusNode(l, r, p.lex.row, p.lex.col);
            } else {
                l = new MinusNode(l, r, p.lex.row, p.lex.col);
            }
        }
    }

    private FqlNodeInterface pushBack(FqlNodeInterface l) {
        p.lex.pushBack();
        return l;
    }

    protected FqlNodeInterface parseAtom() throws FqlParseException {
        Token t = next();
        if (t == Lexer.Token.ConstInteger) {
            return new ConstIntNode(p.lex.intVal, p.lex.getRow(), p.lex.getCol());
        } else if (t == Lexer.Token.ConstFloat) {
            return new ConstFloatNode(p.lex.floatVal, p.lex.getRow(), p.lex.getCol());
        } else if (t == Lexer.Token.String) {
            return new ConstStringNode(p.lex.stringVal, p.lex.getRow(), p.lex.getCol());
        } else if (t == Lexer.Token.True) {
            return new ConstBooleanNode(true, p.lex.getRow(), p.lex.getCol());
        } else if (t == Lexer.Token.False) {
            return new ConstBooleanNode(false, p.lex.getRow(), p.lex.getCol());
        } else if (t == Lexer.Token.Nil) {
            return new NilNode(p.lex.getRow(), p.lex.getCol());
        } else if (t == Lexer.Token.LParen) {
            final FqlNodeInterface node = parseQuestion();
            p.expect_next(Token.RParen);
            return node;
        } else if (t == Token.Name) {
            return parseNav();
        } else if (t == Token.Param) {
            return p.parseParam();
        } else if (t == Token.From) {
            return p.parseNestedQuery();
        } else if (t == Lexer.Token.EOFComment || t == Lexer.Token.EOF) {
            throw new FqlParseException(Res.str("Missing expression"), p);
        }
        throw new FqlParseException(Res.str("Unexpected token: The character at this position cannot be understood"),
                p);
    }

/*
    FqlNodeInterface parseAs() throws FqlParseException {
        FqlNodeInterface left = parseQuestion();
        final Lexer.Token t = next();

        if (t != Token.As)
            return pushBack(left);
        final String className = p.lex.nameVal;
        return new TypeCastNode(left, className, p.lex.getRow(), p.lex.getCol());
    }
    */

    FqlNodeInterface parseAssign() throws FqlParseException {
        // parse expression that can be assigned by name: expr
        FqlNodeInterface left = parseQuestion();

        Lexer.Token t = next();
        if (t != Token.Colon) {
            return pushBack(left);
        }
        if (!(left instanceof MemberNode)) {
            throw new FqlParseException("left of assignment is not a name but a " + left.getClass().getName(), p);
        }
        MemberNode an = (MemberNode) left;
        String targetName = an.getMemberName();

        final FqlNodeInterface right = parseQuestion();
        return pushBack(new AssignNode(targetName, right, p.lex.getRow(), p.lex.getCol()));
    }


    protected Lexer.Token next() throws FqlParseException {
        return p.lex.nextToken();
    }


    static FqlNodeInterface parseExpression(FqlParser p) throws FqlParseException {
        FqlExpressionParser fqlExpressionParser = new FqlExpressionParser(p);
        return fqlExpressionParser.parseAssign();

    }

    static FqlNodeInterface parseQuestion(FqlParser p) throws FqlParseException {
        FqlExpressionParser fqlExpressionParser = new FqlExpressionParser(p);
        return fqlExpressionParser.parseQuestion();

    }

    public static FqlNodeInterface parseAssignedValue(FqlParser parser) throws FqlParseException {
        FqlExpressionParser fqlExpressionParser = new FqlExpressionParser(parser);
        return fqlExpressionParser.parseAssign();
    }

    /**
     * nav starts with a name
     * if the name is followed by a Left parenthesis then it is a function call
     * if it is a left bracket, then it is a lookup, either in a map-like member or in another source
     * if it is a dot it is a member or a member of an upper object.
     * cases
     * func(params) // a built in function
     * func(params).field // a built in function returning an object, that is navigated into
     * func(params).method(params) // a built in function returning an object, with the call of a built in method
     * it.field // a field in the current object
     * field // a field in the current object
     * it(n).field //a field in an outer object
     * map[key_expr] // a field in a map member or a mapping top-level source
     *
     * @return
     * @throws FqlParseException
     */
    FqlNodeInterface parseNav() throws FqlParseException {
        String symName = p.lex.nameVal;
        Lexer.Token tok = next();
        FqlNodeInterface left;
       ContainerSlot source = p.iteratorStack.peek();

        if (tok == Lexer.Token.LParen) {
            left = parseFunctionCall(symName, source);
            tok = next();
        } else if (tok == Token.LBracket) {
            source = p.maps.get(symName);
            if (source != null)
                left = new ContainerNameNode(source, p.lex.getRow(), p.lex.getCol());
            else
                left = new MemberNode(symName, source,  p.lex.getRow(), p.lex.getCol());
            left = parseBracket(left);
            tok = next();

        } else {
            BuiltIns func = BuiltIns.getParameterless(symName);
            if (func != null)
                left = new FunctionNode(func, source, null, p.lex.getRow(), p.lex.getCol());
            else
                left = new MemberNode(symName, source, p.lex.getRow(), p.lex.getCol());
        }

        while (tok == Token.Dot) {
            if (next() != Token.Name)
                throw new FqlParseException(Res.str("Name expected after dot"), p);

            symName = p.lex.nameVal;
            left = new DotNode(left, symName, source, p.lex.getRow(), p.lex.getCol());

            if ((tok = next()) == Token.LBracket) {
                left = parseBracket(left);
                tok = next();
            }
        }
        p.lex.pushBack();
        return left;
    }
}
