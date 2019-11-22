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

package io.tesler.core.diag;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Slf4j
@Component
@Aspect
public final class InvokeMethodProfiler extends AbstractProfiler {

	@Pointcut("@within(io.tesler.core.diag.ProfilerPointcut) || @annotation(io.tesler.core.diag.ProfilerPointcut)")
	public void annotatedByProfilerPointcut() {
	}

	@Pointcut("execution(* io.tesler.core.crudma.Crudma+.*(..))")
	public void crudmaMethods() {
	}

	@Around("annotatedByProfilerPointcut() || crudmaMethods()")
	public Object profile(ProceedingJoinPoint pjp) throws Throwable {
		return super.profile(pjp);
	}

}
