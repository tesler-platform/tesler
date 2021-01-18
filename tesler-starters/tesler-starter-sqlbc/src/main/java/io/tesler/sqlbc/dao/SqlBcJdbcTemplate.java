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
import io.tesler.core.util.session.SessionService;
import io.tesler.sqlbc.crudma.SqlBcDescription;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.NoResultException;
import javax.sql.DataSource;

import io.tesler.sqlbc.exception.BadSqlComponentException;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.BadSqlGrammarException;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.orm.jpa.vendor.Database;


public final class SqlBcJdbcTemplate {

	private final SessionService sessionService;

	private final NamedParameterJdbcTemplate jdbcTemplate;

	private final Database database;

	public SqlBcJdbcTemplate(SessionService sessionService, @Qualifier("primaryDS") DataSource dataSource,
			@Qualifier("primaryDatabase") Database primaryDatabase) {
		this.sessionService = sessionService;
		this.jdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
		this.database = primaryDatabase;
	}

	public <T> ResultPage<T> page(SqlBcDescription bcDescription, String parentId, QueryParameters queryParameters,
			RowMapper<T> rowMapper) {

		SqlBcQuery query = SqlBcQuery.build(sessionService, bcDescription, null, parentId, queryParameters, database);
		try {
			return jdbcTemplate.query(
					query.pageQuery(),
					query.parameterSource(),
					new PageableResultSetExtractor<>(queryParameters.getPageSize(), rowMapper)
			);
		} catch (BadSqlGrammarException e) {
			throw new BadSqlComponentException(bcDescription.getName(), e);
		}
	}

	public long count(SqlBcDescription bcDescription, String parentId, QueryParameters queryParameters) {
		SqlBcQuery query = SqlBcQuery.build(sessionService, bcDescription, null, parentId, queryParameters, database);
		try {
			return jdbcTemplate.queryForObject(
					query.countQuery(),
					query.parameterSource(),
					Long.class
			);
		} catch (BadSqlGrammarException e) {
			throw new BadSqlComponentException(bcDescription.getName(), e);
		}
	}

	public <T> T one(SqlBcDescription bcDescription, String id, String parentId, QueryParameters queryParameters,
			RowMapper<T> rowMapper) {
		SqlBcQuery query = SqlBcQuery.build(sessionService, bcDescription, id, parentId, queryParameters, database);
		try {
			return jdbcTemplate.queryForObject(
					query.idQuery(),
					query.parameterSource(),
					rowMapper
			);
		} catch (BadSqlGrammarException e) {
			throw new BadSqlComponentException(bcDescription.getName(), e);
		} catch (EmptyResultDataAccessException e) {
			throw new NoResultException(
					"При разборе компоненты " + bcDescription.getName() + " не найдено записи с id=" + id
			);
		}
	}

	@AllArgsConstructor
	private static final class PageableResultSetExtractor<T> implements ResultSetExtractor<ResultPage<T>> {

		private final long pageSize;

		private final RowMapper<T> rowMapper;

		@Override
		public ResultPage<T> extractData(ResultSet rs) throws SQLException {
			List<T> results = new ArrayList<>();
			int rowNum = 0;
			while (rowNum < pageSize && rs.next()) {
				results.add(rowMapper.mapRow(rs, rowNum++));
			}
			return new ResultPage<>(results, rs.next());
		}

	}

}
