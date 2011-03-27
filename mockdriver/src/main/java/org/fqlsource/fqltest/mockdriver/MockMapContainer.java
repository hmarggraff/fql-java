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

import org.fqlsource.data.FqlDataException;
import org.fqlsource.data.FqlMapContainer;
import org.fqlsource.data.RunEnv;
import org.fqlsource.util.NamedImpl;

public class MockMapContainer extends NamedImpl implements FqlMapContainer<MockDriverConnection>
{
    private MockDriverConnection connection;


    public MockMapContainer(String name, MockDriverConnection connection)
    {
        super(name);
        this.connection = connection;
    }

    public MockDriverConnection getConnection()
    {
        return connection;
    }

    public Object getObject(RunEnv runEnv, Object from, String member) throws FqlDataException
    {
        return connection.getObject(from, member, this);
    }

    public Object lookup(RunEnv runEnv, Object key) throws FqlDataException
    {
        String fieldName = (String) key;
        if (fieldName.startsWith("L"))
            return connection.letterNum(fieldName);
        else if (fieldName.startsWith("D"))
            return connection.getDouble(fieldName);
        else if (fieldName.startsWith("T"))
            return connection.getTime(fieldName);
        else if ("yes".equalsIgnoreCase(fieldName))
            return true;
        else if ("no".equalsIgnoreCase(fieldName))
            return false;

        return "M" + key.toString();
    }
}
