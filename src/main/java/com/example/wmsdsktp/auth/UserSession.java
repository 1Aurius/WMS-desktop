package com.example.wmsdsktp.auth;

import java.util.Date;

public class UserSession {
    private static UserSession user;

    private Long id;
    private String nome;
    private String password;
    private Date dn;
    private boolean isAdmin;
    private boolean isGestorRotas;
    private boolean isGestor;
    private boolean isLoja;
    private boolean isArmazem;

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

    public UserSession(Long id, String nome, String password, Date dn, boolean isAdmin, boolean isGestorRotas, boolean isGestor, boolean isLoja, boolean isArmazem) {
        this.id = id;
        this.nome = nome;
        this.password = password;
        this.dn = dn;
        this.isAdmin = isAdmin;
        this.isGestorRotas = isGestorRotas;
        this.isGestor = isGestor;
        this.isLoja = isLoja;
        this.isArmazem = isArmazem;
    }

    public static UserSession getUser() {
        return user;
    }

    public static void setUser(UserSession user) {
        UserSession.user = user;
    }

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

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
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
