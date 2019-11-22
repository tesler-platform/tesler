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

import io.tesler.api.util.proxy.DefaultDecorator;
import io.tesler.api.util.proxy.impl.CglibDecorators;
import io.tesler.api.util.proxy.impl.JDKDecorators;
import java.util.Map;
import org.hibernate.HibernateException;
import org.hibernate.LockMode;
import org.hibernate.dialect.Dialect;
import org.hibernate.dialect.lock.LockingStrategy;
import org.hibernate.engine.jdbc.dialect.internal.DialectFactoryImpl;
import org.hibernate.engine.jdbc.dialect.spi.DialectResolutionInfoSource;
import org.hibernate.persister.entity.Lockable;

public class EnhancedDialectFactoryImpl extends DialectFactoryImpl {

	@Override
	public Dialect buildDialect(Map configValues, DialectResolutionInfoSource resolutionInfoSource)
			throws HibernateException {
		Dialect dialect = super.buildDialect(configValues, resolutionInfoSource);
		return CglibDecorators.wrap(new DefaultDecorator<Dialect>(dialect) {

			@SuppressWarnings("unused")
			public LockingStrategy getLockingStrategy(Lockable lockable, LockMode lockMode) {
				if (lockMode == LockMode.PESSIMISTIC_READ) {
					return wrapped.getLockingStrategy(wrap(lockable), lockMode);
				}
				return wrapped.getLockingStrategy(lockable, lockMode);
			}

		});
	}


	private Lockable wrap(Lockable lockable) {
		return JDKDecorators.wrap(new DefaultDecorator<Lockable>(lockable) {

			@SuppressWarnings("unused")
			boolean isVersioned() {
				return false;
			}

		}, Lockable.class);
	}

}
