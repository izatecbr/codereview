package sis.pousada.api;

public class ApiException extends RuntimeException {

    private final int status;

    public ApiException(int status, String mensagem) {
        super(mensagem);
        this.status = status;
    }

    public int getStatus() {
        return status;
    }

    // 400: a API recusou os dados (validação); a mensagem já é amigável para o usuário.
    public boolean isRequisicaoInvalida() {
        return status == 400;
    }
}
