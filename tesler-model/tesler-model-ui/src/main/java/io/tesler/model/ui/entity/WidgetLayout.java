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

import io.tesler.model.core.entity.BaseEntity;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import io.tesler.model.core.hbn.ExtSequenceGenerator;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Parameter;
import org.hibernate.id.enhanced.OptimizerFactory;
import org.hibernate.id.enhanced.SequenceStyleGenerator;

/**
 * Positioning the widget on the grid
 */
@Entity
@Table(name = "layouts_widgets")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ExtSequenceGenerator(
		parameters = {
				@Parameter(name = SequenceStyleGenerator.SEQUENCE_PARAM, value = "META_SEQ"),
				@Parameter(name = SequenceStyleGenerator.INITIAL_PARAM, value = "1"),
				@Parameter(name = SequenceStyleGenerator.INCREMENT_PARAM, value = "100"),
				@Parameter(name = SequenceStyleGenerator.OPT_PARAM, value = OptimizerFactory.POOL_LO)
		}
)
public class WidgetLayout extends BaseEntity {

	@ManyToOne
	@JoinColumn(name = "layout_id", nullable = false)
	ViewLayout layout;

	Long widgetId;

	Integer x;

	Integer y;

	Integer minWidth;

	Integer width;

	Integer maxWidth;

	Integer minHeight;

	Integer height;

	Integer maxHeight;

	Boolean isDraggable;

	Boolean isResizable;

	public void merge(WidgetLayout source) {
		this.setX(source.getX());
		this.setY(source.getY());
		this.setMinHeight(source.getMinHeight());
		this.setMaxHeight(source.getMaxHeight());
		this.setMinWidth(source.getMinWidth());
		this.setMaxWidth(source.getMaxWidth());
		this.setHeight(source.getHeight());
		this.setWidth(source.getWidth());
		this.setIsDraggable(source.getIsDraggable());
		this.setIsResizable(source.getIsResizable());
	}

}
