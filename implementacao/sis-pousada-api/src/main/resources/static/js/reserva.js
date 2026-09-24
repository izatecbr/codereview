const URL_RESERVAS = '/api/hospedagens/reservas';

const form = document.getElementById('form-reserva');
const campoCliente = document.getElementById('clienteId');
const campoEntrada = document.getElementById('dataEntrada');
const campoSaida = document.getElementById('dataSaida');
const botao = document.getElementById('btnReservar');
const mensagem = document.getElementById('mensagem');

// yyyy-MM-dd no fuso local (toISOString usaria UTC)
function formatarIso(data) {
    const mes = String(data.getMonth() + 1).padStart(2, '0');
    const dia = String(data.getDate()).padStart(2, '0');
    return `${data.getFullYear()}-${mes}-${dia}`;
}

function formatarBr(iso) {
    const [ano, mes, dia] = iso.split('-');
    return `${dia}/${mes}/${ano}`;
}

function contarDiarias(inicio, fim) {
    return Math.round((new Date(fim) - new Date(inicio)) / 86400000);
}

function mostrarMensagem(tipo, texto) {
    mensagem.className = `alert alert-${tipo}`;
    mensagem.textContent = texto;
}

function limparMensagem() {
    mensagem.className = 'alert d-none';
    mensagem.textContent = '';
}

campoEntrada.min = formatarIso(new Date());

campoEntrada.addEventListener('change', () => {
    if (!campoEntrada.value) {
        return;
    }
    const minimoSaida = new Date(campoEntrada.value + 'T00:00:00');
    minimoSaida.setDate(minimoSaida.getDate() + 1);
    campoSaida.min = formatarIso(minimoSaida);
    if (campoSaida.value && campoSaida.value < campoSaida.min) {
        campoSaida.value = '';
    }
});

function formularioValido() {
    const clienteOk = Number.isInteger(Number(campoCliente.value)) && Number(campoCliente.value) > 0;
    const entradaOk = campoEntrada.value !== '';
    const saidaOk = campoSaida.value !== '' && campoSaida.value > campoEntrada.value;

    campoCliente.classList.toggle('is-invalid', !clienteOk);
    campoEntrada.classList.toggle('is-invalid', !entradaOk);
    campoSaida.classList.toggle('is-invalid', !saidaOk);
    return clienteOk && entradaOk && saidaOk;
}

form.addEventListener('submit', async (evento) => {
    evento.preventDefault();
    limparMensagem();

    if (!formularioValido()) {
        return;
    }

    const reserva = {
        hospede: { id: Number(campoCliente.value) },
        duracao: { dataInicial: campoEntrada.value, dataFinal: campoSaida.value }
    };

    botao.disabled = true;
    botao.textContent = 'Reservando...';

    try {
        const resposta = await fetch(URL_RESERVAS, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(reserva)
        });
        const dados = await resposta.json().catch(() => ({}));

        if (!resposta.ok) {
            throw new Error(dados.detail || `A API respondeu com erro ${resposta.status}.`);
        }

        const diarias = contarDiarias(dados.duracao.dataInicial, dados.duracao.dataFinal);
        mostrarMensagem('success',
            `Reserva nº ${dados.id} confirmada para ${dados.hospede.nome}, ` +
            `de ${formatarBr(dados.duracao.dataInicial)} a ${formatarBr(dados.duracao.dataFinal)} ` +
            `(${diarias} ${diarias === 1 ? 'diária' : 'diárias'}).`);
        form.reset();
        campoSaida.min = '';
    } catch (erro) {
        const semConexao = erro instanceof TypeError;
        mostrarMensagem('danger', semConexao ? 'Não foi possível conectar à API.' : erro.message);
    } finally {
        botao.disabled = false;
        botao.textContent = 'Reservar';
    }
});
