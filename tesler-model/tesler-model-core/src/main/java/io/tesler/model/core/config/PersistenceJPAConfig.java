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

package io.tesler.model.core.config;

import io.tesler.api.service.tx.TransactionService;
import io.tesler.model.core.api.CurrentUserAware;
import io.tesler.model.core.api.EffectiveUserAware;
import io.tesler.model.core.entity.User;
import lombok.Getter;
import lombok.Setter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * To support force active fields in tesler add following bean:
 * <pre>{@code
 *@Bean
 *public PlatformTransactionManager transactionManager(
 *	final ApplicationContext applicationContext,
 *	final TeslerBeanProperties teslerBeanProperties,
 *	final ITransactionStatus txStatus) {
 *	return new TeslerJpaTransactionManagerForceActiveAware(applicationContext, teslerBeanProperties, txStatus);
 *}
 * }</pre>
 * Tesler do not autowire this bean by name, so fill free to set any bean name you need.
 * This transaction manager use EntityManagerFactory autowired by name = ${tesler.beans.entity-manager-factory} (default "entityManagerFactory")
 */
@Getter
@Setter
@EnableTransactionManagement
@Configuration
public class PersistenceJPAConfig {

	@Bean
	@TransactionScope
	public CurrentUserAware<User> auditorAware(TransactionService txService, EffectiveUserAware<User> effectiveUserAware) {
		User effectiveUser = txService.woAutoFlush(effectiveUserAware::getEffectiveSessionUser);
		return () -> effectiveUser;
	}

}
