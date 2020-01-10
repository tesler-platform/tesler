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

import java.io.Serializable;
import java.util.Properties;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.HibernateException;
import org.hibernate.MappingException;
import org.hibernate.Session;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.id.enhanced.SequenceStyleGenerator;
import org.hibernate.service.ServiceRegistry;
import org.hibernate.type.Type;

public class ExtSequenceStyleGenerator extends SequenceStyleGenerator {

	@Override
	public void configure(Type type, Properties params, ServiceRegistry serviceRegistry) throws MappingException {
		String entityName = params.getProperty(ENTITY_NAME);
		if (StringUtils.isNotBlank(entityName)) {
			Class<?> entityClass = getClassOrNull(entityName);
			if (entityClass != null) {
				ExtSequenceGeneratorSequenceName sequenceNameAnnotation =
						entityClass.getAnnotation(ExtSequenceGeneratorSequenceName.class);
				if (sequenceNameAnnotation != null) {
					params.setProperty(SEQUENCE_PARAM, sequenceNameAnnotation.value());
				}
			}
		}
		super.configure(type, params, serviceRegistry);
	}

	@Override
	public Serializable generate(SharedSessionContractImplementor session, Object object) throws HibernateException {
		final Serializable currentId = session.getEntityPersister(null, object)
				.getClassMetadata()
				.getIdentifier(object, session);
		if (currentId != null) {
			return currentId;
		}
		// todo: optimize for non readonly transactions
		try (Session tempSession = session.getFactory().openSession()) {
			return super.generate(tempSession.unwrap(SharedSessionContractImplementor.class), object);
		}
	}

	private Class<?> getClassOrNull(String className) {
		try {
			return Class.forName(className);
		}  catch (ClassNotFoundException e) {
			return null;
		}
	}

}
