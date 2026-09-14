package sis.pousada.modelo;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
@Getter
@Setter
public class Cadastro {
    private Integer id;
    private String nome;
    private String cpfCnpj;
    private String documento;
    private LocalDate aniversario;
    private String email;
}
