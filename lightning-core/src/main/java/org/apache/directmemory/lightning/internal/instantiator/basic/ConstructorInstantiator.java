/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.directmemory.lightning.internal.instantiator.basic;

import java.lang.reflect.Constructor;

import org.apache.directmemory.lightning.instantiator.ObjectInstantiator;
import org.apache.directmemory.lightning.internal.instantiator.ObjenesisException;

/**
 * Instantiates a class by grabbing the no args constructor and calling Constructor.newInstance(). This can deal with
 * default public constructors, but that's about it.
 * 
 * @author Joe Walnes
 * @see ObjectInstantiator
 */
public class ConstructorInstantiator
    implements ObjectInstantiator
{

    protected Constructor<?> constructor;

    public ConstructorInstantiator( Class<?> type )
    {
        try
        {
            constructor = type.getDeclaredConstructor( (Class[]) null );
        }
        catch ( Exception e )
        {
            throw new ObjenesisException( e );
        }
    }

    @Override
    public Object newInstance()
    {
        try
        {
            return constructor.newInstance( (Object[]) null );
        }
        catch ( Exception e )
        {
            throw new ObjenesisException( e );
        }
    }
}
