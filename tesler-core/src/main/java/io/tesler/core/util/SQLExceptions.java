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

package io.tesler.core.util;

import java.sql.SQLException;
import lombok.experimental.UtilityClass;


@UtilityClass
public class SQLExceptions {

	public static boolean isUniqueConstraintViolation(Throwable ex) {
		return isOra(ex, 1);
	}

	public static boolean isNotNullViolation(Throwable ex) {
		return isOra(ex, 1400);
	}

	public static boolean isChildRecordsFound(Throwable ex) {
		return isOra(ex, 2292);
	}

	public static boolean isParentRecordNotFound(Throwable ex) {
		return isOra(ex, 2291);
	}

	public static boolean isOra(Throwable ex, int code) {
		if (ex == null) {
			return false;
		}
		if (ex instanceof SQLException) {
			if (hasErrorCode((SQLException) ex, code)) {
				return true;
			}
		}
		return isOra(ex.getCause(), code);
	}

	public static boolean hasErrorCode(SQLException ex, int code) {
		if (ex == null) {
			return false;
		}
		if (ex.getErrorCode() == code) {
			return true;
		}
		return isOra(ex.getNextException(), code);
	}

}
