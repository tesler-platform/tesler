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

package io.tesler.model.core.entity.security;

import io.tesler.model.core.api.security.IAccessorSupplier;
import io.tesler.model.core.entity.BaseEntity;
import io.tesler.model.core.entity.security.types.AccessorType;
import io.tesler.model.core.hbn.ExtSequenceGenerator;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Parameter;
import org.hibernate.id.enhanced.OptimizerFactory;
import org.hibernate.id.enhanced.SequenceStyleGenerator;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * Group
 */
@Setter
@Getter
@Entity
@Table(name = "groups")
@ExtSequenceGenerator(
		parameters = {
				@Parameter(name = SequenceStyleGenerator.SEQUENCE_PARAM, value = "APP_BATCH_SEQ"),
				@Parameter(name = SequenceStyleGenerator.INITIAL_PARAM, value = "2000000000"),
				@Parameter(name = SequenceStyleGenerator.INCREMENT_PARAM, value = "100"),
				@Parameter(name = SequenceStyleGenerator.OPT_PARAM, value = OptimizerFactory.POOL_LO)
		}
)
public class Group extends BaseEntity implements IAccessorSupplier {

	/**
	 * Name
	 */
	@Column(name = "name")
	private String name;

	@Override
	public Accessor getAccessor() {
		return AccessorType.GROUP.toAccessor(getId());
	}

}