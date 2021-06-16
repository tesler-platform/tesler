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

package liquibase.ext.tesler.ui.load;

import io.tesler.db.migration.liquibase.data.LqbAbstractEntity;
import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.JsonSerializable;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.node.TextNode;

import java.io.*;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import liquibase.change.AbstractChange;
import liquibase.change.ColumnConfig;
import liquibase.change.DatabaseChangeProperty;
import liquibase.changelog.ChangeSet;
import liquibase.changelog.DatabaseChangeLog;
import liquibase.database.Database;
import liquibase.exception.SetupException;
import liquibase.exception.UnexpectedLiquibaseException;
import liquibase.exception.ValidationErrors;
import liquibase.ext.tesler.stmt.InsertPreparedStatement;
import liquibase.parser.core.ParsedNode;
import liquibase.parser.core.ParsedNodeException;
import liquibase.resource.ResourceAccessor;
import liquibase.statement.DatabaseFunction;
import liquibase.statement.SequenceNextValueFunction;
import liquibase.statement.SqlStatement;
import liquibase.statement.core.InsertOrUpdateStatement;
import liquibase.statement.core.InsertStatement;
import liquibase.util.StreamUtil;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

@Slf4j
public abstract class AbstractEntityChange<T extends LqbAbstractEntity> extends AbstractChange {

	public static final int MAX_CHARS = 2000;

	public static final ChangeSet EMPTY_CHANGE_SET = new ChangeSet(new DatabaseChangeLog(null));

	private String file;

	private String path;

	private String filter;

	private String encoding;

	private Boolean recursive;

	private List<T> elements = new ArrayList<>();

	@SneakyThrows
	private static Reader createReader(InputStream in, String encoding) {
		if (StringUtils.trimToNull(encoding) == null) {
			return new BufferedReader(new InputStreamReader(in));
		} else {
			return new BufferedReader(new InputStreamReader(in, encoding));
		}
	}

	@Override
	public ValidationErrors validate(Database database) {
		return new ValidationErrors();
	}

	@DatabaseChangeProperty(description = "JSON file to load", exampleValue = "com/example/widget.json")
	public String getFile() {
		return file;
	}

	public void setFile(String file) {
		this.file = file;
	}

	@DatabaseChangeProperty(description = "Encoding of the JSON file (defaults to UTF-8)", exampleValue = "UTF-8")
	public String getEncoding() {
		if (encoding == null) {
			return "UTF-8";
		}
		return encoding;
	}

	public void setEncoding(String encoding) {
		this.encoding = encoding;
	}

	public String getPath() {
		return path;
	}

	@DatabaseChangeProperty(description = "Directory containing JSON files")
	public void setPath(String path) {
		this.path = path;
	}

	@DatabaseChangeProperty(description = "Regexp to filter directory content")
	public String getFilter() {
		return filter;
	}

	public void setFilter(String filter) {
		this.filter = filter;
	}

	@DatabaseChangeProperty(description = "Whether to search JSON files recursively")
	public Boolean isRecursive() {
		return recursive;
	}

	public void setRecursive(Boolean recursive) {
		if (recursive == null) {
			this.recursive = false;
		} else {
			this.recursive = recursive;
		}
	}

	@Override
	public String getConfirmationMessage() {
		StringBuilder message = new StringBuilder("Data loaded from ");
		if (getFile() != null) {
			message.append(getFile());
		} else {
			message.append(getPath());
		}
		if (isRecursive()) {
			message.append(" recursively");
		}
		if (getFilter() != null) {
			message.append(", filter: ").append(getFilter());
		}
		return message.toString();
	}

	@Override
	public Set<String> getSerializableFields() {
		LinkedHashSet<String> result = new LinkedHashSet<>(super.getSerializableFields());
		result.add("elements");
		return result;
	}

