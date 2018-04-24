package com.unb.meau.objects;

public class Animal {

    private String dono;
    private Boolean cadastro_adocao;
    private Boolean cadastro_apadrinhar;
    private Boolean cadastro_ajuda;
    private String nome;
    private String especie;
    private String sexo;
    private String porte;
    private String idade;
    private Boolean brincalhao;
    private Boolean timido;
    private Boolean calmo;
    private Boolean guarda;
    private Boolean amoroso;
    private Boolean preguicoso;
    private Boolean vacinado;
    private Boolean vermifugado;
    private Boolean castrado;
    private String doencas;
    private Boolean termo_de_adocao;
    private Boolean fotos_da_casa;
    private Boolean visita_previa_ao_animal;
    private String acompanhamento_pos_adocao;
    private Boolean termo_de_apadrinhamento;
    private Boolean auxilio_financeiro;
    private Boolean auxilio_alimentacao;
    private Boolean auxilio_saude;
    private Boolean auxilio_objetos;
    private Boolean visitas_ao_animal;
    private Boolean alimento;
    private Boolean ajuda_financeira;
    private Boolean ajuda_medicamento;
    private String ajuda_medicamento_nome;
    private Boolean ajuda_objeto;
    private String ajuda_objetos_nome;
    private String historia;
    private String localizacao;

    public Animal() {} // Needed for Firebase

    public Animal(String dono, Boolean cadastro_adocao, Boolean cadastro_apadrinhar, Boolean cadastro_ajuda, String nome, String especie, String sexo, String porte, String idade, Boolean brincalhao, Boolean timido, Boolean calmo, Boolean guarda, Boolean amoroso, Boolean preguicoso, Boolean vacinado, Boolean vermifugado, Boolean castrado, String doencas, Boolean termo_de_adocao, Boolean fotos_da_casa, Boolean visita_previa_ao_animal, String acompanhamento_pos_adocao, Boolean termo_de_apadrinhamento, Boolean auxilio_financeiro, Boolean auxilio_alimentacao, Boolean auxilio_saude, Boolean auxilio_objetos, Boolean visitas_ao_animal, Boolean alimento, Boolean ajuda_financeira, Boolean ajuda_medicamento, String ajuda_medicamento_nome, Boolean ajuda_objeto, String ajuda_objetos_nome, String historia, String localizacao) {
        this.dono = dono;
        this.cadastro_adocao = cadastro_adocao;
        this.cadastro_apadrinhar = cadastro_apadrinhar;
        this.cadastro_ajuda = cadastro_ajuda;
        this.nome = nome;
        this.especie = especie;
        this.sexo = sexo;
        this.porte = porte;
        this.idade = idade;
        this.brincalhao = brincalhao;
        this.timido = timido;
        this.calmo = calmo;
        this.guarda = guarda;
        this.amoroso = amoroso;
        this.preguicoso = preguicoso;
        this.vacinado = vacinado;
        this.vermifugado = vermifugado;
        this.castrado = castrado;
        this.doencas = doencas;
        this.termo_de_adocao = termo_de_adocao;
        this.fotos_da_casa = fotos_da_casa;
        this.visita_previa_ao_animal = visita_previa_ao_animal;
        this.acompanhamento_pos_adocao = acompanhamento_pos_adocao;
        this.termo_de_apadrinhamento = termo_de_apadrinhamento;
        this.auxilio_financeiro = auxilio_financeiro;
        this.auxilio_alimentacao = auxilio_alimentacao;
        this.auxilio_saude = auxilio_saude;
        this.auxilio_objetos = auxilio_objetos;
        this.visitas_ao_animal = visitas_ao_animal;
        this.alimento = alimento;
        this.ajuda_financeira = ajuda_financeira;
        this.ajuda_medicamento = ajuda_medicamento;
        this.ajuda_medicamento_nome = ajuda_medicamento_nome;
        this.ajuda_objeto = ajuda_objeto;
        this.ajuda_objetos_nome = ajuda_objetos_nome;
        this.historia = historia;
        this.localizacao = localizacao;
    }

    public String getDono() {
        return dono;
    }

    public void setDono(String dono) {
        this.dono = dono;
    }

    public Boolean getCadastro_adocao() {
        return cadastro_adocao;
    }

    public void setCadastro_adocao(Boolean cadastro_adocao) {
        this.cadastro_adocao = cadastro_adocao;
    }

    public Boolean getCadastro_apadrinhar() {
        return cadastro_apadrinhar;
    }

    public void setCadastro_apadrinhar(Boolean cadastro_apadrinhar) {
        this.cadastro_apadrinhar = cadastro_apadrinhar;
    }

    public Boolean getCadastro_ajuda() {
        return cadastro_ajuda;
    }

