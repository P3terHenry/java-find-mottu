package br.com.fiap.find_mottu.security;

import br.com.fiap.find_mottu.model.Usuario;
import br.com.fiap.find_mottu.repository.UsuarioRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;

@Component
public class JWTAuthFilter extends OncePerRequestFilter {

    @Autowired
    private JWTUtil jwtUtil;

    @Autowired
    private UserDetailsService userDetailsService;

    @Autowired
    private UsuarioRepository usuarioRepository;

    // === ROTAS QUE NUNCA DEVEM PASSAR POR JWT ===
    private static final List<String> EXCLUDED_PATHS = List.of(
            // Swagger/OpenAPI - NUNCA aplicar JWT
            "/swagger-ui/", "/v3/api-docs", "/swagger-resources", "/webjars", "/configuration",

            // Recursos estáticos - NUNCA aplicar JWT
            "/css/", "/js/", "/images/", "/favicon.ico",

            // Páginas web/ModelAndView - NUNCA aplicar JWT (usam session)
            "/login", "/logout", "/dashboard", "/index", "/error",

            // Utilitários
            "/h2-console",

            // Autenticação pública
            "/auth/",

            // Uploads e arquivos públicos
            "/api/arquivos/upload",
            "/api/arquivos/"
    );

    // === ROTAS QUE DEVEM USAR JWT (APENAS APIs) ===
    private boolean shouldApplyJwtFilter(String path) {
        // REGRA 1: Se não é uma rota de API, NÃO aplicar JWT
        if (!path.startsWith("/api/")) {
            return false;
        }

        // REGRA 2: Rotas de autenticação da API são PÚBLICAS (geram tokens)
        if (path.startsWith("/api/autenticacao/")) {
            return false;
        }

        // REGRA 3: Rotas públicas de arquivos
        if (path.startsWith("/api/arquivos/")) {
            return false;
        }

        // REGRA 4: Verificar se está na lista de exclusões gerais
        return EXCLUDED_PATHS.stream().noneMatch(path::startsWith);
    }

    private void escreverErro(HttpServletResponse response, String mensagem, int statusCode, String path) throws IOException {
        response.setStatus(statusCode);
        response.setContentType("application/json;charset=UTF-8");
        response.getWriter().write(String.format("""
                {
                    "timestamp": "%s",
                    "status": %d,
                    "message": "%s",
                    "path": "%s"
                }
                """, LocalDateTime.now(), statusCode, mensagem, path));
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        String path = request.getRequestURI();

        // === DECISÃO PRINCIPAL: APLICAR JWT OU NÃO ===
        if (!shouldApplyJwtFilter(path)) {
            // Para rotas não-API (web/swagger/recursos), pular JWT e deixar Spring Security handle
            filterChain.doFilter(request, response);
            return;
        }

        // === A PARTIR DAQUI: APENAS ROTAS /api/** ===
        String token = request.getHeader("Authorization");

        // Para APIs, token JWT é obrigatório
        if (token == null || !token.startsWith("Bearer ")) {
            escreverErro(response, "Token JWT obrigatório para APIs. Use: Authorization: Bearer <token>",
                    HttpStatus.UNAUTHORIZED.value(), path);
            return;
        }

        try {
            token = token.substring(7); // Remove "Bearer "
            Long usuarioId = jwtUtil.extrairUsuariobyId(token);

            if (usuarioId != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                Usuario usuario = usuarioRepository.findById(usuarioId).orElse(null);

                if (usuario == null) {
                    escreverErro(response, "Usuário do token não encontrado.", HttpStatus.FORBIDDEN.value(), path);
                    return;
                }

                UserDetails userDetails = userDetailsService.loadUserByUsername(usuario.getEmail());

                if (jwtUtil.validarToken(token)) {
                    Authentication auth = new UsernamePasswordAuthenticationToken(
                            userDetails,
                            null,
                            userDetails.getAuthorities()
                    );
                    SecurityContextHolder.getContext().setAuthentication(auth);
                } else {
                    escreverErro(response, "Token JWT inválido ou expirado.", HttpStatus.FORBIDDEN.value(), path);
                    return;
                }
            }
        } catch (Exception e) {
            escreverErro(response, "Erro ao processar token JWT: " + e.getMessage(),
                    HttpStatus.FORBIDDEN.value(), path);
            return;
        }

        filterChain.doFilter(request, response);
    }
}
