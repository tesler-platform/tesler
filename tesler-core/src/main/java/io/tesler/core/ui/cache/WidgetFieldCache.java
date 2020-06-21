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
import io.tesler.core.ui.WidgetUtils;
import io.tesler.core.ui.model.BcField;
import io.tesler.model.core.dao.JpaDao;
import io.tesler.model.ui.entity.Widget;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public final class WidgetFieldCache {

	@Autowired
	private JpaDao jpaDao;

	@Autowired
	private BcFieldCache bcFieldCache;

	public final LoadingCache<Long, Set<BcField>> widgetFields = CacheBuilder
			.newBuilder()
			.build(new WidgetFieldCacheLoader());

	private final class WidgetFieldCacheLoader extends CacheLoader<Long, Set<BcField>> {

		@Override
		@SneakyThrows
		public Set<BcField> load(final Long widgetId) {
			final Widget widget = jpaDao.findById(Widget.class, widgetId);
			final Set<BcField> fields = new HashSet<>(WidgetUtils.extractAllFields(widget));
			final Set<String> bcNames = fields.stream().map(BcField::getBc).filter(Objects::nonNull)
					.collect(Collectors.toSet());
			for (String bcName : bcNames) {
				fields.addAll(bcFieldCache.bcFields.get(bcName));
			}
			return fields;
		}

	}

}
