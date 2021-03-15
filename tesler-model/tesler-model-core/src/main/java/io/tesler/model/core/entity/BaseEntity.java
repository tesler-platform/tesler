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

import io.tesler.model.core.hbn.PropagateAnnotations;
import io.tesler.model.core.listeners.jpa.DelegatingBaseEntityListener;
import java.io.Serializable;
import java.time.LocalDateTime;
import javax.persistence.Column;
import javax.persistence.EntityListeners;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;
import javax.persistence.Transient;
import javax.persistence.Version;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.DiscriminatorOptions;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;
import org.hibernate.envers.Audited;
import org.hibernate.envers.NotAudited;
import org.hibernate.id.enhanced.SequenceStyleGenerator;
import org.springframework.data.annotation.CreatedDate;

@Audited
@Setter
@Getter
@MappedSuperclass
@EqualsAndHashCode(of = {"id"}, callSuper = false)
@EntityListeners(DelegatingBaseEntityListener.class)
@DiscriminatorOptions(insert = false)
@PropagateAnnotations({DiscriminatorOptions.class})
public abstract class BaseEntity extends AbstractEntity implements Serializable {

	/* TODO>>IBORISENKO>>Refactor.This is a legacy strategy without pooling.
	   Can cause performance problems in batch operations.
	   See https://vladmihalcea.com/hibernate-hidden-gem-the-pooled-lo-optimizer/ */
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "extSequenceGenerator")
	@GenericGenerator(
			name = "extSequenceGenerator",
			strategy = "io.tesler.model.core.hbn.ExtSequenceStyleGenerator",
			parameters = {
					@Parameter(name = SequenceStyleGenerator.SEQUENCE_PARAM, value = "app_seq"),
					@Parameter(name = SequenceStyleGenerator.INCREMENT_PARAM, value = "1"),
			}
	)
	protected Long id;

	@Version
	@NotAudited
	@Column(name = "vstamp")
	private long vstamp;

	@CreatedDate
	@Column(name = "created_date", nullable = false, updatable = false)
	private LocalDateTime createdDate;

	@Column(name = "updated_date", nullable = false)
	private LocalDateTime updatedDate;

	@Column(name = "CREATED_BY_USER_ID", nullable = false)
	private Long createdBy;

	@Column(name = "LAST_UPD_BY_USER_ID", nullable = false)
	private Long lastUpdBy;

	@Transient
	private long loadVstamp = -1;

	@Override
	public String toString() {
		return String.format("%s:%d", getClass().getSimpleName(), getId());
	}

}
