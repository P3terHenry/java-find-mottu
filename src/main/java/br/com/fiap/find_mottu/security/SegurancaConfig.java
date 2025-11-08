package br.com.fiap.find_mottu.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
public class SegurancaConfig {

    @Autowired
    private JWTAuthFilter jwtAuthFilter;

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain chain(HttpSecurity http) throws Exception {

        http.csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(request ->
                        request
                                // === ROTAS COMPLETAMENTE PÚBLICAS (SEM AUTENTICAÇÃO) ===
                                .requestMatchers(
                                        // Swagger/OpenAPI
                                        "/swagger-ui/**",
                                        "/v3/api-docs/**",
                                        "/v3/api-docs",
                                        "/swagger-resources/**",
                                        "/configuration/**",
                                        "/webjars/**",
                                        "swagger-ui.html",

                                        // Recursos estáticos
                                        "/css/**",
                                        "/js/**",
                                        "/images/**",
                                        "/favicon.ico",

                                        // Páginas de erro e utilitários
                                        "/error/**",
                                        "/h2-console/**",

                                        // Autenticação Web
                                        "/login",      // Página de login web
                                        "/login?**",   // Login com parâmetros

                                        // === ROTAS DE AUTENTICAÇÃO DA API (PÚBLICAS) ===
                                        "/api/autenticacao/**",  // Login e validação de token
                                        "/api/auth/**",          // Outras rotas de auth
                                        "/api/arquivos/**"       // Upload de arquivos (público)
                                ).permitAll()

                                // === ROTAS DA API (PROTEGIDAS POR JWT) ===
                                .requestMatchers("/api/**").authenticated() // Outras APIs precisam de JWT

                                // === ROTAS WEB ESPECÍFICAS (PROTEGIDAS POR SESSION) ===
                                .requestMatchers("/dashboard/**", "/index").authenticated() // Dashboard web

                                // === ROTAS COM AUTORIZAÇÃO ESPECÍFICA ===
                                .requestMatchers(
                                        "/usuario/novo",
                                        "/usuario/editar/**",
                                        "/usuario/deletar/**",
                                        "/filial/nova",
                                        "/filial/editar/**",
                                        "/filial/deletar/**")
                                .hasAuthority("ADMIN")

                                // === QUALQUER OUTRA ROTA ===
                                .anyRequest().authenticated()
                )

                // === CONFIGURAÇÃO DE LOGIN WEB (FORM-BASED) ===
                .formLogin(login -> login
                        .loginPage("/login")
                        .defaultSuccessUrl("/dashboard", true)  // Redireciona para dashboard após login
                        .failureUrl("/login?error=true")
                        .permitAll()
                )

                // === CONFIGURAÇÃO DE LOGOUT ===
                .logout(logout -> logout
                        .logoutUrl("/logout")
                        .logoutSuccessUrl("/login?logout=true")
                        .invalidateHttpSession(true)
                        .deleteCookies("JSESSIONID")
                        .permitAll()
                )

                // === CONFIGURAÇÃO DE ERRO DE ACESSO NEGADO ===
                .exceptionHandling((exception) ->
                        exception.accessDeniedHandler((request, response, AccessDeniedException)
                                -> {response.sendRedirect("/forbidden");}) )

                // === ADICIONA O FILTRO JWT APENAS PARA ROTAS /api/** ===
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public PasswordEncoder encoder() {
        return new BCryptPasswordEncoder();
    }

}
