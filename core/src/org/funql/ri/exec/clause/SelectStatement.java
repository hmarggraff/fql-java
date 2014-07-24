package org.funql.ri.exec.clause;

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

import org.funql.ri.data.FqlDataException;
import org.funql.ri.data.FqlIterator;
import org.funql.ri.data.NamedValues;
import org.funql.ri.exec.FqlStatement;
import org.funql.ri.exec.RunEnv;
import org.funql.ri.exec.node.FqlNodeInterface;
import org.funql.ri.util.NamedValuesImpl;

import java.util.ArrayList;
import java.util.HashSet;

/**
 * Implements the select clause
 * User: hmf
 * Date: 07.03.11
 */
public class SelectStatement implements FqlStatement {
    private final ArrayList<FqlNodeInterface> fieldList;
    String[] fieldNames;


    public SelectStatement(ArrayList<FqlNodeInterface> fieldList) {
	this.fieldList = fieldList;
	fieldNames = new String[fieldList.size()];
	HashSet<String> usednames = new HashSet<>();
	for (int i = 0; i < fieldList.size(); i++) {
	    String fieldName = buildFieldName(fieldList.get(i), usednames);
	    fieldNames[i] = fieldName;
	}
    }

    private String buildFieldName(FqlNodeInterface node, HashSet<String> usednames) {
	final String result;
	int i = 1;
	String ret;
	StringBuffer fieldNameBuffer = new StringBuffer();
	node.buildMemberName(fieldNameBuffer);
	if (fieldNameBuffer.length() > 0) {
	    result = fieldNameBuffer.toString();
	    ret = result;
	} else if (fieldList.size() == 1)
	    return "f";
	else {
	    result = "f";
	    ret = result + i;
	}
	while (usednames.contains(ret)) {
	    ret = result + i;
	    i++;
	}
	usednames.add(ret);
	return ret;
    }


    public FqlIterator execute(final RunEnv env, final FqlIterator precedent) throws FqlDataException {
	return new FqlIterator() {

	    @Override
	    public NamedValues next() {
		final Object parent = precedent.next();
		if (parent == FqlIterator.sentinel)
		    return FqlIterator.sentinel;
		Object[] fields = new Object[fieldList.size()];

		try {
		    env.pushObject(parent);
		    for (int i = 0; i < fieldList.size(); i++) {
			FqlNodeInterface node = fieldList.get(i);
			Object value = node.getValue(env, parent);
			fields[i] = value;
		    }
		} finally {
		    env.popObject();
		}
		return new NamedValuesImpl(fieldNames, fields);
	    }
	};
    }

    public String[] getFieldNames() {
        return fieldNames;
    }
}
