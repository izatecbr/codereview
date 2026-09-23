package sis.pousada.modelo.hospedagem;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import sis.pousada.modelo.acomodacao.AcomodacaoTipo;

@Getter
@Setter
@Entity
@Table(name = "tab_hospedagem")
public class Hospedagem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @Column(name = "vl_total", nullable = false)
    private double valorTotal;
    @Enumerated(EnumType.STRING)
    private HospedagamStatus status;
    @Embedded
    private Hospede hospede;
    //@Enumerated(EnumType.STRING)
    //private AcomodacaoTipo tipo;
    @Embedded
    private UnidadeLocacao unidadeLocacao;
    @Embedded
    private Duracao duracao;

}
