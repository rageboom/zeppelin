/*
* Licensed to the Apache Software Foundation (ASF) under one or more
* contributor license agreements.  See the NOTICE file distributed with
* this work for additional information regarding copyright ownership.
* The ASF licenses this file to You under the Apache License, Version 2.0
* (the "License"); you may not use this file except in compliance with
* the License.  You may obtain a copy of the License at
*
*  http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*/
package org.apache.zeppelin.python;

import org.apache.zeppelin.display.GUI;
import org.apache.zeppelin.interpreter.*;
import org.apache.zeppelin.user.AuthenticationInfo;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Properties;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.*;

public class PythonDockerInterpreterTest implements InterpreterOutputListener {
  private PythonDockerInterpreter docker;
  private PythonInterpreter python;

  @Before
  public void setUp() {
    docker = spy(new PythonDockerInterpreter(new Properties()));
    python = mock(PythonInterpreter.class);

    InterpreterGroup group = new InterpreterGroup();
    group.put("note", Arrays.asList(python, docker));
    python.setInterpreterGroup(group);
    docker.setInterpreterGroup(group);
    doReturn(true).when(docker).pull(any(InterpreterOutput.class), anyString());
    doReturn(python).when(docker).getPythonInterpreter();
  }

  @Test
  public void testActivateEnv() {
    InterpreterContext context = getInterpreterContext();
    docker.interpret("activate env", context);
    verify(python, times(1)).open();
    verify(python, times(1)).close();
    verify(docker, times(1)).pull(any(InterpreterOutput.class), anyString());
    verify(python).setPythonCommand("docker run -i --rm env python -iu");
  }

  @Test
  public void testDeactivate() {
    InterpreterContext context = getInterpreterContext();
    docker.interpret("deactivate", context);
    verify(python, times(1)).open();
    verify(python, times(1)).close();
    verify(python).setPythonCommand(null);
  }

  private InterpreterContext getInterpreterContext() {
    return new InterpreterContext(
        "noteId",
        "paragraphId",
        "paragraphTitle",
        "paragraphText",
        new AuthenticationInfo(),
        new HashMap<String, Object>(),
        new GUI(),
        null,
        null,
        null,
        new InterpreterOutput(this));
  }

  @Override
  public void onAppend(InterpreterOutput out, byte[] line) {

  }

  @Override
  public void onUpdate(InterpreterOutput out, byte[] output) {

  }
}