    public void setCadastro_ajuda(Boolean cadastro_ajuda) {
        this.cadastro_ajuda = cadastro_ajuda;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getEspecie() {
        return especie;
    }

    public void setEspecie(String especie) {
        this.especie = especie;
    }

    public String getSexo() {
        return sexo;
    }

    public void setSexo(String sexo) {
        this.sexo = sexo;
    }

    public String getPorte() {
        return porte;
    }

    public void setPorte(String porte) {
        this.porte = porte;
    }

    public String getIdade() {
        return idade;
    }

    public void setIdade(String idade) {
        this.idade = idade;
    }

    public Boolean getBrincalhao() {
        return brincalhao;
    }

    public void setBrincalhao(Boolean brincalhao) {
        this.brincalhao = brincalhao;
    }

    public Boolean getTimido() {
        return timido;
    }

    public void setTimido(Boolean timido) {
        this.timido = timido;
    }

    public Boolean getCalmo() {
        return calmo;
    }

    public void setCalmo(Boolean calmo) {
        this.calmo = calmo;
    }

    public Boolean getGuarda() {
        return guarda;
    }

    public void setGuarda(Boolean guarda) {
        this.guarda = guarda;
    }

    public Boolean getAmoroso() {
        return amoroso;
    }

    public void setAmoroso(Boolean amoroso) {
        this.amoroso = amoroso;
    }

    public Boolean getPreguicoso() {
        return preguicoso;
    }

    public void setPreguicoso(Boolean preguicoso) {
        this.preguicoso = preguicoso;
    }

    public Boolean getVacinado() {
        return vacinado;
    }

    public void setVacinado(Boolean vacinado) {
        this.vacinado = vacinado;
    }

    public Boolean getVermifugado() {
        return vermifugado;
    }

    public void setVermifugado(Boolean vermifugado) {
        this.vermifugado = vermifugado;
    }

    public Boolean getCastrado() {
        return castrado;
    }

    public void setCastrado(Boolean castrado) {
        this.castrado = castrado;
    }

    public String getDoencas() {
        return doencas;
    }

    public void setDoencas(String doencas) {
        this.doencas = doencas;
    }

    public Boolean getTermo_de_adocao() {
        return termo_de_adocao;
    }

    public void setTermo_de_adocao(Boolean termo_de_adocao) {
        this.termo_de_adocao = termo_de_adocao;
    }

    public Boolean getFotos_da_casa() {
        return fotos_da_casa;
    }

    public void setFotos_da_casa(Boolean fotos_da_casa) {
        this.fotos_da_casa = fotos_da_casa;
    }

    public Boolean getVisita_previa_ao_animal() {
        return visita_previa_ao_animal;
    }

    public void setVisita_previa_ao_animal(Boolean visita_previa_ao_animal) {
        this.visita_previa_ao_animal = visita_previa_ao_animal;
    }

    public String getAcompanhamento_pos_adocao() {
        return acompanhamento_pos_adocao;
    }

    public void setAcompanhamento_pos_adocao(String acompanhamento_pos_adocao) {
        this.acompanhamento_pos_adocao = acompanhamento_pos_adocao;
    }

    public Boolean getTermo_de_apadrinhamento() {
        return termo_de_apadrinhamento;
    }

    public void setTermo_de_apadrinhamento(Boolean termo_de_apadrinhamento) {
        this.termo_de_apadrinhamento = termo_de_apadrinhamento;
    }

    public Boolean getAuxilio_financeiro() {
        return auxilio_financeiro;
    }

    public void setAuxilio_financeiro(Boolean auxilio_financeiro) {
        this.auxilio_financeiro = auxilio_financeiro;
    }

    public Boolean getAuxilio_alimentacao() {
        return auxilio_alimentacao;
    }

    public void setAuxilio_alimentacao(Boolean auxilio_alimentacao) {
        this.auxilio_alimentacao = auxilio_alimentacao;
    }

    public Boolean getAuxilio_saude() {
        return auxilio_saude;
    }

    public void setAuxilio_saude(Boolean auxilio_saude) {
        this.auxilio_saude = auxilio_saude;
    }

    public Boolean getAuxilio_objetos() {
        return auxilio_objetos;
    }

    public void setAuxilio_objetos(Boolean auxilio_objetos) {
        this.auxilio_objetos = auxilio_objetos;
    }

    public Boolean getVisitas_ao_animal() {
        return visitas_ao_animal;
    }

    public void setVisitas_ao_animal(Boolean visitas_ao_animal) {
        this.visitas_ao_animal = visitas_ao_animal;
    }

    public Boolean getAlimento() {
        return alimento;
    }

    public void setAlimento(Boolean alimento) {
        this.alimento = alimento;
    }

    public Boolean getAjuda_financeira() {
        return ajuda_financeira;
    }

    public void setAjuda_financeira(Boolean ajuda_financeira) {
        this.ajuda_financeira = ajuda_financeira;
    }

    public Boolean getAjuda_medicamento() {
        return ajuda_medicamento;
    }

    public void setAjuda_medicamento(Boolean ajuda_medicamento) {
        this.ajuda_medicamento = ajuda_medicamento;
    }

    public String getAjuda_medicamento_nome() {
        return ajuda_medicamento_nome;
    }

    public void setAjuda_medicamento_nome(String ajuda_medicamento_nome) {
        this.ajuda_medicamento_nome = ajuda_medicamento_nome;
    }

    public Boolean getAjuda_objeto() {
        return ajuda_objeto;
    }

    public void setAjuda_objeto(Boolean ajuda_objeto) {
        this.ajuda_objeto = ajuda_objeto;
    }

    public String getAjuda_objetos_nome() {
        return ajuda_objetos_nome;
    }

    public void setAjuda_objetos_nome(String ajuda_objetos_nome) {
        this.ajuda_objetos_nome = ajuda_objetos_nome;
    }

    public String getHistoria() {
        return historia;
    }

    public void setHistoria(String historia) {
        this.historia = historia;
    }

    public String getLocalizacao() {
        return localizacao;
    }

    public void setLocalizacao(String localizacao) {
        this.localizacao = localizacao;
    }
}
