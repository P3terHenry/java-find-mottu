package br.com.fiap.find_mottu.controller;

import br.com.fiap.find_mottu.model.EnumStatusMoto;
import br.com.fiap.find_mottu.model.Moto;
import br.com.fiap.find_mottu.model.Usuario;
import br.com.fiap.find_mottu.repository.FilialRepository;
import br.com.fiap.find_mottu.repository.LocalizacaoRepository;
import br.com.fiap.find_mottu.repository.MotoRepository;
import br.com.fiap.find_mottu.repository.UsuarioRepository;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.Optional;

@Controller
public class WebMotosController {

    @Autowired
    private MotoRepository motoRepository;

    @Autowired
    private FilialRepository filialRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private LocalizacaoRepository localizacaoRepository;

    @GetMapping("/motos")
    public ModelAndView viewMotos(@RequestParam(value = "status", required = false) String statusParam,
                                  @RequestParam(value = "filial", required = false) Long filialId,
                                  @RequestParam(value = "search", required = false) String search) {

        ModelAndView mv = new ModelAndView("/motos/index");

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Optional<Usuario> op = usuarioRepository.findByEmail(auth.getName());

        if (op.isPresent()) {
            mv.addObject("user_logged", op.get());
        }

        List<Moto> motos;
        EnumStatusMoto status = null;

        // Converter string para enum se necessário
        if (statusParam != null && !statusParam.trim().isEmpty()) {
            try {
                status = EnumStatusMoto.valueOf(statusParam.toUpperCase());
            } catch (IllegalArgumentException e) {
                // Status inválido, ignorar filtro
                status = null;
            }
        }

        // Aplicar filtros
        if (search != null && !search.trim().isEmpty()) {
            motos = motoRepository.findBySearchTerm(search.trim());
        } else if (status != null && filialId != null) {
            motos = motoRepository.findByFilialIdAndStatusMoto(filialId, status);
        } else if (status != null) {
            motos = motoRepository.findByStatusMoto(status);
        } else if (filialId != null) {
            motos = motoRepository.findByFilialId(filialId);
        } else {
            motos = motoRepository.findAll();
        }

        mv.addObject("motos", motos);
        mv.addObject("filiais", filialRepository.findAll());
        mv.addObject("statusOptions", EnumStatusMoto.values());
        mv.addObject("totalMotos", motoRepository.count());
        mv.addObject("motosAtivas", motoRepository.countByStatusMoto(EnumStatusMoto.ATIVA));
        mv.addObject("motosInativas", motoRepository.countByStatusMoto(EnumStatusMoto.INATIVA));
        mv.addObject("motosManutencao", motoRepository.countByStatusMoto(EnumStatusMoto.MANUTENCAO));
        mv.addObject("filtroStatus", status);
        mv.addObject("filtroFilial", filialId);
        mv.addObject("filtroSearch", search);

        return mv;
    }

    @GetMapping("/moto/nova")
    public ModelAndView viewNovaMoto() {

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Optional<Usuario> op = usuarioRepository.findByEmail(auth.getName());

        ModelAndView mv = new ModelAndView("/motos/nova");

        if (op.isPresent()) {
            mv.addObject("user_logged", op.get());
        }

        Moto novaMoto = new Moto();

        mv.addObject("moto", novaMoto);
        mv.addObject("filiais", filialRepository.findAll());
        mv.addObject("statusOptions", EnumStatusMoto.values());

        return mv;
    }

