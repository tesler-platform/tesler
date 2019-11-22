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

package io.tesler.db.migration.liquibase.util;

import java.io.IOException;
import java.util.List;
import java.util.Set;
import liquibase.change.AbstractChange;
import liquibase.change.ColumnConfig;
import liquibase.changelog.ChangeSet;
import liquibase.exception.UnexpectedLiquibaseException;
import liquibase.resource.ResourceAccessor;
import liquibase.util.file.FilenameUtils;


public final class ColumnUtils {

	private ColumnUtils() {
		super();
	}


	public static boolean hasBlobs(List<ColumnConfig> columns) {
		for (ColumnConfig columnConfig : columns) {
			if (hasBlobs(columnConfig)) {
				return true;
			}
		}
		return false;
	}

	public static boolean hasBlobs(ColumnConfig column) {
		return column.getValueBlobFile() != null || column.getValueClobFile() != null;
	}


	public static List<ColumnConfig> normalizeLobs(
			List<ColumnConfig> columnConfigs,
			ChangeSet changeSet,
			ResourceAccessor resourceAccessor
	) {
		for (ColumnConfig columnConfig : columnConfigs) {
			normalizeLobs(columnConfig, changeSet, resourceAccessor);
		}
		return columnConfigs;
	}

	public static List<ColumnConfig> normalizeLobs(List<ColumnConfig> columnConfigs, AbstractChange change) {
		for (ColumnConfig columnConfig : columnConfigs) {
			normalizeLobs(columnConfig, change.getChangeSet(), change.getResourceAccessor());
		}
		return columnConfigs;
	}

	public static void normalizeLobs(
			ColumnConfig columnConfig,
			ChangeSet changeSet,
			ResourceAccessor resourceAccessor
	) {
		try {
			String changeLog = changeSet.getChangeLog().getPhysicalFilePath();
			columnConfig.setValueBlobFile(getRelativePath(resourceAccessor, columnConfig.getValueBlobFile(), changeLog));
			columnConfig.setValueClobFile(getRelativePath(resourceAccessor, columnConfig.getValueClobFile(), changeLog));
		} catch (IOException ex) {
			throw new UnexpectedLiquibaseException(ex);
		}
	}

	private static String getRelativePath(
			ResourceAccessor resourceAccessor,
			String path,
			String changeLogPath
	) throws IOException {
		if (path == null) {
			return null;
		}
		Set<String> candidates = resourceAccessor.list(changeLogPath, path, true, false, false);
		if (candidates != null && !candidates.isEmpty()) {
			return path;
		}
		candidates = resourceAccessor.list(null, path, true, false, false);
		if (candidates == null || candidates.isEmpty()) {
			return path;
		}
		int slashCount = FilenameUtils.getFullPath(changeLogPath).split("/").length;
		StringBuilder pathBuilder = new StringBuilder();
		for (int i = 0; i < slashCount; i++) {
			pathBuilder.append("../");
		}
		pathBuilder.append(path);
		return pathBuilder.toString();
	}

}
