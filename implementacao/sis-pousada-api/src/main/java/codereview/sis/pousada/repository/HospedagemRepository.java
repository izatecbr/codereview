package codereview.sis.pousada.repository;

import codereview.sis.pousada.modelo.hospedagem.HospedagamStatus;
import codereview.sis.pousada.modelo.hospedagem.Hospedagem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;

public interface HospedagemRepository extends JpaRepository<Hospedagem, Integer> {

    // Filtros opcionais (null = ignora). O período retorna quem tem estadia que
    // sobrepõe [dataInicial, dataFinal].
    @Query("""
            SELECT h FROM Hospedagem h
             WHERE LOWER(h.hospede.nome) LIKE LOWER(CONCAT('%', :nome, '%'))
               AND h.status IN :status
               AND (:cadastroId IS NULL OR h.hospede.id = :cadastroId)
               AND (CAST(:dataInicial AS LocalDate) IS NULL OR h.duracao.dataFinal >= :dataInicial)
               AND (CAST(:dataFinal AS LocalDate) IS NULL OR h.duracao.dataInicial <= :dataFinal)
             ORDER BY h.duracao.dataInicial DESC, h.id DESC
            """)
    List<Hospedagem> listar(@Param("nome") String nome,
                            @Param("status") Collection<HospedagamStatus> status,
                            @Param("cadastroId") Integer cadastroId,
                            @Param("dataInicial") LocalDate dataInicial,
                            @Param("dataFinal") LocalDate dataFinal);
}
