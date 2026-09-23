package codereview.sis.pousada.repository;

import codereview.sis.pousada.modelo.acomodacao.Acomodacao;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AcomodacaoRepository extends JpaRepository<Acomodacao, Integer> {

    List<Acomodacao> findByLegendaContainingIgnoreCaseOrderByLegenda(String legenda);
}
