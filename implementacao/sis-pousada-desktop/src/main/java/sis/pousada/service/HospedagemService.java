package sis.pousada.service;

import com.fasterxml.jackson.core.type.TypeReference;
import sis.pousada.api.ApiClient;
import sis.pousada.modelo.hospedagem.HospedagamStatus;
import sis.pousada.modelo.hospedagem.Hospedagem;
import sis.pousada.modelo.hospedagem.UnidadeLocacao;

import java.util.List;
import java.util.Map;

public class HospedagemService {

    private static final String CAMINHO = "/api/hospedagens";
    private static final String CAMINHO_RESERVAS = CAMINHO + "/reservas";

    private final ApiClient api = ApiClient.instancia();

    // Reserva e hospedagem têm endpoints de criação diferentes (o status é definido pela API).
    public Hospedagem incluir(Hospedagem hospedagem) {
        String caminho = hospedagem.getStatus() == HospedagamStatus.RESERVADA ? CAMINHO_RESERVAS : CAMINHO;
        return api.post(caminho, hospedagem, Hospedagem.class);
    }

    public Hospedagem alterar(Hospedagem hospedagem) {
        return api.put(CAMINHO + "/" + hospedagem.getId(), hospedagem, Hospedagem.class);
    }

    public List<Hospedagem> listarReservas(String nomeHospede) {
        return api.get(CAMINHO_RESERVAS, filtroNome(nomeHospede), new TypeReference<List<Hospedagem>>() { });
    }

    public List<Hospedagem> listarHospedagens(String nomeHospede) {
        return api.get(CAMINHO, filtroNome(nomeHospede), new TypeReference<List<Hospedagem>>() { });
    }

    // Apenas prévia para a tela; quem grava o valor definitivo é a API.
    public double calcularValorTotal(Hospedagem hospedagem) {
        HospedagamStatus status = hospedagem.getStatus();
        boolean cobra = status == HospedagamStatus.HOSPEDADA || status == HospedagamStatus.FINALIZADA;

        UnidadeLocacao unidade = hospedagem.getUnidadeLocacao();
        if (!cobra || unidade == null || unidade.getValorDiaria() == null || hospedagem.getDuracao() == null) {
            return 0;
        }
        return hospedagem.getDuracao().contarDias() * unidade.getValorDiaria();
    }

    private Map<String, String> filtroNome(String nomeHospede) {
        return Map.of("hospede", nomeHospede == null ? "" : nomeHospede.trim());
    }
}
