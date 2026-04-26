package com.example.wmsdsktp.auth;

import java.util.Date;

public class UserSession {
    private static UserSession user;

    private Long id;
    private String nome;
    private Date dn;
    private boolean isAdmin;
    private boolean isGestorRotas;
    private boolean isGestor;
    private boolean isLoja;
    private boolean isArmazem;
    private String token;



    private UserSession() {}

    public static UserSession getInstance() {
        if (user == null) {
            user = new UserSession();
        }
        return user;
    }

    public void logout() {
        user = null; // clears the session
    }

    public static void login() {
        user = new UserSession();
    }

    public UserSession(Long id, String nome, Date dn, boolean isAdmin, boolean isGestorRotas, boolean isGestor, boolean isLoja, boolean isArmazem, String token) {
        this.id = id;
        this.nome = nome;
        this.dn = dn;
        this.isAdmin = isAdmin;
        this.isGestorRotas = isGestorRotas;
        this.isGestor = isGestor;
        this.isLoja = isLoja;
        this.isArmazem = isArmazem;
        this.token = token;
    }

    public static UserSession getUser() {
        return user;
    }

    public static void setUser(UserSession user) {
        UserSession.user = user;
    }

    public String getToken() {return token;}

    public void setToken(String token) {this.token = token;}

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public Date getDn() {
        return dn;
    }

    public void setDn(Date dn) {
        this.dn = dn;
    }

    public boolean isAdmin() {
        return isAdmin;
    }

    public void setAdmin(boolean admin) {
        isAdmin = admin;
    }

    public boolean isGestorRotas() {
        return isGestorRotas;
    }

    public void setGestorRotas(boolean gestorRotas) {
        isGestorRotas = gestorRotas;
    }

    public boolean isGestor() {
        return isGestor;
    }

    public void setGestor(boolean gestor) {
        isGestor = gestor;
    }

    public boolean isLoja() {
        return isLoja;
    }

    public void setLoja(boolean loja) {
        isLoja = loja;
    }

    public boolean isArmazem() {
        return isArmazem;
    }

    public void setArmazem(boolean armazem) {
        isArmazem = armazem;
    }
}
