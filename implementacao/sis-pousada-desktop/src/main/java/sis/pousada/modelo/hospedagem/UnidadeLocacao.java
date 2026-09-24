package sis.pousada.modelo.hospedagem;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UnidadeLocacao {
    private Integer id;
    private String legenda;
    private Double valorDiaria;
    private String numero;
}
