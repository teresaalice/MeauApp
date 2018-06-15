package com.unb.meau.objects;

import java.util.HashMap;

public class User {

    private String nome;
    private String username;
    private String email;
    private String foto;
    private String cidade;
    private String estado;
    private String endereco;
    private Integer idade;
    private String telefone;
    private String token;
    private String uid;
    private Boolean notificacoes_chat;
    private Boolean notificacoes_recordacao;
    private Boolean notificacoes_eventos;
    private HashMap<String, Boolean> interesses;

    public User() {
    } // Needed for Firebase

    public User(String nome, String username, String email, String foto, String cidade, String estado, String endereco, Integer idade, String telefone, String token, String uid, Boolean notificacoes_chat, Boolean notificacoes_recordacao, Boolean notificacoes_eventos, HashMap<String, Boolean> interesses) {
        this.nome = nome;
        this.username = username;
        this.email = email;
        this.foto = foto;
        this.cidade = cidade;
        this.estado = estado;
        this.endereco = endereco;
        this.idade = idade;
        this.telefone = telefone;
        this.token = token;
        this.uid = uid;
        this.notificacoes_chat = notificacoes_chat;
        this.notificacoes_recordacao = notificacoes_recordacao;
        this.notificacoes_eventos = notificacoes_eventos;
        this.interesses = interesses;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getFoto() {
        return foto;
    }

    public void setFoto(String foto) {
        this.foto = foto;
    }

    public String getCidade() {
        return cidade;
    }

    public void setCidade(String cidade) {
        this.cidade = cidade;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public String getEndereco() {
        return endereco;
    }

    public void setEndereco(String endereco) {
        this.endereco = endereco;
    }

    public Integer getIdade() {
        return idade;
    }

    public void setIdade(Integer idade) {
        this.idade = idade;
    }

    public String getTelefone() {
        return telefone;
    }

    public void setTelefone(String telefone) {
        this.telefone = telefone;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public Boolean getNotificacoes_chat() {
        return notificacoes_chat;
    }

    public void setNotificacoes_chat(Boolean notificacoes_chat) {
        this.notificacoes_chat = notificacoes_chat;
    }

    public Boolean getNotificacoes_recordacao() {
        return notificacoes_recordacao;
    }

    public void setNotificacoes_recordacao(Boolean notificacoes_recordacao) {
        this.notificacoes_recordacao = notificacoes_recordacao;
    }

    public Boolean getNotificacoes_eventos() {
        return notificacoes_eventos;
    }

    public void setNotificacoes_eventos(Boolean notificacoes_eventos) {
        this.notificacoes_eventos = notificacoes_eventos;
    }

    public HashMap<String, Boolean> getInteresses() {
        return interesses;
    }

    public void setInteresses(HashMap<String, Boolean> interesses) {
        this.interesses = interesses;
    }
}
