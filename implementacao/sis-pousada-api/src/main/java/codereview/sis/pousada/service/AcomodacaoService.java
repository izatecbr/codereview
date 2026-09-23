package codereview.sis.pousada.service;

import codereview.sis.pousada.modelo.acomodacao.Acomodacao;
import codereview.sis.pousada.repository.AcomodacaoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.NoSuchElementException;

@Service
public class AcomodacaoService {

    private final AcomodacaoRepository acomodacaoRepository;

    public AcomodacaoService(AcomodacaoRepository acomodacaoRepository) {
        this.acomodacaoRepository = acomodacaoRepository;
    }

    @Transactional
    public Acomodacao incluir(Acomodacao acomodacao) {
        validar(acomodacao);
        acomodacao.setId(null);
        return acomodacaoRepository.save(acomodacao);
    }

    @Transactional
    public Acomodacao alterar(Acomodacao acomodacao) {
        if (acomodacao.getId() == null || !acomodacaoRepository.existsById(acomodacao.getId())) {
            throw new NoSuchElementException("Acomodação não encontrada.");
        }
        validar(acomodacao);
        return acomodacaoRepository.save(acomodacao);
    }

    @Transactional(readOnly = true)
    public Acomodacao buscarPorId(Integer id) {
        return acomodacaoRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Acomodação não encontrada."));
    }

    @Transactional(readOnly = true)
    public List<Acomodacao> listarPorLegenda(String legenda) {
        return acomodacaoRepository.findByLegendaContainingIgnoreCaseOrderByLegenda(
                legenda == null ? "" : legenda.trim());
    }

    private void validar(Acomodacao acomodacao) {
        if (acomodacao.getLegenda() == null || acomodacao.getLegenda().isBlank()) {
            throw new IllegalArgumentException("Informe a legenda.");
        }
        if (acomodacao.getNumero() == null || acomodacao.getNumero().isBlank()) {
            throw new IllegalArgumentException("Informe o número da acomodação.");
        }
        if (acomodacao.getDescricao() == null || acomodacao.getDescricao().isBlank()) {
            throw new IllegalArgumentException("Informe a descrição.");
        }
        if (acomodacao.getTipo() == null) {
            throw new IllegalArgumentException("Informe o tipo.");
        }
    }
}
