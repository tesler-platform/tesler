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

package io.tesler.sqlbc.dao.binds;

import com.google.common.collect.ImmutableMap;
import io.tesler.api.config.TeslerBeanProperties;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.List;
import java.util.Map;
import javax.sql.DataSource;
import org.springframework.context.ApplicationContext;
import org.springframework.jdbc.core.SqlParameter;
import org.springframework.jdbc.core.namedparam.NamedParameterUtils;
import org.springframework.jdbc.core.namedparam.ParsedSql;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.stereotype.Component;

@Component
public class SqlNamedParameterQueryBinderImpl implements SqlNamedParameterQueryBinder {

	private final Map<Class, String> escapeTypes = ImmutableMap.<Class, String>builder()
			.put(Timestamp.class, "{ts '%s'}")
			.put(String.class, "'%s'")
			.build();
	private final DataSource dataSource;

	public SqlNamedParameterQueryBinderImpl(ApplicationContext applicationContext, TeslerBeanProperties teslerBeanProperties) {
		this.dataSource = applicationContext.getBean(teslerBeanProperties.getDataSource(), DataSource.class);
	}

	@Override
	public String bindVariables(String sql, SqlParameterSource paramSource) throws SQLException {
		ParsedSql parsedSql = NamedParameterUtils.parseSqlStatement(sql);
		List<SqlParameter> declaredParameters = NamedParameterUtils.buildSqlParameterList(parsedSql, paramSource);

		for (SqlParameter sp : declaredParameters) {
			Object paramValue = paramSource.getValue(sp.getName());
			if (paramValue != null) {
				String replacement = paramValue.toString();
				if (escapeTypes.containsKey(paramValue.getClass())) {
					replacement = String.format(escapeTypes.get(paramValue.getClass()), paramValue.toString());
				}
				// todo: нужно заменять все и учитывать что может быть общий префикс
				sql = sql.replaceFirst(":" + sp.getName(), replacement);
			}
		}
		return validate(sql);
	}

	private String validate(String sql) throws SQLException {
		try (
				Connection connection = dataSource.getConnection();
				PreparedStatement statement = connection.prepareStatement(sql);
		) {
			statement.getParameterMetaData();
			return sql;
		}
	}

}
