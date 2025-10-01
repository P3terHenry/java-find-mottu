package br.com.fiap.find_mottu.controller;

import br.com.fiap.find_mottu.model.Cargo;
import br.com.fiap.find_mottu.model.EnumCargo;
import br.com.fiap.find_mottu.model.Usuario;
import br.com.fiap.find_mottu.repository.CargoRepository;
import br.com.fiap.find_mottu.repository.FilialRepository;
import br.com.fiap.find_mottu.repository.UsuarioRepository;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

@Controller
public class WebUsuariosController {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private FilialRepository filialRepository;

    @Autowired
    private CargoRepository cargoRepository;

    @Autowired
    private PasswordEncoder encoder;

    @GetMapping("/usuarios")
    public ModelAndView viewUsuarios() {

        ModelAndView mv = new ModelAndView("usuarios/index");

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        Optional<Usuario> op = usuarioRepository.findByEmail(auth.getName());

        if (op.isPresent()) {
            mv.addObject("user_logged", op.get());
        }

        mv.addObject("usuarios", usuarioRepository.findAll());

        return mv;
    }

    @GetMapping("/usuario/novo")
    public ModelAndView viewNovoUsuario() {

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        Optional<Usuario> op = usuarioRepository.findByEmail(auth.getName());

        ModelAndView mv = new ModelAndView("usuarios/novo");

        if (op.isPresent()) {
            mv.addObject("user_logged", op.get());
        }
            mv.addObject("usuario", new Usuario());
            mv.addObject("cargos", EnumCargo.values());
            mv.addObject("filiais", filialRepository.findAll());

        return mv;
    }

    @PostMapping("/usuario/salvar")
    public ModelAndView salvarUsuario(@Valid Usuario usuario, BindingResult bindingResult, @RequestParam(name = "cargo", required = false) String cargoSelecionado) {

        // Validar se cargo foi selecionado (já que não está no @Valid)
        if (cargoSelecionado == null || cargoSelecionado.trim().isEmpty()) {
            bindingResult.rejectValue("cargos", "cargo.required", "É obrigatório selecionar um cargo");
        }

        // Validar tamanho da senha ANTES de criptografar
        if (usuario.getSenha() != null && (usuario.getSenha().length() < 6 || usuario.getSenha().length() > 16)) {
            bindingResult.rejectValue("senha", "senha.size", "A senha deve ter entre 6 e 16 caracteres");
        }

        // Se houver erros de validação, retornar para o formulário
        if (bindingResult.hasErrors()) {
            ModelAndView mv = new ModelAndView("usuarios/novo");

            // Adicionar o usuário logado
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            Optional<Usuario> op = usuarioRepository.findByEmail(auth.getName());
            if (op.isPresent()) {
                mv.addObject("user_logged", op.get());
            }

            // Re-adicionar os dados necessários para o formulário
            mv.addObject("cargos", EnumCargo.values());
            mv.addObject("filiais", filialRepository.findAll());

            // Log para debug
            System.out.println("Erros de validação encontrados:");
            bindingResult.getAllErrors().forEach(error -> System.out.println(error.getDefaultMessage()));

            return mv;
        }

        try {
            // Processar cargo selecionado ANTES de validar
            Set<Cargo> cargos = new HashSet<>();

            if (cargoSelecionado != null && !cargoSelecionado.isEmpty()) {
                // Converter string para enum
                EnumCargo enumCargo = EnumCargo.valueOf(cargoSelecionado);

                // Buscar ou criar cargo
                Optional<Cargo> cargoOpt = cargoRepository.findByNome(enumCargo);
                if (cargoOpt.isPresent()) {
                    cargos.add(cargoOpt.get());
                } else {
                    // Se o cargo não existir, criar um novo
                    Cargo novoCargo = new Cargo();
                    novoCargo.setNome(enumCargo);
                    Cargo cargoSalvo = cargoRepository.save(novoCargo);
                    cargos.add(cargoSalvo);
                }
            }

            // Definir cargos no usuário
            usuario.setCargos(cargos);

            // Criptografar a senha (APÓS validações)
            usuario.setSenha(encoder.encode(usuario.getSenha()));

            // Salvar usuário
            Usuario usuarioSalvo = usuarioRepository.save(usuario);

            System.out.println("Usuário salvo com ID: " + usuarioSalvo.getId());

            return new ModelAndView("redirect:/usuarios");

        } catch (Exception e) {

            // Em caso de erro, retornar para o formulário com mensagem de erro
            ModelAndView mv = new ModelAndView("usuarios/novo");

            // Adicionar o usuário logado
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            Optional<Usuario> op = usuarioRepository.findByEmail(auth.getName());
            if (op.isPresent()) {
                mv.addObject("user_logged", op.get());
            }

            // Re-adicionar os dados necessários para o formulário
            mv.addObject("cargos", EnumCargo.values());
            mv.addObject("filiais", filialRepository.findAll());

            // Adicionar mensagem de erro
            mv.addObject("erro", "Erro ao salvar usuário: " + e.getMessage());

            return mv;
        }
    }

