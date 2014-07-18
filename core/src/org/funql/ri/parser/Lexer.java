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

import java.util.Hashtable;

public class Lexer {


    public static enum Token {
	All,
	And,
	Any,
	As,
	Avg,
	By,
	Colon,
	Comma,
	ConstFloat,
	ConstInteger,
	Count,
	Distinct,
	Dot,
	EOF,
	EOFComment,
	Elipses,
	End,
	Equal,
	Error,
	ExRange,
	Exists,
	False,
	ForAll,
	From,
	Greater,
	GreaterOrEqual,
	In,
	Into,
	Instanceof,
	Is,
	Join,
	LBrace,
	LBracket,
	LParen,
	Left,
	Less,
	LessOrEqual,
	Like,
	Limit,
	Link,
	Matches,
	Max,
	Min,
	Minus,
	Mod,
	Name,
	Nil,
	Not,
	Open,
        On,
	Or,
	Outer,
	Param,
	Plus,
	Put,
	Question,
	RBrace,
	RBracket,
	RParen,
	Right,
	Select,
	Single,
	Slash,
	Some,
	Star,
	String,
	Sum,
	True,
	Unequal,
	Unknown,
        Where
    }

    protected static Hashtable<String, Token> keywords = new Hashtable<String, Token>();

    protected Token currToken;
    protected int pos;
    protected int row = 1;
    protected int col;
    protected int lastRowLen;

    protected String src;
    protected long intVal;
    protected double floatVal;
    protected String stringVal;
    protected String nameVal;
    protected boolean isPushBack;
    protected boolean haveCR;

    static {
	keywords.put("all", Token.All);
	keywords.put("and", Token.And);
	keywords.put("any", Token.Any);
	keywords.put("as", Token.As);
	keywords.put("by", Token.By);
	keywords.put("end", Token.End);
	keywords.put("exists", Token.Exists);
	keywords.put("false", Token.False);
	keywords.put("from", Token.From);
	keywords.put("in", Token.In);
	keywords.put("into", Token.Into);
	keywords.put("is", Token.Instanceof);
	keywords.put("join", Token.Join);
	keywords.put("left", Token.Left);
	keywords.put("like", Token.Like);
	keywords.put("limit", Token.Limit);
	keywords.put("link", Token.Link);
	keywords.put("matches", Token.Matches);
	keywords.put("mod", Token.Mod);
	keywords.put("nil", Token.Nil);
	keywords.put("not", Token.Not);
	keywords.put("null", Token.Nil);
	keywords.put("open", Token.Open);
	keywords.put("on", Token.On);
	keywords.put("or", Token.Or);
	keywords.put("outer", Token.Outer);
	keywords.put("put", Token.Put);
	keywords.put("right", Token.Right);
	keywords.put("select", Token.Select);
	keywords.put("single", Token.Single);
	keywords.put("some", Token.Some);
	keywords.put("true", Token.True);
	keywords.put("where", Token.Where);
	keywords.put("count", Token.Count);
    }

    public Lexer(String expression) {
	src = expression;
    }

    public int getPos() {
	return pos;
    }

    public int getCol() {
	return col;
    }

    public int getRow() {
	return row;
    }

    protected char nextChar() throws FqlParseException {
	if (pos >= src.length()) {
	    throw new FqlParseException("UnexpectedEof", src, row, col);
	} else {
	    char c = src.charAt(pos++);
	    if (c == '\n') {
		if (!haveCR) {
		    row++;
		    lastRowLen = col;
		}
		col = 0;
		haveCR = false;
	    } else if (c == '\r') {
		row++;
		lastRowLen = col;
		col = 0;
		haveCR = true;
	    }
	    col++;
	    return c;
	}
    }

    public Token nextToken() throws FqlParseException {
	if (isPushBack) {
	    isPushBack = false;
	} else {
	    currToken = nextToken1();
	}
	return currToken;
    }

