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

package io.tesler.core.util;

import io.tesler.core.util.session.SessionService;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.stereotype.Service;

@Service("springBeanUtils")
public class SpringBeanUtils implements BeanFactoryAware {

	private static BeanFactory beanFactory;

	public static <T> T getBean(Class<T> clazz) {
		return beanFactory.getBean(clazz);
	}

	public static <T> T getBean(Class<T> clazz, String name) {
		return beanFactory.getBean(name, clazz);
	}

	@SuppressWarnings("unchecked")
	public static <T> T getBean(String name) {
		return (T) beanFactory.getBean(name);
	}

	public static SessionService session() {
		return getBean(SessionService.class);
	}

	@Override
	public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
		SpringBeanUtils.beanFactory = beanFactory;
	}

}
