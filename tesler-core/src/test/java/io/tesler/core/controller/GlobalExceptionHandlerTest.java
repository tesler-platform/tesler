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

package io.tesler.core.controller;

import static io.tesler.api.util.i18n.ErrorMessageSource.errorMessage;
import static org.mockito.Mockito.when;

import io.tesler.core.dto.ErrorResponseDTO;
import io.tesler.core.exception.BusinessException;
import io.tesler.core.exception.BusinessIntermediateException;
import io.tesler.core.exception.ClientException;
import io.tesler.core.exception.ExceptionHandlerSettings;
import io.tesler.core.exception.UnconfirmedException;
import io.tesler.core.exception.VersionMismatchException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.slf4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

class GlobalExceptionHandlerTest {

	@Mock
	ExceptionHandlerSettings settings;

	@Mock
	Logger log;

	@InjectMocks
	GlobalExceptionHandler globalExceptionHandler;

	@BeforeEach
	void setUp() {
		MockitoAnnotations.initMocks(this);
		when(settings.isTrackExceptions()).thenReturn(true);
		when(settings.isFullStackTraces()).thenReturn(true);
	}

	@Test
	void testException() {
		ErrorResponseDTO result = globalExceptionHandler.exception(new Exception("something is wrong"));
		Assertions.assertTrue(result.getErrorMessage().contains("something is wrong"));
	}

	@Test
	void testClientException() {
		ResponseEntity<String> result = globalExceptionHandler.clientException(new ClientException("message", null));
		Assertions.assertEquals(HttpStatus.BAD_REQUEST, result.getStatusCode());
	}

	@Test
	void testBusinessException() {
		ErrorResponseDTO result = globalExceptionHandler
				.businessException(new BusinessException());
		Assertions.assertFalse(result.isSuccess());
	}

	@Test
	void testBusinessIntermediateException() {
		ErrorResponseDTO result = globalExceptionHandler
				.businessIntermediateException("data", new BusinessIntermediateException());
		Assertions.assertFalse(result.isSuccess());
	}

	@Test
	void testVersionMismatchException() {
		ErrorResponseDTO result = globalExceptionHandler
				.versionMismatchException(new VersionMismatchException());
		Assertions.assertEquals(errorMessage("error.version_mismatch_simple"), result.getError().getPopup().get(0));
	}

	@Test
	void testUnconfirmedException() {
		ErrorResponseDTO result = globalExceptionHandler.unconfirmedException(new UnconfirmedException());
		Assertions.assertFalse(result.isSuccess());
	}

}
