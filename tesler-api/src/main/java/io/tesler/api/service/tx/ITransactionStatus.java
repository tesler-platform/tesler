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

package io.tesler.api.service.tx;


public interface ITransactionStatus {

	int BEFORE_COMMIT = 1;
	int BEFORE_COMPLETION = 2;
	int AFTER_COMMIT = 4;
	int AFTER_COMPLETION = 8;

	void setStatus(int status);

	boolean isBeforeCommit();

	boolean isBeforeCompletion();

	boolean isAfterCommit();

	boolean isAfterCompletion();

	boolean isCommitting();

}