	@Override
	public Object getSerializableFieldValue(String field) {
		try {
			if ("elements".equals(field)) {
				ByteArrayOutputStream baos = new ByteArrayOutputStream();
				ObjectMapper mapper = new ObjectMapper();
				mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
				mapper.enable(SerializationFeature.INDENT_OUTPUT);
				mapper.setVisibility(mapper.getSerializationConfig().getDefaultVisibilityChecker()
						.withFieldVisibility(JsonAutoDetect.Visibility.ANY).withGetterVisibility(JsonAutoDetect.Visibility.NONE)
						.withSetterVisibility(JsonAutoDetect.Visibility.NONE)
						.withCreatorVisibility(JsonAutoDetect.Visibility.NONE));
				mapper.writeValue(baos, elements);
				return new String(baos.toByteArray(), StandardCharsets.UTF_8);
			}
			return super.getSerializableFieldValue(field);
		} catch (Exception ex) {
			throw new UnexpectedLiquibaseException(ex);
		}
	}

	protected List<SqlStatement> generateStatements(
			Database database,
			ResourceAccessor resourceAccessor,
			T entity
	) throws Exception {
		boolean isPrepared = false;
		List<SqlStatement> result = new ArrayList<>();
		List<ColumnConfig> columnConfigs = new ArrayList<>();

		for (Field field : entity.getDBRelatedFields()) {
			ColumnConfig columnConfig = toColumnConfig(field, entity, resourceAccessor);
			if (columnConfig != null) {
				isPrepared |= columnConfig.getValueClobFile() != null;
				isPrepared |= columnConfig.getValueBlobFile() != null;
				columnConfigs.add(columnConfig);
			}
		}

		if (isPrepared) {
			result.add(
					new InsertPreparedStatement(
							database,
							entity.getTable(),
							columnConfigs,
							EMPTY_CHANGE_SET,
							resourceAccessor
					)
			);
			return result;
		}

		String primaryKey = entity.getPrimaryKey();
		Object pkValue = entity.getPKValue();
		InsertStatement statement;
		// TODO
		if (StringUtils.isEmpty(primaryKey) || pkValue == null) {
			statement = new InsertStatement(null, null, entity.getTable());
		} else {
			statement = new InsertOrUpdateStatement(null, null, entity.getTable(), primaryKey);
		}

		for (ColumnConfig columnConfig : columnConfigs) {
			statement.addColumn(columnConfig);
		}
		result.add(statement);

		return result;
	}

	public ColumnConfig toColumnConfig(Field field, T entity, ResourceAccessor resourceAccessor) throws Exception {
		Object value = field.get(entity);
		if (value != null) {
			return getColumnConfig(entity.getColumn(field), value);
		}

		ColumnConfig columnConfig = getComputedColumnConfig(field, entity);
		if (columnConfig != null) {
			return columnConfig;
		}

		columnConfig = getSequenceColumnConfig(field, entity);
		if (columnConfig != null) {
			return columnConfig;
		}

		columnConfig = getFileColumnConfig(field, entity, resourceAccessor);
		if (columnConfig != null) {
			return columnConfig;
		}

		if (entity.insertNulls(field)) {
			return getStringColumnConfig(entity.getColumn(field), null);
		}

		return null;
	}

	protected ColumnConfig getFileColumnConfig(Field field, T entity, ResourceAccessor resourceAccessor)
			throws Exception {
		String file = entity.getFileValue(field);
		if (StringUtils.isEmpty(file)) {
			return null;
		}

		boolean isText = CharSequence.class.isAssignableFrom(field.getType());
		isText |= JsonSerializable.class.isAssignableFrom(field.getType());
		if (isText) {
			try (Reader reader = getReader(file, resourceAccessor)) {
				StringBuilder value = new StringBuilder();
				int i = -1;
				while ((i = reader.read()) != -1) {
					value.append((char) i);
				}
				return getStringColumnConfig(entity.getColumn(field), value.toString());
			}
		}

		ColumnConfig columnConfig = new ColumnConfig();
		columnConfig.setName(entity.getColumn(field));
		columnConfig.setValueBlobFile(file);
		return columnConfig;
	}

