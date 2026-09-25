package com.casino.backend.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

@Entity
@Table(name = "payment_cards")
public class PaymentCard {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nombreTitular;
    private String numeroTarjeta;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    @JsonIgnore 
    private User user;

    public PaymentCard() {}

    public PaymentCard(String nombreTitular, String numeroTarjeta, User user) {
        this.nombreTitular = nombreTitular;
        this.numeroTarjeta = numeroTarjeta;
        this.user = user;
    }

    // Getters y Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getNombreTitular() { return nombreTitular; }
    public void setNombreTitular(String nombreTitular) { this.nombreTitular = nombreTitular; }

    public String getNumeroTarjeta() { return numeroTarjeta; }
    public void setNumeroTarjeta(String numeroTarjeta) { this.numeroTarjeta = numeroTarjeta; }

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }
}