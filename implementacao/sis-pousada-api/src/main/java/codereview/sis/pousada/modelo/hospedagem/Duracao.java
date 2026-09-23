package codereview.sis.pousada.modelo.hospedagem;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

@Getter
@Setter
@Embeddable
public class Duracao {
    @Column(name = "dur_data_inicial")
    private LocalDate dataInicial;
    @Column(name = "dur_data_final")
    private LocalDate dataFinal;

    public long contarDias() {
        if (dataInicial == null || dataFinal == null) {
            return 0;
        }
        return ChronoUnit.DAYS.between(dataInicial, dataFinal);
    }
}
