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

package io.tesler.model.core.entity;

import io.tesler.api.data.dictionary.LOV;
import io.tesler.model.core.api.security.IAccessorSupplier;
import io.tesler.model.core.entity.security.Accessor;
import io.tesler.model.core.entity.security.types.AccessorType;
import com.fasterxml.jackson.annotation.JsonIgnore;
import java.time.ZoneId;
import java.util.List;
import java.util.Optional;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.envers.Audited;
import org.hibernate.envers.NotAudited;

/**
 * User
 */
@Audited
@Entity
@Table(name = "users") // users, а не user, т.к. это служебное слово oracle
@Getter
@Setter
public class User extends BaseEntity implements IAccessorSupplier {

	@ManyToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
	@JoinTable(name = "PRJ_GRP_USER",
			joinColumns = {
					@JoinColumn(name = "USER_ID", nullable = false, updatable = false)},
			inverseJoinColumns = {
					@JoinColumn(name = "PRJ_GRP_ID", nullable = false, updatable = false)})
	List<ProjectGroup> projectGroup;

	private String login;

	private String firstName;   // имя

	private String lastName;    // фамилия

	private String patronymic;

	private String phone;

	private String email;

	private String fullUserName;

	private String title;

	@Column(name = "ext_attr_11")
	private String extensionAttribute11;

	@Column(name = "ext_attr_12")
	private String extensionAttribute12;

	@Column(name = "ext_attr_13")
	private String extensionAttribute13;

	@Column(name = "ext_attr_14")
	private String extensionAttribute14;

	@Column(name = "ext_attr_15")
	private String extensionAttribute15;

	@Column(name = "DN")
	private String dn;

	@NotAudited
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id", insertable = false, updatable = false)
	private UserDivisions userDivisions;

	private String origDeptCode;

	@ManyToOne
	@JoinColumn(name = "dept_id")
	private Department department;

	@JsonIgnore
	private String password;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "default_prj_id")
	private Project project;

	@Deprecated
	@Column(name = "internal_role_cd")
	private LOV internalRole;

	@NotAudited
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "user")
	private List<UserRole> userRoleList;

	private LOV timezone;

	private LOV locale;

	private String userPrincipalName;

	@Column(name = "active")
	private Boolean active;

	public String getFullName() {
		StringBuilder sB = new StringBuilder();
		if (lastName != null) {
			sB.append(lastName);
			if (firstName != null || patronymic != null) {
				sB.append(" ");
			}
		}
		if (firstName != null) {
			sB.append(firstName);
			if (patronymic != null) {
				sB.append(" ");
			}
		}
		if (patronymic != null) {
			sB.append(patronymic);
		}
		return sB.toString();
	}

	public String getUserNameInitials() {
		StringBuilder sB = new StringBuilder();
		if (lastName != null) {
			sB.append(lastName);
			if (firstName != null || patronymic != null) {
				sB.append(" ");
			}
		}
		if (firstName != null) {
			sB.append(StringUtils.left(firstName, 1).toUpperCase() + ".");
			if (patronymic != null) {
				sB.append(" ");
			}
		}
		if (patronymic != null) {
			sB.append(StringUtils.left(patronymic, 1).toUpperCase() + ".");
		}
		return sB.toString();
	}

	public String getUserNameInitialsWithoutSpace() {
		StringBuilder sB = new StringBuilder();
		if (lastName != null) {
			sB.append(lastName);
			if (firstName != null || patronymic != null) {
				sB.append(" ");
			}
		}
		if (firstName != null) {
			sB.append(StringUtils.left(firstName, 1).toUpperCase() + ".");
		}
		if (patronymic != null) {
			sB.append(StringUtils.left(patronymic, 1).toUpperCase() + ".");
		}
		return sB.toString();
	}

	public ZoneId getZoneId() {
		return Optional.ofNullable(getTimezone())
				.map(t -> ZoneId.of(t.getKey()))
				.orElseGet(ZoneId::systemDefault);
	}

	@Override
	public Accessor getAccessor() {
		return AccessorType.USER.toAccessor(getId());
	}

}
