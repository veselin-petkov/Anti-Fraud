package antifraud.security;

import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@AllArgsConstructor
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {
    RestAuthenticationEntryPoint restAuthenticationEntryPoint;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.httpBasic()
                .authenticationEntryPoint(restAuthenticationEntryPoint) // Handles auth error
                .and()
                .csrf().disable().headers().frameOptions().disable().and() // for Postman, the H2 console
                .authorizeRequests() // manage access
                .mvcMatchers(HttpMethod.POST, "/api/auth/user").permitAll()
                .mvcMatchers(HttpMethod.POST, "/api/antifraud/transaction").hasRole("MERCHANT")
                .mvcMatchers("/actuator/shutdown").permitAll() // needs to run test
                // other matchers
                .mvcMatchers("/api/**").authenticated()
                //.anyRequest().denyAll()
                .and()
                .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS); // no session

        http.cors();
        return http.build();

    }
    @Bean @Lazy
    public PasswordEncoder getEncoder() {
        return new BCryptPasswordEncoder();
    }
}
