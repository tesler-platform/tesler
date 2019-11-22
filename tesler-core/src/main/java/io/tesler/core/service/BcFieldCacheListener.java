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

package io.tesler.core.service;

import io.tesler.api.data.dao.databaselistener.IChangeListener;
import io.tesler.api.data.dao.databaselistener.IChangeVector;
import io.tesler.api.data.dictionary.LOV;
import io.tesler.core.ui.BcUtils;
import io.tesler.model.ui.entity.ViewWidgets;
import io.tesler.model.ui.entity.ViewWidgets_;
import io.tesler.model.ui.entity.Widget;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


public class BcFieldCacheListener {

	@Autowired
	protected BcUtils bcUtils;

	@Service
	public static class WidgetChangeListener extends BcFieldCacheListener implements IChangeListener<Widget> {

		@Override
		public Class<? extends Widget> getType() {
			return Widget.class;
		}

		@Override
		public void process(final IChangeVector vector, final LOV event) {
			bcUtils.invalidateFieldCacheByWidget(vector.unwrap(getType()).getId());
		}

	}

	@Service
	public static class ViewWidgetsChangeListener extends BcFieldCacheListener implements IChangeListener<ViewWidgets> {

		@Override
		public Class<? extends ViewWidgets> getType() {
			return ViewWidgets.class;
		}

		@Override
		public void process(final IChangeVector vector, final LOV event) {
			bcUtils.invalidateFieldCacheByView(vector.unwrap(getType()).getViewName());
			if (vector.hasChanged(ViewWidgets_.viewName) && vector.isUpdate()) {
				bcUtils.invalidateFieldCacheByView(vector.getOldValue(ViewWidgets_.viewName));
			}
		}

	}

}
