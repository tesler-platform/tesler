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

package io.tesler.core.ui;

import io.tesler.api.data.dto.DataResponseDTO;
import io.tesler.constgen.DtoField;
import io.tesler.core.crudma.bc.BcIdentifier;
import java.util.Set;

public interface BcUtils {

	void invalidateFieldCache();

	void invalidateFieldCacheByView(final String viewName);

	void invalidateFieldCacheByWidget(final Long widgetId);

	/**
	 * Returns a set of dto fields ({@link DtoField}) for the passed dto class
	 */
	<D extends DataResponseDTO> Set<DtoField<D, ?>> getDtoFields(final Class<D> dtoClass);

	/**
	 * Returns the set of required fields for the transferred business component on the current screen.
	 */
	Set<String> getBcFieldsForCurrentScreen(final BcIdentifier bc);

	/**
	 * Returns the set of required dto fields ({@link DtoField}) for the transferred business component on the current screen
	 */
	<D extends DataResponseDTO> Set<DtoField<D, ?>> getDtoFieldsForCurrentScreen(final BcIdentifier bc);

}
