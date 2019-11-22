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

package io.tesler.db.migration.liquibase.data;

import io.tesler.db.migration.liquibase.annotations.DBEntity;
import io.tesler.db.migration.liquibase.annotations.DBField;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@DBEntity(tableName = "SCREEN_VIEW_GROUP")
public class ScreenViewGroup extends AbstractEntity {

	@DBField(columnName = "DEPT_ID")
	private Long deptId;

	@DBField(columnName = "TYPE_CD")
	private String typeCd;

	@DBField(columnName = "SCREEN_NAME")
	private String screenName;

	@DBField(columnName = "TITLE")
	private String title;

	@DBField(columnName = "PARENT_ID")
	private Long parentId;

	@DBField(columnName = "SEQ")
	private Integer seq;

	@DBField(columnName = "DESCRIPTION")
	private String description;

	@DBField(columnName = "ROOT")
	private Integer root;

}
