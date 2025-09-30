package br.com.fiap.find_mottu.service;

import br.com.fiap.find_mottu.model.Usuario;
import br.com.fiap.find_mottu.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.stream.Collectors;

@Service
public class UsuarioDetailsService implements UserDetailsService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws ResponseStatusException {

        Usuario usuario = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Usuário não encontrado com o e-mail: " + email));

        return new User(usuario.getEmail(), usuario.getSenha(),
                usuario.getCargos().stream()
                        .map(cargo -> new SimpleGrantedAuthority(cargo.getNome().toString()))
                        .collect(Collectors.toList()));
    }
}
