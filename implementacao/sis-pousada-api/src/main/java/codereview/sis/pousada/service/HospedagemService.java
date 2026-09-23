package codereview.sis.pousada.service;

import codereview.sis.pousada.modelo.hospedagem.Duracao;
import codereview.sis.pousada.modelo.hospedagem.HospedagamStatus;
import codereview.sis.pousada.modelo.hospedagem.Hospedagem;
import codereview.sis.pousada.modelo.hospedagem.Hospede;
import codereview.sis.pousada.modelo.hospedagem.UnidadeLocacao;
import codereview.sis.pousada.repository.HospedagemRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.NoSuchElementException;

@Service
public class HospedagemService {

    private static final List<HospedagamStatus> STATUS_RESERVAS = List.of(HospedagamStatus.RESERVADA);
    private static final List<HospedagamStatus> STATUS_HOSPEDAGENS =
            List.of(HospedagamStatus.HOSPEDADA, HospedagamStatus.FINALIZADA);

    private final HospedagemRepository hospedagemRepository;

    public HospedagemService(HospedagemRepository hospedagemRepository) {
        this.hospedagemRepository = hospedagemRepository;
    }

    @Transactional
    public Hospedagem incluir(Hospedagem hospedagem) {
        prepararParaGravar(hospedagem);
        hospedagem.setId(null);
        return hospedagemRepository.save(hospedagem);
    }

    @Transactional
    public Hospedagem alterar(Hospedagem hospedagem) {
        if (hospedagem.getId() == null || !hospedagemRepository.existsById(hospedagem.getId())) {
            throw new NoSuchElementException("Hospedagem não encontrada.");
        }
        prepararParaGravar(hospedagem);
        return hospedagemRepository.save(hospedagem);
    }

    @Transactional(readOnly = true)
    public Hospedagem buscarPorId(Integer id) {
        return hospedagemRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Hospedagem não encontrada."));
    }

    @Transactional(readOnly = true)
    public List<Hospedagem> listarReservas(String nomeHospede) {
        return hospedagemRepository.listarPorHospede(normalizar(nomeHospede), STATUS_RESERVAS);
    }

    @Transactional(readOnly = true)
    public List<Hospedagem> listarHospedagens(String nomeHospede) {
        return hospedagemRepository.listarPorHospede(normalizar(nomeHospede), STATUS_HOSPEDAGENS);
    }

    // Só hospedagem de fato gera valor; reserva (e cancelada) fica com zero.
    public double calcularValorTotal(Hospedagem hospedagem) {
        HospedagamStatus status = hospedagem.getStatus();
        boolean cobra = status == HospedagamStatus.HOSPEDADA || status == HospedagamStatus.FINALIZADA;

        UnidadeLocacao unidade = hospedagem.getUnidadeLocacao();
        if (!cobra || unidade == null || unidade.getValorDiaria() == null || hospedagem.getDuracao() == null) {
            return 0;
        }
        return hospedagem.getDuracao().contarDias() * unidade.getValorDiaria();
    }

    private String normalizar(String nome) {
        return nome == null ? "" : nome.trim();
    }

    private void prepararParaGravar(Hospedagem hospedagem) {
        validar(hospedagem);

        if (hospedagem.getStatus() == HospedagamStatus.RESERVADA) {
            hospedagem.setUnidadeLocacao(null);
        }
        hospedagem.setValorTotal(calcularValorTotal(hospedagem));
    }

    private void validar(Hospedagem hospedagem) {
        if (hospedagem.getStatus() == null) {
            throw new IllegalArgumentException("Informe o status.");
        }

        Hospede hospede = hospedagem.getHospede();
        if (hospede == null || hospede.getId() <= 0 || hospede.getNome() == null || hospede.getNome().isBlank()) {
            throw new IllegalArgumentException("Informe o hóspede.");
        }

        Duracao duracao = hospedagem.getDuracao();
        if (duracao == null || duracao.getDataInicial() == null || duracao.getDataFinal() == null) {
            throw new IllegalArgumentException("Informe a data inicial e a data final.");
        }
        if (!duracao.getDataFinal().isAfter(duracao.getDataInicial())) {
            throw new IllegalArgumentException("A data final deve ser posterior à data inicial.");
        }

        boolean exigeUnidade = hospedagem.getStatus() == HospedagamStatus.HOSPEDADA
                || hospedagem.getStatus() == HospedagamStatus.FINALIZADA;
        UnidadeLocacao unidade = hospedagem.getUnidadeLocacao();
        if (exigeUnidade && (unidade == null || unidade.getId() == null
                || unidade.getNumero() == null || unidade.getNumero().isBlank())) {
            throw new IllegalArgumentException("Informe a acomodação (ela precisa ter número cadastrado).");
        }
    }
}
