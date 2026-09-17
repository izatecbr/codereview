package sis.pousada.modelo;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
@Getter
@Setter
@Entity
@Table(name = "tab_cadastro")
public class Cadastro {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @Column(name = "nome", nullable = false, length = 80)
    private String nome;
    @Column(name = "cpf_cnpj", nullable = false, length = 20)
    private String cpfCnpj;
    private String documento;
    private LocalDate aniversario;
    private String email;
    @Embedded
    private Endereco endereco;
    @Embedded
    private Celular celular;
}
