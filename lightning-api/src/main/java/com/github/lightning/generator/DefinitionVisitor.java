/**
 * Copyright 2012 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.github.lightning.generator;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

import com.github.lightning.Marshaller;
import com.github.lightning.configuration.SerializerDefinition;
import com.github.lightning.metadata.PropertyDescriptor;

public interface DefinitionVisitor {

	void visitSerializerDefinition(SerializerDefinition serializerDefinition);

	void visitAttributeAnnotation(Class<? extends Annotation> attributeAnnotation);

	void visitClassDefine(Type type, Marshaller marshaller);

	void visitAnnotatedAttribute(PropertyDescriptor propertyDescriptor, Marshaller marshaller);

	void visitPropertyDescriptor(PropertyDescriptor propertyDescriptor, Marshaller marshaller);

	void visitFinalizeSerializerDefinition(SerializerDefinition serializerDefinition);

}
