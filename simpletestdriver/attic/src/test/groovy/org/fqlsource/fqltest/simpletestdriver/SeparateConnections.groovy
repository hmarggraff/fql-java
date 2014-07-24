/*
 *   Copyright (C) 2011, Hans Marggraff and other copyright owners as documented in the project's IP log.
 * This program and the accompanying materials are made available under the terms of the Eclipse Distribution License v1.0 which accompanies this distribution, is reproduced below, and is available at http://www.eclipse.org/org/documents/edl-v10.php
 * All rights reserved.
 * Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following conditions are met:
 * - Redistributions of source code must retain the above copyright notice, this list of conditions and the following disclaimer.
 * - Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following disclaimer in the documentation and/or other materials provided with the distribution.
 * - Neither the name of the Eclipse Foundation, Inc. nor the names of its contributors may be used to endorse or promote products derived from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS 'AS IS' AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED.
 * IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package org.funql.ri.simpletestdriver

import org.funql.ri.exec.RunEnv
import org.funql.ri.exec.node.ConstStringNode
import org.funql.ri.exec.node.ContainerNameNode
import org.funql.ri.exec.node.DotNode
import org.funql.ri.exec.node.IndexOpNode
import org.funql.ri.exec.EntryPointSlot
import org.funql.ri.simpletestdriver.SimpleTestConnection
import org.funql.ri.util.NamedIndex
import spock.lang.Shared

/**
 */
class SeparateConnections extends spock.lang.Specification {
  @Shared long millis = System.currentTimeMillis();

  @Shared EntryPointSlot it00 = new EntryPointSlot('TestLookup', 0, 'a1', 0)
  @Shared EntryPointSlot map00 = new EntryPointSlot(it00.name, it00.index, 'map', 0)
  @Shared EntryPointSlot it11 = new EntryPointSlot('Separate', 1, 'b1', 1)
  @Shared EntryPointSlot map11 = new EntryPointSlot(it11.name, it11.index, 'sep', 1)
  @Shared RunEnv env = makeRunEnv()

  RunEnv makeRunEnv()
  {
    def conn1 = new SimpleTestConnection(it00.name, ['driver': 'SimpleTest', 'count':'1'])
    def conn2 = new SimpleTestConnection(it11.name, ['driver': 'SimpleTest', 'count':'1', 'prefix':'_c2', 'factor':'3'])
    RunEnv env = new RunEnv(2, 2, null)
    env.setConnectionAt(0, conn1)
    env.setConnectionAt(1, conn2)
    List<String> path0 = [map00.entryPointName]
    List<String> fp0 = ['bla', 'blubb']
    env.putMapContainer(0, conn1.useMap(fp0))
    env.putMapContainer(1, conn2.useMap(['f1.f1a']))
    env.setIterator(0, conn1.getIterator(it00.entryPointName))
    env.setIterator(1, conn2.getIterator(it11.entryPointName))
    return env
  }

  def 'Separate Connection Navigation'() {
    setup:
      ContainerNameNode lookup = new ContainerNameNode(new NamedIndex(map11.entryPointName, 1), 0, 1)
      ConstStringNode csn = new ConstStringNode('lookup', 0, 2)
      IndexOpNode ion = new IndexOpNode(lookup, csn, 0, 1)

      DotNode dn = new DotNode(ion, 'nav', map11, 0, 1)
    when:
      def value = dn.getValue(env, null)
    then:
      value instanceof String
      value == 'from sep where f1.f1a=lookup.nav'
  }

  def 'Separate Map'(String key, Class resultClass, Object result) {
    setup:
        ContainerNameNode lookup = new ContainerNameNode(new NamedIndex(map11.entryPointName, 1), 0, 1)
        ConstStringNode csn = new ConstStringNode(key, 0, 2)
        IndexOpNode ion = new IndexOpNode(lookup, csn, 0, 1)
    expect:
        final value = ion.getValue(env, null)
        println value
        ((Class) resultClass).isInstance(value)
        value == result
    where:
      key          | resultClass   | result
      'T' + millis | Date.class    | new Date(millis)
      'L42'        | Long.class    | 42
      'D1_4142'    | Double.class  | 1.4142
      'lookup'     | String.class  | 'from sep where f1.f1a=lookup'
      'yes'        | Boolean.class | true
      'no'         | Boolean.class | false
  }

}
