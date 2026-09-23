package com.ecommerce.service;

import com.ecommerce.model.Categoria;
import com.ecommerce.model.Produto;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

public class ProdutoService {
    private final Map<Long, Produto> produtos = new ConcurrentHashMap<>();
    private final AtomicLong idCounter = new AtomicLong(1);

    public ProdutoService() {
        carregarProdutosIniciais();
    }

    private void carregarProdutosIniciais() {
        // Eletrônicos
        cadastrar(new Produto(
                idCounter.getAndIncrement(),
                "Smartphone Galaxy Pro Max 256GB",
                "Tela AMOLED de 6.7 polegadas, câmera quádrupla de 108MP, processador Octa-Core ultra veloz e bateria de 5000mAh com carregamento turbo.",
                3899.90,
                15,
                Categoria.ELETRONICOS,
                "https://images.unsplash.com/photo-1598327105666-5b89351aff97?auto=format&fit=crop&w=800&q=80",
                4.9,
                128
        ));

        cadastrar(new Produto(
                idCounter.getAndIncrement(),
                "Notebook Ultra Slim 16GB SSD 512GB",
                "Design elegante em alumínio anodizado, processador Intel Core i7 de 13ª geração, tela Full HD anti-reflexo e teclado retroiluminado.",
                4799.00,
                8,
                Categoria.ELETRONICOS,
                "https://images.unsplash.com/photo-1496181133206-80ce9b88a853?auto=format&fit=crop&w=800&q=80",
                4.8,
                94
        ));

        cadastrar(new Produto(
                idCounter.getAndIncrement(),
                "Fone de Ouvido Bluetooth Noise Cancelling",
                "Cancelamento ativo de ruído inteligente, drivers de neodímio de 40mm, bateria com até 40 horas de reprodução contínua.",
                599.90,
                25,
                Categoria.ELETRONICOS,
                "https://images.unsplash.com/photo-1505740420928-5e560c06d30e?auto=format&fit=crop&w=800&q=80",
                4.7,
                210
        ));

        cadastrar(new Produto(
                idCounter.getAndIncrement(),
                "Smartwatch Fitness Resistente à Água",
                "Monitoramento de frequência cardíaca, oxímetro de pulso, GPS integrado, compatível com iOS e Android com tela Always-On.",
                449.00,
                20,
                Categoria.ELETRONICOS,
                "https://images.unsplash.com/photo-1523275335684-37898b6baf30?auto=format&fit=crop&w=800&q=80",
                4.6,
                87
        ));

        // Moda
        cadastrar(new Produto(
                idCounter.getAndIncrement(),
                "Tênis Esportivo Runner Air Comfort",
                "Amortecimento em espuma reativa, cabedal em malha respirável, sola antiderrapante ideal para corridas e treinos de alta performance.",
                299.90,
                18,
                Categoria.MODA,
                "https://images.unsplash.com/photo-1542291026-7eec264c27ff?auto=format&fit=crop&w=800&q=80",
                4.8,
                340
        ));

        cadastrar(new Produto(
                idCounter.getAndIncrement(),
                "Jaqueta Corta-Vento Streetwear Urbana",
                "Tecido impermeável e resistente ao vento, bolsos laterais com zíper selado, capuz ajustável e forro térmico respirável.",
                219.00,
                12,
                Categoria.MODA,
                "https://images.unsplash.com/photo-1551028719-00167b16eac5?auto=format&fit=crop&w=800&q=80",
                4.5,
                65
        ));

        cadastrar(new Produto(
                idCounter.getAndIncrement(),
                "Mochila Executiva Impermeável com Porta USB",
                "Compartimento acolchoado para notebook de até 16 polegadas, sistema antifurto, porta USB externa para carregamento portátil.",
                189.90,
                30,
                Categoria.MODA,
                "https://images.unsplash.com/photo-1553062407-98eeb64c6a62?auto=format&fit=crop&w=800&q=80",
                4.9,
                142
        ));

        // Casa & Decoração
        cadastrar(new Produto(
                idCounter.getAndIncrement(),
                "Cafeteira Expresso Automática em Inox",
                "Bomba de 15 bar de pressão italiana, vaporizador integrado para cappuccino cremoso, reservatório removível de 1.5 litros.",
                689.90,
                10,
                Categoria.CASA,
                "https://images.unsplash.com/photo-1517668808822-9ebb02f2a0e6?auto=format&fit=crop&w=800&q=80",
                4.7,
                78
        ));

        cadastrar(new Produto(
                idCounter.getAndIncrement(),
                "Luminária Articulada Minimalista LED",
                "Três modos de temperatura de cor, controle de intensidade touch, haste articulada e base antiderrapante em alumínio escovado.",
                139.90,
                22,
                Categoria.CASA,
                "https://images.unsplash.com/photo-1507473885765-e6ed057f782c?auto=format&fit=crop&w=800&q=80",
                4.6,
                53
        ));

        // Esportes & Lazer
        cadastrar(new Produto(
                idCounter.getAndIncrement(),
                "Kit Halteres Ajustáveis 20kg com Estojo",
                "Barras maciças em aço cromado com rosca estrela de segurança, anilhas emborrachadas e pegada ergonômica anti-deslizante.",
                329.90,
                14,
                Categoria.ESPORTES,
                "https://images.unsplash.com/photo-1584735935682-2f2b69dff9d2?auto=format&fit=crop&w=800&q=80",
                4.8,
                119
        ));

        cadastrar(new Produto(
                idCounter.getAndIncrement(),
                "Garrafa Térmica Inox 1000ml a Vácuo",
                "Mantém bebidas geladas por até 24 horas e quentes por até 12 horas. Tampa hermética com vedação de silicone e alça prática.",
                89.90,
                45,
                Categoria.ESPORTES,
                "https://images.unsplash.com/photo-1602143407151-7111542de6e8?auto=format&fit=crop&w=800&q=80",
                4.9,
                312
        ));

        // Beleza & Cuidado
        cadastrar(new Produto(
                idCounter.getAndIncrement(),
                "Kit Skincare Facial Sérum Vitamina C + Ácido Hialurônico",
                "Fórmula dermatologicamente testada, potente ação antioxidante, hidratação profunda e redução de linhas finas.",
                159.90,
                28,
                Categoria.BELEZA,
                "https://images.unsplash.com/photo-1608248597359-0f6222b93df9?auto=format&fit=crop&w=800&q=80",
                4.7,
                91
        ));
    }

