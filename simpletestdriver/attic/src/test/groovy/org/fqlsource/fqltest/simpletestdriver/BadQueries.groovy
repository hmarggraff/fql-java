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

package org.fqlsource.fqltest.simpletestdriver;


import org.funql.ri.parser.FqlParser

import spock.lang.Shared
import org.funql.ri.parser.FqlParseException
import org.funql.ri.data.FqlDataException

/**
 */
class BadQueries extends spock.lang.Specification
{
  @Shared conn = makeConn();

  SimpleTestConnection makeConn()
  {
    Map<String, String> p = new HashMap<String, String>();
    p.put("driver", "SimpleTest");
    p.put("count", "1");
    SimpleTestConnection tconn = new SimpleTestConnection("BadQueries", p);
    return tconn
  }

  def "Recognize erroneous queries"()
  {
    when:
    println query

    FqlParser.runQuery(query, null, conn)
    then:
      FqlParseException x = thrown()
      println x.message
    where:
      query << [ 'use x from e1 select y[1]',  'use x from e1 select x[]', 'silly', '"eofinstring', 'from "', 'use "ep2"', 'use b as from a ', 'use b', 'use b as "bla"',
                'open {}', 'open { driver="bla"}', 'open from', 'open { from="bla"', 'from e1 where !a', 'use x from e1 select y(yes)']
  }

  def String run(String query) {
    println query
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
    println('unexpected result: ' + shortRes);

  }

  def "Check for runtime errors"()
  {
    when:
      run(query)
    then:
      FqlDataException x = thrown()
      println x.message
    where:
      query << ['from "ep1"']
  }
}
