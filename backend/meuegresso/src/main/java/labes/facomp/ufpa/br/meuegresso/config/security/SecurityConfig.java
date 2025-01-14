package labes.facomp.ufpa.br.meuegresso.config.security;

import java.util.Arrays;
import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.oauth2.server.resource.OAuth2ResourceServerConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import labes.facomp.ufpa.br.meuegresso.config.properties.CorsProperties;
import lombok.RequiredArgsConstructor;

/**
 * Responsável pelas configurações de segurança da aplicação.
 *
 * @author Alfredo Gabriel
 * @since 26/03/2023
 * @version 1.0
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

	private final CorsProperties corsProperties;

	@Bean
	public SecurityFilterChain filterChain(HttpSecurity httpSecurity) throws Exception {

		return httpSecurity.csrf(AbstractHttpConfigurer::disable)
				.cors(cors -> cors.configurationSource(corsConfigurationSource()))
				.anonymous(AbstractHttpConfigurer::disable)
				.authorizeHttpRequests(authorize -> authorize
						.requestMatchers(
								"/auth/**",
								"/v3/api-docs/**",
								"/swagger-ui/**",
								"/swagger-ui.html",
								"/")
						.permitAll()
						.anyRequest().authenticated())
				.oauth2ResourceServer(OAuth2ResourceServerConfigurer::jwt)
				.sessionManagement(session -> session
						.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
				.exceptionHandling(exeption -> exeption
						.authenticationEntryPoint(
								new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED)))
				.build();

	}

	@Bean
	CorsConfigurationSource corsConfigurationSource() {
		CorsConfiguration configuration = new CorsConfiguration();
		configuration.setAllowedOriginPatterns(List.of(corsProperties.getAllowedOriginPatterns()));
		configuration.setAllowedHeaders(List.of("Authorization"));
		configuration.setAllowCredentials(true);
		configuration.setAllowedHeaders(Arrays.asList("Authorization", "Requestor-Type", "Content-Type",
				"Access-Control-Allow-Headers", "access-control-allow-origin"));
		configuration.setExposedHeaders(Arrays.asList("X-Get-Header", "Access-Control-Allow-Methods"));
		configuration.setAllowedMethods(
				Arrays.asList("POST", "GET", "PUT", "DELETE", "PATCH"));

		final UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
		source.registerCorsConfiguration("/**", configuration);
		return source;
	}

	@Bean
	public PasswordEncoder passwordEncoder() {
		return PasswordEncoderFactories.createDelegatingPasswordEncoder();
	}
}
