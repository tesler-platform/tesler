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
 * Implementation of AnnotationReader adding parent entity annotations marked
 * using @PropagateAnnotations, to the annotations of the child entity. Created for the reason
 * that JPA and Hibernate annotations are not marked as @Inherited and therefore do not apply to
 * child entities
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
	 * Returns a set of potentially inherited annotation types
	 *
	 * @param annotatedElement annotated element
	 * @return set of annotations
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
	 * Returns an annotation of the specified type for an element, taking into account inheritance
	 *
	 * @param annotationType annotation type
	 * @return annotation
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
	 * returns whether an annotation of the specified type is specified for the element being annotated, considering inheritance
	 *
	 * @param annotationType annotation type
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
	 * returns an array of all element annotations taking into account inheritance
	 *
	 * @return array of annotations
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
	 * Gets the AnnotationReader of the parent element
	 *
	 * @param annotatedElement annotated element
	 * @return AnnotationReader attached to the parent element (class)
	 */
	private AnnotationReader getParentAnnotationReader(AnnotatedElement annotatedElement) {
		if (!(annotatedElement instanceof Class) || annotatedElement == Object.class) {
			return null;
		}
		return metadataProvider.getAnnotationReader(((Class) annotatedElement).getSuperclass());
	}

}
