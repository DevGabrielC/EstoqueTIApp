package com.github.devgabrielc.model.repositories;

public class Estoque {
    private int id;
    private String grupoEquipamento;
    private String tipoEquipamento;
    private String marca;
    private String modelo;
    private String numeroSerie;
    private Integer quantidade;
    private Integer patrimonio;
    private String descricao;

    public Estoque(int id, String grupoEquipamento, String tipoEquipamento, String marca, String modelo, String numeroSerie, Integer quantidade, Integer patrimonio, String descricao) {
        this.id = id;
        this.grupoEquipamento = grupoEquipamento;
        this.tipoEquipamento = tipoEquipamento;
        this.marca = marca;
        this.modelo = modelo;
        this.numeroSerie = numeroSerie;
        this.quantidade = quantidade;
        this.patrimonio = patrimonio;
        this.descricao = descricao;
    }

    public int getId() {
        return this.id;
    }

    public String getGrupoEquipamento() {
        return this.grupoEquipamento;
    }

    public String getTipoEquipamento() {
        return this.tipoEquipamento;
    }

    public String getMarca() {
        return this.marca;
    }

    public String getModelo() {
        return this.modelo;
    }

    public String getNumeroSerie() {
        return this.numeroSerie;
    }

    public int getQuantidade() {
        return this.quantidade;
    }

    public Integer getPatrimonio() {
        return this.patrimonio;
    }

    public String getDescricao() {
        return this.descricao;
    }

    public void setGrupoEquipamento(String grupoEquipamento) {
        this.grupoEquipamento = grupoEquipamento;
    }

    public void setTipoEquipamento(String tipoEquipamento) {
        this.tipoEquipamento = tipoEquipamento;
    }

    public void setMarca(String marca) {
        this.marca = marca;
    }

    public void setModelo(String modelo) {
        this.modelo = modelo;
    }

    public void setNumeroSerie(String numeroSerie) {
        this.numeroSerie = numeroSerie;
    }

    public void setQuantidade(Integer quantidade) {
        this.quantidade = quantidade;
    }

    public void setPatrimonio(Integer patrimonio) {
        this.patrimonio = patrimonio;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }
}