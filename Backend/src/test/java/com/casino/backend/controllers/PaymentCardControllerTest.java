package com.casino.backend.controllers;

import com.casino.backend.models.PaymentCard;
import com.casino.backend.models.User;
import com.casino.backend.repositories.PaymentCardRepository;
import com.casino.backend.repositories.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class PaymentCardControllerTest {

    @Mock
    private PaymentCardRepository paymentCardRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private Authentication authentication;

    @Mock
    private SecurityContext securityContext;

    @InjectMocks
    private PaymentCardController paymentCardController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        // Simulamos la sesión del usuario con SecurityContextHolder (como está en tu controlador)
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);
    }

    // 1. Prueba: Obtener las tarjetas (GET)
    @Test
    void testGetMyCards_DebeRetornarListaDeTarjetas() {
        String username = "usuario1@gmail.com";
        User mockUser = new User();
        mockUser.setId(1L);
        mockUser.setUsername(username);

        PaymentCard tarjeta = new PaymentCard();
        tarjeta.setNumeroTarjeta("1234567812345678");
        
        when(authentication.getName()).thenReturn(username);
        when(userRepository.findByUsername(username)).thenReturn(Optional.of(mockUser));
        when(paymentCardRepository.findByUserId(mockUser.getId())).thenReturn(Arrays.asList(tarjeta));

        // Ejecución (Aquí llamamos a getMyCards)
        ResponseEntity<?> response = paymentCardController.getMyCards();

        // Verificación
        assertEquals(HttpStatus.OK, response.getStatusCode());
        List<PaymentCard> cards = (List<PaymentCard>) response.getBody();
        assertEquals(1, cards.size());
        assertEquals("1234567812345678", cards.get(0).getNumeroTarjeta());
    }

    // 2. Prueba: Guardar una nueva tarjeta (POST)
    @Test
    void testCreateCard_DebeGuardarYRetornarMensaje() {
        String username = "usuario1@gmail.com";
        User mockUser = new User();
        mockUser.setId(1L);

        // Tu controlador espera un Map, así que se lo enviamos
        Map<String, String> request = new HashMap<>();
        request.put("nombreTitular", "MARCELO SILVA");
        request.put("numeroTarjeta", "1111222233334444");

        when(authentication.getName()).thenReturn(username);
        when(userRepository.findByUsername(username)).thenReturn(Optional.of(mockUser));

        // Ejecución (Aquí llamamos a createCard)
        ResponseEntity<?> response = paymentCardController.createCard(request);

        // Verificación
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Tarjeta terminación 4444 guardada con éxito.", response.getBody());
        verify(paymentCardRepository, times(1)).save(any(PaymentCard.class));
    }

    // 3. Prueba: Eliminar una tarjeta (DELETE)
    @Test
    void testDeleteCard_DebeEliminarYRetornarMensaje() {
        Long tarjetaId = 15L;
        String username = "usuario1@gmail.com";
        User mockUser = new User();
        mockUser.setId(1L);

        PaymentCard tarjetaGuardada = new PaymentCard("MARCELO SILVA", "1111222233334444", mockUser);
        tarjetaGuardada.setId(tarjetaId);

        when(authentication.getName()).thenReturn(username);
        when(userRepository.findByUsername(username)).thenReturn(Optional.of(mockUser));
        when(paymentCardRepository.findById(tarjetaId)).thenReturn(Optional.of(tarjetaGuardada));

        // Ejecución (Aquí llamamos a deleteCard)
        ResponseEntity<?> response = paymentCardController.deleteCard(tarjetaId);

        // Verificación
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Tarjeta eliminada correctamente del sistema.", response.getBody());
        verify(paymentCardRepository, times(1)).delete(tarjetaGuardada);
    }
}