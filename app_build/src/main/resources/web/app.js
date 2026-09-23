const { useState, useEffect, useMemo } = React;

// --- Ícones SVG Inspirados no Lucide ---
const Icons = {
    Search: () => (
        <svg className="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
            <circle cx="11" cy="11" r="8" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round"/>
            <line x1="21" y1="21" x2="16.65" y2="16.65" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round"/>
        </svg>
    ),
    ShoppingBag: () => (
        <svg className="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
            <path strokeLinecap="round" strokeLinejoin="round" strokeWidth="2" d="M16 11V7a4 4 0 00-8 0v4M5 9h14l1 12H4L5 9z" />
        </svg>
    ),
    Star: ({ filled }) => (
        <svg className={`w-4 h-4 ${filled ? 'text-amber-400 fill-amber-400' : 'text-slate-600'}`} viewBox="0 0 24 24" stroke="currentColor" strokeWidth="2">
            <polygon points="12 2 15.09 8.26 22 9.27 17 14.14 18.18 21.02 12 17.77 5.82 21.02 7 14.14 2 9.27 8.91 8.26 12 2" />
        </svg>
    ),
    X: () => (
        <svg className="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
            <path strokeLinecap="round" strokeLinejoin="round" strokeWidth="2" d="M6 18L18 6M6 6l12 12" />
        </svg>
    ),
    Plus: () => (
        <svg className="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
            <path strokeLinecap="round" strokeLinejoin="round" strokeWidth="2" d="M12 4v16m8-8H4" />
        </svg>
    ),
    Minus: () => (
        <svg className="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
            <path strokeLinecap="round" strokeLinejoin="round" strokeWidth="2" d="M20 12H4" />
        </svg>
    ),
    Trash: () => (
        <svg className="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
            <path strokeLinecap="round" strokeLinejoin="round" strokeWidth="2" d="M19 7l-.867 12.142A2 2 0 0116.138 21H7.862a2 2 0 01-1.995-1.858L5 7m5 4v6m4-6v6m1-10V4a1 1 0 00-1-1h-4a1 1 0 00-1 1v3M4 7h16" />
        </svg>
    ),
    Check: () => (
        <svg className="w-6 h-6 text-brand-500" fill="none" stroke="currentColor" viewBox="0 0 24 24">
            <path strokeLinecap="round" strokeLinejoin="round" strokeWidth="2" d="M5 13l4 4L19 7" />
        </svg>
    ),
    User: () => (
        <svg className="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
            <path strokeLinecap="round" strokeLinejoin="round" strokeWidth="2" d="M16 7a4 4 0 11-8 0 4 4 0 018 0zM12 14a7 7 0 00-7 7h14a7 7 0 00-7-7z" />
        </svg>
    ),
    Package: () => (
        <svg className="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
            <path strokeLinecap="round" strokeLinejoin="round" strokeWidth="2" d="M20 7l-8-4-8 4m16 0l-8 4m8-4v10l-8 4m0-10L4 7m8 4v10M4 7v10l8 4" />
        </svg>
    ),
    ShieldCheck: () => (
        <svg className="w-5 h-5 text-brand-500" fill="none" stroke="currentColor" viewBox="0 0 24 24">
            <path strokeLinecap="round" strokeLinejoin="round" strokeWidth="2" d="M9 12l2 2 4-4m5.618-4.016A11.955 11.955 0 0112 2.944a11.955 11.955 0 01-8.618 3.04A12.02 12.02 0 003 9c0 5.591 3.824 10.29 9 11.622 5.176-1.332 9-6.03 9-11.622 0-1.042-.133-2.052-.382-3.016z" />
        </svg>
    ),
    Truck: () => (
        <svg className="w-5 h-5 text-sky-400" fill="none" stroke="currentColor" viewBox="0 0 24 24">
            <path strokeLinecap="round" strokeLinejoin="round" strokeWidth="2" d="M13 16V6a1 1 0 00-1-1H4a1 1 0 00-1 1v10a1 1 0 001 1h1m8-1a1 1 0 01-1 1H9m4-1V8a1 1 0 011-1h2.586a1 1 0 01.707.293l3.414 3.414a1 1 0 01.293.707V16a1 1 0 01-1 1h-1m-6-1a1 1 0 001 1h2m-6 0a2 2 0 104 0m-4 0a2 2 0 114 0m6 0a2 2 0 104 0m-4 0a2 2 0 114 0" />
        </svg>
    )
};

// Formatação BRL
const formatarBRL = (valor) => {
    return new Intl.NumberFormat('pt-BR', { style: 'currency', currency: 'BRL' }).format(valor || 0);
};

