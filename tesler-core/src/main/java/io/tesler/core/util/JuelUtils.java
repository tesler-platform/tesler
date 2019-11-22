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

import static org.apache.commons.lang3.ObjectUtils.defaultIfNull;

import de.odysseus.el.ExpressionFactoryImpl;
import de.odysseus.el.tree.ExpressionNode;
import de.odysseus.el.tree.TreeStore;
import de.odysseus.el.tree.impl.Builder;
import de.odysseus.el.tree.impl.ast.AstDot;
import de.odysseus.el.tree.impl.ast.AstIdentifier;
import de.odysseus.el.util.SimpleContext;
import java.net.URLEncoder;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.el.ExpressionFactory;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.reflect.MethodUtils;

@Slf4j
@UtilityClass
public final class JuelUtils {

	public static Set<Property> getProperties(final String template) {
		return getProperties(new TreeStore(new Builder(), null).get(template).getRoot());
	}

	private static Set<Property> getProperties(final ExpressionNode node) {
		final Set<Property> result = new HashSet<>();
		if (node instanceof AstIdentifier) {
			result.add(new Property(node.toString(), Collections.emptyList()));
		} else if (node instanceof AstDot) {
			final List<String> properties = new ArrayList<>();
			ExpressionNode childNode = node;
			while (childNode instanceof AstDot) {
				properties.add(0, childNode.toString().replace(". ", ""));
				childNode = (ExpressionNode) childNode.getChild(0);
			}
			if (childNode instanceof AstIdentifier) {
				result.add(new Property(childNode.toString(), properties));
			} else {
				for (int i = 0; i < childNode.getCardinality(); i++) {
					result.addAll(getProperties((ExpressionNode) childNode.getChild(i)));
				}
			}
		} else {
			for (int i = 0; i < node.getCardinality(); i++) {
				result.addAll(getProperties((ExpressionNode) node.getChild(i)));
			}
		}
		return result;
	}

	public static String format(String template, Map<String, Object> parameters) {
		final SimpleContext context = new SimpleContext();
		context.setFunction(
				"",
				"format",
				MethodUtils.getAccessibleMethod(JuelUtils.class, "formatDate", Object.class, String.class)
		);
		context.setFunction(
				"",
				"urlEncode",
				MethodUtils.getAccessibleMethod(JuelUtils.class, "encode", Object.class)
		);
		context.setFunction(
				"",
				"plusDays",
				MethodUtils.getAccessibleMethod(JuelUtils.class, "plusDays", LocalDateTime.class, Integer.class)
		);
		context.setFunction(
				"",
				"plusMonth",
				MethodUtils.getAccessibleMethod(JuelUtils.class, "plusMonth", LocalDateTime.class, Integer.class)
		);
		final ExpressionFactory factory = new ExpressionFactoryImpl();
		parameters.forEach((k, v) -> {
			context.setVariable(k, factory.createValueExpression(v, v == null ? Object.class : v.getClass()));
		});
		return (String) factory.createValueExpression(context, defaultIfNull(template, ""), String.class).getValue(context);
	}

	public static String formatDate(Object date, String format) {
		if (date == null) {
			return null;
		} else if (date instanceof Date) {
			DateFormat df = new SimpleDateFormat(format);
			return df.format(date);
		} else if (date instanceof LocalDate) {
			DateTimeFormatter dtf = DateTimeFormatter.ofPattern(format);
			return ((LocalDate) date).format(dtf);
		} else if (date instanceof LocalDateTime) {
			DateTimeFormatter dtf = DateTimeFormatter.ofPattern(format);
			return ((LocalDateTime) date).format(dtf);
		} else {
			throw new IllegalArgumentException(
					"JuelUtils#format: Expected Date, LocalDate or LocalDateTime. Found: " + date.getClass().getCanonicalName());
		}
	}

	@SneakyThrows
	public String encode(Object object) {
		return object == null ? null : URLEncoder.encode(object.toString(), "UTF-8");
	}

	public LocalDateTime plusDays(LocalDateTime dateTime, Integer value) {
		return dateTime == null ? null : dateTime.plusDays(value);
	}

	public LocalDateTime plusMonth(LocalDateTime dateTime, Integer value) {
		return dateTime == null ? null : dateTime.plusMonths(value);
	}

	@Data
	@RequiredArgsConstructor
	public static class Property {

		private final String identifier;

		private final List<String> properties;

	}

}
