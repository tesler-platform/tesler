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

package io.tesler.core.service.file;

import io.tesler.api.exception.ServerException;
import io.tesler.core.exception.ClientException;
import io.tesler.model.core.dao.JpaDao;
import io.tesler.model.core.entity.FileDatasource;
import io.tesler.model.core.entity.FileDatasource_;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import java.sql.PreparedStatement;
import java.util.Collections;
import java.util.Objects;
import java.util.Optional;
import javax.sql.DataSource;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.lob.DefaultLobHandler;
import org.springframework.jdbc.support.lob.LobHandler;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
@Slf4j
public class CustomSourceFileService {

	private final NamedParameterJdbcTemplate jdbcTemplate;

	private final JpaDao jpaDao;

	@Autowired
	public CustomSourceFileService(@Qualifier("primaryDS") DataSource dataSource, JpaDao jpaDao) {
		this.jdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
		this.jpaDao = jpaDao;
	}

	/**
	 * Returns a file from a custom source
	 *
	 * @param source - source name
	 * @param id - file identifier
	 */
	public CustomSourceFile getFileFromSource(String source, Long id) {
		FileDatasource fileDatasource = jpaDao
				.getList(FileDatasource.class, (root, query, cb) -> cb.equal(root.get(FileDatasource_.name), source))
				.stream().findFirst()
				.orElseThrow(() -> new ServerException("Ошибка конфигурации источника файла. Источник не найден."));
		String sourceTable = fileDatasource.getSourceTable();
		ImmutableList<Object> parametersList = ImmutableList.of(
				fileDatasource.getIdField() + " as id ",
				fileDatasource.getContentField() + " as content",
				fileDatasource.getNameField() + " as name",
				Objects.nonNull(fileDatasource.getTypeField()) ? fileDatasource.getTypeField() + " as type"
						: "'application/octet-stream' as type"
		);
		LobHandler lobHandler = new DefaultLobHandler();
		RowMapper<CustomSourceFile> fileRowMapper = (rs, rowNum) -> CustomSourceFile.builder()
				.id(rs.getLong("id"))
				.content(lobHandler.getBlobAsBytes(rs, "content"))
				.name(rs.getString("name"))
				.type(rs.getString("type"))
				.build();
		String sql = String
				.format("select %s from %s where id = :id", StringUtils.join(parametersList, ','), sourceTable);
		return jdbcTemplate.queryForObject(sql, ImmutableMap.of("id", id), fileRowMapper);
	}


	/**
	 * Saves the file to the specified source
	 *
	 * @param source - source name
	 * @return generated file identifier
	 */
	public Long saveFileToSource(String source, MultipartFile file) {
		FileDatasource fileDatasource = jpaDao
				.getList(FileDatasource.class, (root, query, cb) -> cb.equal(root.get(FileDatasource_.name), source))
				.stream().findFirst()
				.orElseThrow(() -> new ServerException("Ошибка конфигурации источника файла. Источник не найден."));
		try {
			String sourceTable = fileDatasource.getSourceTable();
			String idField = fileDatasource.getIdField();
			String contentField = fileDatasource.getContentField();
			String typeField = fileDatasource.getTypeField();
			String nameField = fileDatasource.getNameField();
			String sizeField = fileDatasource.getSizeField();
			Long id = Optional.of("SELECT APP_SEQ.NEXTVAL FROM DUAL")
					.map(sql -> jdbcTemplate.queryForObject(sql, Collections.emptyMap(), Long.class))
					.orElseThrow(Exception::new);
			String insertStatement = "INSERT INTO " +
					sourceTable + " (" +
					idField + ", " +
					contentField + ", " +
					typeField + ", " +
					nameField + ", " +
					sizeField + ") VALUES (:id,:content,:type,:name,:size)";
			byte[] fileContent = file.getBytes();
			MapSqlParameterSource paramSource = new MapSqlParameterSource();
			paramSource.addValue("id", id);
			paramSource.addValue("content", fileContent);
			paramSource.addValue("type", file.getContentType());
			paramSource.addValue("name", file.getOriginalFilename());
			paramSource.addValue("size", file.getSize());
			jdbcTemplate.execute(insertStatement, paramSource, PreparedStatement::executeUpdate);
			return id;
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			throw new ClientException("Ошибка при загрузке файла в источник: " + file.getName());
		}
	}

}
