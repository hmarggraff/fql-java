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

import spock.lang.Shared
import org.fqlsource.mockdriver.MockDriverConnection
import org.fqlsource.mockdriver.MockDriver
import org.yaml.snakeyaml.Yaml
import org.fqlsource.parser.FqlParser
import org.fqlsource.exec.FqlIterator

/**
 */
class SpockQueries extends spock.lang.Specification
{
  @Shared conn = makeConn();
  @Shared yaml = new Yaml();

  MockDriverConnection makeConn()
  {
    Properties p = new Properties();
    p.put("driver", "org.fqlsource.mockdriver.MockDriver");
    p.put("count", "2");
    final MockDriver mockDriver = new MockDriver();
    return mockDriver.open(p);
  }

  def String run(String query)
  {
    final FqlIterator it = FqlParser.runQuery(query,null,conn);
    ArrayList<Object[]> result = new ArrayList<Object[]>();
    while (it.hasNext())
    {
        final Object[] next = (Object[]) it.next();
        result.add(next);
    }
    final String dump = yaml.dump(result);
    System.out.println(dump);
    return dump
  }

  def "Basic Use And From Clauses"()
  {
    expect:
      run(query).equals(result)

    where:
    query | result
    "from e2 select a,b" | "- [1.a, 1.b]\n- [2.a, 2.b]\n"
    "from e2 select a" | "- [1.a]\n- [2.a]\n"
    "from e2" | "- [1]\n- [2]\n"
  }
}
