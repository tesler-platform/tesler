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

package io.tesler.core.metahotreload.service;

import static io.tesler.api.service.session.InternalAuthorizationService.VANILLA;

import io.tesler.core.metahotreload.dto.ScreenSourceDto;
import io.tesler.core.metahotreload.dto.ViewSourceDTO;
import io.tesler.core.metahotreload.dto.WidgetSourceDTO;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.tesler.api.service.session.InternalAuthorizationService;
import io.tesler.api.service.tx.TransactionService;
import io.tesler.api.util.privileges.PrivilegeUtil;
import io.tesler.model.core.dao.JpaDao;
import io.tesler.model.ui.entity.Screen;
import io.tesler.model.ui.entity.View;
import io.tesler.model.ui.entity.ViewWidgets;
import io.tesler.model.ui.entity.Widget;
import io.tesler.model.ui.entity.WidgetProperty;
import io.tesler.model.ui.navigation.NavigationGroup;
import io.tesler.model.ui.navigation.NavigationView;
import java.util.List;
import java.util.Map;
import javax.persistence.EntityManager;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class MetaHotReloadService {

	final MetaResourceReaderService metaResourceReaderService;

	final InternalAuthorizationService authzService;

	final TransactionService txService;

	final EntityManager entityManager;

	final JpaDao jpaDao;

	final ObjectMapper objMapper;

	public void loadMeta() {
		List<ScreenSourceDto> screenDtos = metaResourceReaderService.getScreens();
		List<WidgetSourceDTO> widgetDtos = metaResourceReaderService.getWidgets();
		List<ViewSourceDTO> viewDtos = metaResourceReaderService.getViews();

		PrivilegeUtil.runPrivileged(() -> {
			authzService.loginAs(authzService.createAuthentication(VANILLA));

			txService.invokeInTx(() -> {
				deleteAllMeta(jpaDao);
				Map<String, Widget> nameToWidget = WidgetUtil.process(widgetDtos, entityManager, objMapper);
				WidgetPropertyUtil.process(widgetDtos, nameToWidget, entityManager);
				ViewAndViewWidgetUtil.process(viewDtos, nameToWidget, entityManager, objMapper);
				ScreenAndNavigationGroupAndNavigationViewUtil.process(screenDtos, entityManager, objMapper);
				return null;
			});
			return null;
		});
	}

	private static void deleteAllMeta(@NonNull JpaDao jpaDao) {
		jpaDao.delete(NavigationView.class, (root, query, cb) -> cb.and());
		jpaDao.delete(NavigationGroup.class, (root, query, cb) -> cb.and());
		jpaDao.delete(Screen.class, (root, query, cb) -> cb.and());
		jpaDao.delete(ViewWidgets.class, (root, query, cb) -> cb.and());
		jpaDao.delete(View.class, (root, query, cb) -> cb.and());
		jpaDao.delete(WidgetProperty.class, (root, query, cb) -> cb.and());
		jpaDao.delete(Widget.class, (root, query, cb) -> cb.and());
	}

}
