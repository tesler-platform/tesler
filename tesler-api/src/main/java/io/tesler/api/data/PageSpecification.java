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

package io.tesler.api.data;

import lombok.Data;

@Data
public class PageSpecification {

	public static final int DEFAULT_PAGE_NUMBER = 1;

	public static final int DEFAULT_PAGE_SIZE = 5;

	public static final PageSpecification DEFAULT = new PageSpecification(0, DEFAULT_PAGE_SIZE, false);

	private final int pageNo;

	private final int pageSize;

	private final boolean provided;

	public static boolean isValid(PageSpecification page) {
		return page != null && page.pageSize > 0;
	}

	public int getFrom() {
		return pageNo * pageSize;
	}

	public int getTo() {
		return pageNo * (pageSize + 1) + 1;
	}

}
