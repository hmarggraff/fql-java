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

package org.funql.ri.simpletestdriver

import org.funql.ri.parser.FqlParser
import org.funql.ri.simpletestdriver.SimpleTestConnection
import org.yaml.snakeyaml.Yaml
import spock.lang.IgnoreRest
import spock.lang.Shared
/**
 */
class SimpleQueries extends spock.lang.Specification {
  def @Shared conn = makeConn();
  def @Shared yaml = new Yaml();

  SimpleTestConnection makeConn() {
    Map<String, String> p = new HashMap<String, String>();
    def tconn = new SimpleTestConnection("SimpleTest", p);
    return tconn
  }

  def String run(String query) {
    print query
    print ' --> '
    final org.funql.ri.data.FqlIterator it = FqlParser.runQuery(query, null, conn)
    def result = new ArrayList()
    while (it.hasNext()) {
      Object[] next = (Object[]) it.next()
      if (next.length == 1)
        result.add(next[0]);
      else
        result.add(next)
    }
    final String dump;
    if (result.size() == 1)
      dump = yaml.dump(result[0])
    else
      dump = yaml.dump(result);
    def shortRes = dump.substring(0, dump.length() - 1);
    System.out.println(shortRes);

    return shortRes
  }

  def testBasicParsing() {
    setup:
    def query = 'from\n\t\r\ne0'
    def p = new FqlParser(query, conn)
    when:
    def clauses = p.parseClauses()
    then:
    clauses.size() == 1
    p.getQueryString() == query
    p.getPos() == query.length()

  }

  def "Basic Use And From Clauses"() {
    expect:
    run(query) == result

    where:
    query               | result
    "from e1 select e1" | '1.e1'
    "from e1 select e12" | '1.e12'
    "from e2 select a,b" | "- [1.a, 1.b]\n- [2.a, 2.b]"
    "from e2 select a" | "[1.a, 2.a]"
    "from e2" | "[1, 2]"
  }

  def "Ref Clauses"() {
    expect:
    run(query) == result
    println query
    println result

    where:
    query               | result
    "access x from e1 select x[yes]" | 'from x where primary=true'
    "access x from e1 select x[a]" | 'from x where primary=1.a'
    "access x from e2 select x[bla]" | '[from x where primary=1.bla, from x where primary=2.bla]'
  }

  def Expressions() {
    expect:
    run(query) == result

    where:
    query | result
    //"from e3 as x where not x >= 2 select x" | '1'
    //'from e1 select 3+5, 3.0 + 1, 3.0 = 3' | '[8, 4.0, true]'
    //'from e1 select true and false' | 'false'
    //'from e1 select true and true' | 'true'
    //'from e1 select not true' | 'false'
    //'from e1 select  3> 5 = false' | 'true'
    'from e1 select  3> 5 = false = true' | 'true'
    'from e1 select  3> 5 = 5<3' | 'true'

    /*
    "from e2 where e2 > 1 select e2" | '2'
    "from e3 as x where x <\n3 select x" | '[1, 2]'
    "from e3 as x where x <= 2" | '[1, 2]'
    "from e3 as x where x >= 2" | '[2, 3]'
    "from e3 as x where x != 2" | '[1, 3]'
    "from e3 as x where x = 2" | '2'
    "from e3 where true " | '[1, 2, 3]'
      "from e3 where false " | '[]'
      "from e3 as x select x+2" | '[3, 4, 5]'
      "from e3 as x select x-2" | '[-1, 0, 1]'
      "from e3 as x select x*x" | '[1, 4, 9]'
      */
  }

}
