/*-
 * #%L
 * IO Tesler - Liquibase
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

package liquibase.ext.tesler.ui.unload;

import io.tesler.db.migration.liquibase.data.BcEntity;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.File;
import java.io.FileOutputStream;
import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import liquibase.change.ChangeMetaData;
import liquibase.change.DatabaseChange;
import liquibase.database.jvm.JdbcConnection;

@DatabaseChange(name = "bcunload", description = "Unload bcs", priority = ChangeMetaData.PRIORITY_DEFAULT)
public class BcUnload extends AbstractEntityUnload {

	@Override
	protected void unload(JdbcConnection connection) throws Exception {
		ObjectMapper mapper = createMapper();
		try (PreparedStatement statement = connection.prepareStatement("SELECT * FROM BC")) {
			try (ResultSet resultSet = statement.executeQuery()) {
				while (resultSet.next()) {
					BcEntity bcEntity = new BcEntity();
					bcEntity.setName(resultSet.getString("NAME"));
					bcEntity.setParentName(resultSet.getString("PARENT_NAME"));
					bcEntity.setDefaultOrder(resultSet.getString("DEFAULT_ORDER"));
					JsonNode binds = asJson(mapper, resultSet, "BINDS");
					if (binds != null && binds.size() > 0) {
						bcEntity.setBinds(binds);
					}
					bcEntity.setReportDateField(resultSet.getString("REPORT_DATE_FIELD"));
					BigDecimal editable = resultSet.getBigDecimal("EDITABLE");
					if (editable != null && editable.compareTo(new BigDecimal(0)) != 0) {
						bcEntity.setEditable(editable.longValue());
					}
					if (BigDecimal.ONE.equals(resultSet.getBigDecimal("REFRESH"))) {
						bcEntity.setRefresh(1L);
					}
					BigDecimal pageLimit = resultSet.getBigDecimal("PAGE_LIMIT");
					if (pageLimit != null && pageLimit.compareTo(new BigDecimal(5)) != 0) {
						bcEntity.setPageLimit(pageLimit.longValue());
					}
					String query = resultSet.getString("QUERY");
					File destination = buildDirectory("sqlbc", bcEntity.getName());

					if (query != null) {
						try (FileOutputStream fos = new FileOutputStream(
								new File(destination, String.format("%s.sqlbc.sql", bcEntity.getName())))) {
							fos.write(query.getBytes());
						}
						bcEntity.setQueryFile(String.format("db/migration/liquidbase/data/latest/sqlbc/%s/%s.sqlbc.sql",
								bcEntity.getName(), bcEntity.getName()
						));
					}

					try (FileOutputStream fos = new FileOutputStream(
							new File(destination, String.format("%s.sqlbc.json", bcEntity.getName())))) {
						mapper.writeValue(fos, bcEntity);
					}
				}
			}
		}
	}

}
