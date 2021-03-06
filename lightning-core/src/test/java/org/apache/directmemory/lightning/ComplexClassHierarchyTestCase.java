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
package org.apache.directmemory.lightning;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;

import org.apache.directmemory.lightning.Lightning;
import org.apache.directmemory.lightning.Serializer;
import org.apache.directmemory.lightning.base.AbstractSerializerDefinition;
import org.apache.directmemory.lightning.internal.util.DebugLogger;
import org.apache.directmemory.lightning.io.SerializerInputStream;
import org.apache.directmemory.lightning.io.SerializerOutputStream;
import org.apache.directmemory.lightning.metadata.Attribute;
import org.apache.directmemory.lightning.metadata.ClassDefinitionContainer;
import org.junit.Test;

public class ComplexClassHierarchyTestCase
{

    @Test
    public void testSimpleObject()
        throws Exception
    {
        Serializer serializer =
            Lightning.newBuilder().logger( new DebugLogger() ).debugCacheDirectory( new File( "target" ) ).serializerDefinitions( new SerializerDefinition() ).build();

        ClassDefinitionContainer container = serializer.getClassDefinitionContainer();

        Serializer remoteSerializer =
            Lightning.newBuilder().logger( new DebugLogger() ).serializerDefinitions( new SerializerDefinition() ).build();

        remoteSerializer.setClassDefinitionContainer( container );

        Foo foo = new Foo();
        foo.setId( 10000 );
        foo.setName( "SomeName" );

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        SerializerOutputStream out = new SerializerOutputStream( baos, serializer );
        out.writeObject( foo );

        assertNotNull( baos );
        assertNotNull( out );
        assertNotNull( baos.toByteArray() );

        ByteArrayInputStream bais = new ByteArrayInputStream( baos.toByteArray() );
        SerializerInputStream in = new SerializerInputStream( bais, serializer );
        Object value = in.readObject();
        assertNotNull( value );
        assertEquals( foo, value );
    }

    @Test
    public void testSomeMoreComplexObjectWithOneDefinition()
        throws Exception
    {
        Serializer serializer =
            Lightning.newBuilder().logger( new DebugLogger() ).debugCacheDirectory( new File( "target" ) ).serializerDefinitions( new SerializerDefinition() ).build();

        ClassDefinitionContainer container = serializer.getClassDefinitionContainer();

        Serializer remoteSerializer =
            Lightning.newBuilder().logger( new DebugLogger() ).serializerDefinitions( new SerializerDefinition() ).build();

        remoteSerializer.setClassDefinitionContainer( container );

        Foo foo = new Foo();
        foo.setId( 10000 );
        foo.setName( "SomeName" );

        Complex complex = new Complex();
        complex.setFoo( foo );
        complex.setBar( Bar.SomeOtherValue );

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        SerializerOutputStream out = new SerializerOutputStream( baos, serializer );
        out.writeObject( complex );

        assertNotNull( baos );
        assertNotNull( out );
        assertNotNull( baos.toByteArray() );

        ByteArrayInputStream bais = new ByteArrayInputStream( baos.toByteArray() );
        SerializerInputStream in = new SerializerInputStream( bais, serializer );
        Object value = in.readObject();
        assertNotNull( value );
        assertEquals( complex, value );
    }

    @Test
    public void testSomeMoreComplexObjectWithTwoDefinition()
        throws Exception
    {
        Serializer serializer =
            Lightning.newBuilder().logger( new DebugLogger() ).debugCacheDirectory( new File( "target" ) ).serializerDefinitions( new ParentSerializerDefinition() ).build();

        ClassDefinitionContainer container = serializer.getClassDefinitionContainer();

        Serializer remoteSerializer =
            Lightning.newBuilder().logger( new DebugLogger() ).serializerDefinitions( new ParentSerializerDefinition() ).build();

        remoteSerializer.setClassDefinitionContainer( container );

        Foo foo = new Foo();
        foo.setId( 10000 );
        foo.setName( "SomeName" );

        Complex complex = new Complex();
        complex.setFoo( foo );
        complex.setBar( Bar.SomeOtherValue );

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        SerializerOutputStream out = new SerializerOutputStream( baos, serializer );
        out.writeObject( complex );

        assertNotNull( baos );
        assertNotNull( out );
        assertNotNull( baos.toByteArray() );

        ByteArrayInputStream bais = new ByteArrayInputStream( baos.toByteArray() );
        SerializerInputStream in = new SerializerInputStream( bais, serializer );
        Object value = in.readObject();
        assertNotNull( value );
        assertEquals( complex, value );
    }

    public static class SerializerDefinition
        extends AbstractSerializerDefinition
    {

        @Override
        protected void configure()
        {
            serialize( Foo.class ).attributes();
            serialize( Complex.class ).attributes();
        }
    }

    public static class ParentSerializerDefinition
        extends AbstractSerializerDefinition
    {

        @Override
        protected void configure()
        {
            serialize( Foo.class ).attributes();

            install( new ChildSerializerDefinition() );
        }
    }

    public static class ChildSerializerDefinition
        extends AbstractSerializerDefinition
    {

        @Override
        protected void configure()
        {
            serialize( Complex.class ).attributes();
        }
    }

    public static class Foo
    {

        @Attribute
        private int id;

        @Attribute
        private String name;

        public int getId()
        {
            return id;
        }

        public void setId( int id )
        {
            this.id = id;
        }

        public String getName()
        {
            return name;
        }

        public void setName( String name )
        {
            this.name = name;
        }

        @Override
        public int hashCode()
        {
            final int prime = 31;
            int result = 1;
            result = prime * result + id;
            result = prime * result + ( ( name == null ) ? 0 : name.hashCode() );
            return result;
        }

        @Override
        public boolean equals( Object obj )
        {
            if ( this == obj )
                return true;
            if ( obj == null )
                return false;
            if ( getClass() != obj.getClass() )
                return false;
            Foo other = (Foo) obj;
            if ( id != other.id )
                return false;
            if ( name == null )
            {
                if ( other.name != null )
                    return false;
            }
            else if ( !name.equals( other.name ) )
                return false;
            return true;
        }

        @Override
        public String toString()
        {
            return "Foo [id=" + id + ", name=" + name + "]";
        }
    }

    public static class Complex
    {

        @Attribute
        private Foo foo;

        @Attribute
        private Bar bar;

        public Foo getFoo()
        {
            return foo;
        }

        public void setFoo( Foo foo )
        {
            this.foo = foo;
        }

        public Bar getBar()
        {
            return bar;
        }

        public void setBar( Bar bar )
        {
            this.bar = bar;
        }

        @Override
        public int hashCode()
        {
            final int prime = 31;
            int result = 1;
            result = prime * result + ( ( bar == null ) ? 0 : bar.hashCode() );
            result = prime * result + ( ( foo == null ) ? 0 : foo.hashCode() );
            return result;
        }

        @Override
        public boolean equals( Object obj )
        {
            if ( this == obj )
                return true;
            if ( obj == null )
                return false;
            if ( getClass() != obj.getClass() )
                return false;
            Complex other = (Complex) obj;
            if ( bar != other.bar )
                return false;
            if ( foo == null )
            {
                if ( other.foo != null )
                    return false;
            }
            else if ( !foo.equals( other.foo ) )
                return false;
            return true;
        }

        @Override
        public String toString()
        {
            return "Complex [foo=" + foo + ", bar=" + bar + "]";
        }
    }

    public static enum Bar
    {
        SomeValue, SomeOtherValue, SomeTotallyDifferentValue
    }
}
