package antifraud.security;

import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import org.springframework.context.annotation.Lazy;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@AllArgsConstructor
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
                .antMatchers("/api/auth/access/**").hasRole("ADMINISTRATOR")
                .antMatchers("/api/auth/role/**").hasRole("ADMINISTRATOR")
                .antMatchers("/api/auth/user/**").hasRole("ADMINISTRATOR")
                .antMatchers("/api/auth/list").hasAnyRole("ADMINISTRATOR","SUPPORT")
                .antMatchers("/api/antifraud/suspicious-ip/**").hasRole("SUPPORT")
                .antMatchers("/api/antifraud/stolencard/**").hasRole("SUPPORT")
                .antMatchers("/api/antifraud/suspicious-ip").hasRole("SUPPORT")
                .antMatchers("/api/antifraud/stolencard").hasRole("SUPPORT")
//                .mvcMatchers("/api/**").authenticated()
//                .anyRequest().denyAll()
                .and()
                .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS); // no session
        return http.build();

    }
    @Bean @Lazy
    public PasswordEncoder getEncoder() {
        return new BCryptPasswordEncoder();
    }
}
