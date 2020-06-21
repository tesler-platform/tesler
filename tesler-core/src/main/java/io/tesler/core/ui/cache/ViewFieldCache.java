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

package io.tesler.core.ui.cache;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import io.tesler.core.ui.model.BcField;
import io.tesler.model.core.dao.JpaDao;
import io.tesler.model.ui.entity.ViewWidgets;
import io.tesler.model.ui.entity.ViewWidgets_;
import io.tesler.model.ui.entity.Widget_;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public final class ViewFieldCache {

	@Autowired
	private JpaDao jpaDao;

	@Autowired
	private WidgetFieldCache widgetFieldCache;

	public final LoadingCache<String, Map<String, Set<BcField>>> viewFields = CacheBuilder
			.newBuilder()
			.build(new ViewFieldCacheLoader());

	private final class ViewFieldCacheLoader extends CacheLoader<String, Map<String, Set<BcField>>> {

		@Override
		@SneakyThrows
		public Map<String, Set<BcField>> load(final String viewName) {
			final List<Long> widgetIds = jpaDao.getList(
					ViewWidgets.class,
					Long.class,
					(root, cb) -> root.get(ViewWidgets_.widget).get(Widget_.id),
					(root, query, cb) -> cb.equal(root.get(ViewWidgets_.viewName), viewName)
			);
			final Set<BcField> fields = new HashSet<>();
			for (final Long widgetId : widgetIds) {
				fields.addAll(widgetFieldCache.widgetFields.get(widgetId));
			}
			return fields.stream().collect(Collectors.groupingBy(BcField::getBc, Collectors.toSet()));
		}

	}

}

