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

package io.tesler.api.system;

import io.tesler.api.data.dictionary.LOV;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Predicate;
import java.util.stream.Stream;
import org.apache.commons.lang3.tuple.Pair;


public interface SystemSettings {

	String SERVICE_NAME = "systemSettings";

	AtomicReference<SystemSettings> instance = new AtomicReference<>();

	static SystemSettings systemSettings() {
		return instance.get();
	}

	String getValue(LOV key);

	String getValue(LOV key, String defaultValue);

	boolean getBooleanValue(LOV key);

	int getIntegerValue(LOV key, int defaultValue);

	long getLongValue(LOV key, long defaultValue);

	List<String> getListValue(LOV key);

	void reload();

	Stream<? extends Pair<String, String>> select(Predicate<String> predicate);

}
