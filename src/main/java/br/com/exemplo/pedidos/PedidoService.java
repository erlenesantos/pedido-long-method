package br.com.exemplo.pedidos;

import java.util.logging.Logger;

public class PedidoService {

    private static final Logger LOG = Logger.getLogger(PedidoService.class.getName());

    private static final double LIMITE_DESCONTO_CLIENTE_VIP = 500.00;
    private static final double LIMITE_ANALISE_FINANCEIRA = 6000.00;
    private static final double LIMITE_FRETE_GRATIS = 3000.00;
    private static final double TAXA_CARTAO = 0.03;
    private static final double TAXA_BOLETO = 4.90;
    private static final double ICMS = 0.12;
    private static final double DESCONTO_CLIENTE_VIP = 0.08;
    private static final double DESCONTO_PRIMEIRA_COMPRA = 0.05;
    private static final double DESCONTO_CUPOM = 0.10;
    private static final double FRETE_CE = 25.00;
    private static final double FRETE_SP = 45.00;
    private static final double FRETE_RJ = 50.00;
    private static final double FRETE_PADRAO = 70.00;

    public ResultadoPedido processarPedido(Pedido pedido) {
        if (pedido == null) {
            throw new IllegalArgumentException("Pedido inválido");
        }

        if (pedido.nomeCliente() == null || pedido.nomeCliente().isBlank()) {
            throw new IllegalArgumentException("Nome do cliente inválido");
        }

        if (pedido.emailCliente() == null || !pedido.emailCliente().contains("@")) {
            throw new IllegalArgumentException("E-mail do cliente inválido");
        }

        if (pedido.produto() == null) {
            throw new IllegalArgumentException("Produto inválido");
        }

        if (pedido.quantidade() <= 0) {
            throw new IllegalArgumentException("Quantidade inválida");
        }

        if (pedido.formaPagamento() == null) {
            throw new IllegalArgumentException("Forma de pagamento inválida");
        }

        if (pedido.estadoEntrega() == null) {
            throw new IllegalArgumentException("Estado de entrega inválido");
        }

        double valorBruto = pedido.produto().preco() * pedido.quantidade();
        double desconto = 0.0;
        double frete = FRETE_PADRAO;
        double taxaPagamento = 0.0;
        int pontosRisco = 0;

        if (pedido.clienteVip()) {
            desconto += valorBruto * DESCONTO_CLIENTE_VIP;
        }

        if (pedido.primeiraCompra()) {
            desconto += valorBruto * DESCONTO_PRIMEIRA_COMPRA;
        }

        if (pedido.cupomValido()) {
            desconto += valorBruto * DESCONTO_CUPOM;
        }

        if (pedido.quantidade() >= 5) {
            desconto += valorBruto * 0.03;
        }

        if (pedido.quantidade() >= 10) {
            desconto += valorBruto * 0.05;
        }

        if (pedido.produto() == Produto.NOTEBOOK) {
            pontosRisco += 2;
        }

        if (pedido.produto() == Produto.MONITOR) {
            pontosRisco += 1;
        }

        if (pedido.quantidade() > pedido.produto().estoqueDisponivel()) {
            pontosRisco += 5;
        }

        if (pedido.quantidade() == pedido.produto().estoqueDisponivel()) {
            pontosRisco += 2;
        }

        if (pedido.produto().estoqueDisponivel() - pedido.quantidade() < 3) {
            pontosRisco += 2;
        }

        if (pedido.formaPagamento() == FormaPagamento.CARTAO) {
            taxaPagamento = valorBruto * TAXA_CARTAO;
        }

        if (pedido.formaPagamento() == FormaPagamento.BOLETO) {
            taxaPagamento = TAXA_BOLETO;
        }

        if (pedido.formaPagamento() == FormaPagamento.PIX) {
            desconto += valorBruto * 0.02;
        }

        if (pedido.estadoEntrega() == EstadoEntrega.CE) {
            frete = FRETE_CE;
        }

        if (pedido.estadoEntrega() == EstadoEntrega.SP) {
            frete = FRETE_SP;
        }

        if (pedido.estadoEntrega() == EstadoEntrega.RJ) {
            frete = FRETE_RJ;
        }

        if (valorBruto >= LIMITE_FRETE_GRATIS) {
            frete = 0.0;
        }

        if (desconto > LIMITE_DESCONTO_CLIENTE_VIP) {
            pontosRisco += 1;
        }

        if (valorBruto > LIMITE_ANALISE_FINANCEIRA) {
            pontosRisco += 3;
        }

        if (pedido.clienteVip() && valorBruto <= LIMITE_ANALISE_FINANCEIRA) {
            pontosRisco -= 1;
        }

        if (pedido.primeiraCompra() && pedido.formaPagamento() == FormaPagamento.BOLETO) {
            pontosRisco += 1;
        }

        if (pedido.cupomValido() && pedido.clienteVip()) {
            pontosRisco += 1;
        }

        if (pedido.estadoEntrega() == EstadoEntrega.OUTRO) {
            pontosRisco += 2;
        }

        double valorComDesconto = valorBruto - desconto;

        if (valorComDesconto < 0) {
            valorComDesconto = 0;
        }

        double imposto = valorComDesconto * ICMS;
        double valorFinal = valorComDesconto + imposto + frete + taxaPagamento;

        StatusPedido status = StatusPedido.APROVADO;

        if (pontosRisco >= 5) {
            status = StatusPedido.PENDENTE_ANALISE;
        }

        if (pedido.quantidade() > pedido.produto().estoqueDisponivel()) {
            status = StatusPedido.SEM_ESTOQUE;
        }

        if (valorFinal > LIMITE_ANALISE_FINANCEIRA && pedido.formaPagamento() == FormaPagamento.CARTAO) {
            status = StatusPedido.PENDENTE_ANALISE;
        }

        return new ResultadoPedido(
                valorBruto,
                desconto,
                frete,
                imposto,
                taxaPagamento,
                valorFinal,
                pontosRisco,
                status
        );
    }

