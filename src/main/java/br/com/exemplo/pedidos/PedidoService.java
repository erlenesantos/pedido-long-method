package br.com.exemplo.pedidos;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Logger;

public class PedidoService {

    private static final Logger LOG = Logger.getLogger(PedidoService.class.getName());

    private static final String PRODUTO_NOTEBOOK = "Notebook";
    private static final String PRODUTO_MONITOR = "Monitor";
    private static final String PRODUTO_TECLADO = "Teclado";

    private static final String PAGAMENTO_PIX = "PIX";
    private static final String PAGAMENTO_CARTAO = "CARTAO";
    private static final String PAGAMENTO_BOLETO = "BOLETO";

    private static final String ESTADO_CE = "CE";
    private static final String ESTADO_SP = "SP";
    private static final String ESTADO_RJ = "RJ";

    private static final String STATUS_APROVADO = "APROVADO";
    private static final String STATUS_ANALISE = "PENDENTE_ANALISE";
    private static final String STATUS_ESTOQUE_OK = "ESTOQUE_OK";
    private static final String STATUS_REPOSICAO = "REPOSICAO_RECOMENDADA";

    private static final double DESCONTO_CLIENTE_FREQUENTE = 0.05;
    private static final double DESCONTO_CAMPANHA = 0.08;
    private static final double ICMS = 0.12;
    private static final double PIS = 0.0165;
    private static final double COFINS = 0.076;
    private static final double FRETE_PADRAO = 49.90;
    private static final double LIMITE_ANALISE_FINANCEIRA = 6000.00;
    private static final int ESTOQUE_MINIMO_ALERTA = 5;

    private static final DateTimeFormatter FORMATADOR_DATA =
            DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");

    private static final Map<String, Double> PRECOS = Map.of(
            PRODUTO_NOTEBOOK, 3500.00,
            PRODUTO_MONITOR, 900.00,
            PRODUTO_TECLADO, 180.00
    );

    private static final Map<String, Integer> ESTOQUE_INICIAL = Map.of(
            PRODUTO_NOTEBOOK, 15,
            PRODUTO_MONITOR, 20,
            PRODUTO_TECLADO, 50
    );

    private static final Map<String, Double> FRETE_POR_ESTADO = Map.of(
            ESTADO_CE, 22.90,
            ESTADO_SP, 39.90,
            ESTADO_RJ, 42.90
    );

    private static final Map<String, Double> TAXA_PAGAMENTO = Map.of(
            PAGAMENTO_PIX, 0.00,
            PAGAMENTO_CARTAO, 0.029,
            PAGAMENTO_BOLETO, 3.99
    );

