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
    private HashMap<String, Boolean> interesses;

    public User() {
    } // Needed for Firebase

    public User(String nome, String username, String email, String foto, String cidade, String estado, String endereco, Integer idade, String telefone, String token, String uid, HashMap<String, Boolean> interesses) {
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

    public HashMap<String, Boolean> getInteresses() {
        return interesses;
    }

    public void setInteresses(HashMap<String, Boolean> interesses) {
        this.interesses = interesses;
    }
}
