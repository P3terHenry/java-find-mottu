package br.com.fiap.find_mottu.controller;

import br.com.fiap.find_mottu.model.Filial;
import br.com.fiap.find_mottu.model.Usuario;
import br.com.fiap.find_mottu.repository.FilialRepository;
import br.com.fiap.find_mottu.repository.MotoRepository;
import br.com.fiap.find_mottu.repository.UsuarioRepository;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Optional;

@Controller
public class WebFiliaisController {

    @Autowired
    private FilialRepository filialRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private MotoRepository motoRepository;

    @GetMapping("/filiais")
    public ModelAndView viewFiliais() {

        ModelAndView mv = new ModelAndView("/filiais/index");

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Optional<Usuario> op = usuarioRepository.findByEmail(auth.getName());

        if (op.isPresent()) {
            mv.addObject("user_logged", op.get());
        }

        mv.addObject("filiais", filialRepository.findAll());

        return mv;
    }

    @GetMapping("/filial/nova")
    public ModelAndView viewNovaFilial() {

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Optional<Usuario> op = usuarioRepository.findByEmail(auth.getName());

        ModelAndView mv = new ModelAndView("/filiais/nova");

        if (op.isPresent()) {
            mv.addObject("user_logged", op.get());
        }

        mv.addObject("filial", new Filial());

        return mv;
    }

    @PostMapping("/filial/salvar")
    public ModelAndView salvarFilial(@Valid Filial filial, BindingResult bindingResult) {

        // Se houver erros de validação, retornar para o formulário
        if (bindingResult.hasErrors()) {
            ModelAndView mv = new ModelAndView("/filiais/nova");

            // Adicionar o usuário logado
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            Optional<Usuario> op = usuarioRepository.findByEmail(auth.getName());
            if (op.isPresent()) {
                mv.addObject("user_logged", op.get());
            }

            // Log para debug
            System.out.println("Erros de validação encontrados:");
            bindingResult.getAllErrors().forEach(error -> System.out.println(error.getDefaultMessage()));

            return mv;
        }

        try {
            // Log para debug
            System.out.println("Salvando filial: " + filial.getEndereco());

            // Salvar filial
            Filial filialSalva = filialRepository.save(filial);

            System.out.println("Filial salva com ID: " + filialSalva.getId());

            return new ModelAndView("redirect:/filiais");

        } catch (Exception e) {
            // Em caso de erro, retornar para o formulário com mensagem de erro
            ModelAndView mv = new ModelAndView("/filiais/nova");

            // Adicionar o usuário logado
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            Optional<Usuario> op = usuarioRepository.findByEmail(auth.getName());
            if (op.isPresent()) {
                mv.addObject("user_logged", op.get());
            }

            // Adicionar mensagem de erro
            mv.addObject("erro", "Erro ao salvar filial: " + e.getMessage());

            return mv;
        }
    }

    @GetMapping("/filial/detalhes/{id}")
    public ModelAndView viewDetalhesFilial(@PathVariable("id") Long id) {

        Optional<Filial> optionalFilial = filialRepository.findById(id);

        if (optionalFilial.isPresent()) {

            ModelAndView mv = new ModelAndView("/filiais/detalhes");

            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            Optional<Usuario> optionalUsuario = usuarioRepository.findByEmail(auth.getName());

            if (optionalUsuario.isPresent()) {
                mv.addObject("user_logged", optionalUsuario.get());
            }

            mv.addObject("filial", optionalFilial.get());

            return mv;

        } else {
            return new ModelAndView("redirect:/filiais");
        }
    }

    @GetMapping("/filial/editar/{id}")
    public ModelAndView viewEditarFilial(@PathVariable("id") Long id) {

        Optional<Filial> optionalFilial = filialRepository.findById(id);

        if (optionalFilial.isPresent()) {

            ModelAndView mv = new ModelAndView("/filiais/editar");

            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            Optional<Usuario> optionalUsuario = usuarioRepository.findByEmail(auth.getName());

            if (optionalUsuario.isPresent()) {
                mv.addObject("user_logged", optionalUsuario.get());
            }

            mv.addObject("filial", optionalFilial.get());

            return mv;

        } else {
            return new ModelAndView("redirect:/filiais");
        }
    }

