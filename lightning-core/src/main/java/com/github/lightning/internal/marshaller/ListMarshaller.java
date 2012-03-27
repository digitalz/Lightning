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
package com.github.lightning.internal.marshaller;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import com.github.lightning.Marshaller;
import com.github.lightning.SerializationContext;
import com.github.lightning.TypeBindableMarshaller;
import com.github.lightning.base.AbstractMarshaller;
import com.github.lightning.metadata.ClassDefinition;

public class ListMarshaller extends AbstractMarshaller implements TypeBindableMarshaller {

	private final Class<?> listType;

	private Marshaller listTypeMarshaller;

	public ListMarshaller() {
		this(null);
	}

	private ListMarshaller(Class<?> listType) {
		this.listType = listType;
	}

	@Override
	public boolean acceptType(Class<?> type) {
		return List.class.isAssignableFrom(type);
	}

	@Override
	public void marshall(Object value, Class<?> type, DataOutput dataOutput, SerializationContext serializationContext) throws IOException {
		if (writePossibleNull(value, dataOutput)) {
			List<?> list = (List<?>) value;
			dataOutput.writeInt(list.size());
			for (Object entry : list) {
				if (writePossibleNull(entry, dataOutput)) {
					Marshaller marshaller;
					if (listType != null) {
						ensureMarshallerInitialized(serializationContext);
						marshaller = listTypeMarshaller;
					}
					else {
						marshaller = serializationContext.findMarshaller(entry.getClass());
					}

					ClassDefinition classDefinition = serializationContext.getClassDefinitionContainer().getClassDefinitionByType(entry.getClass());

					dataOutput.writeLong(classDefinition.getId());
					marshaller.marshall(entry, entry.getClass(), dataOutput, serializationContext);
				}
			}
		}
	}

	@Override
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public <V> V unmarshall(Class<?> type, DataInput dataInput, SerializationContext serializationContext) throws IOException {
		if (isNull(dataInput)) {
			return null;
		}

		int size = dataInput.readInt();
		List list = new ArrayList(size);
		if (size > 0) {
			for (int i = 0; i < size; i++) {
				if (isNull(dataInput)) {
					list.add(null);
				}
				else {
					long classId = dataInput.readLong();
					ClassDefinition classDefinition = serializationContext.getClassDefinitionContainer().getClassDefinitionById(classId);

					Marshaller marshaller;
					if (listType != null) {
						ensureMarshallerInitialized(serializationContext);
						marshaller = listTypeMarshaller;
					}
					else {
						marshaller = serializationContext.findMarshaller(classDefinition.getType());
					}

					list.add(marshaller.unmarshall(classDefinition.getType(), dataInput, serializationContext));
				}
			}
		}

		return (V) list;
	}

	@Override
	public Marshaller bindType(Field property) {
		Type genericType = property.getGenericType();
		if (genericType instanceof ParameterizedType) {
			ParameterizedType type = (ParameterizedType) genericType;
			Type[] types = type.getActualTypeArguments();
			if (types.length == 1) {
				Class<?> listType = (Class<?>) types[0];
				return new ListMarshaller(listType);
			}
		}

		return new ListMarshaller();
	}

	private void ensureMarshallerInitialized(SerializationContext serializationContext) {
		if (listTypeMarshaller != null)
			return;

		listTypeMarshaller = serializationContext.findMarshaller(listType);
	}
}
