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

import static org.hibernate.cfg.AvailableSettings.BATCH_FETCH_STYLE;
import static org.hibernate.cfg.AvailableSettings.BEAN_CONTAINER;
import static org.hibernate.cfg.AvailableSettings.CRITERIA_LITERAL_HANDLING_MODE;
import static org.hibernate.cfg.AvailableSettings.DEFAULT_BATCH_FETCH_SIZE;
import static org.hibernate.cfg.AvailableSettings.FORMAT_SQL;
import static org.hibernate.cfg.AvailableSettings.GENERATE_STATISTICS;
import static org.hibernate.cfg.AvailableSettings.PHYSICAL_NAMING_STRATEGY;
import static org.hibernate.cfg.AvailableSettings.STATEMENT_BATCH_SIZE;
import static org.hibernate.cfg.AvailableSettings.VALIDATE_QUERY_PARAMETERS;

import io.tesler.api.service.tx.ITransactionStatus;
import io.tesler.api.service.tx.TransactionService;
import io.tesler.model.core.api.CurrentUserAware;
import io.tesler.model.core.api.EffectiveUserAware;
import io.tesler.model.core.entity.User;
import io.tesler.model.core.hbn.ImprovedPhysicalNamingStrategy;
import io.tesler.model.core.tx.JpaTransactionManagerCustom;
import java.util.Collections;
import java.util.List;
import java.util.Properties;
import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;
import org.hibernate.loader.BatchFetchStyle;
import org.hibernate.query.criteria.LiteralHandlingMode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Profile;
import org.springframework.orm.hibernate5.SpringBeanContainer;
import org.springframework.orm.jpa.JpaVendorAdapter;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@EnableTransactionManagement
public class PersistenceJPAConfig {

	@Autowired
	private DefaultListableBeanFactory beanFactory;

	@Bean("teslerEntityManagerFactory")
	public LocalContainerEntityManagerFactoryBean entityManagerFactory(
			@Qualifier("primaryDS") final DataSource primaryDS,
			@Qualifier("jpaProperties") final Properties jpaProperties,
			@Qualifier("vendorAdapter") final JpaVendorAdapter vendorAdapter) {
		LocalContainerEntityManagerFactoryBean em = new LocalContainerEntityManagerFactoryBean();
		em.setDataSource(primaryDS);
		em.setPackagesToScan(getPackagesToScan().toArray(new String[0]));
		em.setJpaVendorAdapter(vendorAdapter);
		em.setJpaProperties(jpaProperties);
		em.setPersistenceUnitName("tesler-persistence-unit");
		return em;
	}

	protected List<String> getPackagesToScan() {
		return Collections.singletonList("io.tesler");
	}

	@Bean("teslerTransactionManager")
	public PlatformTransactionManager transactionManager(
			@Qualifier("teslerEntityManagerFactory") final EntityManagerFactory emf,
			final ITransactionStatus txStatus) {
		return new JpaTransactionManagerCustom(emf, txStatus);
	}

	@Profile("!Debug")
	@Bean("jpaProperties")
	public Properties jpaProperties() {
		Properties properties = new Properties();
		properties.setProperty("hibernate.connection.charSet", "UTF-8");
		properties.setProperty(PHYSICAL_NAMING_STRATEGY, ImprovedPhysicalNamingStrategy.class.getName());
		properties.setProperty("hibernate.synonyms", String.valueOf(true));
		properties.setProperty("hibernate.connection.includeSynonyms", String.valueOf(true));
		//properties.setProperty(AvailableSettings.ENHANCER_ENABLE_DIRTY_TRACKING, String.valueOf(true));
		properties.setProperty(FORMAT_SQL, String.valueOf(false));
		properties.setProperty(GENERATE_STATISTICS, String.valueOf(false));
		properties.setProperty(STATEMENT_BATCH_SIZE, String.valueOf(100));
		properties.setProperty(DEFAULT_BATCH_FETCH_SIZE, String.valueOf(100));
		properties.setProperty(BATCH_FETCH_STYLE, BatchFetchStyle.DYNAMIC.name());
		properties.setProperty(CRITERIA_LITERAL_HANDLING_MODE, LiteralHandlingMode.BIND.name());
		properties.setProperty(VALIDATE_QUERY_PARAMETERS, String.valueOf(false));
		properties.put(BEAN_CONTAINER, new SpringBeanContainer(beanFactory));
		return properties;
	}

	@Profile("Debug")
	@Bean("jpaProperties")
	public Properties jpaDebugProperties() {
		Properties properties = new Properties();
		properties.setProperty("hibernate.connection.charSet", "UTF-8");
		properties.setProperty(PHYSICAL_NAMING_STRATEGY, ImprovedPhysicalNamingStrategy.class.getName());
		properties.setProperty("hibernate.synonyms", String.valueOf(true));
		properties.setProperty("hibernate.connection.includeSynonyms", String.valueOf(true));
		//properties.setProperty(AvailableSettings.ENHANCER_ENABLE_DIRTY_TRACKING, String.valueOf(true));
		properties.setProperty(FORMAT_SQL, String.valueOf(true));
		properties.setProperty(GENERATE_STATISTICS, String.valueOf(true));
		properties.setProperty(STATEMENT_BATCH_SIZE, String.valueOf(100));
		properties.setProperty(DEFAULT_BATCH_FETCH_SIZE, String.valueOf(100));
		properties.setProperty(BATCH_FETCH_STYLE, BatchFetchStyle.DYNAMIC.name());
		properties.setProperty(CRITERIA_LITERAL_HANDLING_MODE, LiteralHandlingMode.AUTO.name());
		properties.setProperty(VALIDATE_QUERY_PARAMETERS, String.valueOf(false));
		properties.put(BEAN_CONTAINER, new SpringBeanContainer(beanFactory));
		return properties;
	}

	@Bean
	@TransactionScope
	public CurrentUserAware<User> auditorAware(TransactionService txService, EffectiveUserAware<User> effectiveUserAware) {
		User effectiveUser = txService.woAutoFlush(effectiveUserAware::getEffectiveSessionUser);
		return () -> effectiveUser;
	}

}
