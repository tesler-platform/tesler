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

import com.fasterxml.jackson.databind.ObjectMapper;
import io.tesler.db.migration.liquibase.data.NavigationGroup;
import io.tesler.db.migration.liquibase.data.NavigationView;
import io.tesler.db.migration.liquibase.data.ScreenEntity;
import io.tesler.db.migration.liquibase.data.ScreenEntity.ScreenNavigation.MenuItem;
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
		final Map<String, ScreenEntity.ScreenNavigation.MenuItem> map = new HashMap<>();

		final List<NavigationGroup> groups = getGroups(connection, screenName);
		final List<NavigationView> views = getViews(connection, screenName);

		final List<ScreenEntity.ScreenNavigation.MenuItem> menus = new ArrayList<>();
		for (final NavigationGroup group : groups) {
			final ScreenEntity.ScreenNavigation.MenuItem groupMenuItem = computeIfAbsent(map, group);
			groupMenuItem.setDefaultView(groupMenuItem.getTitle());
			groupMenuItem.setCommentDevelop(group.getDescription());
			groupMenuItem.setTitle(group.getTitle());
			groupMenuItem.setDefaultView(group.getDefaultView());
			groupMenuItem.setHidden(Integer.valueOf(1).equals(group.getHidden()));
			if (group.getParentId() == null) {
				menus.add(group.getSeq() > menus.size() ? menus.size() : group.getSeq(), groupMenuItem);
			} else {
				final NavigationGroup parentGroup = findParentGroup(groups, group);
				final ScreenEntity.ScreenNavigation.MenuItem parentMenu = computeIfAbsent(map, parentGroup);
				if (parentMenu.getChild() == null) {
					parentMenu.setChild(new ArrayList<>());
				}
				final List<MenuItem> child = parentMenu.getChild();
				child.add(
						group.getSeq() > child.size() ? child.size() : group.getSeq(),
						groupMenuItem
				);
			}
		}

		for (final NavigationView view : views) {
			final ScreenEntity.ScreenNavigation.MenuItem viewMenuItem = new ScreenEntity.ScreenNavigation.MenuItem();
			viewMenuItem.setViewName(view.getViewName());
			viewMenuItem.setCommentDevelop(view.getDescription());
			viewMenuItem.setHidden(Integer.valueOf(1).equals(view.getHidden()));
			if (view.getParentGroupId() == null) {
				menus.add(view.getSeq() > menus.size() ? menus.size() : view.getSeq(), viewMenuItem);
			} else {
				final ScreenEntity.ScreenNavigation.MenuItem parentMenu = map.get(view.getParentGroupId());
				if (parentMenu.getChild() == null) {
					parentMenu.setChild(new ArrayList<>());
				}
				final List<MenuItem> child = parentMenu.getChild();
				child.add(view.getSeq() > child.size() ? child.size() : view.getSeq(), viewMenuItem);
			}
		}

		final ScreenEntity.ScreenNavigation screenNavigation = new ScreenEntity.ScreenNavigation();
		screenNavigation.setMenu(menus);
		return screenNavigation;
	}

	private List<NavigationGroup> getGroups(final JdbcConnection connection, final String screenName) throws Exception {
		final List<NavigationGroup> result = new ArrayList<>();
		try (final PreparedStatement statement = connection.prepareStatement(
				"SELECT * FROM NAVIGATION_GROUP WHERE (TYPE_CD != 'USER_GROUP' OR TYPE_CD IS NULL) AND SCREEN_NAME = ? ORDER BY SEQ ASC")) {
			statement.setString(1, screenName);
			try (final ResultSet resultSet = statement.executeQuery()) {
				while (resultSet.next()) {
					final NavigationGroup entity = new NavigationGroup();
					entity.setId(resultSet.getObject("ID", String.class));
					entity.setTypeCd(resultSet.getString("TYPE_CD"));
					entity.setScreenName(resultSet.getString("SCREEN_NAME"));
					entity.setTitle(resultSet.getString("TITLE"));
					entity.setParentId(resultSet.getObject("PARENT_ID", String.class));
					entity.setSeq(resultSet.getObject("SEQ", Integer.class));
					entity.setDescription(resultSet.getString("DESCRIPTION"));
					entity.setHidden(resultSet.getInt("HIDDEN"));
					result.add(entity);
				}
			}
		}
		return result;
	}

	private List<NavigationView> getViews(final JdbcConnection connection, final String screenName)
			throws Exception {
		final List<NavigationView> result = new ArrayList<>();
		try (final PreparedStatement statement = connection.prepareStatement(
				"SELECT * FROM NAVIGATION_VIEW WHERE PARENT_GROUP_ID IN (SELECT NAVIGATION_GROUP.ID FROM NAVIGATION_GROUP WHERE (TYPE_CD != 'USER_GROUP' OR TYPE_CD IS NULL) AND SCREEN_NAME = ?) ORDER BY SEQ ASC")) {
			statement.setString(1, screenName);
			try (final ResultSet resultSet = statement.executeQuery()) {
				while (resultSet.next()) {
					final NavigationView entity = new NavigationView();
					entity.setId(resultSet.getObject("ID", String.class));
					entity.setScreenName(resultSet.getString("SCREEN_NAME"));
					entity.setViewName(resultSet.getString("VIEW_NAME"));
					entity.setParentGroupId(resultSet.getObject("PARENT_GROUP_ID", String.class));
					entity.setSeq(resultSet.getObject("SEQ", Integer.class));
					entity.setHidden(resultSet.getInt("HIDDEN"));
					result.add(entity);
				}
			}
		}
		return result;
	}

	private NavigationGroup findParentGroup(final List<NavigationGroup> groups, final NavigationGroup currentGroup) {
		for (final NavigationGroup group : groups) {
			if (Objects.equals(currentGroup.getParentId(), group.getId())) {
				return group;
			}
		}
		return null;
	}

	private ScreenEntity.ScreenNavigation.MenuItem computeIfAbsent(
			final Map<String, ScreenEntity.ScreenNavigation.MenuItem> map,
			final NavigationGroup group) {
		final ScreenEntity.ScreenNavigation.MenuItem value = map.get(group.getId());
		if (value != null) {
			return value;
		}
		final ScreenEntity.ScreenNavigation.MenuItem newValue = new ScreenEntity.ScreenNavigation.MenuItem();
		map.put(group.getId(), newValue);
		return newValue;
	}

}
