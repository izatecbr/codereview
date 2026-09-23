package codereview.sis.pousada.repository;

import codereview.sis.pousada.modelo.cadastro.Cadastro;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CadastroRepository extends JpaRepository<Cadastro, Integer> {

    List<Cadastro> findByNomeContainingIgnoreCaseOrderByNome(String nome);
}