    @PostMapping("/moto/salvar")
    public ModelAndView salvarMoto(@Valid Moto moto, BindingResult bindingResult) {

        // Se houver erros de validação, retornar para o formulário
        if (bindingResult.hasErrors()) {
            ModelAndView mv = new ModelAndView("/motos/nova");

            // Adicionar o usuário logado
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            Optional<Usuario> op = usuarioRepository.findByEmail(auth.getName());
            if (op.isPresent()) {
                mv.addObject("user_logged", op.get());
            }

            mv.addObject("filiais", filialRepository.findAll());

            // Log para debug
            System.out.println("Erros de validação encontrados:");
            bindingResult.getAllErrors().forEach(error -> System.out.println(error.getDefaultMessage()));

            return mv;
        }

        try {
            // Verificar se já existe moto com a mesma placa
            if (motoRepository.findByPlacaMoto(moto.getPlacaMoto()).isPresent()) {
                ModelAndView mv = new ModelAndView("/motos/nova");

                Authentication auth = SecurityContextHolder.getContext().getAuthentication();
                Optional<Usuario> op = usuarioRepository.findByEmail(auth.getName());
                if (op.isPresent()) {
                    mv.addObject("user_logged", op.get());
                }

                mv.addObject("filiais", filialRepository.findAll());
                mv.addObject("erro", "Já existe uma moto cadastrada com a placa: " + moto.getPlacaMoto());

                return mv;
            }

            // Verificar se já existe moto com o mesmo QR Code
            if (motoRepository.findByIdQrCode(moto.getIdQrCode()).isPresent()) {
                ModelAndView mv = new ModelAndView("/motos/nova");

                Authentication auth = SecurityContextHolder.getContext().getAuthentication();
                Optional<Usuario> op = usuarioRepository.findByEmail(auth.getName());
                if (op.isPresent()) {
                    mv.addObject("user_logged", op.get());
                }

                mv.addObject("filiais", filialRepository.findAll());
                mv.addObject("erro", "Já existe uma moto cadastrada com o QR Code: " + moto.getIdQrCode());

                return mv;
            }

            // Log para debug
            System.out.println("Salvando moto: " + moto.getPlacaMoto());

            // Converter placa para maiúsculo
            moto.setPlacaMoto(moto.getPlacaMoto().toUpperCase());

            // Salvar moto
            Moto motoSalva = motoRepository.save(moto);

            System.out.println("Moto salva com ID: " + motoSalva.getId());

            return new ModelAndView("redirect:/motos");

        } catch (Exception e) {
            // Em caso de erro, retornar para o formulário com mensagem de erro
            ModelAndView mv = new ModelAndView("/motos/nova");

            // Adicionar o usuário logado
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            Optional<Usuario> op = usuarioRepository.findByEmail(auth.getName());
            if (op.isPresent()) {
                mv.addObject("user_logged", op.get());
            }

            mv.addObject("filiais", filialRepository.findAll());
            mv.addObject("erro", "Erro ao salvar moto: " + e.getMessage());

            return mv;
        }
    }

    @GetMapping("/moto/detalhes/{id}")
    public ModelAndView viewDetalhesMoto(@PathVariable("id") Long id) {

        Optional<Moto> optionalMoto = motoRepository.findById(id);

        if (optionalMoto.isPresent()) {

            ModelAndView mv = new ModelAndView("/motos/detalhes");

            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            Optional<Usuario> optionalUsuario = usuarioRepository.findByEmail(auth.getName());

            if (optionalUsuario.isPresent()) {
                mv.addObject("user_logged", optionalUsuario.get());
            }

            mv.addObject("moto", optionalMoto.get());

            return mv;

        } else {
            return new ModelAndView("redirect:/motos");
        }
    }

    @GetMapping("/moto/editar/{id}")
    public ModelAndView viewEditarMoto(@PathVariable("id") Long id) {

        Optional<Moto> optionalMoto = motoRepository.findById(id);

        if (optionalMoto.isPresent()) {

            ModelAndView mv = new ModelAndView("/motos/editar");

            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            Optional<Usuario> optionalUsuario = usuarioRepository.findByEmail(auth.getName());

            if (optionalUsuario.isPresent()) {
                mv.addObject("user_logged", optionalUsuario.get());
            }

            mv.addObject("moto", optionalMoto.get());
            mv.addObject("filiais", filialRepository.findAll());
            mv.addObject("statusOptions", EnumStatusMoto.values());

            return mv;

        } else {
            return new ModelAndView("redirect:/motos");
        }
    }

