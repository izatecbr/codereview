package codereview.sis.pousada;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class TmpHospedagemControllerTest {

    @Autowired
    MockMvc mvc;

    @Test
    void fluxo() throws Exception {
        String reserva = "{\"hospede\":{\"id\":1},\"duracao\":{\"dataInicial\":\"2026-10-01\",\"dataFinal\":\"2026-10-04\"}}";
        String hospedagem = "{\"hospede\":{\"id\":1},\"unidadeLocacao\":{\"id\":2},\"duracao\":{\"dataInicial\":\"2026-10-01\",\"dataFinal\":\"2026-10-04\"}}";
        String invalida = "{\"hospede\":{\"id\":999},\"duracao\":{\"dataInicial\":\"2026-10-01\",\"dataFinal\":\"2026-10-04\"}}";

        System.out.println("### POST reserva");
        mvc.perform(post("/api/hospedagens/reservas").contentType(MediaType.APPLICATION_JSON).content(reserva)).andDo(print());
        System.out.println("### POST hospedagem");
        mvc.perform(post("/api/hospedagens").contentType(MediaType.APPLICATION_JSON).content(hospedagem)).andDo(print());
        System.out.println("### POST hospede inexistente");
        mvc.perform(post("/api/hospedagens/reservas").contentType(MediaType.APPLICATION_JSON).content(invalida)).andDo(print());
        System.out.println("### GET reservas");
        mvc.perform(get("/api/hospedagens/reservas")).andDo(print());
        System.out.println("### GET hospedagens");
        mvc.perform(get("/api/hospedagens")).andDo(print());
        System.out.println("### GET api-docs");
        mvc.perform(get("/v3/api-docs")).andDo(result -> System.out.println("STATUS docs=" + result.getResponse().getStatus()));
    }
}
