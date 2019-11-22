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

import io.tesler.db.migration.liquibase.data.ScreenEntity;
import io.tesler.db.migration.liquibase.data.ScreenViewGroup;
import io.tesler.db.migration.liquibase.data.ScreenViewGroupData;
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
				generateGroupStatements(
						database,
						resourceAccessor,
						entity.getName(),
						entity.getNavigation()
				)
		);
		return result;
	}

	private List<SqlStatement> generateGroupStatements(
			final Database database,
			final ResourceAccessor resourceAccessor,
			final String screenName,
			final ScreenEntity.ScreenNavigation navigation
	) throws Exception {
		final List<SqlStatement> result = new ArrayList<>();
		final List<ScreenEntity.ScreenNavigation.Menu> menus = navigation.getMenu();
		for (int i = 0; i < menus.size(); i++) {
			final ScreenEntity.ScreenNavigation.Menu menu = menus.get(i);
			result.addAll(
					viewGroupStatements(
							database,
							resourceAccessor,
							menu.getId(),
							"NAVIGATION",
							screenName,
							menu.getTitle(),
							menu.getCategoryName(),
							null,
							menu.getCommentDevelop(),
							i + 1
					)
			);
			result.addAll(
					groupAndDataStatements(
							database,
							resourceAccessor,
							screenName,
							menu.getId(),
							menu.getChild()
					)
			);
		}
		return result;
	}

	private List<SqlStatement> groupAndDataStatements(
			final Database database,
			final ResourceAccessor resourceAccessor,
			final String screenName,
			final Long parentGroupId,
			final List<ScreenEntity.ScreenNavigation.SubMenu> subMenus) throws Exception {
		final List<SqlStatement> result = new ArrayList<>();
		if (subMenus == null || subMenus.isEmpty()) {
			return result;
		}
		int seq = 0;
		for (final ScreenEntity.ScreenNavigation.SubMenu subMenu : subMenus) {
			if (subMenu.getViewName() == null) {
				result.addAll(
						viewGroupStatements(
								database,
								resourceAccessor,
								subMenu.getId(),
								"NAVIGATION",
								screenName,
								null,
								subMenu.getCategoryName(),
								parentGroupId,
								subMenu.getCommentDevelop(),
								seq++
						)
				);
				result.addAll(
						groupAndDataStatements(
								database,
								resourceAccessor,
								screenName,
								subMenu.getId(),
								subMenu.getChild()
						)
				);
			} else {
				result.addAll(
						viewGroupDataStatements(
								database,
								resourceAccessor,
								subMenu.getViewName(),
								parentGroupId,
								seq++
						)
				);
			}
		}
		return result;
	}

	private List<SqlStatement> viewGroupStatements(
			final Database database,
			final ResourceAccessor resourceAccessor,
			final Long id, final String typeCd,
			final String screenName,
			final String title,
			final String categoryName,
			final Long parentId,
			final String comment,
			final Integer seq) throws Exception {
		final AbstractEntityChange<ScreenViewGroup> entityChange = getEntityChange(
				database,
				resourceAccessor,
				ScreenViewGroup.class
		);
		final ScreenViewGroup entity = new ScreenViewGroup();
		entity.setId(id);
		entity.setTypeCd(typeCd);
		entity.setScreenName(screenName);
		entity.setTitle(title == null ? categoryName : title);
		entity.setParentId(parentId);
		entity.setSeq(seq);
		entity.setDescription(comment);
		entity.setRoot(title == null ? 0 : 1);
		return entityChange.generateStatements(database, resourceAccessor, entity);
	}

	private List<SqlStatement> viewGroupDataStatements(
			final Database database,
			final ResourceAccessor resourceAccessor,
			final String viewName,
			final Long viewGroupId,
			final Integer seq
	) throws Exception {
		final AbstractEntityChange<ScreenViewGroupData> entityChange = getEntityChange(
				database,
				resourceAccessor,
				ScreenViewGroupData.class
		);
		final ScreenViewGroupData entity = new ScreenViewGroupData();
		entity.setIdSequence("APP_SEQ");
		entity.setViewName(viewName);
		entity.setViewGroupId(viewGroupId);
		entity.setSeq(seq);
		return entityChange.generateStatements(database, resourceAccessor, entity);
	}

}
