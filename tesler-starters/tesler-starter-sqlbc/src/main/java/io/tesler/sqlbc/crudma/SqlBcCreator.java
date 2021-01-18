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

package io.tesler.sqlbc.crudma;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.tesler.api.exception.ServerException;
import io.tesler.core.controller.param.SearchOperation;
import io.tesler.sqlbc.crudma.SqlBcDescription;
import io.tesler.sqlbc.crudma.SqlBcDescription.Bind;
import io.tesler.sqlbc.crudma.SqlBcDescription.Bindings;
import io.tesler.sqlbc.crudma.SqlBcDescription.Field;
import io.tesler.sqlbc.dao.SqlBcQuery;
import io.tesler.sqlbc.dao.SqlFieldType;
import io.tesler.model.ui.entity.Bc;
import java.io.IOException;
import java.sql.Timestamp;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import javax.sql.DataSource;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.concurrent.LazyInitializer;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.jdbc.support.rowset.SqlRowSetMetaData;
import org.springframework.stereotype.Component;


@Component
public class SqlBcCreator {

	private final NamedParameterJdbcTemplate jdbcTemplate;

	private final ObjectMapper objectMapper;

	public SqlBcCreator(@Qualifier("primaryDS") DataSource dataSource,
			@Qualifier("teslerObjectMapper") ObjectMapper objectMapper
	) {
		this.jdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
		this.objectMapper = objectMapper;
	}

	public SqlBcDescription getDescription(Bc bc) {
		List<Bind> binds = getBindsFromJson(bc.getBinds());
		return new SqlBcDescription(bc, binds, new SqlBcFieldsLazyInitializer(bc.getQuery(), binds));
	}


	private List<Bind> getBindsFromJson(String bindsString) {
		try {
			List<Bindings> binds = objectMapper.readValue(bindsString, new TypeReference<List<Bindings>>() {
			});
			List<SqlBcDescription.Bind> sqlBcDescriptionBinds = new ArrayList<>();
			binds.forEach(bind -> {
				String key = "pickList".equals(bind.getType())
						? bind.getPickMap().entrySet().stream().filter(entry -> "id".equals(entry.getValue()))
						.findFirst().get()
						.getKey()
						: bind.getKey();
				if (bind.getOperations() == null) {
					sqlBcDescriptionBinds.add(new SqlBcDescription.Bind(key, null));
				} else {
					for (Map<String, Object> operationData : bind.getOperations()) {
						String operationType = operationData.entrySet().stream()
								.filter(entry -> "type".equals(entry.getKey()))
								.findFirst().get().getValue().toString();
						sqlBcDescriptionBinds.add(
								new SqlBcDescription.Bind(
										key + "_" + operationType,
										SearchOperation.of(operationType)
								)
						);
					}
				}
			});
			return sqlBcDescriptionBinds;
		} catch (IOException e) {
			throw new ServerException(e.getMessage(), e);
		}
	}


	@RequiredArgsConstructor
	private final class SqlBcFieldsLazyInitializer extends LazyInitializer<List<Field>> {

		private final Timestamp zeroDate = new Timestamp(0);

		private final String query;

		private final List<SqlBcDescription.Bind> binds;

		@Override
		protected List<SqlBcDescription.Field> initialize() {
			MapSqlParameterSource mapSqlParameterSource = new MapSqlParameterSource()
					.addValue("userid", 0L)
					.addValue("userrole", "")
					.addValue("userdeptid", 0L)
					.addValue("isfilterabledata", "N")
					.addValue("parentid", StringUtils.EMPTY)
					.addValue("datefrom", zeroDate)
					.addValue("dateto", zeroDate)
					.addValue("datefrom_tzaware", zeroDate)
					.addValue("dateto_tzaware", zeroDate)
					.addValue("timezone", ZoneId.systemDefault().getId())
					.addValue("datefrom2", zeroDate)
					.addValue("dateto2", zeroDate)
					.addValue("id", 0L);

			for (SqlBcDescription.Bind bind : binds) {
				if (bind.isExistInQuery(query)) {
					mapSqlParameterSource.addValue(bind.getBindName(), null);
				}
			}

			SqlRowSet rowSet = jdbcTemplate.queryForRowSet(
					String.format("select * from (%s) sqlquery where 0=1", query),
					mapSqlParameterSource
			);

			SqlRowSetMetaData metaData = rowSet.getMetaData();
			List<SqlBcDescription.Field> fields = new ArrayList<>();
			for (int columnNumber = 1; columnNumber <= metaData.getColumnCount(); columnNumber++) {
				String columnName = metaData.getColumnName(columnNumber);
				SqlFieldType type = SqlFieldType.Holder.getFromSqlType(metaData.getColumnType(columnNumber));
				fields.add(new SqlBcDescription.Field(columnName, type, Boolean.FALSE));
			}

			addExtraFields(fields);

			return fields;
		}

		private void addExtraFields(List<SqlBcDescription.Field> fields) {
			SqlBcQuery.EXTRA_FIELDS.forEach((k, v) -> fields.add(new Field(k, v, true)));
		}

	}


}
