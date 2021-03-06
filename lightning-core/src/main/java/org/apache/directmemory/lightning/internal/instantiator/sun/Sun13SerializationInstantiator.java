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
package org.apache.directmemory.lightning.internal.instantiator.sun;

import org.apache.directmemory.lightning.internal.instantiator.ObjenesisException;
import org.apache.directmemory.lightning.internal.instantiator.SerializationInstantiatorHelper;

/**
 * Instantiates a class by making a call to internal Sun private methods. It is only supposed to work on Sun HotSpot 1.3
 * JVM. This instantiator will create classes in a way compatible with serialization, calling the first non-serializable
 * superclass' no-arg constructor.
 * 
 * @author Leonardo Mesquita
 * @see org.apache.directmemory.lightning.instantiator.ObjectInstantiator
 */
public class Sun13SerializationInstantiator
    extends Sun13InstantiatorBase
{

    private final Class<?> superType;

    public Sun13SerializationInstantiator( Class<?> type )
    {
        super( type );
        this.superType = SerializationInstantiatorHelper.getNonSerializableSuperClass( type );
    }

    @Override
    public Object newInstance()
    {
        try
        {
            return allocateNewObjectMethod.invoke( null, new Object[] { type, superType } );
        }
        catch ( Exception e )
        {
            throw new ObjenesisException( e );
        }
    }
}
