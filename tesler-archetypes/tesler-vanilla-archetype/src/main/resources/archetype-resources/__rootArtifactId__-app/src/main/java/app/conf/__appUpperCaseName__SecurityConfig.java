package ${package}.app.conf;

import io.tesler.api.service.session.TeslerAuthenticationService;
import io.tesler.core.util.session.CustomBasicAuthenticationEntryPoint;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.core.annotation.Order;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.BeanIds;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.authentication.logout.HttpStatusReturningLogoutSuccessHandler;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;
import org.springframework.security.web.authentication.www.BasicAuthenticationEntryPoint;
import org.springframework.security.web.firewall.StrictHttpFirewall;


@RequiredArgsConstructor
@EnableWebSecurity
@Order(100)
public class ${appUpperCaseName}SecurityConfig extends WebSecurityConfigurerAdapter {

	private final TeslerAuthenticationService teslerAuthenticationService;

	private final ${appUpperCaseName}Configuration configuration;

	private final LogoutSuccessHandler logoutSuccessHandler;

	@Bean
	public StrictHttpFirewall httpFirewall() {
		StrictHttpFirewall strictHttpFirewall = new StrictHttpFirewall();
		strictHttpFirewall.setAllowUrlEncodedPercent(true);
		return strictHttpFirewall;
	}

	@Override
	protected void configure(HttpSecurity http) throws Exception {
		// @formatter:off
		http.csrf().disable();
		http.cors();
		http.authorizeRequests()
				.antMatchers("/rest/**").permitAll()
				.antMatchers("/css/**").permitAll()
				.antMatchers("/favicon.ico").permitAll()
				.antMatchers(String.format("%s/**", configuration.getUiPath())).permitAll()
				.antMatchers("/api/v1/files/**").permitAll()
				.antMatchers("/api/v1/bc-registry/**").permitAll()
				.antMatchers("/admin/**").hasRole("ADMIN")
				.antMatchers("/**").authenticated();
		http.logout()
				.invalidateHttpSession(true)
				.logoutUrl("/api/v1/logout")
				.logoutSuccessHandler(logoutSuccessHandler);
		http.headers().frameOptions().sameOrigin();
		http.httpBasic().authenticationEntryPoint(customBasicAuthenticationEntryPoint());
		// @formatter:on
	}

	@Override
	public void configure(AuthenticationManagerBuilder authenticationManagerBuilder) throws Exception {
		authenticationManagerBuilder.userDetailsService(teslerAuthenticationService);
	}

	@Bean(BeanIds.AUTHENTICATION_MANAGER)
	@Override
	public AuthenticationManager authenticationManagerBean() throws Exception {
		return super.authenticationManagerBean();
	}

	@Bean
	public BasicAuthenticationEntryPoint customBasicAuthenticationEntryPoint() {
		return new CustomBasicAuthenticationEntryPoint("CustomRealm");
	}

	@Bean
	@ConditionalOnMissingBean(LogoutSuccessHandler.class)
	public static LogoutSuccessHandler logoutSuccessHandler() {
		return new HttpStatusReturningLogoutSuccessHandler();
	}


}
