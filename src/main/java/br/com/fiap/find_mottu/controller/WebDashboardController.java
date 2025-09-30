package br.com.fiap.find_mottu.controller;

import br.com.fiap.find_mottu.model.Usuario;
import br.com.fiap.find_mottu.repository.FilialRepository;
import br.com.fiap.find_mottu.repository.MotoRepository;
import br.com.fiap.find_mottu.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.ModelAndView;

import java.util.Optional;

@Controller
public class WebDashboardController {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private MotoRepository motoRepository;

    @Autowired
    private FilialRepository filialRepository;


    @GetMapping("/dashboard")
    public ModelAndView viewIndex() {

        ModelAndView mv = new ModelAndView("/dashboard/index");

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        Optional<Usuario> op = usuarioRepository.findByEmail(auth.getName());

        if (op.isPresent()) {
            mv.addObject("user_logged", op.get());
        }

        mv.addObject("total_motos", motoRepository.count());

        mv.addObject("total_filiais", filialRepository.count());

        mv.addObject("total_usuarios", usuarioRepository.count());

        return mv;
    }

}
