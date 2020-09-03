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

package io.tesler.core.dto.rowmeta;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonInclude;
import io.tesler.core.crudma.bc.BcIdentifier;
import io.tesler.core.dto.MessageType;
import io.tesler.core.service.action.DrillDownTypeSpecifier;
import java.util.HashMap;
import java.util.Map;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class PostAction {

	public class BasePostActionType {

		public static final String REFRESH_BC = "refreshBC";

		public static final String DOWNLOAD_FILE = "downloadFile";

		public static final String DOWNLOAD_FILE_BY_URL = "downloadFileByUrl";

		public static final String OPEN_PICK_LIST = "openPickList";

		public static final String DRILL_DOWN = "drillDown";

		public static final String DELAYED_REFRESH_BC = "delayedRefreshBC";

		public static final String SHOW_MESSAGE = "showMessage";

		public static final String POST_DELETE = "postDelete";

	}

	public class BasePostActionField {

		public static final String TYPE = "type";

		public static final String BC = "bc";

		public static final String FILE_ID = "fileId";

		public static final String DELAY = "delay";

		public static final String MESSAGE_TYPE = "messageType";

		public static final String MESSAGE_TEXT = "messageText";

		public static final String URL = "url";

		public static final String URL_NAME = "urlName";

		public static final String DRILL_DOWN_TYPE = "drillDownType";

		public static final String PICK_LIST = "pickList";

	}

	private final Map<String, String> attributes = new HashMap<>();

	@JsonAnyGetter
	public Map<String, String> getAttributes() {
		return attributes;
	}

	@JsonAnyGetter
	public String getAttribute(String key) {
		return attributes.get(key);
	}

	@Deprecated
	public String getType() {
		return attributes.get(BasePostActionField.TYPE);
	}

	@Deprecated
	public String getBc() {
		return attributes.get(BasePostActionField.BC);
	}

	public PostAction add(String key, String value) {
		attributes.put(key, value);
		return this;
	}

	public static PostAction refreshBc(BcIdentifier bcIdentifier) {
		return new PostAction()
				.add(BasePostActionField.TYPE, BasePostActionType.REFRESH_BC)
				.add(BasePostActionField.BC, bcIdentifier.getName());
	}

	public static PostAction refreshParentBc(BcIdentifier bcIdentifier) {
		return new PostAction()
				.add(BasePostActionField.TYPE, BasePostActionType.REFRESH_BC)
				.add(BasePostActionField.BC, bcIdentifier.getParentName());
	}

	public static PostAction downloadFile(String fileId) {
		return new PostAction()
				.add(BasePostActionField.TYPE, BasePostActionType.DOWNLOAD_FILE)
				.add(BasePostActionField.FILE_ID, fileId);
	}

	public static PostAction downloadFileByUrl(String url) {
		return new PostAction()
				.add(BasePostActionField.TYPE, BasePostActionType.DOWNLOAD_FILE_BY_URL)
				.add(BasePostActionField.URL, url);
	}

	public static PostAction openPickList(final String pickList) {
		return new PostAction()
				.add(BasePostActionField.TYPE, BasePostActionType.OPEN_PICK_LIST)
				.add(BasePostActionField.PICK_LIST, pickList);
	}

	public static PostAction drillDown(DrillDownTypeSpecifier drillDownType, String url) {
		return drillDown(drillDownType, url, null);
	}

	public static PostAction drillDown(DrillDownTypeSpecifier drillDownType, String url, String urlName) {
		return new PostAction()
				.add(BasePostActionField.TYPE, BasePostActionType.DRILL_DOWN)
				.add(BasePostActionField.URL, url)
				.add(BasePostActionField.URL_NAME, urlName)
				.add(BasePostActionField.DRILL_DOWN_TYPE, drillDownType.getValue());
	}

	public static PostAction delayedRefreshBC(BcIdentifier bcIdentifier, Number seconds) {
		return new PostAction()
				.add(BasePostActionField.TYPE, BasePostActionType.DELAYED_REFRESH_BC)
				.add(BasePostActionField.BC, bcIdentifier.getName())
				.add(BasePostActionField.DELAY, seconds.toString());
	}

	public static PostAction showMessage(MessageType messageType, String messageText) {
		return new PostAction()
				.add(BasePostActionField.TYPE, BasePostActionType.SHOW_MESSAGE)
				.add(BasePostActionField.MESSAGE_TYPE, messageType.getValue())
				.add(BasePostActionField.MESSAGE_TEXT, messageText);
	}

	public static PostAction postDelete() {
		return new PostAction()
				.add(BasePostActionField.TYPE, BasePostActionType.POST_DELETE);
	}

}
