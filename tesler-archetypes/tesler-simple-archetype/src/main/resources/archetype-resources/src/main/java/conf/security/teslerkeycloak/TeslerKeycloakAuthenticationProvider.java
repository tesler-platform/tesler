#set( $symbol_pound = '#' )
#set( $symbol_dollar = '$' )
#set( $symbol_escape = '\' )
package ${package}.conf.security.teslerkeycloak;

import io.tesler.api.data.dictionary.LOV;
import io.tesler.api.service.session.InternalAuthorizationService;
import io.tesler.api.service.tx.TransactionService;
import io.tesler.core.service.impl.UserRoleService;
import io.tesler.core.util.SQLExceptions;
import io.tesler.model.core.dao.JpaDao;
import io.tesler.model.core.entity.*;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.LockOptions;
import org.keycloak.adapters.springsecurity.account.SimpleKeycloakAccount;
import org.keycloak.adapters.springsecurity.authentication.KeycloakAuthenticationProvider;
import org.keycloak.adapters.springsecurity.token.KeycloakAuthenticationToken;
import org.keycloak.representations.AccessToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Set;

import static io.tesler.api.service.session.InternalAuthorizationService.VANILLA;

@Component
@Slf4j
public class TeslerKeycloakAuthenticationProvider extends KeycloakAuthenticationProvider {

	@Autowired
	private JpaDao jpaDao;

	@Autowired
	private TransactionService txService;

	@Autowired
	private InternalAuthorizationService authzService;

	@Autowired
	private UserRoleService userRoleService;

	@Override
	public Authentication authenticate(Authentication authentication) throws AuthenticationException {
		Authentication auth = super.authenticate(authentication);
		KeycloakAuthenticationToken accessToken = (KeycloakAuthenticationToken) auth;
		SimpleKeycloakAccount account = (SimpleKeycloakAccount) accessToken.getDetails();

		txService.invokeInTx(() -> {
			upsertUserAndRoles(
					account.getKeycloakSecurityContext().getToken(),
					accessToken.getAccount().getRoles());
			return null;
		});

		return authentication;
	}

	//TODO>>taken "as is" from real project - refactor
	private void upsertUserAndRoles(AccessToken accessToken, Set<String> roles) {
		txService.invokeInNewTx(() -> {
			authzService.loginAs(authzService.createAuthentication(VANILLA));
			User user = null;
			try {
				user = getUserByLogin(accessToken.getName().toUpperCase());
				if (user == null) {
					upsert(accessToken, roles.stream().findFirst().orElse(null));
				}
				user = getUserByLogin(accessToken.getName().toUpperCase());
				userRoleService.upsertUserRoles(user.getId(), new ArrayList<>(roles));
			} catch (Exception e) {
				log.error(e.getLocalizedMessage(), e);
			}

			if (user == null) {
				throw new UsernameNotFoundException(null);
			}
			SecurityContextHolder.getContext().setAuthentication(null);
			return null;
		});
	}

	//TODO>>taken "as is" from real project - refactor
	public User upsert(AccessToken accessToken, String role) {
		txService.invokeInNewTx(() -> {
					authzService.loginAs(authzService.createAuthentication(VANILLA));
					for (int i = 1; i <= 10; i++) {
						User existing = getUserByLogin(accessToken.getName().toUpperCase());
						if (existing != null) {
							jpaDao.lockAndRefresh(existing, LockOptions.WAIT_FOREVER);
							updateUser(accessToken, role, existing);
							return existing;
						}
						try {
							User newUser = new User();
							updateUser(accessToken, role, newUser);
							Long id = txService.invokeNoTx(() -> jpaDao.save(newUser));
							return jpaDao.findById(User.class, id);
						} catch (Exception ex) {
							if (SQLExceptions.isUniqueConstraintViolation(ex)) {
								log.error(ex.getLocalizedMessage(), ex);
							} else {
								throw ex;
							}
						}
					}
					SecurityContextHolder.getContext().setAuthentication(null);
					return null;
				}
		);
		return null;
	}

	private User getUserByLogin(String login) {
		return jpaDao.getSingleResultOrNull(
				User.class,
				(root, cq, cb) -> cb.equal(cb.upper(root.get(User_.login)), login.toUpperCase())
		);
	}

	private void updateUser(AccessToken accessToken, String role, User user) {
		if (user.getLogin() == null) {
			user.setLogin(accessToken.getName().toUpperCase());
		}
		if (user.getProject() == null) {
			user.setProject(jpaDao.findById(Project.class, 1L));
		}
		user.setInternalRole(new LOV(role));
		user.setUserPrincipalName(accessToken.getName());
		user.setFirstName(accessToken.getGivenName());
		user.setLastName(accessToken.getFamilyName());
		user.setTitle(accessToken.getName());
		user.setFullUserName(accessToken.getName());
		user.setEmail(accessToken.getEmail());
		user.setPhone(accessToken.getPhoneNumber());
		user.setActive(true);
		user.setDepartment(jpaDao.findById(Department.class, 0L));
	}
}
