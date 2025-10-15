package com.example.ocpp.domain;

import jakarta.persistence.*;

@Entity @Table(name="user_token")
public class UserToken {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String idTag;

    private boolean active;

    // getters/setters
    public Long getId(){ return id; }
    public void setId(Long i){ this.id = i; }
    public String getIdTag(){ return idTag; }
    public void setIdTag(String s){ this.idTag = s; }
    public boolean isActive(){ return active; }
    public void setActive(boolean a){ this.active = a; }
    
    
}
