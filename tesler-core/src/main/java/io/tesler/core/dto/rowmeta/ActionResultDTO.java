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

package io.tesler.core.dto.rowmeta;

import io.tesler.api.data.IDataContainer;
import io.tesler.api.data.dto.DataResponseDTO;
import com.fasterxml.jackson.annotation.JsonInclude;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import lombok.Getter;

@Getter
public class ActionResultDTO<T extends DataResponseDTO> implements IDataContainer<T> {

	private final List<PostAction> postActions = new ArrayList<>();

	@JsonInclude(JsonInclude.Include.NON_NULL)
	private T record;

	public ActionResultDTO(T record) {
		this.record = record;
	}

	public ActionResultDTO() {
		this(null);
	}

	public ActionResultDTO<T> setAction(PostAction postAction) {
		this.postActions.add(postAction);
		return this;
	}

	public ActionResultDTO<T> setActions(List<PostAction> postActions) {
		this.postActions.addAll(postActions);
		return this;
	}

	public void clearAllActions() {
		this.postActions.clear();
	}

	@Override
	public void transformData(Function<T, T> function) {
		record = function.apply(record);
	}

}