	protected ColumnConfig getComputedColumnConfig(Field field, T entity) throws Exception {
		String function = entity.getFunctionValue(field);
		if (StringUtils.isEmpty(function)) {
			return null;
		}
		ColumnConfig columnConfig = new ColumnConfig();
		columnConfig.setName(entity.getColumn(field), true);
		return columnConfig.setValueComputed(new DatabaseFunction(function));
	}

	protected ColumnConfig getSequenceColumnConfig(Field field, T entity) throws Exception {
		String sequence = entity.getSequenceValue(field);
		if (StringUtils.isEmpty(sequence)) {
			return null;
		}
		ColumnConfig columnConfig = new ColumnConfig();
		columnConfig.setName(entity.getColumn(field), true);
		return columnConfig.setValueSequenceNext(new SequenceNextValueFunction(sequence));
	}

	protected ColumnConfig getColumnConfig(String columnName, Object value) throws Exception {
		if (value == null) {
			return getStringColumnConfig(columnName, null);
		}
		if (value instanceof TextNode) {
			ObjectMapper mapper = new ObjectMapper();
			return getColumnConfig(columnName, mapper.readTree(((TextNode) value).asText()));
		}
		if (value instanceof JsonNode) {
			return getStringColumnConfig(columnName, value.toString());
		}
		if (value instanceof String) {
			return getStringColumnConfig(columnName, (String) value);
		}
		if (value instanceof Number) {
			return getNumberColumnConfig(columnName, (Number) value);
		}
		if (value instanceof Date) {
			return getDateColumnConfig(columnName, (Date) value);
		}
		if (value instanceof Boolean) {
			return (Boolean) value ? getNumberColumnConfig(columnName, 1)
					: getNumberColumnConfig(columnName, 0);
		}
		throw new IllegalArgumentException("Unsupported value type:" + value.getClass());
	}

	protected ColumnConfig getDateColumnConfig(String columnName, Date value) {
		ColumnConfig columnConfig = new ColumnConfig().setName(columnName);
		return columnConfig.setValueDate(value);
	}

	protected ColumnConfig getNumberColumnConfig(String columnName, Number value) {
		ColumnConfig columnConfig = new ColumnConfig().setName(columnName);
		return columnConfig.setValueNumeric(value);
	}

	protected ColumnConfig getStringColumnConfig(String columnName, String value) {
		ColumnConfig columnConfig = new ColumnConfig().setName(columnName);
		if (value == null || value.length() < MAX_CHARS) {
			return columnConfig.setValue(value);
		}
		columnConfig.setComputed(true);
		StringBuilder expression = new StringBuilder();
		char[] chars = value.toCharArray();
		for (int i = 0; i < chars.length; i += MAX_CHARS) {
			char[] part = Arrays.copyOfRange(chars, i, Math.min(chars.length, i + MAX_CHARS));
			expression.append("TO_CLOB('").append(escapeString(new String(part))).append("')");
			if (i < chars.length - MAX_CHARS) {
				expression.append("||");
			}
		}
		columnConfig.setValueComputed(new DatabaseFunction(expression.toString()));
		return columnConfig;
	}

	private String escapeString(String string) {
		return string.replace("'", "''");
	}

	@Override
	protected void customLoadLogic(ParsedNode parsedNode, ResourceAccessor resourceAccessor) throws ParsedNodeException {
		String lastFile = null;
		int lastPosition = 0;
		try {
			Map<Object, String> seenKeys = new HashMap<>();
			for (String file : getResources(resourceAccessor)) {
				lastFile = file;
				T[] elements = read(file, resourceAccessor);
				for (int i = 0, n = elements.length; i < n; i++) {
					lastPosition = i;
					T element = elements[i];
					Object pkValue = element.getPKValue();
					if (pkValue != null) {
						if (seenKeys.containsKey(pkValue)) {
							throw new SetupException("Failed to process file: " + lastFile + ", position: " + lastPosition
									+ ", reason: duplicate has been found in file " + seenKeys.get(pkValue));
						}
						seenKeys.put(pkValue, file);
					}
					this.elements.add(element);
				}
			}
		} catch (ParsedNodeException ex) {
			throw ex;
		} catch (Exception ex) {
			throw new ParsedNodeException("Failed to process file: " + lastFile + ", position: " + lastPosition
					+ ", reason: " + ex.getMessage(), ex);
		}
	}

