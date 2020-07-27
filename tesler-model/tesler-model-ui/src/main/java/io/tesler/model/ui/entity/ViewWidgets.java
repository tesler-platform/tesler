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

import io.tesler.model.core.api.EmbeddedKeyable;
import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

/**
 * View widgets with position and limit
 */
@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ViewWidgets implements EmbeddedKeyable, Serializable {

	@EmbeddedId
	private ViewWidgetsPK pk;

	private Long positon;

	@Column(name = "DESCRIPTION_TITLE")
	private String descriptionTitle;

	private String description;

	private String snippet;

	@Column(name = "PAGE_LIMIT")
	private Long limit;

	@Column(name = "GRID_WIDTH")
	private Long gridWidth;

	@Column(name = "GRID_BREAK")
	private Long gridBreak;

	@Column(name = "HIDE_BY_DEFAULT")
	private Boolean hide;

	@Column(name = "SHOW_EXPORT_STAMP")
	private Boolean showExportStamp;

	@Column(name = "view_name", nullable = false, updatable = false, insertable = false)
	private String viewName;

	@ManyToOne
	@JoinColumn(name = "widget_id", nullable = false, updatable = false, insertable = false)
	@Fetch(value = FetchMode.JOIN)
	private Widget widget;

	public ViewWidgets(ViewWidgetsPK pk) {
		this.pk = pk;
	}

}
