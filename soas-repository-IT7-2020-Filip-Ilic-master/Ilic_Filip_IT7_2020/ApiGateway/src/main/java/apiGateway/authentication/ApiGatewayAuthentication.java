package apiGateway.authentication;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.core.userdetails.MapReactiveUserDetailsService;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.client.WebClient;

import api.dtos.UserDto;
import api.feignProxies.UsersServiceProxy;

@Configuration
@EnableWebFluxSecurity
public class ApiGatewayAuthentication {
/*
	@Autowired
	private UsersServiceProxy proxy;*/
	    
	@Bean
	SecurityWebFilterChain filterChain(ServerHttpSecurity http) {
		http.
		csrf(csrf -> csrf.disable())
		.authorizeExchange(exchange -> exchange.pathMatchers("/currency-exchange").permitAll()
				.pathMatchers("/crypto-exchange").permitAll()
				.pathMatchers("/currency-conversion/**").hasRole("USER")
				.pathMatchers("/crypto-conversion/**").hasRole("USER")
				.pathMatchers("/trade-service/**").hasRole("USER")
				.pathMatchers(HttpMethod.GET,"/bank-account/**").hasAnyRole("ADMIN", "USER")
				.pathMatchers("/bank-account/**").hasRole("ADMIN")
				.pathMatchers(HttpMethod.GET,"/crypto-wallet/**").hasAnyRole("ADMIN", "USER")
				.pathMatchers("/crypto-wallet/**").hasRole("ADMIN")
				.pathMatchers(HttpMethod.POST, "/users/newUser").hasAnyRole("ADMIN", "OWNER")
				.pathMatchers(HttpMethod.PUT, "/users/editUser").hasAnyRole("ADMIN", "OWNER")
				.pathMatchers("/users/**").hasRole("OWNER"))
				.httpBasic(Customizer.withDefaults());
		
		return http.build();
	}
	
	
	@Bean
	MapReactiveUserDetailsService userDetailsService(BCryptPasswordEncoder encoder) {
		
		// Obratiti paznju na URL prilikom dokerizacije
		// Van dokera vrednost URL je localhost:8770/users
		// U dokeru vrednost URL mora biti users-service:8770/users
		ResponseEntity<List<UserDto>> response = new RestTemplate()
				.exchange("http://localhost:8770/users", HttpMethod.GET, null,
						new ParameterizedTypeReference<List<UserDto>> () {});
		
		List<UserDetails> users = new ArrayList<UserDetails>();
		for(UserDto dto: response.getBody()) {
			users.add(
					User.withUsername(dto.getEmail())
					.password(encoder.encode(dto.getPassword()))
					.roles(dto.getRole())
					.build()
					);
		}
		
		return new MapReactiveUserDetailsService(users);
	}
	
	@Bean
	BCryptPasswordEncoder getEncoder() {
		return new BCryptPasswordEncoder();
	}
}
