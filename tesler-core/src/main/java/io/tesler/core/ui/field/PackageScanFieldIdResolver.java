/*-
 * #%L
 * IO Tesler - Core
 * %%
 * Copyright (C) 2018 - 2019 Tesler Contributors
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */

package io.tesler.core.ui.field;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.databind.DatabindContext;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.jsontype.TypeIdResolver;
import com.fasterxml.jackson.databind.type.TypeFactory;
import io.tesler.core.ui.model.json.field.FieldMeta.FieldMetaBase;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.core.type.filter.AnnotationTypeFilter;
import org.springframework.stereotype.Component;
import org.springframework.util.ClassUtils;
import org.springframework.util.StringUtils;

@Component
public class PackageScanFieldIdResolver implements TypeIdResolver {

	private JavaType baseType;

	private Map<String, JavaType> typeMap = new HashMap<>();

	@Value("${tesler.widget.fields.include-packages:io.tesler.core.ui.model.json.field.subtypes}")
	private String[] includePackages;

	@Value("${tesler.widget.fields.exclude-classes:}")
	private String[] excludeClasses;

	@Override
	public void init(JavaType javaType) {
		baseType = javaType;
		Class<?> baseClazz = baseType.getRawClass();
		Set<Class<?>> fieldsClasses = scanForFields(includePackages);
		fieldsClasses.forEach(annotatedClazz -> {
			if (!Arrays.asList(excludeClasses).contains(annotatedClazz.getCanonicalName())) {
				if (!baseClazz.isAssignableFrom(annotatedClazz)) {
					throw new IllegalStateException(
							"Class " + annotatedClazz.getName() + ", annotated with " + TeslerWidgetField.class.getName() +
									" must extends from class " + baseClazz.getName()
					);
				}
				TeslerWidgetField teslerWidgetField = annotatedClazz.getAnnotation(TeslerWidgetField.class);
				for (String widgetType : teslerWidgetField.value()) {
					if (typeMap.containsKey(widgetType)) {
						throw new IllegalStateException(
								"Widget type \"" + widgetType + "\" dublicated in TeslerWidgetField annotations.");
					}
					typeMap.put(
							widgetType,
							TypeFactory.defaultInstance().constructSpecializedType(baseType, annotatedClazz)
					);
				}
			}
		});
	}

	private Set<Class<?>> scanForFields(String[] packages) {
		if (packages.length == 0) {
			return Collections.emptySet();
		}
		ClassPathScanningCandidateComponentProvider scanner = new ClassPathScanningCandidateComponentProvider(false);
		scanner.addIncludeFilter(new AnnotationTypeFilter(TeslerWidgetField.class));
		Set<Class<?>> entitySet = new HashSet<>();
		for (String basePackage : packages) {
			if (StringUtils.hasText(basePackage)) {
				for (BeanDefinition candidate : scanner.findCandidateComponents(basePackage)) {
					try {
						entitySet.add(ClassUtils
								.forName(Objects.requireNonNull(candidate.getBeanClassName()), this.getClass().getClassLoader()));
					} catch (ClassNotFoundException e) {
						throw new IllegalStateException(e);
					}
				}
			}
		}
		return entitySet;
	}

	@Override
	public String idFromValue(Object o) {
		return idFromValueAndType(o, o.getClass());
	}

	@Override
	public String idFromBaseType() {
		return null;
	}

	@Override
	public String idFromValueAndType(Object o, Class<?> aClass) {
		if (o instanceof FieldMetaBase &&
				typeMap.containsKey(((FieldMetaBase) o).getType())) {
			return typeMap.get(((FieldMetaBase) o).getType()).getRawClass().getSimpleName();
		}
		return null;
	}

	@Override
	public JavaType typeFromId(DatabindContext databindContext, String s) throws IOException {
		if (typeMap.containsKey(s)) {
			return typeMap.get(s);
		}
		throw new IOException("Cannot find class for type = \"" + s + "\"");
	}

	@Override
	public String getDescForKnownTypeIds() {
		return null;
	}

	@Override
	public JsonTypeInfo.Id getMechanism() {
		return JsonTypeInfo.Id.CUSTOM;
	}

}
