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

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import java.io.File;
import java.sql.ResultSet;
import liquibase.change.DatabaseChangeProperty;
import liquibase.change.custom.CustomTaskChange;
import liquibase.database.Database;
import liquibase.database.jvm.JdbcConnection;
import liquibase.exception.CustomChangeException;
import liquibase.exception.SetupException;
import liquibase.exception.ValidationErrors;
import liquibase.resource.ResourceAccessor;


public abstract class AbstractEntityUnload implements CustomTaskChange {

	public static final String RELATIVE_PATH = "db/migration/liquidbase/data/latest";

	private String path;

	@DatabaseChangeProperty(description = "Directory to place JSON files")
	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	protected File buildDirectory(String... children) {
		File file = new File(getPath());
		for (String part : RELATIVE_PATH.split("/")) {
			file = new File(file, part);
		}
		for (String part : children) {
			file = new File(file, part);
		}
		file.mkdirs();
		return file;
	}

	@Override
	public void execute(Database database) throws CustomChangeException {
		try {
			JdbcConnection connection = (JdbcConnection) database.getConnection();
			unload(connection);
		} catch (Exception ex) {
			throw new CustomChangeException(ex);
		}
	}

	protected abstract void unload(JdbcConnection connection) throws Exception;

	@Override
	public String getConfirmationMessage() {
		return null;
	}

	@Override
	public void setUp() throws SetupException {

	}

	@Override
	public void setFileOpener(ResourceAccessor resourceAccessor) {

	}

	@Override
	public ValidationErrors validate(Database database) {
		return new ValidationErrors();
	}

	protected JsonNode asJson(ObjectMapper mapper, ResultSet resultSet, String column) throws Exception {
		String data = resultSet.getString(column);
		if (data != null) {
			return mapper.readTree(data);
		}
		return null;
	}

	protected ObjectMapper createMapper() {
		ObjectMapper mapper = new ObjectMapper();
		mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
		mapper.enable(SerializationFeature.INDENT_OUTPUT);
		mapper.setVisibility(mapper.getSerializationConfig().getDefaultVisibilityChecker()
				.withFieldVisibility(JsonAutoDetect.Visibility.ANY).withGetterVisibility(JsonAutoDetect.Visibility.NONE)
				.withSetterVisibility(JsonAutoDetect.Visibility.NONE)
				.withCreatorVisibility(JsonAutoDetect.Visibility.NONE));
		return mapper;
	}

}
