package codereview.sis.pousada.controller;

import codereview.sis.pousada.modelo.hospedagem.HospedagamStatus;
import codereview.sis.pousada.modelo.hospedagem.Hospedagem;
import codereview.sis.pousada.service.HospedagemService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/hospedagens")
@Tag(name = "Hospedagem", description = "Reservas e hospedagens")
public class HospedagemController {

    private static final String EXEMPLO_RESERVA = """
            {
              "hospede": { "id": 1 },
              "duracao": {
                "dataInicial": "2026-10-01",
                "dataFinal": "2026-10-04"
              }
            }
            """;

    private static final String EXEMPLO_HOSPEDAGEM = """
            {
              "hospede": { "id": 1 },
              "unidadeLocacao": { "id": 2 },
              "duracao": {
                "dataInicial": "2026-10-01",
                "dataFinal": "2026-10-04"
              }
            }
            """;

    private final HospedagemService hospedagemService;

    public HospedagemController(HospedagemService hospedagemService) {
        this.hospedagemService = hospedagemService;
    }

    @Operation(summary = "Criar reserva",
            description = "Informe o código do hóspede e a duração. Não usa acomodação; o status é RESERVADA e o valor total é zero.")
    @io.swagger.v3.oas.annotations.parameters.RequestBody(content = @Content(
            examples = @ExampleObject(name = "Reserva", value = EXEMPLO_RESERVA)))
    @PostMapping("/reservas")
    @ResponseStatus(HttpStatus.CREATED)
    public Hospedagem criarReserva(@RequestBody Hospedagem reserva) {
        reserva.setStatus(HospedagamStatus.RESERVADA);
        return hospedagemService.incluir(reserva);
    }

    @Operation(summary = "Listar reservas", description = "Filtra pelo nome do hóspede (contém, sem diferenciar maiúsculas).")
    @GetMapping("/reservas")
    public List<Hospedagem> listarReservas(@RequestParam(required = false, defaultValue = "") String hospede) {
        return hospedagemService.listarReservas(hospede);
    }

    @Operation(summary = "Criar hospedagem",
            description = "Informe o código do hóspede, o código da acomodação (unidadeLocacao.id) e a duração. "
                    + "O status é HOSPEDADA e o valor total é dias x diária da acomodação.")
    @io.swagger.v3.oas.annotations.parameters.RequestBody(content = @Content(
            examples = @ExampleObject(name = "Hospedagem", value = EXEMPLO_HOSPEDAGEM)))
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Hospedagem criarHospedagem(@RequestBody Hospedagem hospedagem) {
        hospedagem.setStatus(HospedagamStatus.HOSPEDADA);
        return hospedagemService.incluir(hospedagem);
    }

    @Operation(summary = "Listar hospedagens", description = "Hospedagens em andamento e finalizadas, filtradas pelo nome do hóspede.")
    @GetMapping
    public List<Hospedagem> listarHospedagens(@RequestParam(required = false, defaultValue = "") String hospede) {
        return hospedagemService.listarHospedagens(hospede);
    }
}
