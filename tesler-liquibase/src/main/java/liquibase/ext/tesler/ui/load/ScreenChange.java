/*-
 * #%L
 * IO Tesler - Liquibase
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

package liquibase.ext.tesler.ui.load;

import io.tesler.db.migration.liquibase.data.NavigationGroup;
import io.tesler.db.migration.liquibase.data.NavigationView;
import io.tesler.db.migration.liquibase.data.ScreenEntity;
import java.util.ArrayList;
import java.util.List;
import liquibase.change.ChangeMetaData;
import liquibase.change.DatabaseChange;
import liquibase.database.Database;
import liquibase.resource.ResourceAccessor;
import liquibase.statement.SqlStatement;

@DatabaseChange(name = "screenload", description = "Create screen", priority = ChangeMetaData.PRIORITY_DEFAULT)
public class ScreenChange extends AbstractEntityChange<ScreenEntity> {

	@Override
	protected Class<ScreenEntity> getElementType() {
		return ScreenEntity.class;
	}

	@Override
	protected List<SqlStatement> generateStatements(
			Database database,
			ResourceAccessor resourceAccessor,
			ScreenEntity entity
	) throws Exception {
		final List<SqlStatement> result = super.generateStatements(database, resourceAccessor, entity);
		result.addAll(
				generateNavigationStatements(
						database,
						resourceAccessor,
						entity.getName(),
						null,
						entity.getNavigation().getMenu()
				)
		);
		return result;
	}

	private List<SqlStatement> generateNavigationStatements(
			final Database database,
			final ResourceAccessor resourceAccessor,
			final String screenName,
			final String parentGroupId,
			final List<ScreenEntity.ScreenNavigation.MenuItem> menuItems
	) throws Exception {
		final List<SqlStatement> result = new ArrayList<>();
		if (menuItems == null || menuItems.isEmpty()) {
			return result;
		}
		int seq = 0;
		for (final ScreenEntity.ScreenNavigation.MenuItem menuItem : menuItems) {
			seq++;
			if (menuItem.getChild() != null) {
				String id = parentGroupId == null ? screenName + "/" + seq + "/" : parentGroupId + seq + "/";
				if (menuItem.getChild().size() < 2) {
					throw new IllegalStateException("Navigation group with id " + id + " must have at least 2 child elements");
				}
				if (menuItem.getTitle() == null) {
					throw new IllegalStateException("Navigation group with id " + id + " must have a title");
				}
				result.addAll(
						navigationGroupStatements(
								database,
								resourceAccessor,
								id,
								"NAVIGATION",
								screenName,
								menuItem.getTitle(),
								parentGroupId,
								seq,
								menuItem.getCommentDevelop(),
								menuItem.getDefaultView(),
								menuItem.isHidden()
						)
				);
				result.addAll(
						generateNavigationStatements(
								database,
								resourceAccessor,
								screenName,
								id,
								menuItem.getChild()
						)
				);
			} else if (menuItem.getViewName() != null) {
				String id = parentGroupId == null ? screenName + "/" + menuItem.getViewName()
						: parentGroupId + menuItem.getViewName();
				result.addAll(
						navigationViewStatements(
								database,
								resourceAccessor,
								id,
								"NAVIGATION",
								menuItem.getViewName(),
								screenName,
								parentGroupId,
								menuItem.getCommentDevelop(),
								seq,
								menuItem.isHidden()
						)
				);
			} else {
				throw new IllegalStateException("Unrecognized navigation element on screen: " + screenName);
			}
		}
		return result;
	}

	private List<SqlStatement> navigationGroupStatements(
			final Database database,
			final ResourceAccessor resourceAccessor,
			final String id,
			final String typeCd,
			final String screenName,
			final String title,
			final String parentId,
			final Integer seq,
			final String description,
			final String defaultView,
			final Boolean hidden
	) throws Exception {
		final AbstractEntityChange<NavigationGroup> entityChange = getEntityChange(
				database,
				resourceAccessor,
				NavigationGroup.class
		);
		final NavigationGroup entity = new NavigationGroup();
		entity.setId(id);
		entity.setTypeCd(typeCd);
		entity.setScreenName(screenName);
		entity.setTitle(title);
		entity.setParentId(parentId);
		entity.setSeq(seq);
		entity.setDescription(description);
		entity.setDefaultView(defaultView);
		entity.setHidden(hidden ? 1 : 0);
		return entityChange.generateStatements(database, resourceAccessor, entity);
	}

	private List<SqlStatement> navigationViewStatements(
			final Database database,
			final ResourceAccessor resourceAccessor,
			final String id,
			final String typeCd,
			final String viewName,
			final String screenName,
			final String parentGroupId,
			final String description,
			final Integer seq,
			final Boolean hidden
	) throws Exception {
		final AbstractEntityChange<NavigationView> entityChange = getEntityChange(
				database,
				resourceAccessor,
				NavigationView.class
		);
		final NavigationView entity = new NavigationView();
		entity.setId(id);
		entity.setTypeCd(typeCd);
		entity.setViewName(viewName);
		entity.setScreenName(screenName);
		entity.setParentGroupId(parentGroupId);
		entity.setSeq(seq);
		entity.setDescription(description);
		entity.setHidden(hidden ? 1 : 0);
		return entityChange.generateStatements(database, resourceAccessor, entity);
	}

}