    @GetMapping("/usuario/detalhes/{id}")
    public ModelAndView viewDetalhesUsuario(@PathVariable("id") Long id) {

        Optional<Usuario> optionalUsuario = usuarioRepository.findById(id);

        if(optionalUsuario.isPresent()) {

            ModelAndView mv = new ModelAndView("usuarios/detalhes");

            Authentication auth = SecurityContextHolder.getContext().getAuthentication();

            Optional<Usuario> optionalUsuario2 = usuarioRepository.findByEmail(auth.getName());

            if (optionalUsuario2.isPresent()) {
                mv.addObject("user_logged", optionalUsuario2.get());
            }

            mv.addObject("usuario", optionalUsuario.get());

            return mv;

        } else {
            return new ModelAndView("redirect:/usuarios");
        }
    }

    @GetMapping("/usuario/editar/{id}")
    public ModelAndView viewEditarUsuario(@PathVariable("id") Long id) {

        Optional<Usuario> optionalUsuario = usuarioRepository.findById(id);

        if(optionalUsuario.isPresent()) {

            ModelAndView mv = new ModelAndView("usuarios/editar");

            Authentication auth = SecurityContextHolder.getContext().getAuthentication();

            Optional<Usuario> optionalUsuario2 = usuarioRepository.findByEmail(auth.getName());

            if (optionalUsuario2.isPresent()) {
                mv.addObject("user_logged", optionalUsuario2.get());
            }

            mv.addObject("usuario", optionalUsuario.get());
            mv.addObject("cargos", EnumCargo.values());
            mv.addObject("filiais", filialRepository.findAll());

            return mv;

        } else {
            return new ModelAndView("redirect:/usuarios");
        }
    }

