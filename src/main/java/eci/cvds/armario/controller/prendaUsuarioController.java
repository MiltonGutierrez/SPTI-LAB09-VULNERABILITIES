package eci.cvds.armario.controller;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import eci.cvds.armario.model.PrendaUsuario;
import eci.cvds.armario.model.User;
import eci.cvds.armario.model.Prenda;
import eci.cvds.armario.repository.SessionRepository;
import eci.cvds.armario.repository.PrendaUsuarioRepository;
import eci.cvds.armario.repository.PrendaRepository;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
@RestController
@CrossOrigin(origins = {"http://localhost:3000", "https://witty-field-0ab72731e.5.azurestaticapps.net"})
@RequestMapping(value = "/user")
public class PrendaUsuarioController {
    private SessionRepository sessionRepository;
    private PrendaUsuarioRepository prendaUsuarioRepository;
    private PrendaRepository prendaRepository;

    public PrendaUsuarioController(SessionRepository sessionRepository, PrendaUsuarioRepository prendaUsuarioRepository) {
        this.sessionRepository = sessionRepository;
        this.prendaUsuarioRepository = prendaUsuarioRepository;
    }

    @GetMapping("/client/UsuarioPrendas")
    public List<Prenda> getAllPrendasOfUser(@RequestHeader("authToken") UUID authToken) {
        User user = this.sessionRepository.findByToken(authToken).getUser();
        List<PrendaUsuario> prendaUsuarios = prendaUsuarioRepository.findByUser(user);
        return prendaUsuarios.stream().map(PrendaUsuario::getPrenda).collect(Collectors.toList());
    }
    @GetMapping("/client/UsuarioPrenda/{idPrenda}")
    public ResponseEntity<PrendaUsuario> getPrendaById(@RequestHeader("authToken") UUID authToken, @PathVariable("idPrenda") UUID idPrenda) {
        User user = this.sessionRepository.findByToken(authToken).getUser();
        Prenda prenda = prendaRepository.findByPrendaId(idPrenda);
        PrendaUsuario prendaUser = prendaUsuarioRepository.findByPrenda(prenda);
        if (user != null && prenda != null) {
            return new ResponseEntity<>(prendaUser, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(null, HttpStatus.FORBIDDEN);
        }
    }
    @PostMapping("/client/UsuarioPrenda")
    public PrendaUsuario addPrenda(@RequestBody PrendaUsuario prendaUser, @RequestHeader("authToken") UUID authToken) {
        return prendaUsuarioRepository.save(prendaUser);
    }
    
    @DeleteMapping("/client/UsuarioPrenda/{idPrenda}")
    public ResponseEntity<String> deletePrenda(@RequestHeader("authToken") UUID authToken, @PathVariable("idPrenda") UUID idPrenda) {
        User user = this.sessionRepository.findByToken(authToken).getUser();
        Prenda prenda = prendaRepository.findByPrendaId(idPrenda);
        PrendaUsuario prendaUsuario = prendaUsuarioRepository.findByPrenda(prenda);
        if (user != null && prendaUsuario != null && prendaUsuario.getUser().equals(user)) {
            prendaUsuarioRepository.delete(prendaUsuario);
            return new ResponseEntity<>("Prenda eliminada correctamente", HttpStatus.OK);
        } else {
            return new ResponseEntity<>("No autorizado o prenda no encontrada", HttpStatus.FORBIDDEN);
        }
    }

}
