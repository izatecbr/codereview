package codereview.sis.pousada.modelo.hospedagem;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Embeddable
public class UnidadeLocacao {
    @Column(name = "ul_id")
    private Integer id;
    @Column(name = "ul_legenda")
    private String legenda;
    @Column(name = "ul_vl_diaria")
    private Double valorDiaria;
    @Column(name = "ul_numero")
    private String numero;
}
