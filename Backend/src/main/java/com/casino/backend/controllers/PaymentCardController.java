package com.casino.backend.controllers;

import com.casino.backend.models.PaymentCard;
import com.casino.backend.models.User;
import com.casino.backend.repositories.PaymentCardRepository;
import com.casino.backend.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/cards")
public class PaymentCardController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PaymentCardRepository paymentCardRepository;

    // LEER (GET)
    @GetMapping("/mis-tarjetas")
    public ResponseEntity<?> getMyCards() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByUsername(username).get();
        List<PaymentCard> cards = paymentCardRepository.findByUserId(user.getId());
        return ResponseEntity.ok(cards);
    }

    // CREAR (POST)
    @PostMapping("/")
    public ResponseEntity<?> createCard(@RequestBody Map<String, String> request) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByUsername(username).get();

        String nombreTitular = request.get("nombreTitular");
        String numeroTarjeta = request.get("numeroTarjeta");

        if (nombreTitular == null || numeroTarjeta == null) {
            return ResponseEntity.badRequest().body("Error: Faltan datos de la tarjeta.");
        }

        PaymentCard newCard = new PaymentCard(nombreTitular, numeroTarjeta, user);
        paymentCardRepository.save(newCard);

        return ResponseEntity.ok("Tarjeta terminación " + numeroTarjeta.substring(Math.max(0, numeroTarjeta.length() - 4)) + " guardada con éxito.");
    }

    // ACTUALIZAR (PUT)
    @PutMapping("/{id}")
    public ResponseEntity<?> updateCard(@PathVariable Long id, @RequestBody Map<String, String> request) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByUsername(username).get();

        Optional<PaymentCard> cardOptional = paymentCardRepository.findById(id);
        if (cardOptional.isEmpty() || !cardOptional.get().getUser().getId().equals(user.getId())) {
            return ResponseEntity.status(403).body("Error: Tarjeta no encontrada o no te pertenece.");
        }

        PaymentCard card = cardOptional.get();
        String nuevoNombre = request.get("nombreTitular");
        if (nuevoNombre != null) {
            card.setNombreTitular(nuevoNombre);
            paymentCardRepository.save(card);
            return ResponseEntity.ok("Nombre del titular actualizado correctamente.");
        }

        return ResponseEntity.badRequest().body("Error: No se envió el nuevo nombre.");
    }

    // BORRAR (DELETE)
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteCard(@PathVariable Long id) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByUsername(username).get();

        Optional<PaymentCard> cardOptional = paymentCardRepository.findById(id);
        if (cardOptional.isEmpty() || !cardOptional.get().getUser().getId().equals(user.getId())) {
            return ResponseEntity.status(403).body("Error: Tarjeta no encontrada o no te pertenece.");
        }

        paymentCardRepository.delete(cardOptional.get());
        return ResponseEntity.ok("Tarjeta eliminada correctamente del sistema.");
    }
}