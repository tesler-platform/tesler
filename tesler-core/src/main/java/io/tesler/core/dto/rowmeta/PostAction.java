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

import io.tesler.core.crudma.bc.BcIdentifier;
import io.tesler.core.dto.DrillDownType;
import io.tesler.core.dto.MessageType;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PostAction {

	public static final String ACTION_REFRESH_BC = "refreshBC";

	public static final String ACTION_DOWNLOAD_FILE = "downloadFile";

	public static final String ACTION_DOWNLOAD_FILE_BY_URL = "downloadFileByUrl";

	public static final String ACTION_SHOW_PICK_LIST = "openPickList";

	public static final String ACTION_DRILL_DOWN = "drillDown";

	public static final String ACTION_DELAYED_REFRESH_BC = "delayedRefreshBC";

	public static final String ACTION_SHOW_MESSAGE = "showMessage";

	public static final String ACTION_POST_DELETE = "postDelete";

	private final String type;

	private final String bc;

	private final String url;

	private final String urlName;

	private final String fileId;

	private final String pickList;

	private final Number delay;

	private final String drillDownType;

	private final String messageType;

	private final String messageText;

	public static PostAction refreshBc(BcIdentifier bcIdentifier) {
		return builder()
				.type(ACTION_REFRESH_BC)
				.bc(bcIdentifier.getName())
				.build();
	}

	public static PostAction refreshParentBc(BcIdentifier bcIdentifier) {
		return builder()
				.type(ACTION_REFRESH_BC)
				.bc(bcIdentifier.getParentName())
				.build();
	}

	public static PostAction downloadFile(String fileId) {
		return builder()
				.type(ACTION_DOWNLOAD_FILE)
				.fileId(fileId)
				.build();
	}

	public static PostAction downloadFileByUrl(String url) {
		return builder()
				.type(ACTION_DOWNLOAD_FILE_BY_URL)
				.url(url)
				.build();
	}

	public static PostAction openPickList(final String pickList) {
		return builder()
				.type(ACTION_SHOW_PICK_LIST)
				.pickList(pickList)
				.build();
	}

	public static PostAction drillDown(DrillDownType drillDownType, String url) {
		return drillDown(drillDownType, url, null);
	}

	public static PostAction drillDown(DrillDownType drillDownType, String url, String urlName) {
		return builder()
				.type(ACTION_DRILL_DOWN)
				.url(url)
				.urlName(urlName)
				.drillDownType(drillDownType.getValue())
				.build();
	}

	public static PostAction delayedRefreshBC(BcIdentifier bcIdentifier, Number seconds) {
		return builder()
				.type(ACTION_DELAYED_REFRESH_BC)
				.bc(bcIdentifier.getName())
				.delay(seconds)
				.build();
	}

	public static PostAction showMessage(MessageType messageType, String messageText) {
		return builder()
				.type(ACTION_SHOW_MESSAGE)
				.messageType(messageType.getValue())
				.messageText(messageText)
				.build();
	}

	public static PostAction postDelete() {
		return builder()
				.type(ACTION_POST_DELETE)
				.build();
	}

}
