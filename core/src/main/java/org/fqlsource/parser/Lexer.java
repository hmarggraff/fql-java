/*
 * Copyright (c) reportsanywhere.com.  All rights reserved.  http://www.reportsanywhere.com
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * The software in this package is published under the terms of the GPL v2.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE-GPL.txt file.
 */
package org.fqlsource.parser;


import java.util.Hashtable;

public class Lexer
{
    public static enum Token
    {
        EOFComment,
        Error,
        EOF,
        Unknown,
        Nil,
        True,
        False,
        Mod,
        Abs,
        Like,
        Not,
        And,
        Or,
        First,
        Last,
        Distinct,
        ForAll,
        Exists,
        Instanceof,
        In,
        Some,
        Any,
        All,
        Count,
        Sum,
        Min,
        Max,
        Avg,
        Intersect,
        Union,
        Except,
        Element,
        LBrace,
        RBrace,
        LBracket,
        RBracket,
        Dot,
        Minus,
        Plus,
        Star,
        Slash,
        Less,
        Greater,
        LessOrEqual,
        GreaterOrEqual,
        Equal,
        Unequal,
        Elipses,
        Colon,
        Comma,
        Question,
        Name,
        String,
        ConstInteger,
        ConstFloat,
        ExRange,
        From,
        Where,
        Param,
        As,
        Connect,
        Open,
    }

    protected static Hashtable<String, Token> keywords = new Hashtable<String, Token>();
    protected Token currToken;
    protected int pos;
    protected String src;
    protected long intVal;
    protected double floatVal;
    protected String stringVal;
    protected String nameVal;
    protected boolean isPushBack;

    static
    {
        keywords.put("nil", Token.Nil);
        keywords.put("null", Token.Nil);
        keywords.put("true", Token.True);
        keywords.put("false", Token.False);
        keywords.put("mod", Token.Mod);
        keywords.put("like", Token.Like);
        keywords.put("not", Token.Not);
        keywords.put("and", Token.And);
        keywords.put("or", Token.Or);
        keywords.put("all", Token.All);
        keywords.put("in", Token.In);
        keywords.put("exists", Token.Exists);
        keywords.put("any", Token.Any);
        keywords.put("is", Token.Instanceof);
        keywords.put("as", Token.As);
        keywords.put("from", Token.From);
        keywords.put("where", Token.Where);
    }

    public Lexer(String expression)
    {
        src = expression;
    }

    public int getPos()
    {
        return pos;
    }

    public String getText()
    {
        return src;
    }

    protected char nextChar() throws FqlParseException
    {
        if (pos >= src.length())
            throw new FqlParseException("UnexpectedEof", src, pos);
        else
            return src.charAt(pos++);
    }

    public Token nextToken() throws FqlParseException
    {
        if (isPushBack)
            isPushBack = false;
        else
            currToken = nextToken1();
        return currToken;
    }

