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

import org.hibernate.annotations.common.reflection.java.JavaReflectionManager;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.spi.BootstrapContext;
import org.hibernate.boot.spi.MetadataBuilderFactory;
import org.hibernate.boot.spi.MetadataBuilderImplementor;
import org.hibernate.cfg.annotations.reflection.JPAMetadataProvider;

/**
 * Modifies the standard MetadataBuilderImplementor to use
 * customized AnnotationReader (PropagateAnnotationReader) instead of standard reader
 */
public class PropagateMetadataBuilderFactory implements MetadataBuilderFactory {

	@Override
	public MetadataBuilderImplementor getMetadataBuilder(MetadataSources metadatasources,
			MetadataBuilderImplementor defaultBuilder) {
		BootstrapContext bootstrapContext = defaultBuilder.getBootstrapContext();
		JavaReflectionManager reflectionManager = (JavaReflectionManager) bootstrapContext.getReflectionManager();
		JPAMetadataProvider metadataProvider = (JPAMetadataProvider) reflectionManager.getMetadataProvider();
		reflectionManager.setMetadataProvider(new PropagateMetadataProvider(bootstrapContext, metadataProvider));
		return defaultBuilder;
	}

}
