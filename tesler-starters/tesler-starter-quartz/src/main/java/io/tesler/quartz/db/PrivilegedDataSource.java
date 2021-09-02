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

package io.tesler.quartz.db;

import io.tesler.api.util.privileges.PrivilegeUtil;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Objects;
import java.util.function.Supplier;
import javax.sql.DataSource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.datasource.DelegatingDataSource;


@Slf4j
public class PrivilegedDataSource extends DelegatingDataSource {

	private final Supplier<Boolean> active;

	@SuppressWarnings("WeakerAccess")
	public PrivilegedDataSource(DataSource targetDataSource) {
		this(targetDataSource, () -> true);
	}

	@SuppressWarnings("WeakerAccess")
	public PrivilegedDataSource(DataSource targetDataSource, Supplier<Boolean> active) {
		super(targetDataSource);
		this.active = Objects.requireNonNull(active);
	}

	@Override
	public Connection getConnection() throws SQLException {
		try {
			if (!isActive()) {
				return super.getConnection();
			}
			return PrivilegeUtil.runPrivileged(PrivilegedDataSource.super::getConnection);
		} catch (SQLException ex) {
			log.error(ex.getLocalizedMessage(), ex);
			throw ex;
		}
	}

	@Override
	public Connection getConnection(String username, String password) throws SQLException {
		try {
			if (!isActive()) {
				return super.getConnection(username, password);
			}
			return PrivilegeUtil.runPrivileged(() -> PrivilegedDataSource.super.getConnection(username, password));
		} catch (SQLException ex) {
			log.error(ex.getLocalizedMessage(), ex);
			throw ex;
		}
	}

	@SuppressWarnings("BooleanMethodIsAlwaysInverted")
	private boolean isActive() {
		return active.get();
	}

}
