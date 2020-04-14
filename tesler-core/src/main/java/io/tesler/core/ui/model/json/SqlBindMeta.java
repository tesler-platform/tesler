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

package io.tesler.core.ui.model.json;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import io.tesler.core.ui.model.json.field.FieldMeta;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@JsonDeserialize(using = SqlBindMetaDeserializer.class)
public final class SqlBindMeta {

	private FieldMeta field;

	private List<SqlBindOperations> operations;

	private List<String> dictionaryValues;

	@Getter
	@Setter
	public static class SqlBindOperations {

		private FilterType type;

		@JsonProperty(value = "default")
		private Default defaultValue;

		@Getter
		@Setter
		public static class Default {

			private Object value;

			private List<Transform> transform;

			@Getter
			@Setter
			public static class Transform {

				private MomentOperations operation;

				private Object variable;

			}

		}

	}

}
