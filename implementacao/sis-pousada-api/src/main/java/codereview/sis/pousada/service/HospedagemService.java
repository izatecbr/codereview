package codereview.sis.pousada.service;

import codereview.sis.pousada.modelo.acomodacao.Acomodacao;
import codereview.sis.pousada.modelo.cadastro.Cadastro;
import codereview.sis.pousada.modelo.hospedagem.Duracao;
import codereview.sis.pousada.modelo.hospedagem.HospedagamStatus;
import codereview.sis.pousada.modelo.hospedagem.Hospedagem;
import codereview.sis.pousada.modelo.hospedagem.Hospede;
import codereview.sis.pousada.modelo.hospedagem.UnidadeLocacao;
import codereview.sis.pousada.repository.AcomodacaoRepository;
import codereview.sis.pousada.repository.CadastroRepository;
import codereview.sis.pousada.repository.HospedagemRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.NoSuchElementException;

@Service
public class HospedagemService {

    private static final List<HospedagamStatus> STATUS_RESERVAS = List.of(HospedagamStatus.RESERVADA);
    private static final List<HospedagamStatus> STATUS_HOSPEDAGENS =
            List.of(HospedagamStatus.HOSPEDADA, HospedagamStatus.FINALIZADA);

    private final HospedagemRepository hospedagemRepository;
    private final CadastroRepository cadastroRepository;
    private final AcomodacaoRepository acomodacaoRepository;

    public HospedagemService(HospedagemRepository hospedagemRepository,
                             CadastroRepository cadastroRepository,
                             AcomodacaoRepository acomodacaoRepository) {
        this.hospedagemRepository = hospedagemRepository;
        this.cadastroRepository = cadastroRepository;
        this.acomodacaoRepository = acomodacaoRepository;
    }

    @Transactional
    public Hospedagem incluir(Hospedagem hospedagem) {
        prepararParaGravar(hospedagem, null);
        hospedagem.setId(null);
        return hospedagemRepository.save(hospedagem);
    }

    @Transactional
    public Hospedagem alterar(Hospedagem hospedagem) {
        Hospedagem gravada = hospedagem.getId() == null ? null
                : hospedagemRepository.findById(hospedagem.getId()).orElse(null);
        if (gravada == null) {
            throw new NoSuchElementException("Hospedagem não encontrada.");
        }
        prepararParaGravar(hospedagem, gravada.getUnidadeLocacao());
        return hospedagemRepository.save(hospedagem);
    }

    @Transactional(readOnly = true)
    public Hospedagem buscarPorId(Integer id) {
        return hospedagemRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Hospedagem não encontrada."));
    }

    // Todos os filtros são opcionais. O período retorna estadias que sobrepõem [dataInicial, dataFinal].
    @Transactional(readOnly = true)
    public List<Hospedagem> listarReservas(String nomeHospede, Integer cadastroId,
                                           LocalDate dataInicial, LocalDate dataFinal) {
        return listar(nomeHospede, cadastroId, dataInicial, dataFinal, STATUS_RESERVAS);
    }

    @Transactional(readOnly = true)
    public List<Hospedagem> listarHospedagens(String nomeHospede, Integer cadastroId,
                                              LocalDate dataInicial, LocalDate dataFinal) {
        return listar(nomeHospede, cadastroId, dataInicial, dataFinal, STATUS_HOSPEDAGENS);
    }

    private List<Hospedagem> listar(String nomeHospede, Integer cadastroId, LocalDate dataInicial,
                                    LocalDate dataFinal, List<HospedagamStatus> status) {
        if (dataInicial != null && dataFinal != null && dataFinal.isBefore(dataInicial)) {
            throw new IllegalArgumentException("A data final do filtro não pode ser anterior à data inicial.");
        }
        return hospedagemRepository.listar(normalizar(nomeHospede), status, cadastroId, dataInicial, dataFinal);
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

    private void prepararParaGravar(Hospedagem hospedagem, UnidadeLocacao unidadeGravada) {
        if (hospedagem.getStatus() == HospedagamStatus.RESERVADA) {
            hospedagem.setUnidadeLocacao(null);
        }
        completarDados(hospedagem, unidadeGravada);
        validar(hospedagem);
        hospedagem.setValorTotal(calcularValorTotal(hospedagem));
    }

    // A requisição informa só os códigos; nome, legenda, número e diária vêm dos cadastros existentes.
    // Se a unidade não mudou em relação à já gravada, mantém o snapshot (legenda/número/diária).
    private void completarDados(Hospedagem hospedagem, UnidadeLocacao unidadeGravada) {
        Hospede hospede = hospedagem.getHospede();
        if (hospede != null && hospede.getId() > 0) {
            Cadastro cadastro = cadastroRepository.findById(hospede.getId())
                    .orElseThrow(() -> new IllegalArgumentException(
                            "Hóspede não encontrado (código " + hospede.getId() + ")."));
            hospede.setNome(cadastro.getNome());
        }

        UnidadeLocacao unidade = hospedagem.getUnidadeLocacao();
        if (unidade != null && unidade.getId() != null) {
            if (unidadeGravada != null && unidade.getId().equals(unidadeGravada.getId())) {
                unidade.setLegenda(unidadeGravada.getLegenda());
                unidade.setNumero(unidadeGravada.getNumero());
                unidade.setValorDiaria(unidadeGravada.getValorDiaria());
            } else {
                Acomodacao acomodacao = acomodacaoRepository.findById(unidade.getId())
                        .orElseThrow(() -> new IllegalArgumentException(
                                "Acomodação não encontrada (código " + unidade.getId() + ")."));
                unidade.setLegenda(acomodacao.getLegenda());
                unidade.setNumero(acomodacao.getNumero());
                unidade.setValorDiaria(acomodacao.getValorDiaria());
            }
        }
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
