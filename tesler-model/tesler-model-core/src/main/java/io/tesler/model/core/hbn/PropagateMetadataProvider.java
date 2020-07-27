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

import java.lang.reflect.AnnotatedElement;
import java.util.HashMap;
import java.util.Map;
import org.hibernate.annotations.common.reflection.AnnotationReader;
import org.hibernate.boot.spi.BootstrapContext;
import org.hibernate.cfg.annotations.reflection.JPAMetadataProvider;
import org.hibernate.cfg.annotations.reflection.XMLContext;

/**
 * Decorator base JPAMetadataProvider, inheritance is used because
 * HBN uses explicit conversions to JPAMetadataProvider
 */
public class PropagateMetadataProvider extends JPAMetadataProvider {

	private final JPAMetadataProvider delegate;

	private Map<AnnotatedElement, AnnotationReader> cache = new HashMap<>(100);

	public PropagateMetadataProvider(BootstrapContext bootstrapContext, JPAMetadataProvider delegate) {
		super(bootstrapContext);
		this.delegate = delegate;
	}

	@Override
	public Map<Object, Object> getDefaults() {
		return delegate.getDefaults();
	}

	@Override
	public AnnotationReader getAnnotationReader(AnnotatedElement annotatedElement) {
		// computeIfAbsent не работает в JDK9 из-за рекурсивных вызовов,
		// а использовать ConcurrentHashMap согласно документации нельзя:
		//	 * ... Some attempted update operations
		//	 * on this map by other threads may be blocked while computation
		//	 * is in progress, so the computation should be short and simple,
		//	 * and must not attempt to update any other mappings of this map.
		AnnotationReader reader = cache.get(annotatedElement);
		if (reader == null) {
			reader = new PropagateAnnotationReader(
					delegate.getAnnotationReader(annotatedElement),
					this,
					annotatedElement
			);
			cache.put(annotatedElement, reader);
		}
		return reader;
	}

	@Override
	public XMLContext getXMLContext() {
		return delegate.getXMLContext();
	}

}
