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

import org.funql.ri.jsondriver.JsonConnection
import java.util.HashMap
import org.testng.annotations.Test
import org.testng.Assert
import org.funql.ri.parser.FqlParser
import org.funql.ri.test.util.dump

/**
 * Unit test for simple MockDriver.
 */
class JsonLookupTest
{
    fun run(txt: String, q: String, expect: Any)
    {
        val p = HashMap<String, String>()
        p.put("driver", "JsonDriver")
        p.put("text", txt)
        val conn = JsonConnection("Json", p)
        val iter = FqlParser.runQuery(q, null, conn)!!
        //val nextVal = iter.next()
        val strRes = dump(iter)
        Assert.assertEquals(strRes, expect)
        conn.close()
    }

    Test fun lookup() {
        //run("[{'a':'val1'},{'a':'val2'}]", "link 'other.json' by a as other from top select other['val2']", "[{f:'val2'}]")
        run("[{'a':'val1'},{'a':'val2'}]", "link 'jsondriver/testresources/other.json' by a.b as other from top select other[a] limit 1", "[{f:[{a:{b:'val1'},c:'result1'}]}]")
        run("[{'a':'val1','cmp':'result1'},{'a':'val2','cmp':'result2'}]", "link 'jsondriver/testresources/other.json' by a.b as other from top select other[a].c = cmp", "[{f1:true},{f2:true}]")
    }


}