    public List<Produto> listarTodos() {
        return new ArrayList<>(produtos.values());
    }

    public Optional<Produto> buscarPorId(Long id) {
        if (id == null) return Optional.empty();
        return Optional.ofNullable(produtos.get(id));
    }

    public List<Produto> buscar(String termo, String categoriaNome) {
        return produtos.values().stream()
                .filter(p -> {
                    boolean bateCategoria = true;
                    if (categoriaNome != null && !categoriaNome.trim().isEmpty() && !categoriaNome.equalsIgnoreCase("todas")) {
                        bateCategoria = p.getCategoria() != null &&
                                (p.getCategoria().name().equalsIgnoreCase(categoriaNome) ||
                                 p.getCategoria().getDescricao().equalsIgnoreCase(categoriaNome));
                    }

                    boolean bateTermo = true;
                    if (termo != null && !termo.trim().isEmpty()) {
                        String termoLower = termo.toLowerCase().trim();
                        bateTermo = p.getNome().toLowerCase().contains(termoLower) ||
                                   p.getDescricao().toLowerCase().contains(termoLower) ||
                                   (p.getCategoria() != null && p.getCategoria().getDescricao().toLowerCase().contains(termoLower));
                    }

                    return bateCategoria && bateTermo;
                })
                .sorted(Comparator.comparing(Produto::getNome))
                .collect(Collectors.toList());
    }

    public boolean verificarEstoque(Long produtoId, int quantidade) {
        Produto p = produtos.get(produtoId);
        return p != null && p.temEstoque(quantidade);
    }

    public boolean debitarEstoque(Long produtoId, int quantidade) {
        Produto p = produtos.get(produtoId);
        return p != null && p.debitarEstoque(quantidade);
    }

    public void reporEstoque(Long produtoId, int quantidade) {
        Produto p = produtos.get(produtoId);
        if (p != null) {
            p.reporEstoque(quantidade);
        }
    }

    public Produto cadastrar(Produto produto) {
        if (produto.getId() == null) {
            produto.setId(idCounter.getAndIncrement());
        }
        produtos.put(produto.getId(), produto);
        return produto;
    }
}
