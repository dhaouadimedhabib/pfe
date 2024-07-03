package com.example.pfe.payload.response;

import com.example.pfe.Domain.Professionnel;

import java.util.List;

public class JwtResponse {
    private String token;
    private String type = "Bearer";
    private Long id;
    private String username;
    private List<String> roles;
    private Long idProfessionnel;


    public JwtResponse(String accessToken, Long id, String username, Long idProfessionnel, List<String> roles) {
        this.token = accessToken;
        this.id = id;
        this.username = username;
        this.roles = roles;
        this.idProfessionnel = idProfessionnel;

    }

    public String getAccessToken() {
        return token;
    }

    public void setAccessToken(String accessToken) {
        this.token = accessToken;
    }

    public String getTokenType() {
        return type;
    }

    public void setTokenType(String tokenType) {
        this.type = tokenType;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }



    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Long getIdProfessionnel() {
        return idProfessionnel;
    }

    public void setIdProfessionnel(Long idProfessionnel) {
        this.idProfessionnel = idProfessionnel;
    }

    public List<String> getRoles() {
        return roles;
    }

}
