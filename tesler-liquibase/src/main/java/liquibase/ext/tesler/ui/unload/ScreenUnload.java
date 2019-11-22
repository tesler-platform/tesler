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

package liquibase.ext.tesler.ui.unload;

import io.tesler.db.migration.liquibase.data.ScreenEntity;
import io.tesler.db.migration.liquibase.data.ScreenEntity.ScreenNavigation.SubMenu;
import io.tesler.db.migration.liquibase.data.ScreenViewGroup;
import io.tesler.db.migration.liquibase.data.ScreenViewGroupData;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.File;
import java.io.FileOutputStream;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import liquibase.change.ChangeMetaData;
import liquibase.change.DatabaseChange;
import liquibase.database.jvm.JdbcConnection;

@DatabaseChange(name = "screenunload", description = "Unload screens", priority = ChangeMetaData.PRIORITY_DEFAULT)
public class ScreenUnload extends AbstractEntityUnload {

	@Override
	protected void unload(JdbcConnection connection) throws Exception {
		ObjectMapper mapper = createMapper();
		try (PreparedStatement statement = connection.prepareStatement("SELECT * FROM SCREEN")) {
			try (ResultSet resultSet = statement.executeQuery()) {
				while (resultSet.next()) {
					ScreenEntity screenEntity = new ScreenEntity();
					screenEntity.setPrimaryViewName(resultSet.getString("PRIMARY_VIEW_NAME"));
					screenEntity.setName(resultSet.getString("NAME"));
					screenEntity.setTitle(resultSet.getString("TITLE"));
					screenEntity.setPrimaryViews(asJson(mapper, resultSet, "PRIMARY_VIEWS"));
					screenEntity.setNavigation(buildScreenNavigation(connection, resultSet.getString("NAME")));

					File destination = buildDirectory("screens", screenEntity.getName());

					try (FileOutputStream fos = new FileOutputStream(
							new File(destination, String.format("%s.screen.json", screenEntity.getName())))) {
						mapper.writeValue(fos, screenEntity);
					}
				}
			}
		}
	}

	private ScreenEntity.ScreenNavigation buildScreenNavigation(final JdbcConnection connection, final String screenName)
			throws Exception {
		final Map<Long, ScreenEntity.ScreenNavigation.Menu> map = new HashMap<>();

		final List<ScreenViewGroup> groups = getGroups(connection, screenName);
		final List<ScreenViewGroupData> views = getViews(connection, screenName);

		final List<ScreenEntity.ScreenNavigation.Menu> menus = new ArrayList<>();
		for (final ScreenViewGroup group : groups) {
			final ScreenEntity.ScreenNavigation.Menu menu = computeIfAbsent(map, group);
			menu.setId(group.getId());
			menu.setCommentDevelop(group.getDescription());
			if (Objects.equals(group.getRoot(), 1)) {
				menu.setTitle(group.getTitle());
			} else {
				menu.setCategoryName(group.getTitle());
			}
			if (group.getParentId() == null) {
				menus.add(group.getSeq() > menus.size() ? menus.size() : group.getSeq(), menu);
			} else if (menu instanceof ScreenEntity.ScreenNavigation.SubMenu) {
				final ScreenViewGroup parentGroup = findParentGroup(groups, group);
				final ScreenEntity.ScreenNavigation.Menu parentMenu = computeIfAbsent(map, parentGroup);
				if (parentMenu.getChild() == null) {
					parentMenu.setChild(new ArrayList<ScreenEntity.ScreenNavigation.SubMenu>());
				}
				final List<SubMenu> child = parentMenu.getChild();
				child.add(
						group.getSeq() > child.size() ? child.size() : group.getSeq(),
						(ScreenEntity.ScreenNavigation.SubMenu) menu
				);
			}
		}

		for (final ScreenViewGroupData view : views) {
			final ScreenEntity.ScreenNavigation.SubMenu menu = new ScreenEntity.ScreenNavigation.SubMenu();
			menu.setViewName(view.getViewName());
			final ScreenEntity.ScreenNavigation.Menu parentMenu = map.get(view.getViewGroupId());
			if (parentMenu.getChild() == null) {
				parentMenu.setChild(new ArrayList<ScreenEntity.ScreenNavigation.SubMenu>());
			}
			final List<SubMenu> child = parentMenu.getChild();
			child.add(view.getSeq() > child.size() ? child.size() : view.getSeq(), menu);
		}

		final ScreenEntity.ScreenNavigation screenNavigation = new ScreenEntity.ScreenNavigation();
		screenNavigation.setMenu(menus);
		return screenNavigation;
	}

