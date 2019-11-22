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

package io.tesler.core.controller;

import io.tesler.api.data.PageSpecification;
import io.tesler.api.data.ResultPage;
import io.tesler.api.service.tx.TransactionService;
import io.tesler.api.util.Invoker;
import io.tesler.core.crudma.impl.sql.utils.SqlFieldType;
import io.tesler.core.dto.ResponseBuilder;
import io.tesler.core.dto.ResponseDTO;
import java.sql.CallableStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import javax.sql.DataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.CallableStatementCallback;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("sql")
public class SQLController {

	private static final int DEFAULT_PAGE_NUMBER = 1;

	private static final int DEFAULT_PAGE_SIZE = 100;

	private static final int MAX_PAGE_SIZE = 1000;

	private final JdbcTemplate jdbcTemplate;

	@Autowired
	private TransactionService txService;

	@Autowired
	private ResponseBuilder resp;

	@Autowired
	public SQLController(@Qualifier("primaryDS") DataSource dataSource) {
		jdbcTemplate = new JdbcTemplate(dataSource);
	}

	@RequestMapping(method = RequestMethod.POST, value = "execute")
	public ResponseDTO execute(@RequestBody Map<String, Object> requestBody,
			PageSpecification page) {
		boolean readOnly = true;
		String query = getQuery(requestBody, page, readOnly);
		Invoker<ResponseDTO, RuntimeException> callable =
				() -> executeQuery(
						query,
						Math.min(page.getPageSize(), MAX_PAGE_SIZE)
				);
		if (readOnly) {
			return txService.invokeInNewROTx(callable);
		} else {
			return txService.invokeInNewTx(callable);
		}
	}

	private String getQuery(Map<String, Object> requestBody, PageSpecification page, boolean readOnly) {
		String query = (String) requestBody.get("query");
		if (readOnly) {
			query = String.format(
					"select * from (select row_.*, rownum rownum_ from (%s) row_ where rownum <= %d) where rownum_ > %d",
					query, page.getTo(), page.getTo()
			);
		}
		return query;
	}

	private ResponseDTO executeQuery(String query, int pageSize) {
		return jdbcTemplate.execute(query, (CallableStatementCallback<ResponseDTO>) cs -> {
			boolean result = cs.execute();
			if (result) {
				return new ResponseDTO(extractRows(cs, pageSize));
			}
			Map<String, Integer> data = new HashMap<>();
			data.put("affected_rows", cs.getUpdateCount());
			return resp.build(Collections.singletonList(data));
		});
	}

	private Map<String, ?> extractRow(ResultSet rs, Map<String, SqlFieldType> meta) throws SQLException {
		Map<String, Object> result = new LinkedHashMap<>();
		for (Map.Entry<String, SqlFieldType> entry : meta.entrySet()) {
			String key = entry.getKey();
			SqlFieldType value = entry.getValue();
			result.put(key, rs.getObject(key, value.getJavaClass()));
		}
		return result;
	}

	private Map<String, SqlFieldType> extractMeta(ResultSet rs) throws SQLException {
		ResultSetMetaData metaData = rs.getMetaData();
		Map<String, SqlFieldType> result = new LinkedHashMap<>();
		for (int i = 1; i <= metaData.getColumnCount(); i++) {
			String columnName = metaData.getColumnName(i);
			SqlFieldType columnType = SqlFieldType.Holder.getFromSqlType(metaData.getColumnType(i));
			result.put(columnName, columnType);
		}
		return result;
	}

	private ResultPage<?> extractRows(CallableStatement cs, int pageSize) throws SQLException {
		try (ResultSet rs = cs.getResultSet()) {
			Map<String, SqlFieldType> meta = extractMeta(rs);
			List<Map<String, ?>> result = new ArrayList<>(pageSize);
			for (int rows = 0; rows < pageSize && rs.next(); rows++) {
				result.add(extractRow(rs, meta));
			}
			return ResultPage.of(result, rs.next());
		}
	}

}
