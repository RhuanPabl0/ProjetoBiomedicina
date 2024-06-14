package com.example.projetobiomedicina;

import com.google.gson.annotations.SerializedName;

import java.util.Date;

public class Paciente {
    @SerializedName("cpfpac")
    private Long cpfPac;
    @SerializedName("nomepac")
    private String nomePac;
    @SerializedName("codpac")
    private int codPac;
    @SerializedName("telpac")
    private Long telPac;
    @SerializedName("ceppac")
    private Integer cepPac;
    @SerializedName("lograpac")
    private String logradouroPac;
    @SerializedName("numlograpac")
    private Integer numLogradouroPac;
    @SerializedName("complpac")
    private String complementoPac;
    @SerializedName("bairropac")
    private String bairroPac;
    @SerializedName("cidadepac")
    private String cidadePac;
    @SerializedName("ufpac")
    private String ufPac;
    @SerializedName("rgpac")
    private Long rgPac;
    @SerializedName("estrgpac")
    private String estRgPac;
    @SerializedName("nomemaepac")
    private String nomeMaePac;
    @SerializedName("dtnascpac")
    private Date dataNascimentoPac;

    // Getters e Setters

    public Long getCpfPac() {
        return cpfPac;
    }

    public void setCpfPac(Long cpfPac) {
        this.cpfPac = cpfPac;
    }

    public String getNomePac() {
        return nomePac;
    }

    public void setNomePac(String nomePac) {
        this.nomePac = nomePac;
    }

    public int getCodPac() {
        return codPac;
    }

    public void setCodPac(int codPac) {
        this.codPac = codPac;
    }

    public Long getTelPac() {
        return telPac;
    }

    public void setTelPac(Long telPac) {
        this.telPac = telPac;
    }

    public Integer getCepPac() {
        return cepPac;
    }

    public void setCepPac(Integer cepPac) {
        this.cepPac = cepPac;
    }

    public String getLogradouroPac() {
        return logradouroPac;
    }

    public void setLogradouroPac(String logradouroPac) {
        this.logradouroPac = logradouroPac;
    }

    public Integer getNumLogradouroPac() {
        return numLogradouroPac;
    }

    public void setNumLogradouroPac(Integer numLogradouroPac) {
        this.numLogradouroPac = numLogradouroPac;
    }

    public String getComplementoPac() {
        return complementoPac;
    }

    public void setComplementoPac(String complementoPac) {
        this.complementoPac = complementoPac;
    }

    public String getBairroPac() {
        return bairroPac;
    }

    public void setBairroPac(String bairroPac) {
        this.bairroPac = bairroPac;
    }

    public String getCidadePac() {
        return cidadePac;
    }

    public void setCidadePac(String cidadePac) {
        this.cidadePac = cidadePac;
    }

    public String getUfPac() {
        return ufPac;
    }

    public void setUfPac(String ufPac) {
        this.ufPac = ufPac;
    }

    public Long getRgPac() {
        return rgPac;
    }

    public void setRgPac(Long rgPac) {
        this.rgPac = rgPac;
    }

    public String getEstRgPac() {
        return estRgPac;
    }

    public void setEstRgPac(String estRgPac) {
        this.estRgPac = estRgPac;
    }

    public String getNomeMaePac() {
        return nomeMaePac;
    }

    public void setNomeMaePac(String nomeMaePac) {
        this.nomeMaePac = nomeMaePac;
    }

    public Date getDataNascimentoPac() {
        return dataNascimentoPac;
    }

    public void setDataNascimentoPac(Date dataNascimentoPac) {
        this.dataNascimentoPac = dataNascimentoPac;
    }
}
