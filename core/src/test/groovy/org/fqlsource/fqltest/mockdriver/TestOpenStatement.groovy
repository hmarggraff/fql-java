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

import org.fqlsource.parser.FqlParser
import org.yaml.snakeyaml.Yaml
import spock.lang.Shared
import org.fqlsource.parser.FqlParseException
import org.fqlsource.data.FqlDataException
import spock.lang.IgnoreRest

/**
 */
class TestOpenStatement extends spock.lang.Specification
{
  @Shared yaml = new Yaml();

  def String run(String query)
  {
    final org.fqlsource.data.FqlIterator it = FqlParser.runQuery(query)
    def result = new ArrayList()
    while (it.hasNext())
    {
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
    //System.out.println(shortRes);
    return shortRes
  }


  def CheckSyntaxErrorReporting()
  {
    when: FqlParser.runQuery(query)
    then:
      FqlParseException x = thrown()
      println x.messageLong
    where:
      query << ['from e1', // this must fail, because no connection is provided from here, nor in the query
              'open {driver = ""; more=""} from e1', // semicolon is bad
              'open {} ', // properties may not be empty

      ]
  }

  def CheckRuntimeErrorReporting()
  {
    when: FqlParser.runQuery(query)
    then:
      FqlDataException x = thrown()
      println x.getMessage()
    where:
      query <<
      [
        'open {driver = "non-existing"} from e1',
        'open {driver = "org.fqlsource.fqltest.mockdriver.MockDriverConnection", testExceptionHandling="true"} from e1',
      ]
  }
  def "Variations of the open statement"()
  {
    expect:
      run(query).equals(result)

    where:
      query | result
      'open {"driver" = "org.fqlsource.fqltest.mockdriver.MockDriverConnection"} from e1' | '1'
      'open {driver = "org.fqlsource.fqltest.mockdriver.MockDriverConnection"} from e1' | '1'
      'open {driver = "org.fqlsource.fqltest.mockdriver.MockDriverConnection",} from e1' | '1'
      'open {driver = "org.fqlsource.fqltest.mockdriver.MockDriverConnection", more=""} from e1' | '1'
      'open {driver = "org.fqlsource.fqltest.mockdriver.MockDriverConnection"} as c from e1' | '1'
  }

}
