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

package io.tesler.core.util.export.base;

import io.tesler.core.util.export.base.model.ColumnMeta;
import io.tesler.core.util.export.base.model.ExportedRecord;
import io.tesler.core.util.export.base.model.TableMeta;
import java.sql.ResultSet;
import java.sql.SQLException;
import org.springframework.jdbc.core.RowMapper;

public class ExportedRecordRowMapper implements RowMapper<ExportedRecord> {

	private final TableMeta tableMeta;

	public ExportedRecordRowMapper(TableMeta tableMeta) {
		this.tableMeta = tableMeta;
	}

	@Override
	public ExportedRecord mapRow(ResultSet rs, int rowNum) throws SQLException {
		ExportedRecord exportedRecord = new ExportedRecord(tableMeta.getTableName());
		for (final ColumnMeta columnMeta : tableMeta.getColumns()) {
			if (columnMeta.getName().equals("ID")) {
				exportedRecord.setId(rs.getBigDecimal(columnMeta.getName()));
			}
			exportedRecord.addColumn(
					columnMeta,
					rs.getObject(columnMeta.getName(), columnMeta.getType().getJavaClass())
			);
		}
		return exportedRecord;
	}

}
