package com.unb.meau.objects;

public class Story {

    private String tipo;
    private String nome;
    private String fotos;
    private String data;
    private String historia;
    private String user;
    private String userId;
    private String storyId;

    public Story() {
    } // Needed for Firebase

    public Story(String tipo, String nome, String fotos, String data, String historia, String user, String userId, String storyId) {
        this.tipo = tipo;
        this.nome = nome;
        this.fotos = fotos;
        this.data = data;
        this.historia = historia;
        this.user = user;
        this.userId = userId;
        this.storyId = storyId;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getFotos() {
        return fotos;
    }

    public void setFotos(String fotos) {
        this.fotos = fotos;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public String getHistoria() {
        return historia;
    }

    public void setHistoria(String historia) {
        this.historia = historia;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getStoryId() {
        return storyId;
    }

    public void setStoryId(String storyId) {
        this.storyId = storyId;
    }
}
