package sis.pousada.service;

import com.fasterxml.jackson.core.type.TypeReference;
import sis.pousada.api.ApiClient;
import sis.pousada.modelo.acomodacao.Acomodacao;

import java.util.List;
import java.util.Map;

public class AcomodacaoService {

    private static final String CAMINHO = "/api/acomodacoes";

    private final ApiClient api = ApiClient.instancia();

    public Acomodacao incluir(Acomodacao acomodacao) {
        return api.post(CAMINHO, acomodacao, Acomodacao.class);
    }

    public Acomodacao alterar(Acomodacao acomodacao) {
        return api.put(CAMINHO + "/" + acomodacao.getId(), acomodacao, Acomodacao.class);
    }

    public List<Acomodacao> listarPorLegenda(String legenda) {
        return api.get(CAMINHO, Map.of("legenda", legenda == null ? "" : legenda.trim()),
                new TypeReference<List<Acomodacao>>() { });
    }

    public List<Acomodacao> listar() {
        return listarPorLegenda("");
    }
}
