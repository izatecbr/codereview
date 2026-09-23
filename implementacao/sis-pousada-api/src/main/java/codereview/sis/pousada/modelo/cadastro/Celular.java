package codereview.sis.pousada.modelo.cadastro;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.Getter;
import lombok.Setter;

@Embeddable
@Getter
@Setter
public class Celular {
    @Column(name = "cel_numero")
    private Long numero;
    @Column(name = "cel_whatsapp")
    private boolean whatsapp;
}
