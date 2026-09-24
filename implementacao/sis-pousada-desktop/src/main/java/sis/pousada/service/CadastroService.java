package sis.pousada.service;

import com.fasterxml.jackson.core.type.TypeReference;
import sis.pousada.api.ApiClient;
import sis.pousada.modelo.cadastro.Cadastro;

import java.util.List;
import java.util.Map;

public class CadastroService {

    private static final String CAMINHO = "/api/cadastros";

    private final ApiClient api = ApiClient.instancia();

    public Cadastro incluir(Cadastro cadastro) {
        return api.post(CAMINHO, cadastro, Cadastro.class);
    }

    public Cadastro alterar(Cadastro cadastro) {
        return api.put(CAMINHO + "/" + cadastro.getId(), cadastro, Cadastro.class);
    }

    public List<Cadastro> listarPorNome(String nome) {
        return api.get(CAMINHO, Map.of("nome", nome == null ? "" : nome.trim()),
                new TypeReference<List<Cadastro>>() { });
    }
}