    @PostMapping("/filial/editar/{id}")
    public ModelAndView editarFilial(@PathVariable("id") Long id, @Valid Filial filial, BindingResult bindingResult) {

        // Buscar filial existente
        Optional<Filial> filialExistenteOpt = filialRepository.findById(id);
        if (!filialExistenteOpt.isPresent()) {
            return new ModelAndView("redirect:/filiais");
        }

        Filial filialExistente = filialExistenteOpt.get();

        // Se houver erros de validação, retornar para o formulário
        if (bindingResult.hasErrors()) {
            ModelAndView mv = new ModelAndView("/filiais/editar");

            // Adicionar o usuário logado
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            Optional<Usuario> op = usuarioRepository.findByEmail(auth.getName());
            if (op.isPresent()) {
                mv.addObject("user_logged", op.get());
            }

            // Re-adicionar os dados necessários para o formulário
            mv.addObject("filial", filialExistente); // Usar a filial existente para manter os dados originais

            // Log para debug
            System.out.println("Erros de validação encontrados:");
            bindingResult.getAllErrors().forEach(error -> System.out.println(error.getDefaultMessage()));

            return mv;
        }

        try {
            // Atualizar dados da filial existente
            filialExistente.setEndereco(filial.getEndereco());

            // Log para debug
            System.out.println("Editando filial ID: " + id);
            System.out.println("Endereço: " + filialExistente.getEndereco());

            // Salvar filial editada
            Filial filialSalva = filialRepository.save(filialExistente);

            System.out.println("Filial editada com sucesso: " + filialSalva.getId());

            return new ModelAndView("redirect:/filiais");

        } catch (Exception e) {
            // Em caso de erro, retornar para o formulário com mensagem de erro
            ModelAndView mv = new ModelAndView("/filiais/editar");

            // Adicionar o usuário logado
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            Optional<Usuario> op = usuarioRepository.findByEmail(auth.getName());
            if (op.isPresent()) {
                mv.addObject("user_logged", op.get());
            }

            // Re-adicionar os dados necessários para o formulário
            mv.addObject("filial", filialExistente);

            // Adicionar mensagem de erro
            mv.addObject("erro", "Erro ao editar filial: " + e.getMessage());

            System.out.println("Erro ao editar filial: " + e.getMessage());
            e.printStackTrace();

            return mv;
        }
    }

    @GetMapping("/filial/deletar/{id}")
    public ModelAndView excluirFilial(@PathVariable("id") Long id, RedirectAttributes redirectAttributes) {

        // Buscar filial existente
        Optional<Filial> filialOpt = filialRepository.findById(id);

        if (filialOpt.isPresent()) {
            try {
                // Verificar se há usuários associados à filial
                long usuariosCount = usuarioRepository.countByFilialId(id);

                // Verificar se há motos associadas à filial
                long motosCount = motoRepository.countByFilialId(id);

                if (usuariosCount > 0) {
                    redirectAttributes.addFlashAttribute("erro", "Não é possível excluir filial com usuários associados.");

                    return new ModelAndView("redirect:/filiais");
                } else if (motosCount > 0) {
                    redirectAttributes.addFlashAttribute("erro", "Não é possível excluir filial com motos associadas.");

                    return new ModelAndView("redirect:/filiais");
                }

                // Excluir a filial
                filialRepository.deleteById(id);

                redirectAttributes.addFlashAttribute("sucesso", "Filial excluída com sucesso.");

                return new ModelAndView("redirect:/filiais");

            } catch (Exception e) {
                redirectAttributes.addFlashAttribute("erro", "Erro ao deletar filial: " + e.getMessage());

                return new ModelAndView("redirect:/filiais");
            }
        } else {
            redirectAttributes.addFlashAttribute("erro", "Filial não encontrada para exclusão: ID " + id);
            return new ModelAndView("redirect:/filiais");
        }
    }
}
