package com.unb.meau.objects;

public class Animal {

    String mNome;
    String mSexo;
    String mPorte;
    String mIdade;
    String mDono;

    public Animal() {} // Needed for Firebase

    public Animal(String mNome, String mSexo, String mPorte, String mIdade, String mDono) {
        this.mNome = mNome;
        this.mSexo = mSexo;
        this.mPorte = mPorte;
        this.mIdade = mIdade;
        this.mDono = mDono;
    }

    public String getNome() {
        return mNome;
    }

    public void setNome(String mNome) {
        this.mNome = mNome;
    }

    public String getSexo() {
        return mSexo;
    }

    public void setSexo(String mSexo) {
        this.mSexo = mSexo;
    }

    public String getPorte() {
        return mPorte;
    }

    public void setPorte(String mPorte) {
        this.mPorte = mPorte;
    }

    public String getIdade() {
        return mIdade;
    }

    public void setIdade(String mIdade) {
        this.mIdade = mIdade;
    }

    public String getDono() {
        return mDono;
    }

    public void setDono(String mDono) {
        this.mDono = mDono;
    }
}