    public void processarPedido(Pedido pedido) {
        log("Iniciando processamento operacional do pedido.");

        if (pedido == null) {
            throw new IllegalArgumentException("O pedido não pode ser nulo.");
        }

        if (pedido.nomeCliente().isBlank()) {
            throw new IllegalArgumentException("O nome do cliente é obrigatório.");
        }

        if (!pedido.emailCliente().contains("@")) {
            throw new IllegalArgumentException("O e-mail do cliente é inválido.");
        }

        if (!PRECOS.containsKey(pedido.produto())) {
            throw new IllegalArgumentException("O produto informado não existe no catálogo.");
        }

        if (pedido.quantidade() <= 0) {
            throw new IllegalArgumentException("A quantidade deve ser maior que zero.");
        }

        if (!TAXA_PAGAMENTO.containsKey(pedido.formaPagamento())) {
            throw new IllegalArgumentException("A forma de pagamento informada é inválida.");
        }

        log("Etapa 01: pedido recebido para validação.");
        log("Etapa 02: dados básicos do cliente conferidos.");
        log("Etapa 03: e-mail do cliente validado.");
        log("Etapa 04: produto localizado no catálogo.");
        log("Etapa 05: quantidade solicitada validada.");
        log("Etapa 06: forma de pagamento reconhecida.");
        log("Etapa 07: endereço de entrega preparado para cálculo logístico.");

        String protocolo = UUID.randomUUID().toString();
        String dataProcessamento = LocalDateTime.now().format(FORMATADOR_DATA);

        log("Protocolo gerado: " + protocolo);
        log("Data do processamento: " + dataProcessamento);
        log("Cliente: " + pedido.nomeCliente());
        log("E-mail: " + pedido.emailCliente());
        log("Produto: " + pedido.produto());
        log("Quantidade: " + pedido.quantidade());
        log("Forma de pagamento: " + pedido.formaPagamento());
        log("Estado de entrega: " + pedido.estadoEntrega());

        double precoUnitario = PRECOS.get(pedido.produto());
        int estoqueAtual = ESTOQUE_INICIAL.get(pedido.produto());
        int estoqueFinal = estoqueAtual - pedido.quantidade();

        log("Etapa 08: preço unitário recuperado.");
        log("Preço unitário: " + moeda(precoUnitario));
        log("Etapa 09: estoque atual recuperado.");
        log("Estoque atual: " + estoqueAtual);
        log("Etapa 10: estoque final projetado.");
        log("Estoque final projetado: " + estoqueFinal);

        double valorProdutos = precoUnitario * pedido.quantidade();
        double descontoCliente = valorProdutos * DESCONTO_CLIENTE_FREQUENTE;
        double descontoCampanha = valorProdutos * DESCONTO_CAMPANHA;
        double valorDescontos = descontoCliente + descontoCampanha;
        double valorDepoisDescontos = valorProdutos - valorDescontos;

        log("Etapa 11: valor bruto dos produtos calculado.");
        log("Valor bruto dos produtos: " + moeda(valorProdutos));
        log("Etapa 12: desconto de cliente frequente calculado.");
        log("Desconto de cliente frequente: " + moeda(descontoCliente));
        log("Etapa 13: desconto de campanha calculado.");
        log("Desconto de campanha: " + moeda(descontoCampanha));
        log("Etapa 14: descontos consolidados.");
        log("Total de descontos: " + moeda(valorDescontos));
        log("Etapa 15: valor após descontos calculado.");
        log("Valor após descontos: " + moeda(valorDepoisDescontos));

        double freteBase = FRETE_POR_ESTADO.getOrDefault(pedido.estadoEntrega(), FRETE_PADRAO);
        double pesoEstimado = pedido.quantidade() * 1.75;
        double adicionalLogistico = pesoEstimado * 2.10;
        double valorFrete = freteBase + adicionalLogistico;

        log("Etapa 16: frete base definido.");
        log("Frete base: " + moeda(freteBase));
        log("Etapa 17: peso estimado calculado.");
        log("Peso estimado: " + String.format(Locale.US, "%.2f kg", pesoEstimado));
        log("Etapa 18: adicional logístico calculado.");
        log("Adicional logístico: " + moeda(adicionalLogistico));
        log("Etapa 19: valor total do frete calculado.");
        log("Valor do frete: " + moeda(valorFrete));

        double baseImpostos = valorDepoisDescontos;
        double valorIcms = baseImpostos * ICMS;
        double valorPis = baseImpostos * PIS;
        double valorCofins = baseImpostos * COFINS;
        double totalImpostos = valorIcms + valorPis + valorCofins;

        log("Etapa 20: base de impostos definida.");
        log("Base de impostos: " + moeda(baseImpostos));
        log("Etapa 21: ICMS calculado.");
        log("Valor de ICMS: " + moeda(valorIcms));
        log("Etapa 22: PIS calculado.");
        log("Valor de PIS: " + moeda(valorPis));
        log("Etapa 23: COFINS calculado.");
        log("Valor de COFINS: " + moeda(valorCofins));
        log("Etapa 24: total de impostos consolidado.");
        log("Total de impostos: " + moeda(totalImpostos));

        double taxaPercentualPagamento = TAXA_PAGAMENTO.get(pedido.formaPagamento());
        double taxaPagamento = valorDepoisDescontos * taxaPercentualPagamento;
        double valorAntesArredondamento = valorDepoisDescontos + valorFrete + totalImpostos + taxaPagamento;
        double valorFinal = Math.round(valorAntesArredondamento * 100.0) / 100.0;

        log("Etapa 25: taxa percentual de pagamento recuperada.");
        log("Taxa percentual de pagamento: " + percentual(taxaPercentualPagamento));
        log("Etapa 26: taxa monetária de pagamento calculada.");
        log("Taxa de pagamento: " + moeda(taxaPagamento));
        log("Etapa 27: valor antes do arredondamento calculado.");
        log("Valor antes do arredondamento: " + moeda(valorAntesArredondamento));
        log("Etapa 28: valor final arredondado.");
        log("Valor final: " + moeda(valorFinal));

        String statusPagamento = valorFinal > LIMITE_ANALISE_FINANCEIRA
                ? STATUS_ANALISE
                : STATUS_APROVADO;

        String statusEstoque = estoqueFinal < ESTOQUE_MINIMO_ALERTA
                ? STATUS_REPOSICAO
                : STATUS_ESTOQUE_OK;

        log("Etapa 29: status de pagamento definido.");
        log("Status de pagamento: " + statusPagamento);
        log("Etapa 30: status de estoque definido.");
        log("Status de estoque: " + statusEstoque);

        String numeroNota = "NF-" + protocolo.substring(0, 8).toUpperCase(Locale.ROOT);
        String chaveAcesso = "CHAVE-" + protocolo.replace("-", "").toUpperCase(Locale.ROOT);

        log("Etapa 31: número da nota fiscal gerado.");
        log("Número da nota fiscal: " + numeroNota);
        log("Etapa 32: chave de acesso gerada.");
        log("Chave de acesso: " + chaveAcesso);

        String resumoFinanceiro = "Resumo financeiro: bruto="
                + moeda(valorProdutos)
                + ", descontos="
                + moeda(valorDescontos)
                + ", frete="
                + moeda(valorFrete)
                + ", impostos="
                + moeda(totalImpostos)
                + ", taxaPagamento="
                + moeda(taxaPagamento)
                + ", final="
                + moeda(valorFinal);

        log("Etapa 33: resumo financeiro montado.");
        log(resumoFinanceiro);

        String resumoOperacional = "Resumo operacional: protocolo="
                + protocolo
                + ", produto="
                + pedido.produto()
                + ", quantidade="
                + pedido.quantidade()
                + ", estoqueInicial="
                + estoqueAtual
                + ", estoqueFinal="
                + estoqueFinal
                + ", statusEstoque="
                + statusEstoque;

        log("Etapa 34: resumo operacional montado.");
        log(resumoOperacional);

        String resumoCliente = "Resumo do cliente: nome="
                + pedido.nomeCliente()
                + ", email="
                + pedido.emailCliente()
                + ", estadoEntrega="
                + pedido.estadoEntrega()
                + ", pagamento="
                + pedido.formaPagamento();

        log("Etapa 35: resumo do cliente montado.");
        log(resumoCliente);

        String mensagemConfirmacao = "Pedido "
                + protocolo
                + " processado para "
                + pedido.nomeCliente()
                + " com valor final de "
                + moeda(valorFinal)
                + " e status "
                + statusPagamento
                + ".";

        log("Etapa 36: mensagem de confirmação montada.");
        log(mensagemConfirmacao);

        log("Etapa 37: pedido registrado no histórico operacional.");
        log("Etapa 38: dados preparados para emissão fiscal.");
        log("Etapa 39: dados preparados para integração logística.");
        log("Etapa 40: dados preparados para comunicação com o cliente.");
        log("Etapa 41: dados preparados para baixa de estoque.");
        log("Etapa 42: dados preparados para conciliação financeira.");
        log("Etapa 43: dados preparados para painel administrativo.");
        log("Etapa 44: dados preparados para auditoria interna.");
        log("Etapa 45: processamento do pedido concluído.");
    }

    private static String moeda(double valor) {
        return String.format(Locale.US, "R$ %.2f", valor);
    }

    private static String percentual(double valor) {
        return String.format(Locale.US, "%.2f%%", valor * 100);
    }

    private static void log(String mensagem) {
        LOG.info(mensagem);
    }

    public record Pedido(
            String nomeCliente,
            String emailCliente,
            String produto,
            int quantidade,
            String formaPagamento,
            String estadoEntrega
    ) {
    }

    public static void main(String[] args) {
        PedidoService service = new PedidoService();

        Pedido pedido = new Pedido(
                "João Silva",
                "joao@email.com",
                PRODUTO_NOTEBOOK,
                2,
                PAGAMENTO_CARTAO,
                ESTADO_CE
        );

        service.processarPedido(pedido);
    }
}