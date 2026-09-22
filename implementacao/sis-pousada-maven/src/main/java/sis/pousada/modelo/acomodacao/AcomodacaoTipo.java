package sis.pousada.modelo.acomodacao;

public enum AcomodacaoTipo {
    SUITE("Suíte"),//0
    QUARTO("Quarto"),//1
    CASA("Casa"),
    CHALE("Chalé"),
    GLAMPING("Glamping");

    private final String descricao;

    AcomodacaoTipo(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }

    @Override
    public String toString() {
        return descricao;
    }
}
