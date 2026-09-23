package codereview.sis.pousada.repository;

import codereview.sis.pousada.modelo.hospedagem.HospedagamStatus;
import codereview.sis.pousada.modelo.hospedagem.Hospedagem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Collection;
import java.util.List;

public interface HospedagemRepository extends JpaRepository<Hospedagem, Integer> {

    @Query("""
            SELECT h FROM Hospedagem h
             WHERE LOWER(h.hospede.nome) LIKE LOWER(CONCAT('%', :nome, '%'))
               AND h.status IN :status
             ORDER BY h.duracao.dataInicial DESC, h.id DESC
            """)
    List<Hospedagem> listarPorHospede(@Param("nome") String nome,
                                      @Param("status") Collection<HospedagamStatus> status);
}
