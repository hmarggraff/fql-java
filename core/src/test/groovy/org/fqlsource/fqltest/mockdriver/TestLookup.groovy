/*
 *   Copyright (C) 2011, Hans Marggraff and other copyright owners as documented in the project's IP log.
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

package org.fqlsource.fqltest.mockdriver

import org.fqlsource.data.FqlMapContainer
import org.fqlsource.data.RunEnv
import org.fqlsource.exec.ConstStringNode
import org.fqlsource.exec.ContainerNameNode
import org.fqlsource.exec.IndexOpNode
import org.fqlsource.util.NamedIndex
import spock.lang.IgnoreRest
import spock.lang.Shared
import org.fqlsource.exec.DotNode

/**
 */
class TestLookup extends spock.lang.Specification
{
  @Shared conn = makeConn()
  @Shared long millis = System.currentTimeMillis()

  MockDriverConnection makeConn()
  {
    Map<String, String> p = new HashMap<String, String>();
    p.put("driver", "MockDriver");
    p.put("count", "1");
    MockDriverConnection tconn = new MockDriverConnection();
    tconn.init(p);
    return tconn
  }

  def Object node(String key)
  {
    FqlMapContainer map = conn.getMap("map")
    RunEnv env = new RunEnv(1, 1, 1, null)
    env.setMapContainer(0, map)
    ContainerNameNode lookup = new ContainerNameNode(new NamedIndex("map", 0), 0, 1)
    ConstStringNode csn = new ConstStringNode(key, 0, 2)
    IndexOpNode ion = new IndexOpNode(lookup, csn, 0, 1)
    def value = ion.getValue(env, null)
    return value;
  }

  def CheckLookup()
  {
    setup:
    FqlMapContainer map = conn.getMap("map")
    RunEnv env = new RunEnv(1, 1, 1, null)
    env.setMapContainer(0, map)
    ContainerNameNode lookup = new ContainerNameNode(new NamedIndex("map", 0), 0, 1)
    ConstStringNode csn = new ConstStringNode("lookup", 0, 2)
    IndexOpNode ion = new IndexOpNode(lookup, csn, 0, 1)
    when:
    def value = ion.getValue(env, null)
    then:
    value instanceof String
    value == "Mlookup"
  }
  def CheckLookupNav()
  {
    setup:
    FqlMapContainer map = conn.getMap("map")
    RunEnv env = new RunEnv(1, 1, 1, null)
    env.setMapContainer(0, map)
    ContainerNameNode lookup = new ContainerNameNode(new NamedIndex("map", 0), 0, 1)
    ConstStringNode csn = new ConstStringNode("lookup", 0, 2)
    IndexOpNode ion = new IndexOpNode(lookup, csn, 0, 1)
    DotNode dn = new DotNode(ion, "nav", 0,0,1)
    when:
    def value = dn.getValue(env, null)
    then:
    value instanceof String
    value == "Mlookup.nav"
  }

  def LookupTypes(String key, Class resultClass, Object result)
  {
    expect:
    final value = node(key)
    ((Class) resultClass).isInstance(value)
    value == result

    where:
    key | resultClass | result
    'T' + millis | Date.class   | new Date(millis)
    'L42'        | Long.class   | 42
    'D1_4142'    | Double.class | 1.4142
    'lookup'     | String.class | 'Mlookup'
    'yes'        | Boolean.class | true
    'no'         | Boolean.class | false
  }

}
