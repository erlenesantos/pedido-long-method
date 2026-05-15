public class PedidoService {

    public void processarPedido(String nomeCliente, String emailCliente, String produto, int quantidade, double precoUnitario) {
        System.out.println("Iniciando processamento do pedido...");

        if (nomeCliente == null || nomeCliente.isEmpty()) {
            System.out.println("Erro: nome do cliente inválido.");
            return;
        }

        if (emailCliente == null || emailCliente.isEmpty() || !emailCliente.contains("@")) {
            System.out.println("Erro: e-mail do cliente inválido.");
            return;
        }

        if (produto == null || produto.isEmpty()) {
            System.out.println("Erro: produto inválido.");
            return;
        }

        if (quantidade <= 0) {
            System.out.println("Erro: quantidade inválida.");
            return;
        }

        if (precoUnitario <= 0) {
            System.out.println("Erro: preço unitário inválido.");
            return;
        }

        System.out.println("Dados do cliente validados.");
        System.out.println("Cliente: " + nomeCliente);
        System.out.println("E-mail: " + emailCliente);

        System.out.println("Dados do produto validados.");
        System.out.println("Produto: " + produto);
        System.out.println("Quantidade: " + quantidade);
        System.out.println("Preço unitário: R$ " + precoUnitario);

        double valorBruto = quantidade * precoUnitario;
        double desconto = 0.0;

        if (quantidade >= 10 && quantidade < 20) {
            desconto = valorBruto * 0.05;
        } else if (quantidade >= 20 && quantidade < 50) {
            desconto = valorBruto * 0.10;
        } else if (quantidade >= 50) {
            desconto = valorBruto * 0.15;
        }

        double valorComDesconto = valorBruto - desconto;
        double imposto = valorComDesconto * 0.08;
        double valorFinal = valorComDesconto + imposto;

        System.out.println("Calculando valores do pedido...");
        System.out.println("Valor bruto: R$ " + valorBruto);
        System.out.println("Desconto aplicado: R$ " + desconto);
        System.out.println("Valor com desconto: R$ " + valorComDesconto);
        System.out.println("Imposto: R$ " + imposto);
        System.out.println("Valor final: R$ " + valorFinal);

        String statusPagamento = "pendente";

        if (valorFinal <= 100) {
            statusPagamento = "pagamento aprovado automaticamente";
        } else if (valorFinal > 100 && valorFinal <= 1000) {
            statusPagamento = "pagamento em análise";
        } else {
            statusPagamento = "pagamento requer aprovação manual";
        }

        System.out.println("Status do pagamento: " + statusPagamento);

        String statusEstoque;

        if (quantidade <= 5) {
            statusEstoque = "estoque disponível";
        } else if (quantidade <= 20) {
            statusEstoque = "estoque precisa ser verificado";
        } else {
            statusEstoque = "estoque insuficiente ou sujeito à aprovação";
        }

        System.out.println("Status do estoque: " + statusEstoque);

        if (statusEstoque.equals("estoque disponível")) {
            System.out.println("Separando produto no estoque...");
            System.out.println("Produto separado com sucesso.");
        } else if (statusEstoque.equals("estoque precisa ser verificado")) {
            System.out.println("Solicitando verificação manual do estoque...");
            System.out.println("Aguardando confirmação do setor responsável.");
        } else {
            System.out.println("Pedido não pode ser finalizado automaticamente.");
            System.out.println("Motivo: quantidade solicitada acima do limite disponível.");
        }

        System.out.println("Gerando nota fiscal...");

        String numeroNotaFiscal = "NF-" + System.currentTimeMillis();

        System.out.println("Número da nota fiscal: " + numeroNotaFiscal);
        System.out.println("Cliente da nota fiscal: " + nomeCliente);
        System.out.println("Produto da nota fiscal: " + produto);
        System.out.println("Valor da nota fiscal: R$ " + valorFinal);

        System.out.println("Enviando e-mail para o cliente...");

        String mensagemEmail = "Olá, " + nomeCliente + ". Seu pedido do produto " + produto
                + " foi processado. Valor final: R$ " + valorFinal
                + ". Status do pagamento: " + statusPagamento
                + ". Nota fiscal: " + numeroNotaFiscal;

        System.out.println("E-mail enviado para: " + emailCliente);
        System.out.println("Mensagem: " + mensagemEmail);

        System.out.println("Registrando pedido no sistema...");

        String registroPedido = "Cliente: " + nomeCliente
                + " | Email: " + emailCliente
                + " | Produto: " + produto
                + " | Quantidade: " + quantidade
                + " | Valor Final: " + valorFinal
                + " | Status Pagamento: " + statusPagamento
                + " | Status Estoque: " + statusEstoque
                + " | Nota Fiscal: " + numeroNotaFiscal;

        System.out.println("Registro criado: " + registroPedido);

        System.out.println("Finalizando processamento do pedido...");

        if (statusPagamento.equals("pagamento aprovado automaticamente") && statusEstoque.equals("estoque disponível")) {
            System.out.println("Pedido finalizado com sucesso.");
        } else {
            System.out.println("Pedido registrado, mas depende de análise adicional.");
        }

        System.out.println("Processamento encerrado.");
    }

    public static void main(String[] args) {
        PedidoService service = new PedidoService();
        service.processarPedido(
                "João Silva",
                "joao@email.com",
                "Notebook",
                12,
                3500.00
        );
    }
}