package com.casino.backend.controllers;

import com.casino.backend.models.User;
import com.casino.backend.repositories.TransactionRepository;
import com.casino.backend.repositories.UserRepository;
import com.casino.backend.repositories.WalletRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class AdminControllerTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private TransactionRepository transactionRepository;

    @Mock
    private WalletRepository walletRepository;

    @InjectMocks
    private AdminController adminController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    // 1. Prueba: Promover a un usuario que SÍ existe en la base de datos
    @Test
    void testPromoteToAdmin_UsuarioExiste_DebeRetornarExito() {
        // Preparación (Arrange)
        String username = "jugador_prueba@gmail.com";
        User mockUser = new User();
        mockUser.setUsername(username);
        mockUser.setRole("ROLE_USER");

        when(userRepository.findByUsername(username)).thenReturn(Optional.of(mockUser));
        when(userRepository.save(any(User.class))).thenReturn(mockUser);

        // Ejecución (Act)
        ResponseEntity<?> response = adminController.promoteToAdmin(username);

        // Verificación (Assert)
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("ROLE_ADMIN", mockUser.getRole()); // Verifica que el rol realmente cambió
        verify(userRepository, times(1)).save(mockUser); // Verifica que se guardó en la BD
    }

    // 2. Prueba: Intentar promover a un usuario que NO existe
    @Test
    void testPromoteToAdmin_UsuarioNoExiste_DebeRetornarError() {
        // Preparación (Arrange)
        String username = "fantasma@gmail.com";
        when(userRepository.findByUsername(username)).thenReturn(Optional.empty());

        // Ejecución (Act)
        ResponseEntity<?> response = adminController.promoteToAdmin(username);

        // Verificación (Assert)
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Error: Usuario no encontrado.", response.getBody());
        verify(userRepository, never()).save(any(User.class)); // Verifica que no se guardó nada
    }
}