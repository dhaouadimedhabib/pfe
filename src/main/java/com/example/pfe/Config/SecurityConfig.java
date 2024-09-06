package com.example.pfe.Config;

import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;


@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .authorizeRequests()
                // Autoriser l'accès à certaines URL sans authentification
                .antMatchers("/api/auth/signin").permitAll()
                .antMatchers("/api/auth/signup").permitAll()
                .antMatchers("/api/auth/*").permitAll()
                .antMatchers("/api/RendezVous/*").permitAll()
                .antMatchers("/api/Professionnel/*").permitAll()
                .antMatchers("/api/reclamation/*").permitAll()
                .antMatchers("/api/Disponibilite/*").permitAll()
                .antMatchers("/api/user/*").permitAll()
                .antMatchers("/**").permitAll()
                .antMatchers("/api/paiement/payer/*").permitAll()
                .antMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                .antMatchers("/api/auth/google-signin").permitAll()

                // Toutes les autres requêtes nécessitent une authentification
                .anyRequest().authenticated()
                .and()
                .formLogin()
                .and()
                .httpBasic()
                .and()
                .logout()
                .logoutUrl("/api/auth/logout") // URL for logging out
                .logoutSuccessUrl("/login?logout") // Redirect after logout
                .invalidateHttpSession(true) // Invalidate HTTP session
                .deleteCookies("JSESSIONID") // Delete session cookies
                .permitAll()
                .and()
                .csrf().disable()
                .cors();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Override
    @Bean
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }
}





