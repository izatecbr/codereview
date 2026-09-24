package sis.pousada.modelo.hospedagem;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Embeddable
public class Hospede {
    @Column(name = "hospede_id")
    private int id;
    @Column(name = "hospede_nome")
    private String nome;
}
