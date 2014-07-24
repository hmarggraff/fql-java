package org.funql.ri.exec;

/*
 Copyright (C) 2011, Hans Marggraff and other copyright owners as documented in the project's IP log.
 This program and the accompanying materials are made available under the terms of the Eclipse Distribution License
 v1.0 which accompanies this distribution, is reproduced below, and is available at http://www.eclipse
 .org/org/documents/edl-v10.php
 All rights reserved.
 Redistribution and use in source and binary forms, with or without modification,
 are permitted provided that the following conditions are met:
 - Redistributions of source code must retain the above copyright notice, this list of conditions and the following
 disclaimer.
 - Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the
 following disclaimer in the documentation and/or other materials provided with the distribution.
 - Neither the name of the Eclipse Foundation, Inc. nor the names of its contributors may be used to endorse or
 promote products derived from this software without specific prior written permission.

 THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES,
 INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 DISCLAIMED.
 IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY,
 WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

import org.funql.ri.data.*;

import java.util.HashMap;
import java.util.Stack;

public class RunEnv {
    public static final String default_provided_connection_name = "provided_connection";
    Object[] parameterValues;
    /**
     * the array of all the database drivers used in this query
     */
    HashMap<String, FunqlDriver> drivers = new HashMap<String, FunqlDriver>();
    /**
     * the list of connections (i.e. databases)
     */
    FunqlConnection[] connections;
    /**
     * The entry points (i.e. tables, collections, containers, column families)
     */
    FqlMapContainer[] mapContainers;

    Stack<Object> outerObjects = new Stack<>();


    public RunEnv(int connectionCount, int entryPointCount, Object[] parameterValues) {
        connections = new FunqlConnection[connectionCount];
        mapContainers = new FqlMapContainer[entryPointCount];
        this.parameterValues = parameterValues;
    }

    public Object getVariable(int parameterIndex) {
        return parameterValues[parameterIndex];
    }

    public Object getValue(String member, Object from, int index) throws FqlDataException {
        Object object = connections[index].getMember(from, member);
        return object;
    }

    public FunqlConnection getConnection(int connectionIndex) {
        return connections[connectionIndex];
    }

    public void setConnectionAt(int index, FunqlConnection conn) {
        connections[index] = conn;
    }

    public FunqlConnection[] getConnections() {
        return connections;
    }

    public FunqlDriver getDriver(String driverClassName) throws ClassNotFoundException,
            IllegalAccessException, InstantiationException {
        FunqlDriver driver = drivers.get(driverClassName);
        if (driver == null) {
            final Class<?> driverClass = Class.forName(driverClassName);
            if (!FunqlDriver.class.isAssignableFrom(driverClass))
                throw new FqlDataException("The driver  class " + driverClassName + " does not implement FunqlDriver.");
            driver = (FunqlDriver) driverClass.newInstance();
            drivers.put(driverClassName, driver);
        }

        return driver;
    }

    public void putMapContainer(int runtimeIndex, FqlMapContainer mapContainer) {
        mapContainers[runtimeIndex] = mapContainer;
    }

    public FqlMapContainer getMapContainer(int index) {
        return mapContainers[index];
    }

    public Object getMember(Object from, String memberName, EntryPointSlot dataSlot) {
        Object object = connections[dataSlot.getIndex()].getMember(from, memberName);
        return object;
    }

    public void pushObject(Object o) {
        outerObjects.push(o);
    }

    public void popObject() {
        outerObjects.pop();
    }

}
