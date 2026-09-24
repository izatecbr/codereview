package codereview.sis.pousada.controller;

import codereview.sis.pousada.modelo.acomodacao.Acomodacao;
import codereview.sis.pousada.service.AcomodacaoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/acomodacoes")
@Tag(name = "Acomodação", description = "Suítes, quartos, casas, chalés e glampings")
public class AcomodacaoController {

    private static final String EXEMPLO_ACOMODACAO = """
            {
              "legenda": "Suite Master",
              "numero": "235",
              "descricao": "Suíte com vista para o mar",
              "valorDiaria": 160.0,
              "tipo": "SUITE",
              "itens": ["Ar-condicionado", "TV", "Frigobar"]
            }
            """;

    private final AcomodacaoService acomodacaoService;

    public AcomodacaoController(AcomodacaoService acomodacaoService) {
        this.acomodacaoService = acomodacaoService;
    }

    @Operation(summary = "Criar acomodação",
            description = "Tipos: SUITE, QUARTO, CASA, CHALE, GLAMPING. Legenda, número, descrição e tipo são obrigatórios.")
    @io.swagger.v3.oas.annotations.parameters.RequestBody(content = @Content(
            examples = @ExampleObject(name = "Acomodação", value = EXEMPLO_ACOMODACAO)))
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Acomodacao criar(@RequestBody Acomodacao acomodacao) {
        return acomodacaoService.incluir(acomodacao);
    }

    @Operation(summary = "Alterar acomodação", description = "O código vem da URL; o id do corpo é ignorado.")
    @PutMapping("/{id}")
    public Acomodacao alterar(@PathVariable Integer id, @RequestBody Acomodacao acomodacao) {
        acomodacao.setId(id);
        return acomodacaoService.alterar(acomodacao);
    }

    @Operation(summary = "Buscar acomodação por código")
    @GetMapping("/{id}")
    public Acomodacao buscarPorId(@PathVariable Integer id) {
        return acomodacaoService.buscarPorId(id);
    }

    @Operation(summary = "Listar acomodações", description = "Filtra pela legenda (contém, sem diferenciar maiúsculas).")
    @GetMapping
    public List<Acomodacao> listar(@RequestParam(required = false, defaultValue = "") String legenda) {
        return acomodacaoService.listarPorLegenda(legenda);
    }
}
