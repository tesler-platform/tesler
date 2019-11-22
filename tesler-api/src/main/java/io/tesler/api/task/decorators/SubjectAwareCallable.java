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

package io.tesler.api.task.decorators;

import io.tesler.api.util.Invoker;
import io.tesler.api.util.ServiceUtils;
import io.tesler.api.util.privileges.IPrivilegedInvoker;
import java.security.GeneralSecurityException;
import java.util.Objects;
import java.util.concurrent.Callable;
import javax.security.auth.Subject;


public class SubjectAwareCallable<E> implements Callable<E> {

	private final Callable<E> delegate;

	private final IPrivilegedInvoker<E, Exception> invoker;

	private final Subject[] subjects;

	private SubjectAwareCallable(Callable<E> delegate, IPrivilegedInvoker<E, Exception> invoker, Subject... subjects) {
		this.delegate = Objects.requireNonNull(delegate);
		this.invoker = Objects.requireNonNull(invoker);
		this.subjects = Objects.requireNonNull(subjects);
	}

	@SuppressWarnings("unchecked")
	public static <V> Callable<V> wrap(Callable<V> task) throws GeneralSecurityException {
		IPrivilegedInvoker<V, Exception> invoker =
				ServiceUtils.getService(IPrivilegedInvoker.class, SubjectAwareCallable.class);
		if (invoker == null) {
			return task;
		}
		Subject callerSubject = invoker.getCallerSubject();
		Subject runAsSubject = invoker.getRunAsSubject();
		return new SubjectAwareCallable<>(task, invoker, runAsSubject, callerSubject);
	}


	@Override
	public E call() throws Exception {
		return invoker.runAs(Invoker.of(delegate), subjects);
	}

}
