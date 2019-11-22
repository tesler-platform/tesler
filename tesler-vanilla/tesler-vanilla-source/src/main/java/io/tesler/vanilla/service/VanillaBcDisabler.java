/*-
 * #%L
 * IO Tesler - Vanilla Source
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

package io.tesler.vanilla.service;

import static io.tesler.vanilla.VanillaServiceAssociation.bcExample;

import io.tesler.core.crudma.bc.BcIdentifier;
import io.tesler.core.crudma.bc.BusinessComponent;
import io.tesler.core.service.rowmeta.BcDisabler;
import io.tesler.model.core.dao.JpaDao;
import io.tesler.vanilla.entity.VanillaTask;
import java.util.Collection;
import java.util.Collections;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.BooleanUtils;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class VanillaBcDisabler extends BcDisabler {

	private final JpaDao jpaDao;

	@Override
	public Collection<BcIdentifier> getSupportedBc() {
		return Collections.singletonList(bcExample);
	}

	@Override
	public boolean isBcDisabled(final BusinessComponent bc) {
		if (bc.getId() == null) {
			return false;
		}
		final VanillaTask task = jpaDao.findById(VanillaTask.class, bc.getIdAsLong());
		return BooleanUtils.isTrue(task.getBcDisabledFlg());
	}

	@Override
	protected boolean isActionDisabled(final String actionName) {
		return true;
	}

}
