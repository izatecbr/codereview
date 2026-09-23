package codereview.sis.pousada.modelo.cadastro;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.Getter;
import lombok.Setter;

@Embeddable
@Getter
@Setter
public class Endereco {
    @Column(name = "end_logradouro")
    private String logradouro;
    @Column(name = "end_numero")
    private String numero;
    @Column(name = "end_complemento")
    private String complemento;
    @Column(name = "end_bairro")
    private String bairro;
    @Column(name = "end_cidade")
    private String cidade;
    @Column(name = "end_uf")
    private String uf;
    @Column(name = "end_cep")
    private Integer cep;
    @Column(name = "end_ibge")
    private Integer ibge;
}