    protected Token nextToken1() throws FqlParseException {
	loop:
	while (pos < src.length()) {
	    char c = nextChar();
	    switch (c) {
		case ' ':
		case '\t':
		case '\n':
		case '\r':
		    continue;
		case '?': {
		    return Token.Question;
		}
		case '(': {
		    return Token.LParen;
		}
		case ')': {
		    return Token.RParen;
		}
		case '[': {
		    return Token.LBracket;
		}
		case ']': {
		    return Token.RBracket;
		}
		case '{': {
		    return Token.LBrace;
		}
		case '}': {
		    return Token.RBrace;
		}
		case '*': {
		    return Token.Star;
		}
		case '/': {
		    char cc = nextChar();
		    if (cc == '*') {
			boolean foundStar = false;
			for (; ; ) {
			    cc = nextChar();
			    if (cc == '*') {
				foundStar = true;
			    } else if (cc == '/') {
				if (foundStar) {
				    continue loop;
				} else {
				    foundStar = false;
				}
			    } else {
				foundStar = false;
			    }
			}
		    } else if (cc == '/') {
			while (pos < src.length()) {
			    cc = nextChar();
			    if (cc == '\n' || cc == '\r') {
				continue loop;
			    }
			}
			return Token.EOFComment;
		    } else {
			charBack();
			return Token.Slash;
		    }
		}
		case '+': {
		    return Token.Plus;
		}
		case ':': {
		    return Token.Colon;
		}
		case ',': {
		    return Token.Comma;
		}
		case '=': {
		    return Token.Equal;
		}
		case '.': {
		    char cc = nextChar();
		    if (cc == '.') {
			return Token.Elipses;
		    } else {
			charBack();
			return Token.Dot;
		    }
		}
		case '-': {
		    char cc = nextChar();
		    if (cc == '>') {
			return Token.Dot;
		    } else {
			charBack();
			return Token.Minus;
		    }
		}
		case '<': {
		    char cc = nextChar();
		    if (cc == '=') {
			return Token.LessOrEqual;
		    } else if (cc == '<') {
			return Token.ExRange;
		    } else {
			charBack();
			return Token.Less;
		    }
		}
		case '>': {
		    char cc = nextChar();
		    if (cc == '=') {
			return Token.GreaterOrEqual;
		    } else {
			charBack();
			return Token.Greater;
		    }
		}
		case '!': {
		    char cc = nextChar();
		    if (cc == '=') {
			return Token.Unequal;
		    } else {
			charBack();
			throw new FqlParseException("Not a token: '!'. Maybe you meant not.", src, row, col);
		    }
		}
		case '\'': {
		    return scanString();
		}
		case '"': {
		    return scanQuotedName();
		}
		case '$': // Query parameters
		{
		    final char c1 = nextChar();
		    if (Character.isJavaIdentifierStart(c1)) {

			scanName(c1);
			return Token.Param;
		    } else {
			return Token.Unknown;
		    }
		}
		default: {
		    if (Character.isDigit(c)) {
			return scanNumber(c);
		    } else if (Character.isJavaIdentifierStart(c)) {
			return scanName(c);
		    } else {
			return Token.Unknown;
		    }
		}
	    }
	}
	return Token.EOF;
    }


    public void pushBack() {
	isPushBack = true;
    }

    protected Token scanName(char p0) throws FqlParseException {
	StringBuffer b = new StringBuffer();
	b.append(p0);
	while (pos < src.length()) {
	    char c = nextChar();
	    if (Character.isJavaIdentifierPart(c)) {
		b.append(c);
	    } else {
		charBack();
		break;
	    }
	}
	nameVal = b.toString();
	Token ktok = keywords.get(nameVal.toLowerCase());
	if (ktok != null) {
	    return ktok;
	} else {
	    return Token.Name;
	}
    }

    private void charBack() {
	pos--;
	col--;
	if (col == 0) {
	    row--;
	    col = lastRowLen;
	}
    }

    protected Token scanNumber(char p0) throws FqlParseException {
	boolean isFloat = false;
	boolean isDot = false;
	StringBuffer b = new StringBuffer();
	b.append(p0);
	while (pos < src.length()) {
	    char c = nextChar();
	    if (!Character.isDigit(c) && c != 'e' && c != 'E' && c != '.') {
		charBack();
		break;
	    }
	    if (c == '.') {
		if (isDot) {
		    isFloat = false;
		    pos -= 2;
		    b.setLength(b.length() - 1);
		    break;
		}
		isFloat = true;
		isDot = true;
	    } else if (c == 'e' || c == 'E') {
		isFloat = true;
	    }
	    b.append(c);
	}
	if (isFloat) {
	    floatVal = Double.parseDouble(b.toString());
	    return Token.ConstFloat;
	} else {
	    intVal = Long.parseLong(b.toString());
	    return Token.ConstInteger;
	}
    }

    protected Token scanString() throws FqlParseException {
	StringBuilder b = new StringBuilder();
	while (pos < src.length()) {
	    char c = nextChar();
	    if (c == '\'') {
		stringVal = b.toString();
		return Token.String;
	    }
	    if (c == '\\') {
		c = nextChar();
		if (c == 'n') {
		    c = '\n';
		} else if (c == 't') {
		    c = '\t';
		} else if (c == '\'') {
		    c = '\'';
		}
	    }
	    b.append(c);
	}
	throw new FqlParseException("Unexpected EOF in quoted Name", src, row, col);
    }

    protected Token scanQuotedName() throws FqlParseException {
	StringBuilder b = new StringBuilder();
	while (pos < src.length()) {
	    char c = nextChar();
	    if (c == '"') {
		nameVal = b.toString();
		return Token.Name;
	    }
	    if (c == '\\') {
		c = nextChar();
		if (c == 'n') {
		    c = '\n';
		} else if (c == 't') {
		    c = '\t';
		} else if (c == '"') {
		    c = '"';
		}
	    }
	    b.append(c);
	}
	throw new FqlParseException("Unexpected EOF in quoted Name", src, row, col);
    }

}
