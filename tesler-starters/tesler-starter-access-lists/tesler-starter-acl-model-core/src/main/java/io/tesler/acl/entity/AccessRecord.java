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

package io.tesler.acl.entity;

import io.tesler.acl.entity.types.Permission;
import io.tesler.model.core.entity.BaseEntity;
import io.tesler.model.core.hbn.ExtSequenceGenerator;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Parameter;
import org.hibernate.id.enhanced.OptimizerFactory;
import org.hibernate.id.enhanced.SequenceStyleGenerator;

import javax.persistence.*;

/**
 * Access right record
 */
@Setter
@Getter
@Entity
@ExtSequenceGenerator(
		parameters = {
				@Parameter(name = SequenceStyleGenerator.SEQUENCE_PARAM, value = "APP_BATCH_SEQ"),
				@Parameter(name = SequenceStyleGenerator.INITIAL_PARAM, value = "2000000000"),
				@Parameter(name = SequenceStyleGenerator.INCREMENT_PARAM, value = "100"),
				@Parameter(name = SequenceStyleGenerator.OPT_PARAM, value = OptimizerFactory.POOL_LO)
		}
)
@Table(name = "access_record")
public class AccessRecord extends BaseEntity {

	/**
	 * Parent access list
	 */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "access_list_id")
	private AccessList accessList;

	/**
	 * Accessor
	 */
	@Embedded
	private Accessor accessor;

	/**
	 * Access rights
	 */
	@Column(name = "permission")
	private Permission permission;

	/**
	 * Mandatory sign
	 */
	@Column(name = "mandatory")
	private boolean mandatory;

}
