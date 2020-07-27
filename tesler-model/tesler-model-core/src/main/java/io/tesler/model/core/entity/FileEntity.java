/*-
 * #%L
 * IO Tesler - Model Core
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

package io.tesler.model.core.entity;

import io.tesler.api.data.dictionary.CoreDictionaries;
import io.tesler.api.data.dictionary.LOV;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Lob;
import javax.persistence.PrePersist;
import javax.persistence.Table;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

/**
 * File information
 */
@Getter
@Setter
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "FILE_ENTITY")
public class FileEntity extends BaseEntity {

	private String fileName;

	private String fileType;

	@Column(name = "file_size")
	private Long size;

	@Column(name = "IS_TEMPORARY")
	private boolean temporary;

	private String fileUrl;

	/**
	 * Content
	 */
	@Lob
	private byte[] fileContent;

	@Column(name = "file_storage_cd", nullable = false)
	private LOV fileStorageCd;

	@Column(name = "RESTRICTED_FLG")
	private boolean restrictedFlg;

	@PrePersist
	protected void onCreate() {
		restrictedFlg = fileStorageCd.equals(CoreDictionaries.FileStorage.FILENET);
	}

}
