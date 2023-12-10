package com.merantory.dostavim.security;

import com.merantory.dostavim.service.PersonService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig {
    private final PersonService personService;
    private final JwtFilter jwtFilter;

    @Autowired
    public SecurityConfig(PersonService personService, JwtFilter jwtFilter) {
        this.personService = personService;
        this.jwtFilter = jwtFilter;
    }

    @Bean
    public AuthenticationManager authenticationManager(HttpSecurity http) throws Exception {
        AuthenticationManagerBuilder authenticationManagerBuilder = http.getSharedObject(
                AuthenticationManagerBuilder.class);
        authenticationManagerBuilder.userDetailsService(personService);

        return authenticationManagerBuilder.build();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.csrf((csrf) -> csrf.disable())
                .authorizeHttpRequests((authorize) -> authorize
                        .requestMatchers(HttpMethod.GET, "/orders/all").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PATCH, "/restaurants/{id}/edit", "/products/{id}/edit").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/restaurants/{id}", "/products/{id}").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.POST, "/categories", "/restaurants", "/restaurants/add_product", "/products").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PATCH, "/users/update_info").authenticated()
                        .requestMatchers(HttpMethod.GET, "/orders", "/orders/{id}").authenticated()
                        .requestMatchers(HttpMethod.POST, "/orders").authenticated()
                        .requestMatchers("/sign/up", "/sign/in").anonymous()
                        .anyRequest().permitAll()
                )
                .authenticationManager(authenticationManager(http))
                .httpBasic(Customizer.withDefaults())
                .sessionManagement((sessionMan) -> sessionMan.sessionCreationPolicy(SessionCreationPolicy.STATELESS));
        http.addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }

}