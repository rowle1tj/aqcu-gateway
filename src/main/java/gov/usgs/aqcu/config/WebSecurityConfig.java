package gov.usgs.aqcu.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.security.oauth2.client.EnableOAuth2Sso;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.concurrent.ConcurrentTaskScheduler;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.oauth2.provider.authentication.OAuth2AuthenticationManager;
import org.springframework.security.oauth2.provider.authentication.OAuth2AuthenticationProcessingFilter;
import org.springframework.security.oauth2.provider.token.ResourceServerTokenServices;
import org.springframework.security.web.authentication.preauth.AbstractPreAuthenticatedProcessingFilter;

@Configuration
@EnableOAuth2Sso
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

	@Value("${security.oauth2.resource.id}")
	private String resourceId;

	@Autowired
	private ResourceServerTokenServices tokenServices;

	@Override
	protected void configure(HttpSecurity http) throws Exception {
		http
			.httpBasic().disable()
			.csrf().disable()
			.cors()
			.and()
			.authorizeRequests()
				.antMatchers(HttpMethod.OPTIONS, "/**").permitAll()
				.antMatchers("/info**", "/actuator/health/**", "/proxy.stream**", "/favicon.ico").permitAll()
				.anyRequest().fullyAuthenticated()
			.and()
				.logout().permitAll()
			.and()
				.addFilterAfter(oAuth2AuthenticationProcessingFilter(), AbstractPreAuthenticatedProcessingFilter.class)
		;
	}

	@Bean
	public TaskScheduler taskScheduler() {
		return new ConcurrentTaskScheduler();
	}

	private OAuth2AuthenticationProcessingFilter oAuth2AuthenticationProcessingFilter() {
		OAuth2AuthenticationProcessingFilter oAuth2AuthenticationProcessingFilter = new OAuth2AuthenticationProcessingFilter();
		oAuth2AuthenticationProcessingFilter.setAuthenticationManager(oauthAuthenticationManager());
		oAuth2AuthenticationProcessingFilter.setStateless(false);

		return oAuth2AuthenticationProcessingFilter;
	}

	private AuthenticationManager oauthAuthenticationManager() {
		OAuth2AuthenticationManager oauthAuthenticationManager = new OAuth2AuthenticationManager();

		oauthAuthenticationManager.setResourceId(resourceId);
		oauthAuthenticationManager.setTokenServices(tokenServices);
		oauthAuthenticationManager.setClientDetailsService(null);

		return oauthAuthenticationManager;
	}
}
