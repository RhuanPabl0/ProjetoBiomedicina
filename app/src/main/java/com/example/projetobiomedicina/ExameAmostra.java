package com.example.projetobiomedicina;

public class ExameAmostra {
    private int id;
    private String nomeexame;
    private boolean tipo;
    private String dtrealizado;
    private long idcliente;
    private int idinforeferencia;
    private String dataHoraColeta;
    private String nomeProfissional;
    private int numAmostras;
    private String condicoesColeta;
    private String identificacaoTubos;
    private String tempoArmazenamento;
    private String condicoesTransporte;
    private String observacoes;
    private String reacoesAdversas;
    private String acompanhamentoAdicional;

    public int getId() { return id; }
    public String getNomeexame() { return nomeexame; }
    public boolean isTipo() { return tipo; }
    public String getDtrealizado() { return dtrealizado; }
    public long getIdcliente() { return idcliente; }
    public int getIdinforeferencia() { return idinforeferencia; }
    public String getDataHoraColeta() { return dataHoraColeta; }
    public String getNomeProfissional() { return nomeProfissional; }
    public int getNumAmostras() { return numAmostras; }
    public String getCondicoesColeta() { return condicoesColeta; }
    public String getIdentificacaoTubos() { return identificacaoTubos; }
    public String getTempoArmazenamento() { return tempoArmazenamento; }
    public String getCondicoesTransporte() { return condicoesTransporte; }
    public String getObservacoes() { return observacoes; }
    public String getReacoesAdversas() { return reacoesAdversas; }
    public String getAcompanhamentoAdicional() { return acompanhamentoAdicional; }
}
