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

import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Spliterator;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ResultPage<T> implements Iterable<T>, IDataContainer<T> {

	private final List<T> result;

	private final boolean hasNext;

	public ResultPage() {
		this.result = Collections.emptyList();
		this.hasNext = false;
	}

	public static <T> ResultPage<T> of(List<T> data, PageSpecification page) {
		if (PageSpecification.isValid(page)) {
			return of(data, page.getPageSize());
		}
		return of(data, false);
	}

	public static <T> ResultPage<T> of(List<T> data, int maxItems) {
		return of(data.subList(0, Math.min(data.size(), maxItems)), data.size() > maxItems);
	}

	public static <T> ResultPage<T> of(List<T> data, boolean hasNext) {
		return new ResultPage<>(data, hasNext);
	}

	public static <T> ResultPage<T> of(Iterable<T> data, boolean hasNext) {
		return new ResultPage<>(
				StreamSupport.stream(data.spliterator(), false)
						.collect(Collectors.toList()),
				hasNext
		);
	}

	public static <T, E> ResultPage<T> of(List<E> data, Function<E, T> converter, boolean hasNext) {
		return new ResultPage<>(
				data.stream().map(converter)
						.collect(Collectors.toList()),
				hasNext
		);
	}

	public static <T, E> ResultPage<T> of(Iterable<E> data, Function<E, T> converter, boolean hasNext) {
		return new ResultPage<>(
				StreamSupport.stream(data.spliterator(), false)
						.map(converter).collect(Collectors.toList()),
				hasNext
		);
	}

	public static <T, E> ResultPage<T> of(ResultPage<E> data, Function<E, T> converter) {
		return of(data.getResult(), converter, data.isHasNext());
	}

	@Override
	public Iterator<T> iterator() {
		return result.iterator();
	}

	@Override
	public void forEach(Consumer<? super T> action) {
		result.forEach(action);
	}

	@Override
	public Spliterator<T> spliterator() {
		return result.spliterator();
	}

	@Override
	public void transformData(Function<T, T> function) {
		for (int i = 0; i < result.size(); i++) {
			result.set(i, function.apply(result.get(i)));
		}
	}

}
