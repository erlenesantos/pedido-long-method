import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

public class PedidoService {

    private static final Map<String, Integer> ESTOQUE = new HashMap<>();
    private static final Map<String, Double> PRECOS = new HashMap<>();

    static {
        ESTOQUE.put("Notebook", 15);
        ESTOQUE.put("Mouse", 80);
        ESTOQUE.put("Teclado", 40);
        ESTOQUE.put("Monitor", 10);

        PRECOS.put("Notebook", 3500.00);
        PRECOS.put("Mouse", 80.00);
        PRECOS.put("Teclado", 150.00);
        PRECOS.put("Monitor", 900.00);
    }

    public void processarPedido(
            String nomeCliente,
            String emailCliente,
            String cpfCliente,
            String produto,
            int quantidade,
            String formaPagamento,
            String cupomDesconto,
            String enderecoEntrega
    ) {
        System.out.println("===========================================");
        System.out.println("INICIANDO PROCESSAMENTO DO PEDIDO");
        System.out.println("===========================================");

        if (nomeCliente == null || nomeCliente.trim().isEmpty()) {
            System.out.println("Erro: nome do cliente não informado.");
            return;
        }

        if (nomeCliente.length() < 3) {
            System.out.println("Erro: nome do cliente deve ter pelo menos 3 caracteres.");
            return;
        }

        if (emailCliente == null || emailCliente.trim().isEmpty()) {
            System.out.println("Erro: e-mail não informado.");
            return;
        }

        if (!emailCliente.contains("@") || !emailCliente.contains(".")) {
            System.out.println("Erro: e-mail inválido.");
            return;
        }

        if (cpfCliente == null || cpfCliente.trim().isEmpty()) {
            System.out.println("Erro: CPF não informado.");
            return;
        }

        if (cpfCliente.length() != 11) {
            System.out.println("Erro: CPF deve conter 11 dígitos.");
            return;
        }

        if (produto == null || produto.trim().isEmpty()) {
            System.out.println("Erro: produto não informado.");
            return;
        }

        if (!PRECOS.containsKey(produto)) {
            System.out.println("Erro: produto não encontrado no catálogo.");
            return;
        }

        if (quantidade <= 0) {
            System.out.println("Erro: quantidade deve ser maior que zero.");
            return;
        }

        if (formaPagamento == null || formaPagamento.trim().isEmpty()) {
            System.out.println("Erro: forma de pagamento não informada.");
            return;
        }

        if (!formaPagamento.equalsIgnoreCase("PIX")
                && !formaPagamento.equalsIgnoreCase("CARTAO")
                && !formaPagamento.equalsIgnoreCase("BOLETO")) {
            System.out.println("Erro: forma de pagamento inválida.");
            return;
        }

        if (enderecoEntrega == null || enderecoEntrega.trim().isEmpty()) {
            System.out.println("Erro: endereço de entrega não informado.");
            return;
        }

        if (enderecoEntrega.length() < 10) {
            System.out.println("Erro: endereço de entrega muito curto.");
            return;
        }

        System.out.println("Cliente validado com sucesso.");
        System.out.println("Nome: " + nomeCliente);
        System.out.println("E-mail: " + emailCliente);
        System.out.println("CPF: " + cpfCliente);

        System.out.println("-------------------------------------------");
        System.out.println("Validando produto e estoque...");
        System.out.println("Produto solicitado: " + produto);
        System.out.println("Quantidade solicitada: " + quantidade);

        int quantidadeDisponivel = ESTOQUE.get(produto);

        if (quantidadeDisponivel <= 0) {
            System.out.println("Erro: produto sem estoque.");
            return;
        }

        if (quantidade > quantidadeDisponivel) {
            System.out.println("Erro: quantidade solicitada maior que o estoque disponível.");
            System.out.println("Estoque disponível: " + quantidadeDisponivel);
            return;
        }

        double precoUnitario = PRECOS.get(produto);
        double valorBruto = precoUnitario * quantidade;
        double descontoPorQuantidade = 0.0;
        double descontoPorCupom = 0.0;
        double taxaPagamento = 0.0;
        double frete = 0.0;

        System.out.println("-------------------------------------------");
        System.out.println("Calculando valores do pedido...");

        if (quantidade >= 5 && quantidade < 10) {
            descontoPorQuantidade = valorBruto * 0.05;
        } else if (quantidade >= 10 && quantidade < 20) {
            descontoPorQuantidade = valorBruto * 0.10;
        } else if (quantidade >= 20) {
            descontoPorQuantidade = valorBruto * 0.15;
        }

        if (cupomDesconto != null && !cupomDesconto.trim().isEmpty()) {
            if (cupomDesconto.equalsIgnoreCase("PROMO10")) {
                descontoPorCupom = valorBruto * 0.10;
            } else if (cupomDesconto.equalsIgnoreCase("PROMO20")) {
                descontoPorCupom = valorBruto * 0.20;
            } else if (cupomDesconto.equalsIgnoreCase("FRETEGRATIS")) {
                descontoPorCupom = 0.0;
            } else {
                System.out.println("Cupom informado não é válido. O pedido seguirá sem desconto de cupom.");
            }
        }

        if (formaPagamento.equalsIgnoreCase("PIX")) {
            taxaPagamento = 0.0;
        } else if (formaPagamento.equalsIgnoreCase("CARTAO")) {
            taxaPagamento = valorBruto * 0.03;
        } else if (formaPagamento.equalsIgnoreCase("BOLETO")) {
            taxaPagamento = 4.90;
        }

        if (valorBruto >= 5000) {
            frete = 0.0;
        } else if (cupomDesconto != null && cupomDesconto.equalsIgnoreCase("FRETEGRATIS")) {
            frete = 0.0;
        } else if (quantidade <= 2) {
            frete = 25.00;
        } else if (quantidade <= 5) {
            frete = 40.00;
        } else {
            frete = 70.00;
        }

        double valorComDescontos = valorBruto - descontoPorQuantidade - descontoPorCupom;

        if (valorComDescontos < 0) {
            valorComDescontos = 0;
        }

        double imposto = valorComDescontos * 0.08;
        double valorFinal = valorComDescontos + imposto + taxaPagamento + frete;

        System.out.println("Preço unitário: R$ " + precoUnitario);
        System.out.println("Valor bruto: R$ " + valorBruto);
        System.out.println("Desconto por quantidade: R$ " + descontoPorQuantidade);
        System.out.println("Desconto por cupom: R$ " + descontoPorCupom);
        System.out.println("Taxa de pagamento: R$ " + taxaPagamento);
        System.out.println("Frete: R$ " + frete);
        System.out.println("Imposto: R$ " + imposto);
        System.out.println("Valor final: R$ " + valorFinal);

        System.out.println("-------------------------------------------");
        System.out.println("Analisando pagamento...");

        String statusPagamento;

        if (formaPagamento.equalsIgnoreCase("PIX")) {
            if (valorFinal <= 10000) {
                statusPagamento = "APROVADO";
            } else {
                statusPagamento = "PENDENTE_ANALISE";
            }
        } else if (formaPagamento.equalsIgnoreCase("CARTAO")) {
            if (valorFinal <= 3000) {
                statusPagamento = "APROVADO";
            } else if (valorFinal <= 8000) {
                statusPagamento = "PENDENTE_ANALISE";
            } else {
                statusPagamento = "RECUSADO";
            }
        } else {
            if (valorFinal <= 2000) {
                statusPagamento = "BOLETO_GERADO";
            } else {
                statusPagamento = "PENDENTE_ANALISE";
            }
        }

        System.out.println("Status do pagamento: " + statusPagamento);

        if (statusPagamento.equals("RECUSADO")) {
            System.out.println("Pedido não finalizado porque o pagamento foi recusado.");
            return;
        }

        System.out.println("-------------------------------------------");
        System.out.println("Atualizando estoque...");

        int novoEstoque = quantidadeDisponivel - quantidade;
        ESTOQUE.put(produto, novoEstoque);

        System.out.println("Estoque anterior: " + quantidadeDisponivel);
        System.out.println("Estoque atualizado: " + novoEstoque);

        System.out.println("-------------------------------------------");
        System.out.println("Gerando nota fiscal...");

        String dataAtual = LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss"));
        String numeroNotaFiscal = "NF-" + System.currentTimeMillis();

        System.out.println("Nota fiscal gerada: " + numeroNotaFiscal);
        System.out.println("Data de emissão: " + dataAtual);

        System.out.println("-------------------------------------------");
        System.out.println("Resumo do pedido");
        System.out.println("Cliente: " + nomeCliente);
        System.out.println("Produto: " + produto);
        System.out.println("Quantidade: " + quantidade);
        System.out.println("Forma de pagamento: " + formaPagamento);
        System.out.println("Endereço de entrega: " + enderecoEntrega);
        System.out.println("Valor final: R$ " + valorFinal);
        System.out.println("Status do pagamento: " + statusPagamento);
        System.out.println("Nota fiscal: " + numeroNotaFiscal);

        System.out.println("-------------------------------------------");
        System.out.println("Simulando envio de e-mail...");

        String mensagemEmail = "Olá, " + nomeCliente
                + ". Seu pedido do produto " + produto
                + " foi processado com o status " + statusPagamento
                + ". Valor final: R$ " + valorFinal
                + ". Nota fiscal: " + numeroNotaFiscal + ".";

        System.out.println("Enviando e-mail para: " + emailCliente);
        System.out.println("Mensagem: " + mensagemEmail);

        System.out.println("-------------------------------------------");
        System.out.println("Finalizando pedido...");

        if (statusPagamento.equals("APROVADO")) {
            System.out.println("Pedido finalizado com sucesso e liberado para entrega.");
        } else if (statusPagamento.equals("BOLETO_GERADO")) {
            System.out.println("Pedido registrado. Aguardando pagamento do boleto.");
        } else if (statusPagamento.equals("PENDENTE_ANALISE")) {
            System.out.println("Pedido registrado, mas depende de análise manual.");
        } else {
            System.out.println("Pedido registrado com status indefinido.");
        }

        System.out.println("===========================================");
        System.out.println("PROCESSAMENTO ENCERRADO");
        System.out.println("===========================================");
    }

    public static void main(String[] args) {
        PedidoService service = new PedidoService();

        service.processarPedido(
                "João Silva",
                "joao@email.com",
                "12345678901",
                "Notebook",
                2,
                "CARTAO",
                "PROMO10",
                "Rua das Flores, 123 - Centro"
        );
    }
}