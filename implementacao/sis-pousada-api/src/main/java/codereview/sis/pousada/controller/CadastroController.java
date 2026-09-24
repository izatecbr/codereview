package codereview.sis.pousada.controller;

import codereview.sis.pousada.modelo.cadastro.Cadastro;
import codereview.sis.pousada.service.CadastroService;
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
@RequestMapping("/api/cadastros")
@Tag(name = "Cadastro", description = "Clientes / hóspedes")
public class CadastroController {

    private static final String EXEMPLO_CADASTRO = """
            {
              "nome": "Maria da Silva",
              "cpfCnpj": "123.456.789-00",
              "documento": "RG 1234567",
              "aniversario": "1990-05-15",
              "email": "maria@email.com",
              "endereco": {
                "logradouro": "Rua das Flores",
                "numero": "123",
                "complemento": "Apto 101",
                "bairro": "Centro",
                "cidade": "São Paulo",
                "uf": "SP",
                "cep": 12345678,
                "ibge": 3550308
              },
              "celular": {
                "numero": 11987654321,
                "whatsapp": true
              }
            }
            """;

    private final CadastroService cadastroService;

    public CadastroController(CadastroService cadastroService) {
        this.cadastroService = cadastroService;
    }

    @Operation(summary = "Criar cadastro", description = "Nome e CPF/CNPJ são obrigatórios.")
    @io.swagger.v3.oas.annotations.parameters.RequestBody(content = @Content(
            examples = @ExampleObject(name = "Cadastro", value = EXEMPLO_CADASTRO)))
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Cadastro criar(@RequestBody Cadastro cadastro) {
        return cadastroService.incluir(cadastro);
    }

    @Operation(summary = "Alterar cadastro", description = "O código vem da URL; o id do corpo é ignorado.")
    @PutMapping("/{id}")
    public Cadastro alterar(@PathVariable Integer id, @RequestBody Cadastro cadastro) {
        cadastro.setId(id);
        return cadastroService.alterar(cadastro);
    }

    @Operation(summary = "Buscar cadastro por código")
    @GetMapping("/{id}")
    public Cadastro buscarPorId(@PathVariable Integer id) {
        return cadastroService.buscarPorId(id);
    }

    @Operation(summary = "Listar cadastros", description = "Filtra pelo nome (contém, sem diferenciar maiúsculas).")
    @GetMapping
    public List<Cadastro> listar(@RequestParam(required = false, defaultValue = "") String nome) {
        return cadastroService.listarPorNome(nome);
    }
}
