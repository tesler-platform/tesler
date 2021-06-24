#set( $symbol_pound = '#' )
#set( $symbol_dollar = '$' )
#set( $symbol_escape = '\' )
package ${package}.conf.security.teslerkeycloak;

import io.tesler.api.service.session.TeslerUserDetailsInterface;
import io.tesler.api.service.tx.TransactionService;
import io.tesler.core.util.session.CoreSessionServiceImpl;
import io.tesler.model.core.dao.JpaDao;
import io.tesler.model.core.entity.User;
import io.tesler.model.core.entity.User_;
import org.keycloak.adapters.springsecurity.account.SimpleKeycloakAccount;
import org.keycloak.adapters.springsecurity.token.KeycloakAuthenticationToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

@Primary
@Component
public class TeslerKeycloakCoreSessionService extends CoreSessionServiceImpl {

	@Autowired
	private JpaDao jpaDao;

	@Autowired
	private TransactionService txService;

	@Override
	public TeslerUserDetailsInterface getAuthenticationDetails(Authentication auth) {
		if (auth == null) {
			return null;
		} else if (auth instanceof KeycloakAuthenticationToken) {
			KeycloakAuthenticationToken token = (KeycloakAuthenticationToken) auth;
			KeycloakAuthenticationToken accessToken = (KeycloakAuthenticationToken) auth;
			SimpleKeycloakAccount account = (SimpleKeycloakAccount) accessToken.getDetails();
			TeslerKeycloakAccount details = mapTokenToTeslerDetails(token, accessToken, account);
			token.setDetails(details);
			return details;
		} else {
			return super.getAuthenticationDetails(auth);
		}
	}

	private TeslerKeycloakAccount mapTokenToTeslerDetails(KeycloakAuthenticationToken token, KeycloakAuthenticationToken accessToken, SimpleKeycloakAccount account) {
		TeslerKeycloakAccount details = new TeslerKeycloakAccount(
				account.getPrincipal(),
				accessToken.getAccount().getRoles(),
				account.getKeycloakSecurityContext());

		txService.invokeInTx(() -> {
			User user = getUserByLogin(token.getAccount().getKeycloakSecurityContext().getToken().getName().toUpperCase());
			details.setId(user.getId());
			details.setUsername(user.getLogin());
			details.setUserRole(user.getInternalRole());
			details.setLocaleCd(user.getLocale());
			details.setAuthorities(user.getUserRoleList()
					.stream()
					.map(r -> (GrantedAuthority) () -> r.getInternalRoleCd().getKey())
					.collect(Collectors.toSet()));
			details.setTimezone(user.getTimezone());
			return null;
		});
		return details;
	}

	private User getUserByLogin(String login) {
		return jpaDao.getSingleResultOrNull(
				User.class,
				(root, cq, cb) -> cb.equal(cb.upper(root.get(User_.login)), login.toUpperCase())
		);
	}

}
