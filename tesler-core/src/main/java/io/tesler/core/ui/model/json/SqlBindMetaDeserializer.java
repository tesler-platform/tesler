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

import io.tesler.core.ui.model.json.SqlBindMeta.SqlBindOperations;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.ObjectCodec;
import com.fasterxml.jackson.core.TreeNode;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.fasterxml.jackson.databind.node.ObjectNode;
import java.io.IOException;
import java.util.Arrays;


public class SqlBindMetaDeserializer extends StdDeserializer<SqlBindMeta> {

	protected SqlBindMetaDeserializer() {
		super(SqlBindMeta.class);
	}

	@Override
	public SqlBindMeta deserialize(final JsonParser p, final DeserializationContext ctxt) throws IOException {
		final ObjectCodec codec = p.getCodec();
		final ObjectNode treeNode = codec.readTree(p);
		final TreeNode operations = treeNode.remove("operations");
		final TreeNode dictionaryValues = treeNode.remove("dictionaryValues");
		return new SqlBindMeta(
				codec.treeToValue(treeNode, FieldMeta.class),
				operations == null ? null : Arrays.asList(codec.treeToValue(operations, SqlBindOperations[].class)),
				dictionaryValues == null ? null : Arrays.asList(codec.treeToValue(dictionaryValues, String[].class))
		);
	}

}