	private List<ScreenViewGroup> getGroups(final JdbcConnection connection, final String screenName) throws Exception {
		final List<ScreenViewGroup> result = new ArrayList<>();
		try (final PreparedStatement statement = connection.prepareStatement(
				"SELECT * FROM SCREEN_VIEW_GROUP WHERE (TYPE_CD != 'USER_GROUP' OR TYPE_CD IS NULL) AND SCREEN_NAME = ? ORDER BY SEQ ASC")) {
			statement.setString(1, screenName);
			try (final ResultSet resultSet = statement.executeQuery()) {
				while (resultSet.next()) {
					final ScreenViewGroup entity = new ScreenViewGroup();
					entity.setId(resultSet.getObject("ID", Long.class));
					entity.setTypeCd(resultSet.getString("TYPE_CD"));
					entity.setScreenName(resultSet.getString("SCREEN_NAME"));
					entity.setTitle(resultSet.getString("TITLE"));
					entity.setParentId(resultSet.getObject("PARENT_ID", Long.class));
					entity.setSeq(resultSet.getObject("SEQ", Integer.class));
					entity.setDescription(resultSet.getString("DESCRIPTION"));
					entity.setRoot(resultSet.getInt("ROOT"));
					result.add(entity);
				}
			}
		}
		return result;
	}

	private List<ScreenViewGroupData> getViews(final JdbcConnection connection, final String screenName)
			throws Exception {
		final List<ScreenViewGroupData> result = new ArrayList<>();
		try (final PreparedStatement statement = connection.prepareStatement(
				"SELECT * FROM SCREEN_VIEW_GROUP_DATA WHERE VIEW_GROUP_ID IN (SELECT SCREEN_VIEW_GROUP.ID FROM SCREEN_VIEW_GROUP WHERE (TYPE_CD != 'USER_GROUP' OR TYPE_CD IS NULL) AND SCREEN_NAME = ?) ORDER BY SEQ ASC")) {
			statement.setString(1, screenName);
			try (final ResultSet resultSet = statement.executeQuery()) {
				while (resultSet.next()) {
					final ScreenViewGroupData entity = new ScreenViewGroupData();
					entity.setId(resultSet.getObject("ID", Long.class));
					entity.setViewName(resultSet.getString("VIEW_NAME"));
					entity.setViewGroupId(resultSet.getObject("VIEW_GROUP_ID", Long.class));
					entity.setSeq(resultSet.getObject("SEQ", Integer.class));
					result.add(entity);
				}
			}
		}
		return result;
	}

	private ScreenViewGroup findParentGroup(final List<ScreenViewGroup> groups, final ScreenViewGroup currentGroup) {
		for (final ScreenViewGroup group : groups) {
			if (Objects.equals(currentGroup.getParentId(), group.getId())) {
				return group;
			}
		}
		return null;
	}

	private ScreenEntity.ScreenNavigation.Menu computeIfAbsent(final Map<Long, ScreenEntity.ScreenNavigation.Menu> map,
			final ScreenViewGroup group) {
		final ScreenEntity.ScreenNavigation.Menu value = map.get(group.getId());
		if (value != null) {
			return value;
		}
		final ScreenEntity.ScreenNavigation.Menu newValue =
				group.getParentId() == null ? new ScreenEntity.ScreenNavigation.Menu()
						: new ScreenEntity.ScreenNavigation.SubMenu();
		map.put(group.getId(), newValue);
		return newValue;
	}

}
