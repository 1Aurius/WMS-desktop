package com.example.wmsdsktp.Responses;

import java.util.Date;

public class LoginResponse {
    private long id;
    private String username;
    private String token;
    private Date dn;
    private boolean isAdmin;
    private boolean isGestorRotas;
    private boolean isGestor;
    private boolean isLoja;
    private boolean isArmazem;

    public LoginResponse() {
    }

    public LoginResponse(long id, String username, String token, Date dn, boolean isAdmin, boolean isGestorRotas, boolean isGestor, boolean isLoja, boolean isArmazem) {
        this.id = id;
        this.username = username;
        this.token = token;
        this.dn = dn;
        this.isAdmin = isAdmin;
        this.isGestorRotas = isGestorRotas;
        this.isGestor = isGestor;
        this.isLoja = isLoja;
        this.isArmazem = isArmazem;
    }

    public boolean isGestor() {
        return isGestor;
    }

    public void setGestor(boolean gestor) {
        isGestor = gestor;
    }

    public Date getDn() {
        return dn;
    }

    public void setDn(Date dn) {
        this.dn = dn;
    }

    public boolean isGestorRotas() {
        return isGestorRotas;
    }

    public void setGestorRotas(boolean gestorRotas) {
        isGestorRotas = gestorRotas;
    }

    public boolean isArmazem() {
        return isArmazem;
    }

    public void setArmazem(boolean armazem) {
        isArmazem = armazem;
    }

    public boolean isLoja() {
        return isLoja;
    }

    public void setLoja(boolean loja) {
        isLoja = loja;
    }

    public boolean isAdmin() {
        return isAdmin;
    }

    public void setAdmin(boolean admin) {
        isAdmin = admin;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }
}
