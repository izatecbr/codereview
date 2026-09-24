package sis.pousada.api;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.Map;
import java.util.Properties;
import java.util.stream.Collectors;

public class ApiClient {

    private static final ApiClient INSTANCIA = new ApiClient();

    private final HttpClient http = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(5))
            .build();

    private final ObjectMapper mapper = new ObjectMapper()
            .registerModule(new JavaTimeModule())
            .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
            .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);

    private final String urlBase;

    private ApiClient() {
        this.urlBase = carregarUrlBase();
    }

    public static ApiClient instancia() {
        return INSTANCIA;
    }

    public String getUrlBase() {
        return urlBase;
    }

    // Basta a API responder (qualquer status); só falha se não houver conexão.
    public void verificarConexao() {
        try {
            http.send(requisicao("", null).GET().build(), HttpResponse.BodyHandlers.discarding());
        } catch (IOException e) {
            throw new ApiException(0, "Não foi possível se comunicar com a API em " + urlBase + ".");
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new ApiException(0, "Requisição interrompida.");
        }
    }

    public <T> T get(String caminho, Map<String, String> parametros, TypeReference<T> tipo) {
        return enviar(requisicao(caminho, parametros).GET().build(), mapper.getTypeFactory().constructType(tipo));
    }

    public <T> T get(String caminho, Class<T> tipo) {
        return enviar(requisicao(caminho, null).GET().build(), mapper.constructType(tipo));
    }

    public <T> T post(String caminho, Object corpo, Class<T> tipo) {
        return enviar(requisicao(caminho, null).POST(json(corpo)).build(), mapper.constructType(tipo));
    }

    public <T> T put(String caminho, Object corpo, Class<T> tipo) {
        return enviar(requisicao(caminho, null).PUT(json(corpo)).build(), mapper.constructType(tipo));
    }

    private HttpRequest.Builder requisicao(String caminho, Map<String, String> parametros) {
        StringBuilder url = new StringBuilder(urlBase).append(caminho);
        if (parametros != null && !parametros.isEmpty()) {
            url.append('?').append(parametros.entrySet().stream()
                    .map(p -> p.getKey() + "=" + URLEncoder.encode(p.getValue(), StandardCharsets.UTF_8))
                    .collect(Collectors.joining("&")));
        }
        return HttpRequest.newBuilder(URI.create(url.toString()))
                .timeout(Duration.ofSeconds(30))
                .header("Accept", "application/json")
                .header("Content-Type", "application/json");
    }

    private HttpRequest.BodyPublisher json(Object corpo) {
        try {
            return HttpRequest.BodyPublishers.ofString(mapper.writeValueAsString(corpo), StandardCharsets.UTF_8);
        } catch (JsonProcessingException e) {
            throw new ApiException(0, "Não foi possível montar a requisição: " + e.getOriginalMessage());
        }
    }

    private <T> T enviar(HttpRequest requisicao, JavaType tipo) {
        try {
            HttpResponse<String> resposta = http.send(requisicao, HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));
            if (resposta.statusCode() >= 400) {
                throw new ApiException(resposta.statusCode(), extrairMensagem(resposta));
            }
            return mapper.readValue(resposta.body(), tipo);
        } catch (JsonProcessingException e) {
            throw new ApiException(0, "Resposta inválida da API.");
        } catch (IOException e) {
            throw new ApiException(0, "Não foi possível se comunicar com a API em " + urlBase + ".");
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new ApiException(0, "Requisição interrompida.");
        }
    }

    // A API devolve erros no formato ProblemDetail; o campo "detail" traz a mensagem.
    private String extrairMensagem(HttpResponse<String> resposta) {
        try {
            JsonNode detalhe = mapper.readTree(resposta.body()).path("detail");
            if (detalhe.isTextual() && !detalhe.asText().isBlank()) {
                return detalhe.asText();
            }
        } catch (IOException ignorado) {
            // corpo vazio ou não-JSON: cai na mensagem padrão
        }
        return "A API respondeu com erro " + resposta.statusCode() + ".";
    }

    private static String carregarUrlBase() {
        String url = System.getProperty("api.url");
        if (url == null || url.isBlank()) {
            Properties propriedades = new Properties();
            try (InputStream in = ApiClient.class.getResourceAsStream("/api.properties")) {
                if (in != null) {
                    propriedades.load(in);
                }
            } catch (IOException ignorado) {
                // usa o padrão abaixo
            }
            url = propriedades.getProperty("api.url", "http://localhost:8080");
        }
        return url.trim().replaceAll("/+$", "");
    }
}
