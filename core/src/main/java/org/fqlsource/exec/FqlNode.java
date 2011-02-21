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
package org.fqlsource.exec;


import org.fqlsource.data.FqlDataException;

import java.util.Set;

public abstract class FqlNode implements FqlNodeInterface
{
    protected final int row;
    protected final int col;


    protected FqlNode(int row, int col)
    {
        this.row = row;
        this.col = col;
    }

    public void collectVariables(final Set<String> set, final Set<Object> seen)
    {
        // nothing to do
    }

    public int getRow()
    {
        return row;
    }

    public int getCol()
    {
        return col;
    }

    protected void checkClassOf(Object val, Class clazz, final String side, final String operator) throws FqlDataException
    {
        if (!clazz.isInstance(val)){
            String name = clazz.getName();
            name = name.substring(name.lastIndexOf('.'));
            throw new FqlDataException(side + " operand of " + operator + " must be a " + name + ", but is a: \"" + val.getClass() + "\"", this);
        }
    }

}
