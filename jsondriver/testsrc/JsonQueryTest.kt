/*
 * Copyright (C) 2011, Hans Marggraff and other copyright owners as documented in the project's IP log.
 * This program and the accompanying materials are made available under the terms of the Eclipse Distribution License v1.0 which accompanies this distribution, is reproduced below, and is available at http://www.eclipse.org/org/documents/edl-v10.php
 * All rights reserved.
 * Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following conditions are met:
 * - Redistributions of source code must retain the above copyright notice, this list of conditions and the following disclaimer.
 * - Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following disclaimer in the documentation and/or other materials provided with the distribution.
 * - Neither the name of the Eclipse Foundation, Inc. nor the names of its contributors may be used to endorse or promote products derived from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED.
 * IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package org.funql.ri.jsondriver.test

import kotlin.test.assertEquals
import org.funql.ri.jsondriver.JsonConnection
import java.util.HashMap
import java.util.ArrayList
import org.funql.ri.data.FqlMapContainer
import org.funql.ri.data.FqlDataException
import org.funql.ri.data.FqlMultiMapContainer
import org.funql.ri.data.FqlIterator
import org.testng.annotations.Test
import org.testng.Assert
import org.testng.annotations.AfterClass
import org.funql.ri.parser.FqlParser
import org.funql.ri.test.util.dump

/**
 * Unit test for simple MockDriver.
 */
class JsonQueryTest:JsonTestBase()
{

    Test fun updater()
    {
        runQuery("[]", "into text put 'x'", FqlIterator.sentinel)

    }
    //Test
            fun testEmpty() {
        runQuery("[]", "from text", FqlIterator.sentinel)
    }
    Test fun oneObject() {
        runQuery("{a: b, c: d}", "from text select a", "{a:b}")
    }
    Test fun testApp2() { runQuery("{a: b, c: d}", "from text select a + c", "{f:bd}") }
    Test fun testAppFields() { runQuery("[2,3,5,7]", "from text where it > 5 select it", "{f:7}") }
    Test fun lookup() {
        runQuery("[1,6]", "link 'other.json' by a.b from text where it > 5 select it", "{f:7}") }
    //Test fun testMultiMap() { run("{a: [2,3,5,7]}", "from top select from a where it < 3 select it", 2) }


}
