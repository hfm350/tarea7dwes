package com.hfm350.tarea3dweshfm350.seguridad;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.session.HttpSessionEventPublisher;
import org.springframework.stereotype.Component;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.session.HttpSessionEventPublisher;

import jakarta.servlet.http.HttpSessionEvent;
import jakarta.servlet.http.HttpSessionListener;

import java.util.List;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final CredencialUserDetailsService userDetailsService;
    private final AuthenticationSuccessHandler successHandler;
    private final CustomLogoutSuccessHandler logoutSuccessHandler;


    public SecurityConfig(CredencialUserDetailsService userDetailsService, AuthenticationSuccessHandler successHandler, CustomLogoutSuccessHandler logoutSuccessHandler) {
        this.userDetailsService = userDetailsService;
        this.successHandler = successHandler;
        this.logoutSuccessHandler = logoutSuccessHandler;
    }

    @Bean
    public AuthenticationManager authenticationManager() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder());
        return new ProviderManager(List.of(authProvider));
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }



    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http, ClienteSessionFilter clienteSessionFilter) throws Exception {
        http
            .csrf(csrf -> csrf.disable()) 
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/", "/inicioSesion", "/registroCliente").permitAll()
                .requestMatchers("/images/**", "/css/**", "/vivero.png").permitAll()
                .requestMatchers("/menuAdmin").hasAuthority("ROLE_ADMIN")
                .requestMatchers("/menuCliente", "/carrito", "/misPedidos").hasAuthority("ROLE_CLIENTE")
                .requestMatchers("/menuPersonal").hasAuthority("ROLE_PERSONAL")
                .anyRequest().authenticated()
            )
            .formLogin(form -> form
                .loginPage("/inicioSesion") 
                .loginProcessingUrl("/login") 
                .successHandler(successHandler)
                .failureUrl("/inicioSesion?error=true")
                .permitAll()
            )
            .logout(logout -> logout
                .logoutUrl("/logout")
                .logoutSuccessUrl("/inicioSesion")
                .logoutSuccessHandler(logoutSuccessHandler)
                .invalidateHttpSession(true)
                .deleteCookies("JSESSIONID")
                .permitAll()
            )
            .sessionManagement(session -> session
                .sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED)
                .maximumSessions(1) // Evita sesiones múltiples por usuario
                .expiredUrl("/inicioSesion?error=sessionExpired") // Redirigir si la sesión expira
            )
            .securityContext(securityContext -> securityContext
                .requireExplicitSave(true) 
            )
            .addFilterBefore(clienteSessionFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
    
    @Component
    public class SessionCleanupListener implements HttpSessionListener {
        @Override
        public void sessionDestroyed(HttpSessionEvent event) {
            event.getSession().removeAttribute("carritoPedidos");
        }
    }
    
    @Bean
    public HttpSessionEventPublisher httpSessionEventPublisher() {
        return new HttpSessionEventPublisher();
    }
}
