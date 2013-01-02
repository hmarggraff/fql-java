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
package org.funql.ri.exec.node;


import org.funql.ri.data.FqlDataException;
import org.funql.ri.util.Named;

import java.lang.reflect.Field;
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
        if (!clazz.isInstance(val))
        {
            String name = clazz.getName();
            name = name.substring(name.lastIndexOf('.'));
            throw fqlDataException(side + " operand of " + operator + " must be a " + name + ", but is a: \"" + val.getClass() + "\"");
        }
    }


    protected void lispify(StringBuffer sb, String... members)
    {
        lispify1(sb, members);
        sb.append(')');
    }

    private void lispify1(StringBuffer sb, String[] members)
    {
        try
        {
            sb.append('(');
            String simpleName = getClass().getSimpleName();
            if (simpleName.endsWith("Node"))
            {
                simpleName = simpleName.substring(0, simpleName.length() - 4);
            }
            sb.append(simpleName);
            for (String member : members)
            {

                Field field = findField(member, getClass());
                Class<?> type = field.getType();
                field.setAccessible(true);
                Object fieldVal = field.get(this);
                sb.append(' ').append(member).append(':');
                if (fieldVal instanceof FqlNodeInterface)
                {
                    ((FqlNodeInterface) fieldVal).dump(sb);
                }
                else if (Named.class.isAssignableFrom(type))
                {
                    sb.append(((Named) fieldVal).getName());
                }
                else
                {
                    String s = fieldVal.toString();
                    if (s.contains(" "))
                    {
                        sb.append('"').append(s).append('"');
                    }
                    else
                    {
                        sb.append(s);
                    }
                }
            }
        }
        catch (NoSuchFieldException e)
        {
            throw new Error(e);
        }
        catch (IllegalAccessException e)
        {
            throw new Error(e);
        }
    }

    private Field findField(String member, Class<?> thisClass) throws NoSuchFieldException
    {
        if (thisClass == Object.class)
        {
            throw new NoSuchFieldException(member);
        }
        Field field = null;
        try
        {
            field = thisClass.getDeclaredField(member);
            return field;
        }
        catch (NoSuchFieldException e)
        {
            return findField(member, thisClass.getSuperclass());
        }
    }

    protected FqlDataException fqlDataException(final String msg)
    {
        return new FqlDataException(msg, getRow(), getCol());
    }


}
