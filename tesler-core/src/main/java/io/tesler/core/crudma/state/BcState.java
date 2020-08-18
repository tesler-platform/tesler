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

package io.tesler.core.crudma.state;

import io.tesler.api.data.dto.DataResponseDTO;
import java.io.Serializable;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * State object that uses application to store changes between read only requests
 *
 * @see io.tesler.core.crudma.CrudmaGateway
 * @see DataResponseDTO
 * @see BcStateAware
 */
@Getter
@RequiredArgsConstructor
public class BcState implements Serializable {

	/**
	 * Object which stores changes to the current business component in serializable form.
	 * If present, record should be updated with this changes as input argument before Crudma method call
	 */
	private final DataResponseDTO dto;

	/**
	 * Flag that indicates whether the record was stored in persistence layer
	 * if false, pendingAction should be processed before Crudma method call
	 */
	private final boolean isPersisted;

	/**
	 * If record is not persisted, this field can be used to determine which action
	 * should be used to persist the record before Crudma method call
	 */
	private final String pendingAction;

}