    public enum Produto {
        NOTEBOOK(3500.00, 15),
        MONITOR(900.00, 20),
        TECLADO(180.00, 50);

        private final double preco;
        private final int estoqueDisponivel;

        Produto(double preco, int estoqueDisponivel) {
            this.preco = preco;
            this.estoqueDisponivel = estoqueDisponivel;
        }

        public double preco() {
            return preco;
        }

        public int estoqueDisponivel() {
            return estoqueDisponivel;
        }
    }

    public enum FormaPagamento {
        PIX,
        CARTAO,
        BOLETO
    }

    public enum EstadoEntrega {
        CE,
        SP,
        RJ,
        OUTRO
    }

    public enum StatusPedido {
        APROVADO,
        PENDENTE_ANALISE,
        SEM_ESTOQUE
    }

    public record Pedido(
            String nomeCliente,
            String emailCliente,
            Produto produto,
            int quantidade,
            FormaPagamento formaPagamento,
            EstadoEntrega estadoEntrega,
            boolean clienteVip,
            boolean primeiraCompra,
            boolean cupomValido
    ) {
    }

    public record ResultadoPedido(
            double valorBruto,
            double desconto,
            double frete,
            double imposto,
            double taxaPagamento,
            double valorFinal,
            int pontosRisco,
            StatusPedido status
    ) {
    }

    public static void main(String[] args) {
        PedidoService service = new PedidoService();

        Pedido pedido = new Pedido(
                "João Silva",
                "joao@email.com",
                Produto.NOTEBOOK,
                2,
                FormaPagamento.CARTAO,
                EstadoEntrega.CE,
                true,
                true,
                true
        );

        ResultadoPedido resultado = service.processarPedido(pedido);

        LOG.info(resultado.toString());
    }
}