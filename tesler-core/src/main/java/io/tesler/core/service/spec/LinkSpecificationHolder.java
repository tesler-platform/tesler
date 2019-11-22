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

package io.tesler.core.service.spec;

import io.tesler.core.crudma.bc.impl.BcDescription;
import java.util.HashMap;
import java.util.Map;
import lombok.Getter;
import org.springframework.data.jpa.domain.Specification;


public abstract class LinkSpecificationHolder<entity> extends SpecificationHolder<entity> {

	@Getter
	protected Map<SpecificationHeader<entity>, ParentSpecification<entity>> map;

	public LinkSpecificationHolder() {
		super();
		map = new HashMap<>();
	}

	public Specification<entity> get(SpecificationHeader<entity> specificationName, BcDescription bcDescription,
			String parentId) {
		if (map.containsKey(specificationName)) {
			return map.get(specificationName).toSpecification(bcDescription, parentId);
		} else {
			return (root, cq, cb) -> cb.and();
		}
	}

	public interface ParentSpecification<entity> {

		Specification<entity> toSpecification(BcDescription bcDescription, String parentId);

	}

}
