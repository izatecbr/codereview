package sis.pousada.modelo.hospedagem;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

@Getter
@Setter
public class Duracao {
    private LocalDate dataInicial;
    private LocalDate dataFinal;

    public long contarDias() {
        if (dataInicial == null || dataFinal == null) {
            return 0;
        }
        return ChronoUnit.DAYS.between(dataInicial, dataFinal);
    }
}