    @PostMapping("/moto/editar/{id}")
    public ModelAndView editarMoto(@PathVariable("id") Long id, @Valid Moto moto, BindingResult bindingResult) {

        // Buscar moto existente
        Optional<Moto> motoExistenteOpt = motoRepository.findById(id);
        if (!motoExistenteOpt.isPresent()) {
            return new ModelAndView("redirect:/motos");
        }

        Moto motoExistente = motoExistenteOpt.get();

        // Se houver erros de validação, retornar para o formulário
        if (bindingResult.hasErrors()) {
            ModelAndView mv = new ModelAndView("/motos/editar");

            // Adicionar o usuário logado
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            Optional<Usuario> op = usuarioRepository.findByEmail(auth.getName());
            if (op.isPresent()) {
                mv.addObject("user_logged", op.get());
            }

            mv.addObject("moto", motoExistente);
            mv.addObject("filiais", filialRepository.findAll());
            mv.addObject("statusOptions", EnumStatusMoto.values());

            // Log para debug
            System.out.println("Erros de validação encontrados:");
            bindingResult.getAllErrors().forEach(error -> System.out.println(error.getDefaultMessage()));

            return mv;
        }

        try {
            // Verificar se já existe outra moto com a mesma placa
            Optional<Moto> motoComMesmaPlaca = motoRepository.findByPlacaMoto(moto.getPlacaMoto());
            if (motoComMesmaPlaca.isPresent() && !motoComMesmaPlaca.get().getId().equals(id)) {
                ModelAndView mv = new ModelAndView("/motos/editar");

                Authentication auth = SecurityContextHolder.getContext().getAuthentication();
                Optional<Usuario> op = usuarioRepository.findByEmail(auth.getName());
                if (op.isPresent()) {
                    mv.addObject("user_logged", op.get());
                }

                mv.addObject("moto", motoExistente);
                mv.addObject("filiais", filialRepository.findAll());
                mv.addObject("statusOptions", EnumStatusMoto.values());
                mv.addObject("erro", "Já existe uma moto cadastrada com a placa: " + moto.getPlacaMoto());

                return mv;
            }

            // Verificar se já existe outra moto com o mesmo QR Code
            Optional<Moto> motoComMesmoQR = motoRepository.findByIdQrCode(moto.getIdQrCode());
            if (motoComMesmoQR.isPresent() && !motoComMesmoQR.get().getId().equals(id)) {
                ModelAndView mv = new ModelAndView("/motos/editar");

                Authentication auth = SecurityContextHolder.getContext().getAuthentication();
                Optional<Usuario> op = usuarioRepository.findByEmail(auth.getName());
                if (op.isPresent()) {
                    mv.addObject("user_logged", op.get());
                }

                mv.addObject("moto", motoExistente);
                mv.addObject("filiais", filialRepository.findAll());
                mv.addObject("statusOptions", EnumStatusMoto.values());
                mv.addObject("erro", "Já existe uma moto cadastrada com o QR Code: " + moto.getIdQrCode());

                return mv;
            }

            // Atualizar dados da moto existente
            motoExistente.setIdQrCode(moto.getIdQrCode());
            motoExistente.setIdImei(moto.getIdImei());
            motoExistente.setNumChassi(moto.getNumChassi());
            motoExistente.setNumMotor(moto.getNumMotor());
            motoExistente.setModeloMoto(moto.getModeloMoto());
            motoExistente.setPlacaMoto(moto.getPlacaMoto().toUpperCase());
            motoExistente.setStatusMoto(moto.getStatusMoto());
            motoExistente.setFilial(moto.getFilial());

            // Log para debug
            System.out.println("Editando moto ID: " + id);
            System.out.println("Placa: " + motoExistente.getPlacaMoto());

            // Salvar moto atualizada
            motoRepository.save(motoExistente);

            return new ModelAndView("redirect:/motos");

        } catch (Exception e) {
            // Em caso de erro, retornar para o formulário com mensagem de erro
            ModelAndView mv = new ModelAndView("/motos/editar");

            // Adicionar o usuário logado
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            Optional<Usuario> op = usuarioRepository.findByEmail(auth.getName());
            if (op.isPresent()) {
                mv.addObject("user_logged", op.get());
            }

            mv.addObject("moto", motoExistente);
            mv.addObject("filiais", filialRepository.findAll());
            mv.addObject("statusOptions", EnumStatusMoto.values());
            mv.addObject("erro", "Erro ao editar moto: " + e.getMessage());

            return mv;
        }
    }

    @GetMapping("/moto/deletar/{id}")
    public ModelAndView deletarMoto(@PathVariable("id") Long id, RedirectAttributes redirectAttributes) {

        Optional<Moto> optionalMoto = motoRepository.findById(id);

        if (optionalMoto.isPresent()) {
            try {
                // Deletar localizações associadas à moto
                localizacaoRepository.deleteByMotoId(id);

                // Deletar a moto
                motoRepository.deleteById(id);

                redirectAttributes.addFlashAttribute("sucesso", "Moto deletada com sucesso");
            } catch (Exception e) {
                redirectAttributes.addFlashAttribute("erro", "Erro ao deletar moto: " + e.getMessage());
            }
        } else {
            redirectAttributes.addFlashAttribute("erro", "Moto não encontrada");
        }

        return new ModelAndView("redirect:/motos");
    }
}
