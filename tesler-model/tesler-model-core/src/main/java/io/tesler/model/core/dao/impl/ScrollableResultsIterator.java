/*-
 * #%L
 * IO Tesler - Model Core
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

package io.tesler.model.core.dao.impl;

import java.io.Closeable;
import java.util.Iterator;
import java.util.NoSuchElementException;
import org.hibernate.ScrollableResults;


class ScrollableResultsIterator<T> implements Iterator<T>, Closeable {

	private final ScrollableResults scrollableResults;

	private T next;

	private boolean closed;

	ScrollableResultsIterator(ScrollableResults scrollableResults) {
		this.scrollableResults = scrollableResults;
	}

	@Override
	public void close() {
		closed = true;
		scrollableResults.close();
	}

	@Override
	public boolean hasNext() {
		if (next != null) {
			return true;
		}

		if (closed) {
			return false;
		}

		if (scrollableResults.next()) {
			next = getObject();
			return true;
		} else {
			close();
			return false;
		}
	}

	@Override
	public T next() {
		if (hasNext()) {
			T current = next;
			next = null;
			return current;
		}
		throw new NoSuchElementException();
	}

	private T getObject() {
		T result;
		Object[] object = scrollableResults.get();
		if (object.length == 1) {
			result = (T) object[0];
		} else {
			result = (T) object;
		}
		return result;
	}

}


