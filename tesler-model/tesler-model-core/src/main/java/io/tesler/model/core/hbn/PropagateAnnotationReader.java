/*-
 * #%L
 * IO Tesler - Model Core
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

package io.tesler.model.core.hbn;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import org.hibernate.annotations.common.reflection.AnnotationReader;
import org.hibernate.annotations.common.reflection.MetadataProvider;

/**
 * Реализация AnnotationReader, добавляющая аннотации родительской сущности, помеченные
 * при помощи @PropagateAnnotations, к аннотациям дочерней сущности. Создан по причине того,
 * что аннотации JPA и Hibernate не помечены как @Inherited, и поэтому не применяются к
 * дочерним сущностям.
 */
public class PropagateAnnotationReader implements AnnotationReader {

	private final AnnotationReader delegate;

	private final MetadataProvider metadataProvider;

	private final Set<Class<? extends Annotation>> propagated;

	private final AnnotationReader parent;

	public PropagateAnnotationReader(AnnotationReader delegate,
			MetadataProvider metadataProvider, AnnotatedElement annotatedElement) {
		this.delegate = delegate;
		this.metadataProvider = metadataProvider;
		propagated = getPropagatedAnnotations(annotatedElement);
		parent = getParentAnnotationReader(annotatedElement);
	}

	/**
	 * возвращает набор потенциально наследуемых типов аннотаций
	 *
	 * @param annotatedElement аннотируемый элемент
	 * @return набор аннотаций
	 */
	private Set<Class<? extends Annotation>> getPropagatedAnnotations(AnnotatedElement annotatedElement) {
		if (!(annotatedElement instanceof Class)) {
			return Collections.emptySet();
		}
		PropagateAnnotations propagateAnnotations = annotatedElement.getAnnotation(PropagateAnnotations.class);
		if (propagateAnnotations == null) {
			return Collections.emptySet();
		}
		Set<Class<? extends Annotation>> result = new HashSet<>();
		Collections.addAll(result, propagateAnnotations.value());
		return result;
	}

	/**
	 * возвращает аннотацию указанного типа для элемента, а учетом наследования
	 *
	 * @param annotationType тип аннотации
	 * @return аннотация
	 */
	@Override
	public <T extends Annotation> T getAnnotation(Class<T> annotationType) {
		Annotation result = delegate.getAnnotation(annotationType);
		if (result == null && propagated.contains(annotationType)) {
			result = Optional.ofNullable(parent)
					.map(ar -> ar.getAnnotation(annotationType))
					.orElse(null);
		}
		return (T) result;
	}

	/**
	 * возвращает указана ли аннотация указанного типа для аннотируемого элемента с учетом наследования
	 *
	 * @param annotationType тип аннотации
	 * @return true/false
	 */
	@Override
	public <T extends Annotation> boolean isAnnotationPresent(Class<T> annotationType) {
		boolean result = delegate.isAnnotationPresent(annotationType);
		if (!result && propagated.contains(annotationType)) {
			result = Optional.ofNullable(parent)
					.map(ar -> ar.isAnnotationPresent(annotationType))
					.orElse(false);
		}
		return result;
	}

	/**
	 * возвращает массив всех аннотаций элемента с учетом наследования
	 *
	 * @return массив аннотаций
	 */
	@Override
	public Annotation[] getAnnotations() {
		List<Annotation> annotations = new ArrayList<>();
		Set<Class<? extends Annotation>> propagated = new HashSet<>(this.propagated);
		for (Annotation annotation : delegate.getAnnotations()) {
			annotations.add(annotation);
			propagated.remove(annotation.getClass());
		}
		for (Class<? extends Annotation> cls : propagated) {
			Optional.ofNullable(parent)
					.map(ar -> ar.getAnnotation(cls))
					.ifPresent(annotations::add);
		}
		return annotations.toArray(new Annotation[0]);
	}

	/**
	 * Получает AnnotationReader родительского элемента
	 *
	 * @param annotatedElement аннотируемый элемент
	 * @return AnnotationReader, привязанный к родительскому элементу (классу)
	 */
	private AnnotationReader getParentAnnotationReader(AnnotatedElement annotatedElement) {
		if (!(annotatedElement instanceof Class) || annotatedElement == Object.class) {
			return null;
		}
		return metadataProvider.getAnnotationReader(((Class) annotatedElement).getSuperclass());
	}

}
