package sis.pousada.modelo.cadastro;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Endereco {
    private String logradouro;
    private String numero;
    private String complemento;
    private String bairro;
    private String cidade;
    private String uf;
    private Integer cep;
    private Integer ibge;
}
