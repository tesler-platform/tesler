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

package io.tesler.core.dto;

import io.tesler.api.data.dictionary.SimpleDictionary;
import io.tesler.core.dto.data.view.ScreenResponsibility;
import io.tesler.model.core.entity.User;
import com.fasterxml.jackson.databind.JsonNode;
import java.util.Collection;
import java.util.List;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;


@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class LoggedUser {

	private String sessionId;

	private Number userId;

	private String login;

	private String lastName;

	private String firstName;

	private String patronymic;

	private String fullName;

	private String principalName;

	private String phone;

	private String activeRole;

	private List<SimpleDictionary> roles;

	/**
	 * @deprecated TODO: Remove in 3.0 in favor of separate ScreenController endpoint
	 */
	@Deprecated
	private List<ScreenResponsibility> screens;

	private JsonNode userSettingsVersion;

	private Collection<SimpleDictionary> featureSettings;

	private String systemUrl;

	private String timezone;

	private String language;

	public static Builder builder() {
		return new LoggedUser().new Builder();
	}

	public class Builder {

		private Builder() {

		}

		public Builder sessionId(String sessionId) {
			LoggedUser.this.sessionId = sessionId;
			return this;
		}

		public Builder systemUrl(String url) {
			LoggedUser.this.systemUrl = url;
			return this;
		}

		public Builder user(User user) {

			LoggedUser.this.userId = user.getId();
			LoggedUser.this.login = user.getLogin();
			LoggedUser.this.lastName = user.getLastName();
			LoggedUser.this.firstName = user.getFirstName();
			LoggedUser.this.patronymic = user.getPatronymic();
			LoggedUser.this.fullName = user.getFullName();
			LoggedUser.this.principalName = user.getUserPrincipalName();
			LoggedUser.this.phone = user.getPhone();

			return this;
		}

		public Builder activeRole(String activeRole) {
			LoggedUser.this.activeRole = activeRole;
			return this;
		}

		public Builder language(String locale) {
			LoggedUser.this.language = locale;
			return this;
		}

		public Builder timezone(String timezone) {
			LoggedUser.this.timezone = timezone;
			return this;
		}

		public Builder roles(List<SimpleDictionary> roleList) {
			LoggedUser.this.roles = roleList;
			return this;
		}

		public Builder screens(List<ScreenResponsibility> screens) {
			LoggedUser.this.screens = screens;
			return this;
		}

		public Builder userSettings(JsonNode userSettings) {
			LoggedUser.this.userSettingsVersion = userSettings;
			return this;
		}

		public Builder featureSettings(Collection<SimpleDictionary> featureList) {
			LoggedUser.this.featureSettings = featureList;
			return this;
		}

		public LoggedUser build() {
			return LoggedUser.this;
		}

	}

}