    protected Token nextToken1() throws FqlParseException
    {
        while (pos < src.length())
        {
            char c = nextChar();
            switch (c)
            {
                case ' ':
                case '\t':
                case '\n':
                case '\r':
                    continue;
                case '?':
                {
                    return Token.Question;
                }
                case '(':
                {
                    return Token.LBrace;
                }
                case ')':
                {
                    return Token.RBrace;
                }
                case '[':
                {
                    return Token.LBracket;
                }
                case ']':
                {
                    return Token.RBracket;
                }
                case '*':
                {
                    return Token.Star;
                }
                case '/':
                {
                    char cc = nextChar();
                    if (cc == '*')
                    {
                        boolean foundStar = false;
                        for (; ;)
                        {
                            cc = nextChar();
                            if (cc == '*')
                            {
                                foundStar = true;
                            }
                            else if (cc == '/')
                            {
                                if (foundStar)
                                    return nextToken1();
                                else
                                    foundStar = false;
                            }
                        }
                    }
                    else if (cc == '/')
                    {
                        while (pos < src.length())
                        {
                            cc = nextChar();
                            if (cc == '\n' || cc == '\r')
                                return nextToken1();
                        }
                        return Token.EOFComment;
                    }
                    else
                    {
                        pos--;
                        return Token.Slash;
                    }
                }
                case '+':
                {
                    return Token.Plus;
                }
                case ':':
                {
                    return Token.Colon;
                }
                case ',':
                {
                    return Token.Comma;
                }
                case '=':
                {
                    return Token.Equal;
                }
                case '.':
                {
                    char cc = nextChar();
                    if (cc == '.')
                        return Token.Elipses;
                    else
                    {
                        pos--;
                        return Token.Dot;
                    }
                }
                case '-':
                {
                    char cc = nextChar();
                    if (cc == '>')
                        return Token.Dot;
                    else
                    {
                        pos--;
                        return Token.Minus;
                    }
                }
                case '<':
                {
                    char cc = nextChar();
                    if (cc == '=')
                        return Token.LessOrEqual;
                    else if (cc == '<')
                        return Token.ExRange;
                    else
                    {
                        pos--;
                        return Token.Less;
                    }
                }
                case '>':
                {
                    char cc = nextChar();
                    if (cc == '=')
                        return Token.GreaterOrEqual;
                    else
                    {
                        pos--;
                        return Token.Greater;
                    }
                }
                case '!':
                {
                    char cc = nextChar();
                    if (cc == '=')
                        return Token.Unequal;
                    else
                    {
                        pos--;
                        throw new FqlParseException("not a token: !. Maybe you meant not", src, pos);
                    }
                }
                case '"':
                {
                    return scanString();
                }
                case '$': // Query parameters
                {
                    final char c1 = nextChar();
                    if (Character.isJavaIdentifierStart(c1))
                    {

                        scanName(c1);
                        return Token.Param;
                    }
                    else
                        return Token.Unknown;
                }
                default:
                {
                    if (Character.isDigit(c))
                    {
                        return scanNumber(c);
                    }
                    else if (Character.isJavaIdentifierStart(c))
                    {
                        return scanName(c);
                    }
                    else
                        return Token.Unknown;
                }
            }
        }
        return Token.EOF;
    }

    /**
     * ----------------------------------------------------------------------- pushBack
     */
    public void pushBack()
    {
        isPushBack = true;
    }

    protected Token scanName(char p0) throws FqlParseException
    {
        StringBuffer b = new StringBuffer();
        b.append(p0);
        while (pos < src.length())
        {
            char c = nextChar();
            if (Character.isJavaIdentifierPart(c))
            {
                b.append(c);
            }
            else
            {
                pos--;
                break;
            }
        }
        nameVal = b.toString();
        Token ktok = keywords.get(nameVal.toLowerCase());
        if (ktok != null)
        {
            return ktok;
        }
        else
        {
            return Token.Name;
        }
    }

    protected Token scanNumber(char p0) throws FqlParseException
    {
        boolean isFloat = false;
        boolean isDot = false;
        StringBuffer b = new StringBuffer();
        b.append(p0);
        while (pos < src.length())
        {
            char c = nextChar();
            if (!Character.isDigit(c) && c != 'e' && c != 'E' && c != '.')
            {
                pos--;
                break;
            }
            if (c == '.')
            {
                if (isDot)
                {
                    isFloat = false;
                    pos -= 2;
                    b.setLength(b.length() - 1);
                    break;
                }
                isFloat = true;
                isDot = true;
            }
            else if (c == 'e' || c == 'E')
                isFloat = true;
            b.append(c);
        }
        if (isFloat)
        {
            floatVal = Double.parseDouble(b.toString());
            return Token.ConstFloat;
        }
        else
        {
            intVal = Long.parseLong(b.toString());
            return Token.ConstInteger;
        }
    }

    protected Token scanString() throws FqlParseException
    {
        StringBuffer b = new StringBuffer();
        while (pos < src.length())
        {
            char c = nextChar();
            if (c == '"')
            {
                stringVal = b.toString();
                return Token.String;
            }
            if (c == '\\')
            {
                c = nextChar();
                if (c == 'n')
                    c = '\n';
                else if (c == 't')
                    c = '\t';
            }
            b.append(c);
        }
        throw new FqlParseException("Unexpected EOF in String", src, pos);
    }

}
