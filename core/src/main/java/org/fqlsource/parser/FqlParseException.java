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

public class FqlParseException extends Exception
{

    // fields
    public String query;
    public final int col;
    public final int row;

    private FqlParseException(final String message)
    {
        super(message);
        row = col = 0;
    }

    public FqlParseException(String message, String queryText, int row, int col)
    {
        super(message);
        query = queryText;
        this.row = row;
        this.col = col;
    }

    public FqlParseException(String message, FqlParser fqlParser)
    {
        super("Error:(" + fqlParser.lex.getRow()+ ", " + fqlParser.lex.getCol() + ") " + message + '\n' + fqlParser.getQueryString());
        query = fqlParser.getQueryString();
        row = fqlParser.lex.getRow();
        col = fqlParser.lex.getCol();
    }

    public String getMessageLong()
    {
        return "Error:(" + row+ ", " + col + ") " + getMessage();
    }
}
