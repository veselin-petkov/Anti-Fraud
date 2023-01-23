package antifraud.security;

import antifraud.service.MyUserDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class SecurityConfig extends WebSecurityConfigurerAdapter {
    @Autowired
    RestAuthenticationEntryPoint restAuthenticationEntryPoint;

    @Autowired
    MyUserDetailsService myUserDetailsService;

    public void configure(HttpSecurity http) throws Exception {
        http.httpBasic()
                .authenticationEntryPoint(restAuthenticationEntryPoint) // Handles auth error
                .and()
                .csrf().disable().headers().frameOptions().disable().and() // for Postman, the H2 console
                .authorizeRequests() // manage access
                .antMatchers(HttpMethod.POST, "/api/auth/user").permitAll()
                .antMatchers(HttpMethod.POST, "/api/antifraud/**").hasRole("MERCHANT")
                .antMatchers("/actuator/shutdown").permitAll() // needs to run test
                .antMatchers("/api/auth/access/**").hasRole("ADMINISTRATOR")
                .antMatchers("/api/auth/role/**").hasRole("ADMINISTRATOR")
                .antMatchers("/api/auth/user/**").hasRole("ADMINISTRATOR")
                .antMatchers("/api/auth/list").hasAnyRole("ADMINISTRATOR","SUPPORT")
                // other matchers
                //.antMatchers("/h2/***").permitAll()
                .and()
                .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS); // no session
    }
    @Bean
    public PasswordEncoder getEncoder() {
        return new BCryptPasswordEncoder();
    }
}
