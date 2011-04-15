package org.fqlsource.fqltest.mockdriver;


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

import org.fqlsource.data.*;
import org.fqlsource.util.NamedImpl;

import java.util.Date;
import java.util.Map;

public class MockDriverConnection extends NamedImpl implements FqlConnection
{

    public MockDriverConnection()
    {
        super(RunEnv.default_provided_connection_name);
    }

    public void init(Map<String, String> props) throws FqlDataException
    {
        if (props.containsKey("testExceptionHandling")) // used to test error handling
        {
            throw new FqlDataException("Testing exception handling");
        }
    }

    public FqlStreamContainer getStream(String streamName) throws FqlDataException
    {
        if (Character.isJavaIdentifierStart(streamName.charAt(0)) && Character.isDigit(streamName.charAt(1)))
        {
            long count = letterNum(streamName);
            return new MockStreamContainer(this, streamName, (int) count);
        }
        throw new FqlDataException("Entry Point streamNamed " + streamName + " does not exist");
    }

    public FqlMapContainer getMap(String containerName) throws FqlDataException
    {
        return new MockMapContainer(containerName, this);
    }


    public Object getObject(Object parent, String fieldName, FqlContainer container) throws FqlDataException
    {
        if (fieldName.startsWith("L"))
        {
            return letterNum(fieldName);
        }
        else if (fieldName.startsWith("D"))
        {
            return getDouble(fieldName);
        }
        else if (fieldName.startsWith("T"))
        {
            return getTime(fieldName);
        }
        else if ("yes".equalsIgnoreCase(fieldName))
        {
            return true;
        }
        else if ("no".equalsIgnoreCase(fieldName))
        {
            return false;
        }
        else
        {
            return parent.toString() + '.' + fieldName;
        }
    }

    long letterNum(String fieldName) throws FqlDataException
    {
        String vTxt = fieldName.substring(1);
        try
        {
            final long ret = Long.parseLong(vTxt);
            return ret;
        }
        catch (NumberFormatException fex)
        {
            throw new FqlDataException("Mockdriver failed to parse long from fieldname. Should be character + value", fex);
        }
    }

    public double getDouble(String fieldName) throws FqlDataException
    {
        String vTxt = fieldName.substring(1);
        vTxt = vTxt.replace('_', '.');
        try
        {
            final double ret = Double.parseDouble(vTxt);
            return ret;
        }
        catch (NumberFormatException fex)
        {
            throw new FqlDataException("Mockdriver failed to parse long from fieldname. Should be 'D'+ value_decimal", fex);
        }
    }

    public Date getTime(String fieldName) throws FqlDataException
    {
        return new Date(letterNum(fieldName));
    }

    public void close()
    {
        // nothing
    }
}
