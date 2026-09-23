package sis.pousada.modelo.acomodacao;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Entity
@Table(name = "tab_acomodacao")
public class Acomodacao {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @Column(name = "legenda", nullable = false, length = 80)
    private String legenda;
    @Column(name = "descricao", nullable = false, length = 250)
    private String descricao;
    @Column(name = "vl_diaria", nullable = false)
    private double valorDiaria;
    @Enumerated(EnumType.STRING)
    @Column(name = "tipo", nullable = false, length = 10)
    private AcomodacaoTipo tipo;
    //@Transient
    @ElementCollection(fetch = FetchType.EAGER)
    @Column(name = "item",nullable = false, length = 30)
    @CollectionTable(
            name = "tab_acomodacao_item",  // Nome da tabela que armazenará os emails
            joinColumns = @JoinColumn(name = "acomodacao_id")  // Coluna que faz a relação com a tabela Cliente
    )
    private List<String> itens;
    private String numero;
    private boolean ocupada;
}
