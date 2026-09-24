package sis.pousada.modelo.acomodacao;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class Acomodacao {
    private Integer id;
    private String legenda;
    private String descricao;
    private double valorDiaria;
    private AcomodacaoTipo tipo;
    //@Transient
    private List<String> itens;
    private String numero;
    private boolean ocupada;
}
