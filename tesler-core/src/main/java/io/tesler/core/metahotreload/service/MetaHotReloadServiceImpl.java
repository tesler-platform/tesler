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

import io.tesler.api.service.session.InternalAuthorizationService;
import io.tesler.api.service.tx.TransactionService;
import io.tesler.api.util.privileges.PrivilegeUtil;
import io.tesler.core.metahotreload.MetaHotReloadService;
import io.tesler.core.metahotreload.dto.ScreenSourceDto;
import io.tesler.core.metahotreload.dto.BcSourceDTO;
import io.tesler.core.metahotreload.dto.ViewSourceDTO;
import io.tesler.core.metahotreload.dto.WidgetSourceDTO;
import io.tesler.model.core.dao.JpaDao;
import io.tesler.model.ui.entity.*;
import io.tesler.model.ui.navigation.NavigationGroup;
import io.tesler.model.ui.navigation.NavigationView;
import lombok.RequiredArgsConstructor;

import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.Map;

import static io.tesler.api.service.session.InternalAuthorizationService.VANILLA;

@RequiredArgsConstructor
public class MetaHotReloadServiceImpl implements MetaHotReloadService {

	protected final MetaResourceReaderService metaResourceReaderService;

	protected final InternalAuthorizationService authzService;

	protected final TransactionService txService;

	protected final JpaDao jpaDao;

	protected final WidgetUtil widgetUtil;

	protected final WidgetPropertyUtil widgetPropertyUtil;

	protected final ViewAndViewWidgetUtil viewAndViewWidgetUtil;

	protected final ScreenAndNavigationGroupAndNavigationViewUtil screenAndNavigationGroupAndNavigationViewUtil;

	protected final BcUtil bcUtil;

	private static void deleteAllMeta(@NotNull JpaDao jpaDao) {
		jpaDao.delete(NavigationView.class, (root, query, cb) -> cb.and());
		jpaDao.delete(NavigationGroup.class, (root, query, cb) -> cb.and());
		jpaDao.delete(Screen.class, (root, query, cb) -> cb.and());
		jpaDao.delete(ViewWidgets.class, (root, query, cb) -> cb.and());
		jpaDao.delete(View.class, (root, query, cb) -> cb.and());
		jpaDao.delete(WidgetProperty.class, (root, query, cb) -> cb.and());
		jpaDao.delete(Widget.class, (root, query, cb) -> cb.and());
		jpaDao.delete(Bc.class, (root, query, cb) -> cb.and());
	}

	public final void loadMeta() {
		List<BcSourceDTO> bcDtos = metaResourceReaderService.getBcs();
		List<ScreenSourceDto> screenDtos = metaResourceReaderService.getScreens();
		List<WidgetSourceDTO> widgetDtos = metaResourceReaderService.getWidgets();
		List<ViewSourceDTO> viewDtos = metaResourceReaderService.getViews();

		PrivilegeUtil.runPrivileged(() -> {
			authzService.loginAs(authzService.createAuthentication(VANILLA));

			txService.invokeInTx(() -> {
				loadMetaPreProcess(widgetDtos, viewDtos, screenDtos);
				deleteAllMeta(jpaDao);
				bcUtil.process(bcDtos);
				Map<String, Widget> nameToWidget = widgetUtil.process(widgetDtos);
				widgetPropertyUtil.process(widgetDtos, nameToWidget);
				viewAndViewWidgetUtil.process(viewDtos, nameToWidget);
				screenAndNavigationGroupAndNavigationViewUtil.process(screenDtos);
				loadMetaAfterProcess();
				return null;
			});
			return null;
		});
	}

	protected void loadMetaPreProcess(List<WidgetSourceDTO> widgetDtos, List<ViewSourceDTO> viewDtos,
			List<ScreenSourceDto> screenDtos) {

	}

	protected void loadMetaAfterProcess() {

	}

}