	@Override
	public boolean supports(Database database) {
		return true;
	}

	@Override
	public boolean generateStatementsVolatile(Database database) {
		return true;
	}

	@Override
	public boolean generateRollbackStatementsVolatile(Database database) {
		return true;
	}

	@Override
	public SqlStatement[] generateStatements(Database database) {
		try {
			List<SqlStatement> sqlStatements = new ArrayList<>();
			for (T element : elements) {
				sqlStatements.addAll(generateStatements(database, getResourceAccessor(), element));
			}
			return sqlStatements.toArray(new SqlStatement[0]);
		} catch (Exception ex) {
			throw new UnexpectedLiquibaseException(ex);
		}
	}

	private Class<T[]> getArrayClass() {
		return (Class<T[]>) Array.newInstance(getElementType(), 0).getClass();
	}

	protected abstract Class<T> getElementType();

	protected T[] read(String file, ResourceAccessor resourceAccessor) throws UnexpectedLiquibaseException {
		try (Reader reader = getReader(file, resourceAccessor)) {
			ObjectMapper mapper = new ObjectMapper();
			mapper.configure(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY, true);
			return mapper.readValue(reader, getArrayClass());
		} catch (Exception ex) {
			throw new UnexpectedLiquibaseException("Failed to read " + file, ex);
		}
	}

	private Reader getReader(String file, ResourceAccessor resourceAccessor) throws IOException {
		if (resourceAccessor == null) {
			throw new UnexpectedLiquibaseException("No file resourceAccessor specified for " + file);
		}
		InputStream stream = StreamUtil.openStream(file, false, null, resourceAccessor);
		if (stream == null) {
			throw new UnexpectedLiquibaseException("Unable to read " + file);
		}
		return createReader(stream, getEncoding());
	}

	protected Set<String> getResources(ResourceAccessor resourceAccessor) throws IOException, ParsedNodeException {
		if (getFile() != null) {
			return Collections.singleton(getFile());
		}

		String path = getPath();
		if (path == null) {
			throw new ParsedNodeException("You must specify either dir or path");
		}
		path = path.replace('\\', '/');
		if (!path.endsWith("/")) {
			path = path + '/';
		}

		Set<String> unsorted = resourceAccessor.list(null, path, true, false, isRecursive());
		if ((unsorted == null || unsorted.isEmpty()) && isRecursive()) {
			unsorted = resourceAccessor.list(null, path + '*', true, false, isRecursive());
		}
		SortedSet<String> resources = new TreeSet<>(getStandardComparator());
		if (unsorted != null) {
			for (String resourcePath : unsorted) {
				if (filter == null) {
					resources.add(resourcePath);
				} else if (resourcePath.matches(filter)) {
					resources.add(resourcePath);
				}
			}
		}
		if (resources.isEmpty()) {
			throw new ParsedNodeException("Could not find directory or directory was empty for path '" + getPath() + "'");
		}
		return resources;
	}

	protected Comparator<String> getStandardComparator() {
		return new Comparator<String>() {
			@Override
			public int compare(String o1, String o2) {
				return normalize(o1).compareTo(normalize(o2));
			}

			private String normalize(String string) {
				if (string == null) {
					return null;
				}
				return string.replace("WEB-INF/classes/", "");
			}
		};

	}

	<E extends LqbAbstractEntity> AbstractEntityChange<E> getEntityChange(
			final Database database,
			final ResourceAccessor resourceAccessor,
			final Class<E> entityClass
	) {
		AbstractEntityChange<E> viewChange = new AbstractEntityChange<E>() {
			@Override
			protected Class<E> getElementType() {
				return entityClass;
			}
		};
		viewChange.setChangeSet(getChangeSet());
		viewChange.setEncoding(getEncoding());
		viewChange.setResourceAccessor(resourceAccessor);
		return viewChange;
	}

}
