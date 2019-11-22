/*-
 * #%L
 * IO Tesler - Model UI
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

package io.tesler.model.ui.entity;

import io.tesler.api.data.dictionary.LOV;
import io.tesler.model.core.entity.BaseEntity;
import io.tesler.model.core.entity.Department;
import java.util.List;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity(name = "SCREEN_VIEW_GROUP")
public class ScreenViewGroup extends BaseEntity {

	@ManyToOne
	@JoinColumn(name = "DEPT_ID")
	private Department department;

	@Column(name = "TYPE_CD")
	private LOV typeCd;

	@Column(name = "SCREEN_NAME")
	private String screenName;

	@Column(name = "TITLE")
	private String title;

	@ManyToOne
	@JoinColumn(name = "PARENT_ID")
	private ScreenViewGroup parent;

	@Column(name = "SEQ")
	private Integer seq;

	@Column(name = "DESCRIPTION")
	private String description;

	@Column(name = "ROOT")
	private Boolean root;

	@OneToMany(mappedBy = "viewGroup")
	private List<ScreenViewGroupData> viewGroups;

}
