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
import javax.security.auth.Subject;


public class SubjectAwareRunnable implements Runnable {

	private final Runnable delegate;

	private final IPrivilegedInvoker<Void, RuntimeException> invoker;

	private final Subject[] subjects;

	private SubjectAwareRunnable(Runnable command, IPrivilegedInvoker<Void, RuntimeException> invoker,
			Subject... subjects) {
		this.delegate = Objects.requireNonNull(command);
		this.invoker = Objects.requireNonNull(invoker);
		this.subjects = Objects.requireNonNull(subjects);
	}

	@SuppressWarnings("unchecked")
	public static Runnable wrap(Runnable command) throws GeneralSecurityException {
		IPrivilegedInvoker<Void, RuntimeException> invoker =
				ServiceUtils.getService(IPrivilegedInvoker.class, SubjectAwareRunnable.class);
		if (invoker == null) {
			return command;
		}
		Subject callerSubject = invoker.getCallerSubject();
		Subject runAsSubject = invoker.getRunAsSubject();
		return new SubjectAwareRunnable(command, invoker, runAsSubject, callerSubject);
	}


	@Override
	public void run() {
		invoker.runAs(Invoker.of(delegate), subjects);
	}

}
