package eci.cvds.armario.controller;
import eci.cvds.armario.model.Conjuntos;
import eci.cvds.armario.model.Prenda;
import eci.cvds.armario.model.Session;
import eci.cvds.armario.model.User;
import eci.cvds.armario.repository.ConjuntosRepository;
import eci.cvds.armario.repository.PrendaRepository;
import eci.cvds.armario.repository.SessionRepository;
import eci.cvds.armario.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@CrossOrigin(origins = {"http://localhost:3000", "https://witty-field-0ab72731e.5.azurestaticapps.net"})
@RequestMapping(value = "/user")
public class ConjuntosController {
    private PrendaRepository prendaRepository;
    private ConjuntosRepository conjuntosRepository;
    private SessionRepository sessionRepository;

    @Autowired
    public ConjuntosController(PrendaRepository prendaRepository, SessionRepository sessionRepository, ConjuntosRepository conjuntosRepository) {
        this.prendaRepository = prendaRepository;
        this.sessionRepository = sessionRepository;
        this.conjuntosRepository = conjuntosRepository;
    }

    @GetMapping("/client/conjuntos")
    public List<Conjuntos> getAllConjuntosOfUser(@RequestHeader("authToken") UUID authToken) {
        User user = this.sessionRepository.findByToken(authToken).getUser();
        return conjuntosRepository.findByUser(user);
    }

    @GetMapping("/client/conjunto/{idConjunto}")
    public ResponseEntity<Conjuntos> getConjuntoById(@RequestHeader("authToken") UUID authToken, @PathVariable("idConjunto") UUID idConjunto) {
        User user = this.sessionRepository.findByToken(authToken).getUser();
        Conjuntos conjunto = conjuntosRepository.findById(idConjunto).get();
        if (user != null) {
            return new ResponseEntity<>(conjunto, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(null, HttpStatus.FORBIDDEN);
        }
    }

    @PostMapping("/client/conjunto")
    public Conjuntos addConjunto(@RequestBody List<Prenda> prendas, @RequestHeader("authToken") UUID authToken) {
        User user = this.sessionRepository.findByToken(authToken).getUser();
        Conjuntos conjunto = new Conjuntos();
        conjunto.setUser(user);
        int size = prendas.size();
        if (size >= 1) {
            conjunto.setPrenda1(prendaRepository.save(prendas.get(0)));
        }
        if (size >= 2) {
            conjunto.setPrenda2(prendaRepository.save(prendas.get(1)));
        }
        if (size >= 3) {
            conjunto.setPrenda3(prendaRepository.save(prendas.get(2)));
        }
        if (size >= 4) {
            conjunto.setPrenda4(prendaRepository.save(prendas.get(3)));
        }
        return conjuntosRepository.save(conjunto);
    }


}
