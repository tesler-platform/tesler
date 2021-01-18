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

package io.tesler.sqlbc.dao;

import io.tesler.api.data.ResultPage;
import io.tesler.core.controller.param.QueryParameters;
import io.tesler.core.crudma.bc.BusinessComponent;

import io.tesler.core.util.session.SessionService;
import io.tesler.sqlbc.crudma.SqlBcDescription;
import io.tesler.sqlbc.crudma.SqlComponentObject;

import java.sql.ResultSet;
import java.sql.SQLException;
import javax.sql.DataSource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cglib.beans.BeanGenerator;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.orm.jpa.vendor.Database;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class SqlComponentDao {

	private final SqlBcJdbcTemplate jdbcTemplate;

	@Autowired
	public SqlComponentDao(SessionService sessionService, @Qualifier("primaryDS") DataSource dataSource,
			@Qualifier("primaryDatabase") Database primaryDatabase) {
		jdbcTemplate = new SqlBcJdbcTemplate(sessionService, dataSource, primaryDatabase);
	}

	@SuppressWarnings("unchecked")
	public ResultPage<SqlComponentObject> getPage(BusinessComponent bc, QueryParameters queryParameters) {
		SqlBcDescription bcDescription = bc.getDescription();
		return jdbcTemplate.page(
				bcDescription,
				bc.getParentId(),
				queryParameters,
				new SqlComponentRowMapper(bcDescription)
		);
	}

	public SqlComponentObject getOne(BusinessComponent bc, QueryParameters queryParameters) {
		SqlBcDescription bcDescription = bc.getDescription();
		return jdbcTemplate.one(
				bcDescription,
				bc.getId(),
				bc.getParentId(),
				queryParameters,
				new SqlComponentRowMapper<>(bcDescription)
		);
	}

	public long count(BusinessComponent bc, QueryParameters queryParameters) {
		SqlBcDescription bcDescription = bc.getDescription();
		return jdbcTemplate.count(bcDescription, bc.getParentId(), queryParameters);
	}

	private static class SqlComponentRowMapper<T extends SqlComponentObject> implements RowMapper<T> {

		private final SqlBcDescription bcDescription;

		private final Class<T> cls;

		private SqlComponentRowMapper(SqlBcDescription bcDescription) {
			this.bcDescription = bcDescription;
			BeanGenerator generator = new BeanGenerator();
			generator.setSuperclass(SqlComponentObject.class);
			for (SqlBcDescription.Field field : this.bcDescription.getFields()) {
				String fieldName = field.getFieldName();
				if (!SqlComponentObject.isIdField(fieldName)) {
					generator.addProperty(fieldName, field.getType().getJavaClass());
				}
			}
			cls = (Class<T>) generator.createClass();
		}

		@Override
		public T mapRow(ResultSet rs, int rowNum) throws SQLException {
			try {
				T result = cls.newInstance();
				for (SqlBcDescription.Field field : bcDescription.getFields()) {
					Object value = rs.getObject(field.getColumnName(), field.getType().getJavaClass());
					result.set(field.getFieldName(), value);
				}
				return result;
			} catch (InstantiationException | IllegalAccessException ex) {
				throw new SQLException(ex);
			}
		}

	}

}
