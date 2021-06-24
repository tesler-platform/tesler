/*-
 * #%L
 * IO Tesler - API
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

package io.tesler.api.data.dictionary;

import lombok.experimental.UtilityClass;


public class CoreDictionaries {

	@UtilityClass
	public static final class ViewGroupType {

		public static final LOV NAVIGATION = new LOV("NAVIGATION");

		public static final LOV USER_GROUP = new LOV("USER_GROUP");

	}


	@UtilityClass
	public static final class UserActivityType {

		public static final LOV LOGIN = new LOV("LOGIN");

		public static final LOV BROWSE_VIEW = new LOV("BROWSE_VIEW");

	}

	@UtilityClass
	public static final class PreInvokeType {

		public static final LOV CONFIRMATION = new LOV("confirm");

		public static final LOV INFORMATION = new LOV("info");

		public static final LOV ERROR = new LOV("error");

	}

	@UtilityClass
	public static class InternalRole {

		public static final LOV ADMIN = new LOV("ADMIN");

	}

	@UtilityClass
	public static final class SearchSpecType {

		public static final LOV SECURITY = new LOV("SECURITY");

		public static final LOV BC = new LOV("BC");

		public static final LOV LINK = new LOV("LINK");

	}

	@UtilityClass
	public static final class MimeType {

		public static final LOV TEXT = new LOV("text/plain");

		public static final LOV HTML = new LOV("text/html");

	}


	@UtilityClass
	public static final class FileStorage {

		public static final LOV DB = new LOV("DB");

		public static final LOV FILENET = new LOV("FILENET");

		public static final LOV PENDING_TO_FN = new LOV("PENDING_TO_FN");

	}

	@UtilityClass
	public static final class DatabaseEvent {

		//  @Упоминание в комментарии
		public static final LOV COMMENT_MENTION_CREATED = new LOV("COMMENT_MENTION_CREATED");

		// Изменение комментария с @Упоминанием
		public static final LOV COMMENT_MENTION_UPDATED = new LOV("COMMENT_MENTION_UPDATED");

		// Получен ответ на комментарий
		public static final LOV COMMENT_ANSWERED = new LOV("COMMENT_ANSWERED");

		// Миграция workflow на новую версию. Результаты
		public static final LOV TASK_WORKFLOW_MIGRATION_RESULT = new LOV("TASK_WORKFLOW_MIGRATION_RESULT");

	}

	@UtilityClass
	public static final class SystemPref {

		public static final LOV SUPPORTED_LANGUAGES = new LOV("SUPPORTED_LANGUAGES");

		public static final LOV SYSTEM_URL = new LOV("SYSTEM_URL");

		public static final LOV WF_BACKGROUND_TRANSITION_THREADS_COUNT = new LOV("WF_BACKGROUND_TRANSITION_THREADS_COUNT");

		public static final LOV UI_LOCK_TIMEOUT = new LOV("UI_LOCK_TIMEOUT");

		public static final LOV ENABLE_PROFILING = new LOV("ENABLE_PROFILING");

		public static final LOV PROFILING_TIME_TO_LOG = new LOV("PROFILING_TIME_TO_LOG");

		public static final LOV FEATURE_COMMENTS = new LOV("FEATURE_COMMENTS");

		public static final LOV FEATURE_SECURITY_LOGGING = new LOV("FEATURE_SECURITY_LOGGING");

		public static final LOV FEATURE_EXCEPTION_TRACKING = new LOV("FEATURE_EXCEPTION_TRACKING");

		public static final LOV FEATURE_FULL_STACKTRACES = new LOV("FEATURE_FULL_STACKTRACES");

	}


	@UtilityClass
	public static final class RemoteSystemResponse {

		public static final LOV NOT_RECEIVED = new LOV("notReceived");

		public static final LOV RECEIVED_AND_SUCCESS = new LOV("receivedAndSuccess");

		public static final LOV RECEIVED_PARTIALLY = new LOV("receivedPartially");

		public static final LOV RECEIVED_AND_ERROR = new LOV("receivedAndError");

		public static final LOV RECEIVED_PARTIALLY_AND_ERROR = new LOV("receivedPartiallyAndError");

	}

	@UtilityClass
	public static final class WfPostFunction {

		public static final LOV SET_STEP_TERM = new LOV("SetStepTerm");

	}

	@UtilityClass
	public static final class TaskStatus {

		public static final LOV HIDDEN = new LOV("HIDDEN");

	}

	@UtilityClass
	public static final class TaskStatusCategory {

		public static final LOV NO_CATEGORY = new LOV("NO_CATEGORY");

		public static final LOV TO_DO = new LOV("TO_DO");

		public static final LOV IN_PROGRESS = new LOV("IN_PROGRESS");

		public static final LOV ON_AGREEMENT = new LOV("ON_AGREEMENT");

		public static final LOV DONE = new LOV("DONE");

		public static final LOV HIDDEN = new LOV("HIDDEN");

		public static final LOV AUTO_CLOSED = new LOV("AUTO_CLOSED");

		public static boolean isNoCategory(LOV lov) {
			return NO_CATEGORY.equals(lov);
		}

		public static boolean isToDo(LOV lov) {
			return TO_DO.equals(lov);
		}

		public static boolean isInProgress(LOV lov) {
			return IN_PROGRESS.equals(lov);
		}

		public static boolean isOnAgreement(LOV lov) {
			return ON_AGREEMENT.equals(lov);
		}

		public static boolean isDone(LOV lov) {
			return DONE.equals(lov);
		}

		public static boolean isHidden(LOV lov) {
			return HIDDEN.equals(lov);
		}

		public static boolean isAutoClosed(LOV lov) {
			return AUTO_CLOSED.equals(lov);
		}

	}

	@UtilityClass
	public static final class WfTransitionValidate {

		public static final LOV TRANSITION_PRE_INVOKE = new LOV("TRANSITION_PRE_INVOKE");

	}

	@UtilityClass
	public static final class LaunchStatus {

		public static final LOV IN_PROGRESS = new LOV("IN_PROGRESS");

		public static final LOV FAILED = new LOV("FAILED");

		public static final LOV SUCCESS = new LOV("SUCCESS");

		public static final LOV SUCCESS_WITH_WARNINGS = new LOV("SUCCESS_WITH_WARNINGS");

	}


	@UtilityClass
	public static final class WorkflowConditionType {

		public static final LOV STEP_CONDITION = new LOV("STEP_CONDITION");

		public static final LOV TRANSITION_CONDITION = new LOV("TRANSITION_CONDITION");

		public static final LOV STEP_FIELD_CONDITION = new LOV("STEP_FIELD_CONDITION");

		public static final LOV CHILD_BC_CONDITION = new LOV("CHILD_BC_CONDITION");

	}

	@UtilityClass
	public static final class DictionaryTermType {

		public static final LOV DEPT = new LOV("DEPT");

		public static final LOV TEXT_FIELD = new LOV("TEXT_FIELD");

		public static final LOV DICTIONARY_FIELD = new LOV("DICTIONARY_FIELD");

		public static final LOV BC = new LOV("BC");

		public static final LOV FIELD_IS_EMPTY = new LOV("FIELD_IS_EMPTY");

		public static final LOV BOOLEAN_FIELD = new LOV("BOOLEAN_FIELD");

	}

	@UtilityClass
	public static final class DatabaseEventType {

		public static final LOV INSERT = new LOV("INSERT");

		public static final LOV UPDATE = new LOV("UPDATE");

		public static final LOV DELETE = new LOV("DELETE");

	}

}
