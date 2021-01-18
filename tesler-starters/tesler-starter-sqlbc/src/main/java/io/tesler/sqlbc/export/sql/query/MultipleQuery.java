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

package io.tesler.sqlbc.export.sql.query;

import io.tesler.sqlbc.export.sql.query.BlankLine;
import io.tesler.sqlbc.export.sql.query.Comment;
import io.tesler.sqlbc.export.sql.query.Query;
import io.tesler.sqlbc.export.sql.query.Insert;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

@ToString
@RequiredArgsConstructor
public class MultipleQuery implements Query {

	private final boolean onlyQuery;

	private final List<Query> queries = new ArrayList<>();

	public MultipleQuery() {
		this(false);
	}

	public void query(final Query sql) {
		queries.add(sql);
		blankLine();
	}

	public void queries(final List<? extends Query> sql) {
		if (!sql.isEmpty()) {
			queries.addAll(sql);
			blankLine();
		}
	}

	public void comment(final String text) {
		if (!onlyQuery) {
			queries.add(new Comment(text));
		}
	}

	public void blankLine() {
		if (!onlyQuery) {
			queries.add(new BlankLine());
		}
	}

	public boolean isEmpty() {
		return queries.isEmpty();
	}

	public List<Query> getQueries() {
		return queries;
	}

	@Override
	public String toSql() {
		return queries.stream()
				.map(Query::toSql)
				.collect(Collectors.joining("\n"));
	}

	public List<Insert> getInserts(final String tableName) {
		final List<Insert> inserts = new ArrayList<>();
		for (final Query query : queries) {
			if (query instanceof Insert) {
				final Insert queryInsert = (Insert) query;
				if (Objects.equals(queryInsert.getTableName(), tableName)) {
					inserts.add(queryInsert);
				}
			}
		}
		return inserts;
	}

}
