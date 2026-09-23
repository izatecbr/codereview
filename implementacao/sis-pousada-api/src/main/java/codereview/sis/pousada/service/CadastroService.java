package codereview.sis.pousada.service;

import codereview.sis.pousada.modelo.cadastro.Cadastro;
import codereview.sis.pousada.repository.CadastroRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.NoSuchElementException;

@Service
public class CadastroService {

    private final CadastroRepository cadastroRepository;

    public CadastroService(CadastroRepository cadastroRepository) {
        this.cadastroRepository = cadastroRepository;
    }

    @Transactional
    public Cadastro incluir(Cadastro cadastro) {
        validar(cadastro);
        cadastro.setId(null);
        return cadastroRepository.save(cadastro);
    }

    @Transactional
    public Cadastro alterar(Cadastro cadastro) {
        if (cadastro.getId() == null || !cadastroRepository.existsById(cadastro.getId())) {
            throw new NoSuchElementException("Cadastro não encontrado.");
        }
        validar(cadastro);
        return cadastroRepository.save(cadastro);
    }

    @Transactional(readOnly = true)
    public Cadastro buscarPorId(Integer id) {
        return cadastroRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Cadastro não encontrado."));
    }

    @Transactional(readOnly = true)
    public List<Cadastro> listarPorNome(String nome) {
        return cadastroRepository.findByNomeContainingIgnoreCaseOrderByNome(nome == null ? "" : nome.trim());
    }

    private void validar(Cadastro cadastro) {
        if (cadastro.getNome() == null || cadastro.getNome().isBlank()) {
            throw new IllegalArgumentException("Informe o nome.");
        }
        if (cadastro.getCpfCnpj() == null || cadastro.getCpfCnpj().isBlank()) {
            throw new IllegalArgumentException("Informe o CPF/CNPJ.");
        }
    }
}
