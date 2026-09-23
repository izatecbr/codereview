package sis.pousada.service;

import sis.pousada.dao.HospedagemJPA;
import sis.pousada.modelo.hospedagem.Duracao;
import sis.pousada.modelo.hospedagem.HospedagamStatus;
import sis.pousada.modelo.hospedagem.Hospedagem;
import sis.pousada.modelo.hospedagem.Hospede;
import sis.pousada.modelo.hospedagem.UnidadeLocacao;

import java.util.List;

public class HospedagemService {

    private static final List<HospedagamStatus> STATUS_RESERVAS = List.of(HospedagamStatus.RESERVADA);
    private static final List<HospedagamStatus> STATUS_HOSPEDAGENS =
            List.of(HospedagamStatus.HOSPEDADA, HospedagamStatus.FINALIZADA);

    private final HospedagemJPA hospedagemJPA = new HospedagemJPA();

    public Hospedagem incluir(Hospedagem hospedagem) {
        prepararParaGravar(hospedagem);
        return hospedagemJPA.incluir(hospedagem);
    }

    public Hospedagem alterar(Hospedagem hospedagem) {
        prepararParaGravar(hospedagem);
        return hospedagemJPA.alterar(hospedagem);
    }

    public List<Hospedagem> listarReservas(String nomeHospede) {
        return hospedagemJPA.listarPorHospede(nomeHospede, STATUS_RESERVAS);
    }

    public List<Hospedagem> listarHospedagens(String nomeHospede) {
        return hospedagemJPA.listarPorHospede(nomeHospede, STATUS_HOSPEDAGENS);
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
            throw new IllegalArgumentException("Informe a acomodação e o número da unidade.");
        }
    }
}
