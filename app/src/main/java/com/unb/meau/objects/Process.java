package com.unb.meau.objects;

import java.util.Date;
import java.util.HashMap;

public class Process {

    private String acao;
    private String animal;
    private String animalNome;
    private String dono;
    private String estagio;
    private String interessado;
    private String interessadoNome;

    public Process() {
    } // Needed for Firebase

    public Process(String acao, String animal, String animalNome, String dono, String estagio, String interessado, String interessadoNome) {
        this.acao = acao;
        this.animal = animal;
        this.animalNome = animalNome;
        this.dono = dono;
        this.estagio = estagio;
        this.interessado = interessado;
        this.interessadoNome = interessadoNome;
    }

    public String getAcao() {
        return acao;
    }

    public void setAcao(String acao) {
        this.acao = acao;
    }

    public String getAnimal() {
        return animal;
    }

    public void setAnimal(String animal) {
        this.animal = animal;
    }

    public String getAnimalNome() {
        return animalNome;
    }

    public void setAnimalNome(String animalNome) {
        this.animalNome = animalNome;
    }

    public String getDono() {
        return dono;
    }

    public void setDono(String dono) {
        this.dono = dono;
    }

    public String getEstagio() {
        return estagio;
    }

    public void setEstagio(String estagio) {
        this.estagio = estagio;
    }

    public String getInteressado() {
        return interessado;
    }

    public void setInteressado(String interessado) {
        this.interessado = interessado;
    }

    public String getInteressadoNome() {
        return interessadoNome;
    }

    public void setInteressadoNome(String interessadoNome) {
        this.interessadoNome = interessadoNome;
    }
}
