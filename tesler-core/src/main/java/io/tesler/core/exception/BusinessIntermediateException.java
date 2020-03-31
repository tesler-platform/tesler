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


package io.tesler.core.exception;

import io.tesler.core.dto.BusinessError.Entity;
import lombok.Getter;

@Getter
public class BusinessIntermediateException extends RuntimeException {

	private Entity entity = null;

	private Object object = null;

	public BusinessIntermediateException() {
		super();
	}

	public BusinessIntermediateException(String message, Throwable cause) {
		super(message, cause);
	}

	public BusinessIntermediateException(Throwable cause) {
		super(cause);
	}

	public BusinessIntermediateException(String message, Throwable cause, boolean enableSuppression,
			boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

	public BusinessIntermediateException setEntity(Entity entity) {
		this.entity = entity;
		return this;
	}

	public BusinessIntermediateException setObject(Object object) {
		this.object = object;
		return this;
	}

}