    @PostMapping("/usuario/editar/{id}")
    public ModelAndView editarUsuario(@PathVariable("id") Long id, Usuario usuario, BindingResult bindingResult,
                                     @RequestParam(name = "cargo", required = false) String cargoSelecionado,
                                     @RequestParam(name = "novaSenha", required = false) String novaSenha) {

        // Buscar usuário existente
        Optional<Usuario> usuarioExistenteOpt = usuarioRepository.findById(id);
        if (!usuarioExistenteOpt.isPresent()) {
            return new ModelAndView("redirect:/usuarios");
        }

        Usuario usuarioExistente = usuarioExistenteOpt.get();

        // Validações manuais (sem @Valid para evitar validação da senha)
        if (usuario.getPrimeiroNome() == null || usuario.getPrimeiroNome().trim().isEmpty()) {
            bindingResult.rejectValue("primeiroNome", "primeiroNome.required", "O primeiro nome é obrigatório");
        }

        if (usuario.getSobrenome() == null || usuario.getSobrenome().trim().isEmpty()) {
            bindingResult.rejectValue("sobrenome", "sobrenome.required", "O sobrenome é obrigatório");
        }

        if (usuario.getEmail() == null || usuario.getEmail().trim().isEmpty()) {
            bindingResult.rejectValue("email", "email.required", "O e-mail é obrigatório");
        } else if (!usuario.getEmail().matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
            bindingResult.rejectValue("email", "email.invalid", "Formato de e-mail inválido");
        }

        if (usuario.getIdade() == null || usuario.getIdade() <= 0) {
            bindingResult.rejectValue("idade", "idade.required", "A idade é obrigatória e deve ser maior que zero");
        }

        if (usuario.getFilial() == null) {
            bindingResult.rejectValue("filial", "filial.required", "A filial é obrigatória");
        }

        // Validar se cargo foi selecionado
        if (cargoSelecionado == null || cargoSelecionado.trim().isEmpty()) {
            bindingResult.rejectValue("cargos", "cargo.required", "É obrigatório selecionar um cargo");
        }

        // Validar senha apenas se foi fornecida uma nova senha
        if (novaSenha != null && !novaSenha.trim().isEmpty()) {
            if (novaSenha.length() < 6 || novaSenha.length() > 16) {
                bindingResult.rejectValue("senha", "senha.size", "A senha deve ter entre 6 e 16 caracteres");
            }
        }

        // Se houver erros de validação, retornar para o formulário
        if (bindingResult.hasErrors()) {
            ModelAndView mv = new ModelAndView("usuarios/editar");

            // Adicionar o usuário logado
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            Optional<Usuario> op = usuarioRepository.findByEmail(auth.getName());
            if (op.isPresent()) {
                mv.addObject("user_logged", op.get());
            }

            // Re-adicionar os dados necessários para o formulário
            mv.addObject("usuario", usuarioExistente); // Usar o usuário existente para manter os dados originais
            mv.addObject("cargos", EnumCargo.values());
            mv.addObject("filiais", filialRepository.findAll());

            // Log para debug
            System.out.println("Erros de validação encontrados:");
            bindingResult.getAllErrors().forEach(error -> System.out.println(error.getDefaultMessage()));

            return mv;
        }

        try {
            // Processar cargo selecionado
            Set<Cargo> cargos = new HashSet<>();

            if (cargoSelecionado != null && !cargoSelecionado.isEmpty()) {
                // Converter string para enum
                EnumCargo enumCargo = EnumCargo.valueOf(cargoSelecionado);

                // Buscar ou criar cargo
                Optional<Cargo> cargoOpt = cargoRepository.findByNome(enumCargo);
                if (cargoOpt.isPresent()) {
                    cargos.add(cargoOpt.get());
                } else {
                    // Se o cargo não existir, criar um novo
                    Cargo novoCargo = new Cargo();
                    novoCargo.setNome(enumCargo);
                    Cargo cargoSalvo = cargoRepository.save(novoCargo);
                    cargos.add(cargoSalvo);
                }
            }

            // Atualizar dados do usuário existente
            usuarioExistente.setPrimeiroNome(usuario.getPrimeiroNome());
            usuarioExistente.setSobrenome(usuario.getSobrenome());
            usuarioExistente.setEmail(usuario.getEmail());
            usuarioExistente.setIdade(usuario.getIdade());
            usuarioExistente.setFilial(usuario.getFilial());
            usuarioExistente.setCargos(cargos);

            // Atualizar senha apenas se uma nova foi fornecida
            if (novaSenha != null && !novaSenha.trim().isEmpty()) {
                usuarioExistente.setSenha(encoder.encode(novaSenha));
            }
            // Se novaSenha for null ou vazio, mantém a senha existente

            // Salvar usuário editado
            Usuario usuarioSalvo = usuarioRepository.save(usuarioExistente);

            System.out.println("Usuário editado com sucesso: " + usuarioSalvo.getId());

            return new ModelAndView("redirect:/usuarios");

        } catch (Exception e) {
            // Em caso de erro, retornar para o formulário com mensagem de erro
            ModelAndView mv = new ModelAndView("usuarios/editar");

            // Adicionar o usuário logado
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            Optional<Usuario> op = usuarioRepository.findByEmail(auth.getName());
            if (op.isPresent()) {
                mv.addObject("user_logged", op.get());
            }

            // Re-adicionar os dados necessários para o formulário
            mv.addObject("usuario", usuarioExistente);
            mv.addObject("cargos", EnumCargo.values());
            mv.addObject("filiais", filialRepository.findAll());

            // Adicionar mensagem de erro
            mv.addObject("erro", "Erro ao editar usuário: " + e.getMessage());

            return mv;
        }
    }

    @GetMapping("/usuario/deletar/{id}")
    public ModelAndView excluirUsuario(@PathVariable Long id) {

        // Buscar usuário existente
        Optional<Usuario> usuarioOpt = usuarioRepository.findById(id);

        if (usuarioOpt.isPresent()) {
            Usuario usuario = usuarioOpt.get();

            try {
                // Limpar relacionamentos com cargos antes de excluir
                usuario.getCargos().clear();

                // Salvar para limpar a tabela associativa
                usuarioRepository.save(usuario);

                // Agora excluir o usuário
                usuarioRepository.deleteById(id);

                System.out.println("Usuário excluído com sucesso: ID " + id);

                return new ModelAndView("redirect:/usuarios");

            } catch (Exception e) {
                System.err.println("Erro ao excluir usuário: " + e.getMessage());
                e.printStackTrace();

                // Em caso de erro, redirecionar com mensagem (pode implementar flash attributes depois)
                return new ModelAndView("redirect:/usuarios");
            }
        } else {
            System.out.println("Usuário não encontrado para exclusão: ID " + id);
            return new ModelAndView("redirect:/usuarios");
        }
    }

}
