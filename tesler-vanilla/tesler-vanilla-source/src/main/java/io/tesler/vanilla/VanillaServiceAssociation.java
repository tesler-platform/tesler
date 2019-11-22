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

package io.tesler.vanilla;

import io.tesler.core.crudma.bc.EnumBcIdentifier;
import io.tesler.core.crudma.bc.impl.AbstractEnumBcSupplier;
import io.tesler.core.crudma.bc.impl.BcDescription;
import io.tesler.vanilla.service.data.VanillaDocService;
import io.tesler.vanilla.service.data.VanillaRelatedRecordsAssocService;
import io.tesler.vanilla.service.data.VanillaRelatedRecordsService;
import io.tesler.vanilla.service.data.VanillaResidentService;
import io.tesler.vanilla.service.data.VanillaSupFactService;
import io.tesler.vanilla.service.data.VanillaTaskExecutorService;
import io.tesler.vanilla.service.data.VanillaTaskParentService;
import io.tesler.vanilla.service.data.VanillaTaskService;
import io.tesler.vanilla.service.data.VanillaTaskSourceService;
import lombok.Getter;
import org.springframework.stereotype.Component;

@Getter
public enum VanillaServiceAssociation implements EnumBcIdentifier {

	// @formatter:off

	//Vanilla
	legalResidentVanilla(VanillaResidentService.class),
		taskVanilla(legalResidentVanilla, VanillaTaskService.class, true),
			relatedRecordsVanilla(taskVanilla, VanillaRelatedRecordsService.class),
				taskParentVanilla(relatedRecordsVanilla, VanillaTaskParentService.class),
					taskParentVanillaExecutor(taskParentVanilla, VanillaTaskExecutorService.class),
			relatedRecordsVanillaAssoc(taskVanilla, VanillaRelatedRecordsAssocService.class),
			sourceDictVanilla(taskVanilla, VanillaTaskSourceService.class),
			taskExecutorVanilla(taskVanilla, VanillaTaskExecutorService.class),
		supervisoryFactVanilla(legalResidentVanilla, VanillaSupFactService.class),

	//Документация
	bcExample(VanillaDocService.class),
		bcChildExample(bcExample, VanillaTaskService.class, true),
		bcExampleRelatedRecords(bcExample, VanillaRelatedRecordsService.class),
		bcExampleRelatedRecordsAssoc(bcExample, VanillaRelatedRecordsAssocService.class),
	bcPagination(VanillaDocService.class),
	bcBulkChangesTaskVanilla(VanillaTaskService.class, true),
		bcBulkChangesTaskVanillaExecutor(bcBulkChangesTaskVanilla, VanillaTaskExecutorService.class),
	bcPreAction(VanillaDocService.class),
	bcIrresponsibleParent(VanillaDocService.class),
		bcOrphan(bcIrresponsibleParent, VanillaDocService.class),

	;

	// @formatter:on

	public static final Holder<VanillaServiceAssociation> Holder = new Holder<>(VanillaServiceAssociation.class);

	private final BcDescription bcDescription;

	VanillaServiceAssociation(String parent, Class<?> serviceClass, boolean refresh) {
		this.bcDescription = buildDescription(parent, serviceClass, refresh);
	}

	VanillaServiceAssociation(String parent, Class<?> serviceClass) {
		this(parent, serviceClass, false);
	}

	VanillaServiceAssociation(VanillaServiceAssociation parent, Class<?> serviceClass, boolean refresh) {
		this(parent == null ? null : parent.name(), serviceClass, refresh);
	}

	VanillaServiceAssociation(VanillaServiceAssociation parent, Class<?> serviceClass) {
		this(parent, serviceClass, false);
	}

	VanillaServiceAssociation(Class<?> serviceClass, boolean refresh) {
		this((String) null, serviceClass, refresh);
	}

	VanillaServiceAssociation(Class<?> serviceClass) {
		this((String) null, serviceClass, false);
	}

	@Component
	public static class VanillaBcSupplier extends AbstractEnumBcSupplier<VanillaServiceAssociation> {

		public VanillaBcSupplier() {
			super(VanillaServiceAssociation.Holder);
		}

	}


}
