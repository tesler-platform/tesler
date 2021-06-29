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

package io.tesler.notifications.model.hbn.change;

import io.tesler.notifications.api.INotificationTemplate;
import io.tesler.api.util.i18n.LocalizationFormatter;
import io.tesler.notifications.model.hbn.change.methods.TemplateMethod;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMap.Builder;
import freemarker.cache.ClassTemplateLoader;
import freemarker.cache.MultiTemplateLoader;
import freemarker.cache.StringTemplateLoader;
import freemarker.cache.TemplateLoader;
import freemarker.ext.beans.BeansWrapper;
import freemarker.ext.beans.BeansWrapperBuilder;
import freemarker.template.Configuration;
import freemarker.template.SimpleHash;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import java.beans.PropertyDescriptor;
import java.io.IOException;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import javax.persistence.metamodel.Attribute;
import lombok.Getter;
import lombok.SneakyThrows;
import org.hibernate.Hibernate;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;


@Service
public class TemplateProcessingServiceImpl implements TemplateProcessingServiceExt {

	@Getter
	private final Configuration configuration;

	@Getter
	private final StringTemplateLoader stringTemplateLoader;

	@Getter
	private final BeansWrapper beansWrapper;

	@Getter
	private final ResourceBundleMessageSource bundles;

	@Autowired
	private List<TemplateMethod> templateMethods;

	@Autowired
	private Environment environment;

	public TemplateProcessingServiceImpl() throws Exception {
		stringTemplateLoader = new StringTemplateLoader();
		configuration = new Configuration(Configuration.DEFAULT_INCOMPATIBLE_IMPROVEMENTS);
		configuration.setTemplateLoader(new MultiTemplateLoader(
				new TemplateLoader[]{
						new ClassTemplateLoader(getClass(), "/templates"),
						stringTemplateLoader
				}
		));
		configuration.setDefaultEncoding("utf-8");
		Map<String, Object> vars = new HashMap<>();
		vars.put("environment", environment);
		configuration.setAllSharedVariables(
				new SimpleHash(vars, configuration.getObjectWrapper())
		);
		beansWrapper = new BeansWrapperBuilder(
				configuration.getIncompatibleImprovements()
		).build();
		bundles = new ResourceBundleMessageSource();
		bundles.setBasename("templates.messages");
		bundles.setParentMessageSource(
				LocalizationFormatter.getInstance().getBundles()
		);
	}

	private static String processTemplateIntoString(Template template, Object model)
			throws IOException, TemplateException {
		StringWriter result = new StringWriter();
		template.process(model, result);
		return result.toString();
	}

	@Override
	@SafeVarargs
	public final <E extends INotificationTemplate> Map<String, String> processTemplate(E entity,
			Map<String, Object> model,
			Attribute<?, String>... attributes) {
		Map<String, String> result = new HashMap<>();
		for (Attribute<?, String> attribute : attributes) {
			result.put(attribute.getName(), processTemplate(entity, attribute, model));
		}
		return result;
	}

	@SneakyThrows
	private <E extends INotificationTemplate> String processTemplate(
			E entity, Attribute<?, String> attribute,
			Map<String, Object> model) {
		return processTemplate(getTemplate(entity, attribute), model);
	}

	private Template getTemplate(INotificationTemplate entity, Attribute<?, String> attribute) throws Exception {
		long version = entity.getVstamp();
		String attributeName = attribute.getName();
		String key = String.format("%s-%d", attributeName, entity.getId());
		Object source = getStringTemplateLoader().findTemplateSource(key);
		long lastModified = -1;
		if (source != null) {
			lastModified = getStringTemplateLoader().getLastModified(source);
		}
		if (version <= lastModified) {
			return getConfiguration().getTemplate(key);
		}
		PropertyDescriptor propertyDescriptor = BeanUtils.getPropertyDescriptor(Hibernate.getClass(entity), attributeName);
		String template = (String) Objects.requireNonNull(propertyDescriptor).getReadMethod().invoke(entity);
		if (template == null) {
			template = "";
		}
		getStringTemplateLoader().putTemplate(key, template, version);
		return getConfiguration().getTemplate(key);
	}

	private String processTemplate(Template template, Map<String, Object> model) throws IOException, TemplateException {
		return processTemplateIntoString(template, enhanceModel(model));
	}

	@Override
	@SneakyThrows
	public String processTemplate(String templateName, Map<String, Object> model) {
		Template template = getConfiguration().getTemplate(templateName);
		return processTemplate(template, model);
	}

	@Override
	@SneakyThrows
	public String processTempTemplate(String templateString, Map<String, Object> model) {
		if (templateString == null) {
			return null;
		}
		String id = UUID.randomUUID().toString();
		try {
			getStringTemplateLoader().putTemplate(id, templateString);
			return processTemplate(id, model);
		} finally {
			getStringTemplateLoader().removeTemplate(id);
		}
	}

	private Map<String, Object> enhanceModel(Map<String, Object> model) {
		final Builder<String, Object> builder = ImmutableMap.<String, Object>builder().putAll(model);
		for (final TemplateMethod method : templateMethods) {
			builder.put(method.getName(), method);
		}
		return builder.build();
	}

}
