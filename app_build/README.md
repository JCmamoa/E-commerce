# 🛒 E-Commerce Enterprise (Java + React Web SPA)

Sistema completo de comércio eletrônico corporativo desenvolvido em **Java 21**, projetado sob princípios de **Clean Architecture** e acompanhado por duas interfaces de usuário sincronizadas:
1. **Interface de Linha de Comando (CLI)**: Terminal interativo de console com formatação ANSI, tabela de produtos, catálogo, busca, gestão de carrinho e checkout.
2. **Interface Web Moderna (SPA)**: Frontend desenvolvido com **React**, estilizado com **Tailwind CSS**, ícones **Lucide** e fotos de alta resolução do **Unsplash**.

---

## 🏗️ Estrutura Arquitetural

```text
app_build/
├── pom.xml                                   # Configuração Maven / Java 21
├── src/
│   ├── main/
│   │   ├── java/com/ecommerce/
│   │   │   ├── model/                       # Modelos de Domínio
│   │   │   │   ├── Categoria.java           # Categorias de produtos
│   │   │   │   ├── Cliente.java             # Entidade cliente com dados cadastrais
│   │   │   │   ├── ItemCarrinho.java        # Produto associado com quantidade e subtotal
│   │   │   │   ├── Pedido.java              # Pedido com itens, endereço, pagamento e status
│   │   │   │   ├── Produto.java             # Entidade produto com estoque e avaliações
│   │   │   │   └── StatusPedido.java        # Ciclo de vida do pedido
│   │   │   ├── service/                     # Camada de Regras de Negócio e Serviços
│   │   │   │   ├── CarrinhoService.java     # Gestão de itens, quantidades e limites de estoque
│   │   │   │   ├── ClienteService.java      # Gerenciamento de clientes e login
│   │   │   │   ├── PedidoService.java       # Processamento de compras com baixa de estoque
│   │   │   │   └── ProdutoService.java      # Catálogo, buscas, filtros e dados semente
│   │   │   ├── cli/                         # Interface de Linha de Comando
│   │   │   │   ├── ConsoleColors.java       # Paleta e formatação ANSI para terminal
│   │   │   │   └── EcommerceCLI.java        # Menu interativo e navegação por console
│   │   │   ├── server/                      # Servidor HTTP e Endpoints REST
│   │   │   │   ├── ApiHandler.java          # Rotas /api/* para frontend
│   │   │   │   ├── HttpServerApp.java       # Servidor HTTP Java embutido com Virtual Threads
│   │   │   │   └── JsonUtils.java           # Serialização e parsing JSON leve
│   │   │   └── Main.java                    # Ponto de entrada unificado
│   │   └── resources/
│   │       └── web/                         # Frontend Web SPA
│   │           ├── index.html               # Aplicação com Tailwind CSS e React
│   │           ├── app.js                   # Componentes React (Catálogo, Drawer, Checkout)
│   │           └── styles.css               # Efeitos visuais e glassmorphism
└── README.md
```

---

## 🚀 Como Executar o Sistema

### Pré-requisitos
- **Java JDK 21** instalado no sistema.

### Opção 1: Compilação e Execução Direta (Sem necessidade de Maven instalado)
No diretório `app_build/`:
```bash
# 1. Compilar todas as classes Java
javac -encoding UTF-8 -d bin $(Get-ChildItem -Path src/main/java -Filter *.java -Recurse | ForEach-Object FullName)

# 2. Executar a aplicação
java -cp bin com.ecommerce.Main
```

### Opção 2: Execução via Maven
```bash
mvn compile
mvn exec:java -Dexec.mainClass="com.ecommerce.Main"
```

---

## 🌐 Acesso à Interface Web
Assim que a aplicação é iniciada, o servidor embutido fica ativo em:
👉 **http://localhost:8080**

### Recursos da Interface Web:
- **Catálogo Dinâmico**: Grid responsivo com fotos reais (Unsplash), avaliação com estrelas e preços em BRL.
- **Busca em Tempo Real e Filtros**: Filtragem instantânea por digitação ou pílulas de categoria.
- **Drawer de Carrinho**: Gaveta deslizante lateral com contagem de itens, barra de progresso para frete grátis e cálculo automático.
- **Checkout Multi-etapas**: Confirmação de dados, endereço e simulação de pagamento via PIX, Cartão ou Boleto.
- **Histórico de Pedidos**: Visualização dos pedidos já realizados com status atualizado.

---

## 💻 Interface de Linha de Comando (CLI)
Pelo terminal interativo, você pode navegar por todas as funcionalidades do sistema com facilidade:
1. Listar Catálogo Completo em tabela ANSI
2. Buscar produtos por termo ou categoria
3. Ver detalhes e adicionar produtos ao carrinho
4. Gerenciar itens do carrinho e alterar quantidades
5. Concluir compra passo a passo no terminal
6. Consultar histórico de pedidos e status
7. Alternar ou cadastrar novos clientes
