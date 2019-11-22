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

package io.tesler.api.util;

import java.io.File;
import java.nio.file.Path;
import java.security.AccessController;
import java.security.PrivilegedAction;
import lombok.experimental.UtilityClass;

@UtilityClass
public class FsUtils {

	public static String getHomeDirectory() {
		return AccessController.doPrivileged((PrivilegedAction<String>) () -> System.getProperty("user.home"));
	}

	public static String getTempDirectory() {
		return AccessController.doPrivileged((PrivilegedAction<String>) () -> System.getProperty("java.io.tmpdir"));
	}

	public static String createPath(String root, String... parts) {
		return AccessController.doPrivileged((PrivilegedAction<String>) () -> {
			Path path = new File(root).toPath();
			for (String part : parts) {
				path = path.resolve(part);
			}
			File dir = path.toFile();
			dir.mkdirs();
			return dir.getAbsolutePath();
		});
	}

}
