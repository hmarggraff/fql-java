package org.fqlsource.fqltest.simpletestdriver;


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

import org.funql.ri.data.*;
import org.funql.ri.util.NamedImpl;

import java.util.Date;
import java.util.List;
import java.util.Map;

public class SimpleTestConnection extends NamedImpl implements FunqlConnectionWithRange {
    final Map<String, String> props;
    String prefix = "";
    int factor = 1;

    public SimpleTestConnection(String name, Map<String, String> props) {
        super(name);
        this.props = props;
        if (props.containsKey("prefix"))
            prefix = props.get("prefix");
        if (props.containsKey("factor"))
            factor = new Integer(props.get("factor"));
        if (props.containsKey("testExceptionHandling")) // used to test error handling
        {
            throw new FqlDataException("Testing exception handling");
        }
    }

    static int letterNum(String fieldName) {
        String vTxt = fieldName.substring(1);
        try {
            final int ret = Integer.parseInt(vTxt);
            return ret;
        } catch (NumberFormatException fex) {
            throw new FqlDataException("Mockdriver failed to parse long from fieldname. Should be character + value", fex);
        }
    }

    public static double getDouble(String fieldName) {
        String vTxt = fieldName.substring(1);
        vTxt = vTxt.replace('_', '.');
        try {
            final double ret = Double.parseDouble(vTxt);
            return ret;
        } catch (NumberFormatException fex) {
            throw new FqlDataException("Mockdriver failed to parse long from fieldname. Should be 'D'+ value_decimal", fex);
        }
    }

    public static Date getTime(String fieldName) {
        return new Date(letterNum(fieldName));
    }

    public FqlIterator getIterator(String streamName) throws FqlDataException {
        if (Character.isJavaIdentifierStart(streamName.charAt(0)) && Character.isDigit(streamName.charAt(1))) {
            long count = letterNum(streamName);
            return new SimpleTestIterator(this, streamName, (int) count);
        }
        throw new FqlDataException("Stream entry point named " + streamName + " does not exist");
    }

    @Override
    public FqlMapContainer useMap(String name, List<String> fieldPath, boolean single) {
        return new SimpleTestMap(this, name, fieldPath, single);
    }

    @Override
    public FqlIterator range(String name, String startKey, String endKey, boolean includeEnd) {
        return new SimpleTestRange(letterNum(startKey), letterNum(endKey), includeEnd);
    }

    @Override
    public Object getMember(Object from, String member) {
        if (member.startsWith("L")) {
            return letterNum(member) * factor;
        } else if (member.startsWith("D")) {
            return getDouble(member) * factor;
        } else if (member.startsWith("T")) {
            return getTimeFactored(member);
        } else if ("yes".equalsIgnoreCase(member)) {
            return true;
        } else if ("no".equalsIgnoreCase(member)) {
            return false;
        } else {
            return from.toString() + '_' + member;
        }
    }

    public Date getTimeFactored(String fieldName) {
        return new Date(letterNum(fieldName) + 86400000 * (factor - 1));
    }

    public void close() {
        // nothing
    }
}
