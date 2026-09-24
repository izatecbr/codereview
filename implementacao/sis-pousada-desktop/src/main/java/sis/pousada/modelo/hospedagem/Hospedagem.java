package sis.pousada.modelo.hospedagem;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Hospedagem {
    private Integer id;
    private double valorTotal;
    private HospedagamStatus status;
    private Hospede hospede;
    private UnidadeLocacao unidadeLocacao;
    private Duracao duracao;
}