function App() {
    // --- Estados da Aplicação (Em Memória) ---
    const [produtos, setProdutos] = useState([]);
    const [categorias, setCategorias] = useState([]);
    const [categoriaAtiva, setCategoriaAtiva] = useState('TODAS');
    const [termoBusca, setTermoBusca] = useState('');
    const [carregando, setCarregando] = useState(true);

    // Cliente e Sessão
    const [clientes, setClientes] = useState([]);
    const [clienteAtivo, setClienteAtivo] = useState(null);

    // Carrinho
    const [carrinho, setCarrinho] = useState({ itens: [], total: 0, quantidadeTotal: 0 });
    const [carrinhoAberto, setCarrinhoAberto] = useState(false);

    // Modais
    const [produtoModal, setProdutoModal] = useState(null);
    const [qtdModal, setQtdModal] = useState(1);
    const [checkoutAberto, setCheckoutAberto] = useState(false);
    const [historicoAberto, setHistoricoAberto] = useState(false);
    const [pedidos, setPedidos] = useState([]);

    // Checkout Form State
    const [formCheckout, setFormCheckout] = useState({
        endereco: '',
        metodoPagamento: 'PIX',
        numCartao: '•••• •••• •••• 4242',
        titular: 'João Silva',
        validade: '12/28',
        cvv: '123'
    });
    const [pedidoFinalizado, setPedidoFinalizado] = useState(null);
    const [processandoPedido, setProcessandoPedido] = useState(false);

    // Feedback Toast
    const [toast, setToast] = useState(null);

    const mostrarToast = (msg, tipo = 'sucesso') => {
        setToast({ msg, tipo });
        setTimeout(() => setToast(null), 3000);
    };

    // --- Carregamento Inicial ---
    useEffect(() => {
        carregarDadosIniciais();
    }, []);

    const carregarDadosIniciais = async () => {
        setCarregando(true);
        try {
            // 1. Carrega Produtos
            const resProd = await fetch('/api/produtos');
            const dataProd = await resProd.json();
            setProdutos(dataProd);

            // 2. Carrega Categorias
            const resCat = await fetch('/api/categorias');
            const dataCat = await resCat.json();
            setCategorias(dataCat);

            // 3. Carrega Clientes
            const resCli = await fetch('/api/clientes');
            const dataCli = await resCli.json();
            setClientes(dataCli);
            if (dataCli.length > 0) {
                const cliente = dataCli[0];
                setClienteAtivo(cliente);
                setFormCheckout(prev => ({ ...prev, endereco: cliente.endereco, titular: cliente.nome }));
                carregarCarrinho(cliente.id);
            }
        } catch (err) {
            console.error('Erro ao conectar ao backend Java:', err);
        } finally {
            setCarregando(false);
        }
    };

    const carregarCarrinho = async (clienteId) => {
        try {
            const res = await fetch(`/api/carrinho?clienteId=${clienteId}`);
            if (res.ok) {
                const data = await res.json();
                setCarrinho(data);
            }
        } catch (err) {
            console.error('Erro ao obter carrinho:', err);
        }
    };

    const carregarPedidos = async (clienteId) => {
        try {
            const res = await fetch(`/api/pedidos?clienteId=${clienteId}`);
            if (res.ok) {
                const data = await res.json();
                setPedidos(data);
            }
        } catch (err) {
            console.error('Erro ao carregar histórico de pedidos:', err);
        }
    };

    // --- Operações do Carrinho ---
    const adicionarAoCarrinho = async (produtoId, quantidade = 1) => {
        if (!clienteAtivo) return;
        try {
            const res = await fetch('/api/carrinho/itens', {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify({
                    clienteId: clienteAtivo.id,
                    produtoId: produtoId,
                    quantidade: quantidade
                })
            });

            if (res.ok) {
                const data = await res.json();
                setCarrinho(data);
                mostrarToast('Item adicionado ao carrinho com sucesso!');
                if (produtoModal) setProdutoModal(null);
            } else {
                const erro = await res.json();
                mostrarToast(erro.erro || 'Estoque insuficiente!', 'erro');
            }
        } catch (err) {
            mostrarToast('Erro ao comunicar com o servidor', 'erro');
        }
    };

    const alterarQuantidadeCarrinho = async (produtoId, novaQuantidade) => {
        if (!clienteAtivo) return;
        try {
            const res = await fetch('/api/carrinho/itens', {
                method: 'PUT',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify({
                    clienteId: clienteAtivo.id,
                    produtoId: produtoId,
                    quantidade: novaQuantidade
                })
            });
            if (res.ok) {
                const data = await res.json();
                setCarrinho(data);
            } else {
                const erro = await res.json();
                mostrarToast(erro.erro || 'Limite de estoque atingido!', 'erro');
            }
        } catch (err) {
            console.error(err);
        }
    };

    const removerDoCarrinho = async (produtoId) => {
        if (!clienteAtivo) return;
        try {
            const res = await fetch('/api/carrinho/itens', {
                method: 'DELETE',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify({
                    clienteId: clienteAtivo.id,
                    produtoId: produtoId
                })
            });
            if (res.ok) {
                const data = await res.json();
                setCarrinho(data);
                mostrarToast('Item removido do carrinho');
            }
        } catch (err) {
            console.error(err);
        }
    };

    // --- Finalização de Pedido ---
    const finalizarPedido = async (e) => {
        e.preventDefault();
        if (!clienteAtivo || carrinho.itens.length === 0) return;

        setProcessandoPedido(true);
        try {
            const res = await fetch('/api/pedidos', {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify({
                    clienteId: clienteAtivo.id,
                    enderecoEntrega: formCheckout.endereco,
                    metodoPagamento: formCheckout.metodoPagamento
                })
            });

            if (res.ok) {
                const pedido = await res.json();
                setPedidoFinalizado(pedido);
                setCarrinho({ itens: [], total: 0, quantidadeTotal: 0 });
                // Atualiza catálogo para refletir novo estoque
                const resProd = await fetch('/api/produtos');
                setProdutos(await resProd.json());
            } else {
                const erro = await res.json();
                mostrarToast(erro.erro || 'Erro ao processar compra', 'erro');
            }
        } catch (err) {
            mostrarToast('Erro de conexão ao processar compra', 'erro');
        } finally {
            setProcessandoPedido(false);
        }
    };

    // --- Filtros Dinâmicos ---
    const produtosFiltrados = useMemo(() => {
        return produtos.filter(p => {
            const bateCategoria = categoriaAtiva === 'TODAS' || p.categoria === categoriaAtiva;
            const termo = termoBusca.toLowerCase().trim();
            const bateBusca = !termo ||
                p.nome.toLowerCase().includes(termo) ||
                p.descricao.toLowerCase().includes(termo) ||
                (p.categoriaNome && p.categoriaNome.toLowerCase().includes(termo));
            return bateCategoria && bateBusca;
        });
    }, [produtos, categoriaAtiva, termoBusca]);

    return (
        <div className="min-h-screen flex flex-col bg-slate-950 text-slate-100">
            {/* --- Toast de Notificação --- */}
            {toast && (
                <div className={`fixed bottom-6 right-6 z-50 flex items-center gap-3 px-5 py-3 rounded-xl shadow-2xl text-sm font-semibold transition-all animate-fade-in ${
                    toast.tipo === 'erro' ? 'bg-rose-600 text-white' : 'bg-brand-600 text-white'
                }`}>
                    {toast.tipo === 'erro' ? '⚠️' : '✅'} {toast.msg}
                </div>
            )}

            {/* --- Header / Navbar --- */}
            <header className="sticky top-0 z-40 glass-nav">
                <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 h-20 flex items-center justify-between gap-4">
                    {/* Logo & Marca */}
                    <div className="flex items-center gap-3 cursor-pointer" onClick={() => { setCategoriaAtiva('TODAS'); setTermoBusca(''); }}>
                        <div className="w-11 h-11 rounded-xl bg-gradient-to-tr from-brand-600 to-emerald-400 flex items-center justify-center shadow-lg shadow-brand-500/20">
                            <Icons.ShoppingBag />
                        </div>
                        <div>
                            <span className="text-xl font-extrabold tracking-tight bg-gradient-to-r from-white via-slate-200 to-brand-400 bg-clip-text text-transparent">
                                NOVASTORE
                            </span>
                            <span className="block text-xs text-brand-400 font-medium">Enterprise E-Commerce</span>
                        </div>
                    </div>

                    {/* Barra de Busca em Tempo Real */}
                    <div className="flex-1 max-w-xl hidden md:block">
                        <div className="relative">
                            <input
                                type="text"
                                placeholder="Buscar por produto, categoria ou especificação..."
                                value={termoBusca}
                                onChange={(e) => setTermoBusca(e.target.value)}
                                className="w-full bg-slate-900/90 border border-slate-700/80 rounded-xl pl-11 pr-10 py-2.5 text-sm text-slate-200 placeholder-slate-400 focus:outline-none focus:border-brand-500 focus:ring-2 focus:ring-brand-500/20 transition-all shadow-inner"
                            />
                            <div className="absolute left-3.5 top-3 text-slate-400 pointer-events-none">
                                <Icons.Search />
                            </div>
                            {termoBusca && (
                                <button
                                    onClick={() => setTermoBusca('')}
                                    className="absolute right-3 top-3 text-slate-400 hover:text-slate-200"
                                >
                                    <Icons.X />
                                </button>
                            )}
                        </div>
                    </div>

                    {/* Ações do Header (Perfil, Pedidos, Carrinho) */}
                    <div className="flex items-center gap-3">
                        {/* Seletor de Cliente Ativo */}
                        <div className="hidden lg:flex items-center gap-2 bg-slate-900 border border-slate-800 rounded-xl px-3 py-1.5 text-xs text-slate-300">
                            <Icons.User />
                            <select
                                className="bg-transparent text-slate-200 font-medium focus:outline-none cursor-pointer"
                                value={clienteAtivo?.id || ''}
                                onChange={(e) => {
                                    const c = clientes.find(item => item.id === parseInt(e.target.value));
                                    if (c) {
                                        setClienteAtivo(c);
                                        setFormCheckout(prev => ({ ...prev, endereco: c.endereco, titular: c.nome }));
                                        carregarCarrinho(c.id);
                                    }
                                }}
                            >
                                {clientes.map(c => (
                                    <option key={c.id} value={c.id} className="bg-slate-900 text-slate-200">
                                        {c.nome} ({c.email})
                                    </option>
                                ))}
                            </select>
                        </div>

                        {/* Botão de Histórico de Pedidos */}
                        <button
                            onClick={() => {
                                if (clienteAtivo) carregarPedidos(clienteAtivo.id);
                                setHistoricoAberto(true);
                            }}
                            className="flex items-center gap-2 px-3.5 py-2 rounded-xl text-xs font-semibold text-slate-300 hover:text-white bg-slate-900 border border-slate-800 hover:border-slate-700 transition-all"
                            title="Ver Meus Pedidos"
                        >
                            <Icons.Package />
                            <span className="hidden sm:inline">Pedidos</span>
                        </button>

                        {/* Botão do Carrinho de Compras */}
                        <button
                            onClick={() => setCarrinhoAberto(true)}
                            className="relative flex items-center gap-2 px-4 py-2.5 rounded-xl bg-gradient-to-r from-brand-600 to-emerald-500 hover:from-brand-500 hover:to-emerald-400 text-white font-semibold text-sm shadow-lg shadow-brand-500/25 hover:shadow-brand-500/40 transition-all duration-200 active:scale-95"
                        >
                            <Icons.ShoppingBag />
                            <span className="hidden sm:inline">Carrinho</span>
                            {carrinho.quantidadeTotal > 0 && (
                                <span className="absolute -top-1.5 -right-1.5 w-6 h-6 rounded-full bg-amber-400 text-slate-950 font-bold text-xs flex items-center justify-center shadow-md animate-pulse">
                                    {carrinho.quantidadeTotal}
                                </span>
                            )}
                        </button>
                    </div>
                </div>

                {/* Busca Mobile */}
                <div className="md:hidden px-4 pb-3">
                    <div className="relative">
                        <input
                            type="text"
                            placeholder="Buscar produtos..."
                            value={termoBusca}
                            onChange={(e) => setTermoBusca(e.target.value)}
                            className="w-full bg-slate-900 border border-slate-700 rounded-xl pl-10 pr-4 py-2 text-sm text-slate-200 focus:outline-none focus:border-brand-500"
                        />
                        <div className="absolute left-3 top-2.5 text-slate-400 pointer-events-none">
                            <Icons.Search />
                        </div>
                    </div>
                </div>
            </header>

            {/* --- Hero Banner Promocional --- */}
            <section className="relative overflow-hidden bg-gradient-to-b from-slate-900 via-slate-950 to-slate-950 py-10 px-4 sm:px-6 lg:px-8 border-b border-slate-800/80">
                <div className="max-w-7xl mx-auto flex flex-col md:flex-row items-center justify-between gap-8">
                    <div className="max-w-xl text-center md:text-left space-y-4">
                        <div className="inline-flex items-center gap-2 px-3 py-1 rounded-full bg-brand-500/10 border border-brand-500/30 text-brand-400 text-xs font-semibold uppercase tracking-wider">
                            ✨ Ofertas Especiais de Lançamento
                        </div>
                        <h1 className="text-3xl sm:text-5xl font-extrabold text-white tracking-tight leading-tight">
                            Tecnologia & Estilo com <span className="bg-gradient-to-r from-brand-400 to-emerald-300 bg-clip-text text-transparent">Entrega Rápida</span>
                        </h1>
                        <p className="text-slate-400 text-sm sm:text-base leading-relaxed">
                            Aproveite nossa seleção premium com descontos exclusivos, parcelamento sem juros e frete grátis para todo o Brasil.
                        </p>
                        <div className="flex flex-wrap items-center justify-center md:justify-start gap-4 pt-2 text-xs text-slate-300">
                            <div className="flex items-center gap-1.5"><Icons.Truck /> Frete Grátis acima de R$ 199</div>
                            <div className="flex items-center gap-1.5"><Icons.ShieldCheck /> Garantia de 12 Meses</div>
                        </div>
                    </div>

                    <div className="relative group">
                        <div className="absolute -inset-1 bg-gradient-to-r from-brand-500 to-emerald-500 rounded-2xl blur opacity-30 group-hover:opacity-50 transition duration-1000"></div>
                        <img
                            src="https://images.unsplash.com/photo-1505740420928-5e560c06d30e?auto=format&fit=crop&w=800&q=80"
                            alt="Destaque da Loja"
                            className="relative w-80 sm:w-96 h-56 object-cover rounded-2xl shadow-2xl border border-slate-700/60"
                        />
                    </div>
                </div>
            </section>

            {/* --- Filtros por Categoria --- */}
            <section className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 pt-8 pb-4 w-full">
                <div className="flex items-center justify-between gap-4 flex-wrap">
                    <div className="flex items-center gap-2 overflow-x-auto pb-2 scrollbar-none">
                        <button
                            onClick={() => setCategoriaAtiva('TODAS')}
                            className={`px-4 py-2 rounded-xl text-xs sm:text-sm font-semibold transition-all whitespace-nowrap ${
                                categoriaAtiva === 'TODAS'
                                    ? 'bg-brand-500 text-slate-950 shadow-lg shadow-brand-500/25'
                                    : 'bg-slate-900 text-slate-300 hover:bg-slate-800 border border-slate-800'
                            }`}
                        >
                            Todas as Categorias ({produtos.length})
                        </button>
                        {categorias.map(cat => {
                            const qtd = produtos.filter(p => p.categoria === cat.id).length;
                            return (
                                <button
                                    key={cat.id}
                                    onClick={() => setCategoriaAtiva(cat.id)}
                                    className={`px-4 py-2 rounded-xl text-xs sm:text-sm font-semibold transition-all whitespace-nowrap ${
                                        categoriaAtiva === cat.id
                                            ? 'bg-brand-500 text-slate-950 shadow-lg shadow-brand-500/25'
                                            : 'bg-slate-900 text-slate-300 hover:bg-slate-800 border border-slate-800'
                                    }`}
                                >
                                    {cat.nome} ({qtd})
                                </button>
                            );
                        })}
                    </div>
                    <div className="text-xs text-slate-400">
                        Exibindo <span className="text-brand-400 font-bold">{produtosFiltrados.length}</span> produtos
                    </div>
                </div>
            </section>

            {/* --- Grid de Produtos --- */}
            <main className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-6 w-full flex-1">
                {carregando ? (
                    <div className="py-24 text-center">
                        <div className="w-12 h-12 border-4 border-brand-500/20 border-t-brand-500 rounded-full animate-spin mx-auto mb-4"></div>
                        <p className="text-slate-400 text-sm">Carregando catálogo do servidor Java...</p>
                    </div>
                ) : produtosFiltrados.length === 0 ? (
                    <div className="py-20 text-center glass-panel rounded-2xl p-8 max-w-md mx-auto">
                        <div className="w-16 h-16 rounded-full bg-slate-800 flex items-center justify-center text-3xl mx-auto mb-4">🔍</div>
                        <h3 className="text-lg font-bold text-white mb-2">Nenhum produto encontrado</h3>
                        <p className="text-slate-400 text-xs mb-6">Tente ajustar seus termos de busca ou remover os filtros de categoria.</p>
                        <button
                            onClick={() => { setCategoriaAtiva('TODAS'); setTermoBusca(''); }}
                            className="px-5 py-2.5 rounded-xl bg-slate-800 hover:bg-slate-700 text-xs font-semibold text-white transition-all"
                        >
                            Limpar Filtros
                        </button>
                    </div>
                ) : (
                    <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 xl:grid-cols-4 gap-6">
                        {produtosFiltrados.map(prod => (
                            <div
                                key={prod.id}
                                className="glass-card rounded-2xl overflow-hidden flex flex-col group"
                            >
                                {/* Imagem com Badge */}
                                <div
                                    className="relative h-52 overflow-hidden cursor-pointer bg-slate-900"
                                    onClick={() => { setProdutoModal(prod); setQtdModal(1); }}
                                >
                                    <img
                                        src={prod.imagemUrl}
                                        alt={prod.nome}
                                        className="w-full h-full object-cover group-hover:scale-105 transition-transform duration-500"
                                    />
                                    <div className="absolute top-3 left-3 bg-slate-950/80 backdrop-blur-md px-2.5 py-1 rounded-lg text-[10px] font-bold text-slate-300 border border-white/10 uppercase tracking-wider">
                                        {prod.categoriaNome || 'Produto'}
                                    </div>
                                    {prod.estoque <= 5 && (
                                        <div className="absolute top-3 right-3 bg-rose-500/90 text-white px-2 py-0.5 rounded-md text-[10px] font-bold shadow-md">
                                            Últimas {prod.estoque} un!
                                        </div>
                                    )}
                                </div>

                                {/* Conteúdo do Card */}
                                <div className="p-5 flex-1 flex flex-col justify-between">
                                    <div>
                                        {/* Avaliação por Estrelas */}
                                        <div className="flex items-center gap-1.5 mb-2">
                                            <div className="flex">
                                                {[...Array(5)].map((_, i) => (
                                                    <Icons.Star key={i} filled={i < Math.floor(prod.avaliacao)} />
                                                ))}
                                            </div>
                                            <span className="text-xs font-bold text-amber-400">{prod.avaliacao.toFixed(1)}</span>
                                            <span className="text-[11px] text-slate-500">({prod.totalAvaliacoes})</span>
                                        </div>

                                        {/* Título & Descrição Curta */}
                                        <h3
                                            onClick={() => { setProdutoModal(prod); setQtdModal(1); }}
                                            className="font-bold text-white text-base leading-snug line-clamp-2 hover:text-brand-400 cursor-pointer transition-colors mb-1.5"
                                        >
                                            {prod.nome}
                                        </h3>
                                        <p className="text-xs text-slate-400 line-clamp-2 mb-4">
                                            {prod.descricao}
                                        </p>
                                    </div>

                                    {/* Preço & Botão Adicionar */}
                                    <div className="pt-3 border-t border-slate-800/80 flex items-center justify-between gap-3">
                                        <div>
                                            <span className="text-[10px] text-slate-500 block">Preço à vista</span>
                                            <span className="text-lg font-extrabold text-brand-400">
                                                {formatarBRL(prod.preco)}
                                            </span>
                                        </div>

                                        <button
                                            onClick={() => adicionarAoCarrinho(prod.id, 1)}
                                            disabled={prod.estoque === 0}
                                            className="px-3.5 py-2 rounded-xl bg-brand-600 hover:bg-brand-500 disabled:bg-slate-800 text-white font-bold text-xs flex items-center gap-1.5 shadow-md shadow-brand-600/20 active:scale-95 transition-all"
                                        >
                                            <Icons.Plus />
                                            <span>{prod.estoque > 0 ? 'Adicionar' : 'Esgotado'}</span>
                                        </button>
                                    </div>
                                </div>
                            </div>
                        ))}
                    </div>
                )}
            </main>

            {/* --- Modal de Detalhes do Produto --- */}
            {produtoModal && (
                <div className="fixed inset-0 z-50 flex items-center justify-center p-4 bg-slate-950/80 backdrop-blur-md animate-fade-in">
                    <div className="bg-slate-900 border border-slate-800 rounded-3xl max-w-2xl w-full overflow-hidden shadow-2xl relative">
                        <button
                            onClick={() => setProdutoModal(null)}
                            className="absolute top-4 right-4 z-10 w-9 h-9 rounded-full bg-slate-800/80 hover:bg-slate-700 text-slate-300 flex items-center justify-center transition-all"
                        >
                            <Icons.X />
                        </button>

                        <div className="grid grid-cols-1 md:grid-cols-2">
                            <div className="h-64 md:h-full bg-slate-950 relative">
                                <img
                                    src={produtoModal.imagemUrl}
                                    alt={produtoModal.nome}
                                    className="w-full h-full object-cover"
                                />
                                <div className="absolute top-4 left-4 bg-brand-500 text-slate-950 text-xs font-bold px-2.5 py-1 rounded-md">
                                    {produtoModal.categoriaNome}
                                </div>
                            </div>

                            <div className="p-6 md:p-8 flex flex-col justify-between space-y-4">
                                <div>
                                    <div className="flex items-center gap-1.5 mb-2">
                                        <div className="flex">
                                            {[...Array(5)].map((_, i) => (
                                                <Icons.Star key={i} filled={i < Math.floor(produtoModal.avaliacao)} />
                                            ))}
                                        </div>
                                        <span className="text-xs font-bold text-amber-400">{produtoModal.avaliacao.toFixed(1)}</span>
                                        <span className="text-xs text-slate-400">({produtoModal.totalAvaliacoes} avaliações)</span>
                                    </div>

                                    <h2 className="text-xl font-bold text-white mb-2 leading-tight">
                                        {produtoModal.nome}
                                    </h2>

                                    <p className="text-xs text-slate-300 leading-relaxed mb-4">
                                        {produtoModal.descricao}
                                    </p>

                                    <div className="bg-slate-800/60 rounded-xl p-3 text-xs space-y-1 mb-4">
                                        <div className="flex justify-between text-slate-400">
                                            <span>Disponibilidade:</span>
                                            <span className={produtoModal.estoque > 5 ? 'text-brand-400 font-semibold' : 'text-rose-400 font-semibold'}>
                                                {produtoModal.estoque > 0 ? `${produtoModal.estoque} unidades em estoque` : 'Esgotado'}
                                            </span>
                                        </div>
                                        <div className="flex justify-between text-slate-400">
                                            <span>Envio:</span>
                                            <span className="text-slate-200">Pronta entrega (despacho em 24h)</span>
                                        </div>
                                    </div>

                                    <div className="text-2xl font-extrabold text-brand-400 mb-4">
                                        {formatarBRL(produtoModal.preco)}
                                    </div>
                                </div>

                                <div className="flex items-center gap-3 pt-4 border-t border-slate-800">
                                    <div className="flex items-center bg-slate-800 border border-slate-700 rounded-xl">
                                        <button
                                            onClick={() => setQtdModal(Math.max(1, qtdModal - 1))}
                                            className="px-3 py-2 text-slate-300 hover:text-white"
                                        >
                                            <Icons.Minus />
                                        </button>
                                        <span className="px-3 text-sm font-bold text-white">{qtdModal}</span>
                                        <button
                                            onClick={() => setQtdModal(Math.min(produtoModal.estoque, qtdModal + 1))}
                                            className="px-3 py-2 text-slate-300 hover:text-white"
                                        >
                                            <Icons.Plus />
                                        </button>
                                    </div>

                                    <button
                                        onClick={() => adicionarAoCarrinho(produtoModal.id, qtdModal)}
                                        disabled={produtoModal.estoque === 0}
                                        className="flex-1 py-3 rounded-xl bg-gradient-to-r from-brand-600 to-emerald-500 hover:from-brand-500 hover:to-emerald-400 text-white font-bold text-sm flex items-center justify-center gap-2 shadow-lg shadow-brand-500/25 transition-all"
                                    >
                                        <Icons.ShoppingBag />
                                        <span>Adicionar ao Carrinho</span>
                                    </button>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
            )}

            {/* --- Drawer Lateral do Carrinho --- */}
            {carrinhoAberto && (
                <div className="fixed inset-0 z-50 overflow-hidden animate-fade-in">
                    <div
                        className="absolute inset-0 bg-slate-950/70 backdrop-blur-sm transition-opacity"
                        onClick={() => setCarrinhoAberto(false)}
                    />
                    <div className="fixed inset-y-0 right-0 max-w-full flex pl-10">
                        <div className="w-screen max-w-md bg-slate-900 border-l border-slate-800 shadow-2xl flex flex-col animate-slide-in">
                            {/* Header do Drawer */}
                            <div className="p-6 border-b border-slate-800 flex items-center justify-between">
                                <div className="flex items-center gap-2.5">
                                    <div className="w-8 h-8 rounded-lg bg-brand-500/20 text-brand-400 flex items-center justify-center font-bold">
                                        <Icons.ShoppingBag />
                                    </div>
                                    <h2 className="text-lg font-bold text-white">Meu Carrinho</h2>
                                    <span className="text-xs bg-slate-800 text-slate-400 px-2 py-0.5 rounded-full font-semibold">
                                        {carrinho.quantidadeTotal} itens
                                    </span>
                                </div>
                                <button
                                    onClick={() => setCarrinhoAberto(false)}
                                    className="p-1 rounded-lg text-slate-400 hover:text-white hover:bg-slate-800 transition-all"
                                >
                                    <Icons.X />
                                </button>
                            </div>

                            {/* Barra de Progresso de Frete Grátis */}
                            <div className="px-6 py-3 bg-slate-950/60 border-b border-slate-800/80">
                                {carrinho.total >= 199 ? (
                                    <div className="flex items-center gap-2 text-xs font-semibold text-brand-400">
                                        <Icons.Truck /> Parabéns! Você ganhou <strong>Frete Grátis</strong>!
                                    </div>
                                ) : (
                                    <div>
                                        <div className="flex justify-between text-xs text-slate-400 mb-1.5">
                                            <span>Faltam <strong>{formatarBRL(199 - carrinho.total)}</strong> para Frete Grátis!</span>
                                            <span>{Math.min(100, Math.round((carrinho.total / 199) * 100))}%</span>
                                        </div>
                                        <div className="w-full h-1.5 bg-slate-800 rounded-full overflow-hidden">
                                            <div
                                                className="h-full bg-brand-500 rounded-full transition-all duration-300"
                                                style={{ width: `${Math.min(100, (carrinho.total / 199) * 100)}%` }}
                                            />
                                        </div>
                                    </div>
                                )}
                            </div>

                            {/* Lista de Itens do Carrinho */}
                            <div className="flex-1 overflow-y-auto p-6 space-y-4">
                                {carrinho.itens.length === 0 ? (
                                    <div className="py-20 text-center">
                                        <div className="w-16 h-16 rounded-full bg-slate-800 flex items-center justify-center text-2xl mx-auto mb-3 text-slate-500">🛒</div>
                                        <h3 className="font-bold text-white mb-1">Seu carrinho está vazio</h3>
                                        <p className="text-xs text-slate-400 mb-6">Que tal conferir as novidades no catálogo?</p>
                                        <button
                                            onClick={() => setCarrinhoAberto(false)}
                                            className="px-4 py-2 rounded-xl bg-brand-600 hover:bg-brand-500 text-xs font-semibold text-white"
                                        >
                                            Explorar Produtos
                                        </button>
                                    </div>
                                ) : (
                                    carrinho.itens.map(item => (
                                        <div
                                            key={item.produto.id}
                                            className="flex gap-4 p-3.5 rounded-2xl bg-slate-800/40 border border-slate-800 hover:border-slate-700 transition-all"
                                        >
                                            <img
                                                src={item.produto.imagemUrl}
                                                alt={item.produto.nome}
                                                className="w-16 h-16 rounded-xl object-cover bg-slate-900 flex-shrink-0"
                                            />
                                            <div className="flex-1 min-w-0 flex flex-col justify-between">
                                                <div>
                                                    <div className="flex justify-between items-start gap-2">
                                                        <h4 className="font-semibold text-xs text-white truncate">
                                                            {item.produto.nome}
                                                        </h4>
                                                        <button
                                                            onClick={() => removerDoCarrinho(item.produto.id)}
                                                            className="text-slate-500 hover:text-rose-400 p-0.5 transition-colors"
                                                            title="Remover"
                                                        >
                                                            <Icons.Trash />
                                                        </button>
                                                    </div>
                                                    <span className="text-[11px] text-slate-400">
                                                        {formatarBRL(item.produto.preco)} un.
                                                    </span>
                                                </div>

                                                <div className="flex justify-between items-center pt-2">
                                                    <div className="flex items-center bg-slate-900 border border-slate-700 rounded-lg">
                                                        <button
                                                            onClick={() => alterarQuantidadeCarrinho(item.produto.id, item.quantidade - 1)}
                                                            className="px-2 py-1 text-slate-400 hover:text-white"
                                                        >
                                                            <Icons.Minus />
                                                        </button>
                                                        <span className="px-2 text-xs font-bold text-white">{item.quantidade}</span>
                                                        <button
                                                            onClick={() => alterarQuantidadeCarrinho(item.produto.id, item.quantidade + 1)}
                                                            className="px-2 py-1 text-slate-400 hover:text-white"
                                                        >
                                                            <Icons.Plus />
                                                        </button>
                                                    </div>
                                                    <span className="font-bold text-sm text-brand-400">
                                                        {formatarBRL(item.subtotal)}
                                                    </span>
                                                </div>
                                            </div>
                                        </div>
                                    ))
                                )}
                            </div>

                            {/* Footer do Carrinho com Totais e Checkout */}
                            {carrinho.itens.length > 0 && (
                                <div className="p-6 bg-slate-950 border-t border-slate-800 space-y-4">
                                    <div className="space-y-1.5 text-xs text-slate-400">
                                        <div className="flex justify-between">
                                            <span>Subtotal</span>
                                            <span className="text-white font-medium">{formatarBRL(carrinho.total)}</span>
                                        </div>
                                        <div className="flex justify-between">
                                            <span>Frete</span>
                                            <span className="text-brand-400 font-semibold">{carrinho.total >= 199 ? 'GRÁTIS' : 'R$ 19,90'}</span>
                                        </div>
                                        <div className="flex justify-between text-base font-extrabold text-white pt-2 border-t border-slate-800">
                                            <span>Total a Pagar</span>
                                            <span className="text-brand-400">
                                                {formatarBRL(carrinho.total >= 199 ? carrinho.total : carrinho.total + 19.90)}
                                            </span>
                                        </div>
                                    </div>

                                    <button
                                        onClick={() => {
                                            setCarrinhoAberto(false);
                                            setPedidoFinalizado(null);
                                            setCheckoutAberto(true);
                                        }}
                                        className="w-full py-3.5 rounded-xl bg-gradient-to-r from-brand-600 to-emerald-500 hover:from-brand-500 hover:to-emerald-400 text-white font-bold text-sm shadow-xl shadow-brand-500/25 transition-all active:scale-95"
                                    >
                                        Avançar para Checkout
                                    </button>
                                </div>
                            )}
                        </div>
                    </div>
                </div>
            )}

            {/* --- Modal de Checkout Multi-etapas --- */}
            {checkoutAberto && (
                <div className="fixed inset-0 z-50 flex items-center justify-center p-4 bg-slate-950/85 backdrop-blur-md animate-fade-in">
                    <div className="bg-slate-900 border border-slate-800 rounded-3xl max-w-xl w-full p-6 sm:p-8 shadow-2xl relative max-h-[90vh] overflow-y-auto">
                        <button
                            onClick={() => setCheckoutAberto(false)}
                            className="absolute top-5 right-5 text-slate-400 hover:text-white"
                        >
                            <Icons.X />
                        </button>

                        {pedidoFinalizado ? (
                            /* Tela de Pedido Concluído com Sucesso */
                            <div className="text-center py-6 space-y-4">
                                <div className="w-16 h-16 rounded-full bg-brand-500/20 text-brand-400 flex items-center justify-center mx-auto text-3xl">
                                    <Icons.Check />
                                </div>
                                <h2 className="text-2xl font-extrabold text-white">Pedido Realizado com Sucesso!</h2>
                                <p className="text-xs text-slate-400 max-w-md mx-auto">
                                    Obrigado pela sua compra. Seu pedido foi processado no backend Java e o estoque dos produtos foi atualizado.
                                </p>

                                <div className="bg-slate-950 p-4 rounded-2xl border border-slate-800 text-left text-xs space-y-2 mt-4">
                                    <div className="flex justify-between">
                                        <span className="text-slate-400">Código do Pedido:</span>
                                        <strong className="text-amber-400 font-mono">{pedidoFinalizado.id}</strong>
                                    </div>
                                    <div className="flex justify-between">
                                        <span className="text-slate-400">Cliente:</span>
                                        <span className="text-white">{pedidoFinalizado.cliente?.nome}</span>
                                    </div>
                                    <div className="flex justify-between">
                                        <span className="text-slate-400">Forma de Pagamento:</span>
                                        <span className="text-white">{pedidoFinalizado.metodoPagamento}</span>
                                    </div>
                                    <div className="flex justify-between">
                                        <span className="text-slate-400">Status:</span>
                                        <span className="text-brand-400 font-bold">{pedidoFinalizado.statusDescricao || 'Pago'}</span>
                                    </div>
                                    <div className="flex justify-between pt-2 border-t border-slate-800 text-sm font-bold">
                                        <span className="text-white">Total Pago:</span>
                                        <span className="text-brand-400">{formatarBRL(pedidoFinalizado.valorTotal)}</span>
                                    </div>
                                </div>

                                <button
                                    onClick={() => setCheckoutAberto(false)}
                                    className="w-full py-3 rounded-xl bg-brand-600 hover:bg-brand-500 text-white font-bold text-sm shadow-lg transition-all"
                                >
                                    Continuar Comprando
                                </button>
                            </div>
                        ) : (
                            /* Formulário de Checkout */
                            <form onSubmit={finalizarPedido} className="space-y-6">
                                <div>
                                    <h2 className="text-xl font-extrabold text-white">Finalização de Compra</h2>
                                    <p className="text-xs text-slate-400">Confirme seus dados de entrega e forma de pagamento.</p>
                                </div>

                                {/* Dados do Cliente */}
                                <div className="space-y-3">
                                    <label className="text-xs font-bold text-slate-300 uppercase tracking-wider block">
                                        1. Identificação do Cliente
                                    </label>
                                    <div className="bg-slate-950 p-3.5 rounded-xl border border-slate-800 flex justify-between items-center text-xs">
                                        <div>
                                            <p className="font-bold text-white">{clienteAtivo?.nome}</p>
                                            <p className="text-slate-400">{clienteAtivo?.email} • {clienteAtivo?.telefone}</p>
                                        </div>
                                        <span className="text-brand-400 font-semibold">Autenticado</span>
                                    </div>
                                </div>

                                {/* Endereço de Entrega */}
                                <div className="space-y-2">
                                    <label className="text-xs font-bold text-slate-300 uppercase tracking-wider block">
                                        2. Endereço de Entrega
                                    </label>
                                    <input
                                        type="text"
                                        required
                                        value={formCheckout.endereco}
                                        onChange={(e) => setFormCheckout({ ...formCheckout, endereco: e.target.value })}
                                        className="w-full bg-slate-950 border border-slate-800 rounded-xl px-4 py-2.5 text-xs text-white focus:outline-none focus:border-brand-500"
                                        placeholder="Rua, número, complemento, bairro, cidade - UF"
                                    />
                                </div>

                                {/* Forma de Pagamento */}
                                <div className="space-y-3">
                                    <label className="text-xs font-bold text-slate-300 uppercase tracking-wider block">
                                        3. Forma de Pagamento
                                    </label>
                                    <div className="grid grid-cols-3 gap-2">
                                        {['PIX', 'Cartão de Crédito', 'Boleto Bancário'].map(metodo => (
                                            <button
                                                type="button"
                                                key={metodo}
                                                onClick={() => setFormCheckout({ ...formCheckout, metodoPagamento: metodo })}
                                                className={`py-2.5 px-2 rounded-xl text-xs font-bold border transition-all text-center ${
                                                    formCheckout.metodoPagamento === metodo
                                                        ? 'bg-brand-500/20 border-brand-500 text-brand-400 shadow-md'
                                                        : 'bg-slate-950 border-slate-800 text-slate-400 hover:border-slate-700'
                                                }`}
                                            >
                                                {metodo}
                                            </button>
                                        ))}
                                    </div>

                                    {formCheckout.metodoPagamento === 'PIX' && (
                                        <div className="p-4 bg-slate-950 border border-slate-800 rounded-2xl text-center space-y-2">
                                            <p className="text-xs text-brand-400 font-semibold">⚡ Aprovação Instantânea com 5% de Desconto extra!</p>
                                            <div className="w-32 h-32 bg-white rounded-xl mx-auto flex items-center justify-center p-2">
                                                <img src="https://api.qrserver.com/v1/create-qr-code/?size=150x150&data=00020126580014br.gov.bcb.pix0136ecommerce-java-demo" alt="QR Code PIX" className="w-full h-full" />
                                            </div>
                                            <span className="text-[10px] text-slate-500 block">Chave PIX: financeiro@novastore.com.br</span>
                                        </div>
                                    )}

                                    {formCheckout.metodoPagamento === 'Cartão de Crédito' && (
                                        <div className="p-4 bg-slate-950 border border-slate-800 rounded-2xl space-y-3 text-xs">
                                            <input
                                                type="text"
                                                placeholder="Número do Cartão"
                                                value={formCheckout.numCartao}
                                                onChange={(e) => setFormCheckout({ ...formCheckout, numCartao: e.target.value })}
                                                className="w-full bg-slate-900 border border-slate-800 rounded-lg p-2 text-white"
                                            />
                                            <div className="grid grid-cols-2 gap-2">
                                                <input
                                                    type="text"
                                                    placeholder="Validade (MM/AA)"
                                                    value={formCheckout.validade}
                                                    onChange={(e) => setFormCheckout({ ...formCheckout, validade: e.target.value })}
                                                    className="w-full bg-slate-900 border border-slate-800 rounded-lg p-2 text-white"
                                                />
                                                <input
                                                    type="text"
                                                    placeholder="CVV"
                                                    value={formCheckout.cvv}
                                                    onChange={(e) => setFormCheckout({ ...formCheckout, cvv: e.target.value })}
                                                    className="w-full bg-slate-900 border border-slate-800 rounded-lg p-2 text-white"
                                                />
                                            </div>
                                        </div>
                                    )}
                                </div>

                                {/* Resumo de Valores */}
                                <div className="p-4 bg-slate-950 rounded-2xl border border-slate-800 space-y-2 text-xs">
                                    <div className="flex justify-between text-slate-400">
                                        <span>Total dos Produtos ({carrinho.quantidadeTotal} itens):</span>
                                        <span className="text-white font-medium">{formatarBRL(carrinho.total)}</span>
                                    </div>
                                    <div className="flex justify-between text-slate-400">
                                        <span>Frete:</span>
                                        <span className="text-brand-400 font-semibold">{carrinho.total >= 199 ? 'GRÁTIS' : 'R$ 19,90'}</span>
                                    </div>
                                    <div className="flex justify-between text-base font-extrabold text-white pt-2 border-t border-slate-800">
                                        <span>Total Final:</span>
                                        <span className="text-brand-400">
                                            {formatarBRL(carrinho.total >= 199 ? carrinho.total : carrinho.total + 19.90)}
                                        </span>
                                    </div>
                                </div>

                                <button
                                    type="submit"
                                    disabled={processandoPedido}
                                    className="w-full py-3.5 rounded-xl bg-gradient-to-r from-brand-600 to-emerald-500 hover:from-brand-500 hover:to-emerald-400 text-white font-bold text-sm shadow-xl shadow-brand-500/25 transition-all flex items-center justify-center gap-2"
                                >
                                    {processandoPedido ? 'Processando Pedido...' : 'Confirmar e Pagar'}
                                </button>
                            </form>
                        )}
                    </div>
                </div>
            )}

            {/* --- Modal de Histórico de Pedidos --- */}
            {historicoAberto && (
                <div className="fixed inset-0 z-50 flex items-center justify-center p-4 bg-slate-950/80 backdrop-blur-md animate-fade-in">
                    <div className="bg-slate-900 border border-slate-800 rounded-3xl max-w-2xl w-full p-6 sm:p-8 shadow-2xl relative max-h-[85vh] overflow-y-auto">
                        <button
                            onClick={() => setHistoricoAberto(false)}
                            className="absolute top-5 right-5 text-slate-400 hover:text-white"
                        >
                            <Icons.X />
                        </button>

                        <div className="flex items-center gap-3 mb-6">
                            <div className="w-10 h-10 rounded-xl bg-brand-500/20 text-brand-400 flex items-center justify-center">
                                <Icons.Package />
                            </div>
                            <div>
                                <h2 className="text-xl font-bold text-white">Meus Pedidos</h2>
                                <p className="text-xs text-slate-400">Histórico de compras de {clienteAtivo?.nome}</p>
                            </div>
                        </div>

                        {pedidos.length === 0 ? (
                            <div className="py-12 text-center text-slate-400 text-xs">
                                Nenhum pedido encontrado para este cliente.
                            </div>
                        ) : (
                            <div className="space-y-4">
                                {pedidos.map(p => (
                                    <div key={p.id} className="p-4 rounded-2xl bg-slate-950 border border-slate-800 space-y-3">
                                        <div className="flex justify-between items-center text-xs">
                                            <div>
                                                <span className="font-mono font-bold text-amber-400">{p.id}</span>
                                                <span className="text-slate-500 block">{p.dataHora}</span>
                                            </div>
                                            <span className="px-2.5 py-1 rounded-full text-[11px] font-bold bg-brand-500/20 text-brand-400 border border-brand-500/30">
                                                {p.statusIcone} {p.statusDescricao}
                                            </span>
                                        </div>

                                        <div className="divide-y divide-slate-800/60 text-xs">
                                            {p.itens?.map((it, idx) => (
                                                <div key={idx} className="py-2 flex justify-between">
                                                    <span className="text-slate-300">{it.produto?.nome} x{it.quantidade}</span>
                                                    <span className="text-white font-medium">{formatarBRL(it.subtotal)}</span>
                                                </div>
                                            ))}
                                        </div>

                                        <div className="flex justify-between items-center pt-2 border-t border-slate-800 text-xs">
                                            <span className="text-slate-400">Pagamento: <strong>{p.metodoPagamento}</strong></span>
                                            <span className="text-sm font-extrabold text-brand-400">{formatarBRL(p.valorTotal)}</span>
                                        </div>
                                    </div>
                                ))}
                            </div>
                        )}
                    </div>
                </div>
            )}

            {/* --- Footer --- */}
            <footer className="bg-slate-950 border-t border-slate-800/80 py-10 mt-auto">
                <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 text-center text-xs text-slate-500 space-y-2">
                    <p className="font-semibold text-slate-400">
                        🛒 E-Commerce Enterprise • Desenvolvido em Java 21 & React SPA
                    </p>
                    <p>
                        Arquitetura Limpa (Models, Services, CLI Console, REST API & Web Interface)
                    </p>
                    <p className="text-[11px] text-slate-600">
                        © 2026 NovaStore Enterprise. Todos os direitos reservados.
                    </p>
                </div>
            </footer>
        </div>
    );
}

// Renderiza aplicação React no elemento #root
const root = ReactDOM.createRoot(document.getElementById('root'));
root.render(<App />);
