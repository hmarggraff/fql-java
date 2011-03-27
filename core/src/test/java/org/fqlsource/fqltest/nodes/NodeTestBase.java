package org.fqlsource.fqltest.nodes;

/* Copyright (C) 2011, Hans Marggraff and other copyright owners as documented in the project's IP log.
This program and the accompanying materials are made available under the terms of the Eclipse Distribution License v1.0 which accompanies this distribution, is reproduced below, and is available at http://www.eclipse.org/org/documents/edl-v10.php
All rights reserved.
Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following conditions are met:
- Redistributions of source code must retain the above copyright notice, this list of conditions and the following disclaimer.
- Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following disclaimer in the documentation and/or other materials provided with the distribution.
- Neither the name of the Eclipse Foundation, Inc. nor the names of its contributors may be used to endorse or promote products derived from this software without specific prior written permission.

THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED.
IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE. */

import org.fqlsource.data.FqlDataException;
import org.fqlsource.data.RunEnv;
import org.fqlsource.exec.*;
import org.junit.BeforeClass;

import java.util.Arrays;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

public class NodeTestBase
{
    static RunEnv env;
    static ConstIntNode in0;
    static ConstIntNode in1;
    static ConstIntNode in2;
    static ConstIntNode in6;
    static ConstIntNode in9;
    static ConstFloatNode fn2;
    static ConstBooleanNode bnt;
    static ConstBooleanNode bnf;
    static NilNode nilNode;
    static ConstStringNode sne;
    static ConstStringNode sng;
    static String[] stringArray;
    static List<String> listVal;
    static SortedSet<String> setVal;
    static TestValueNode arrayNode;
    static TestValueNode listNode;
    static TestValueNode setNode;

    @BeforeClass
    public static void createNodes() throws FqlDataException
    {
        in0 = new ConstIntNode(0, 1, 1);
        in1 = new ConstIntNode(1, 1, 1);
        in2 = new ConstIntNode(2, 1, 1);
        in6 = new ConstIntNode(6, 1, 1);
        in9 = new ConstIntNode(9, 1, 1);
        fn2 = new ConstFloatNode(2.0, 1, 1);
        bnt = new ConstBooleanNode(true, 1, 1);
        bnf = new ConstBooleanNode(false, 1, 1);
        nilNode = new NilNode(1, 1);
        sne = new ConstStringNode("", 1, 1);
        sng = new ConstStringNode("Germering", 1, 1);
        env = new RunEnv(1, 1, 0, null);
        stringArray = new String[]{"a", "b", "c", "d", "e", "f"};
        arrayNode = new TestValueNode(stringArray);
        listVal = Arrays.asList(stringArray);
        listNode = new TestValueNode(listVal);
        setVal = new TreeSet<String>(listVal);
        setNode = new TestValueNode(setVal);
    }
}
