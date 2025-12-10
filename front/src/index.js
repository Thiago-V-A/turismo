import React, { useState, useEffect, createContext, useContext } from 'react';

// ==================== API Service ====================
const API_URL = 'http://localhost:8080/api';

const api = {
    setToken: (token) => {
        if (token) {
            localStorage.setItem('token', token);
        } else {
            localStorage.removeItem('token');
        }
    },

    getToken: () => localStorage.getItem('token'),

    getHeaders: () => {
        const token = localStorage.getItem('token');
        return {
            'Content-Type': 'application/json',
            ...(token && { 'Authorization': `Bearer ${token}` })
        };
    },

    request: async (url, options = {}) => {
        const response = await fetch(`${API_URL}${url}`, {
            ...options,
            headers: {
                ...api.getHeaders(),
                ...options.headers
            }
        });

        if (!response.ok) {
            const error = await response.text();
            throw new Error(error || 'Erro na requisição');
        }

        return response.json();
    },

    auth: {
        register: (data) => api.request('/auth/register', {
            method: 'POST',
            body: JSON.stringify(data)
        }),
        login: (data) => api.request('/auth/login', {
            method: 'POST',
            body: JSON.stringify(data)
        })
    },

    pontos: {
        listar: (params = {}) => {
            const query = new URLSearchParams(params).toString();
            return api.request(`/pontos?${query}`);
        },
        buscar: (id) => api.request(`/pontos/${id}`),
        criar: (data) => api.request('/pontos', {
            method: 'POST',
            body: JSON.stringify(data)
        }),
        exportar: (format) => {
            window.open(`${API_URL}/pontos/export?format=${format}`, '_blank');
        }
    },

    avaliacoes: {
        listar: (pontoId) => api.request(`/pontos/${pontoId}/avaliacoes`),
        criar: (pontoId, data) => api.request(`/pontos/${pontoId}/avaliacoes`, {
            method: 'POST',
            body: JSON.stringify(data)
        })
    },

    comentarios: {
        listar: (pontoId) => api.request(`/pontos/${pontoId}/comentarios`),
        criar: (pontoId, data) => api.request(`/pontos/${pontoId}/comentarios`, {
            method: 'POST',
            body: JSON.stringify(data)
        })
    },

    hospedagens: {
        listar: (pontoId) => api.request(`/pontos/${pontoId}/hospedagens`)
    }
};

// ==================== Auth Context ====================
const AuthContext = createContext();

const AuthProvider = ({ children }) => {
    const [user, setUser] = useState(null);
    const [loading, setLoading] = useState(true);

    useEffect(() => {
        const token = api.getToken();
        const userData = localStorage.getItem('user');
        if (token && userData) {
            setUser(JSON.parse(userData));
        }
        setLoading(false);
    }, []);

    const login = async (credentials) => {
        const response = await api.auth.login(credentials);
        api.setToken(response.token);
        localStorage.setItem('user', JSON.stringify(response));
        setUser(response);
        return response;
    };

    const register = async (data) => {
        const response = await api.auth.register(data);
        api.setToken(response.token);
        localStorage.setItem('user', JSON.stringify(response));
        setUser(response);
        return response;
    };

    const logout = () => {
        api.setToken(null);
        localStorage.removeItem('user');
        setUser(null);
    };

    return (
        <AuthContext.Provider value={{ user, login, register, logout, loading }}>
            {children}
        </AuthContext.Provider>
    );
};

const useAuth = () => useContext(AuthContext);

// ==================== Components ====================
const LoginForm = ({ onSuccess }) => {
    const [isLogin, setIsLogin] = useState(true);
    const [formData, setFormData] = useState({
        login: '',
        email: '',
        senha: ''
    });
    const [error, setError] = useState('');
    const { login, register } = useAuth();

    const handleSubmit = async () => {
        setError('');

        try {
            if (isLogin) {
                await login({ login: formData.login, senha: formData.senha });
            } else {
                await register(formData);
            }
            onSuccess();
        } catch (err) {
            setError(err.message);
        }
    };

    return (
        <div style={{ maxWidth: '400px', margin: '50px auto', padding: '20px', border: '1px solid #ddd', borderRadius: '8px' }}>
            <h2>{isLogin ? 'Login' : 'Registrar'}</h2>
            {error && <div style={{ color: 'red', marginBottom: '10px' }}>{error}</div>}

            <div>
                <div style={{ marginBottom: '15px' }}>
                    <input
                        type="text"
                        placeholder="Login"
                        value={formData.login}
                        onChange={(e) => setFormData({ ...formData, login: e.target.value })}
                        style={{ width: '100%', padding: '8px', borderRadius: '4px', border: '1px solid #ddd' }}
                    />
                </div>

                {!isLogin && (
                    <div style={{ marginBottom: '15px' }}>
                        <input
                            type="email"
                            placeholder="Email"
                            value={formData.email}
                            onChange={(e) => setFormData({ ...formData, email: e.target.value })}
                            style={{ width: '100%', padding: '8px', borderRadius: '4px', border: '1px solid #ddd' }}
                        />
                    </div>
                )}

                <div style={{ marginBottom: '15px' }}>
                    <input
                        type="password"
                        placeholder="Senha"
                        value={formData.senha}
                        onChange={(e) => setFormData({ ...formData, senha: e.target.value })}
                        style={{ width: '100%', padding: '8px', borderRadius: '4px', border: '1px solid #ddd' }}
                    />
                </div>

                <button onClick={handleSubmit} style={{ width: '100%', padding: '10px', backgroundColor: '#007bff', color: 'white', border: 'none', borderRadius: '4px', cursor: 'pointer' }}>
                    {isLogin ? 'Entrar' : 'Registrar'}
                </button>
            </div>

            <p style={{ textAlign: 'center', marginTop: '15px' }}>
                <button onClick={() => setIsLogin(!isLogin)} style={{ background: 'none', border: 'none', color: '#007bff', cursor: 'pointer' }}>
                    {isLogin ? 'Não tem conta? Registre-se' : 'Já tem conta? Faça login'}
                </button>
            </p>
        </div>
    );
};

const PontosList = ({ onSelect }) => {
    const [pontos, setPontos] = useState([]);
    const [filtros, setFiltros] = useState({ cidade: '', termo: '' });
    const [page, setPage] = useState(0);
    const [totalPages, setTotalPages] = useState(0);

    useEffect(() => {
        loadPontos();
    }, [page]);

    const loadPontos = async () => {
        try {
            const params = { page, size: 10 };
            if (filtros.cidade) params.cidade = filtros.cidade;
            if (filtros.termo) params.termo = filtros.termo;

            const response = await api.pontos.listar(params);
            setPontos(response.content);
            setTotalPages(response.totalPages);
        } catch (err) {
            console.error('Erro ao carregar pontos:', err);
        }
    };

    const handleSearch = () => {
        setPage(0);
        loadPontos();
    };

    return (
        <div>
            <div style={{ marginBottom: '20px', display: 'flex', gap: '10px' }}>
                <input
                    type="text"
                    placeholder="Buscar por cidade"
                    value={filtros.cidade}
                    onChange={(e) => setFiltros({ ...filtros, cidade: e.target.value })}
                    style={{ flex: 1, padding: '8px', borderRadius: '4px', border: '1px solid #ddd' }}
                />
                <input
                    type="text"
                    placeholder="Buscar termo"
                    value={filtros.termo}
                    onChange={(e) => setFiltros({ ...filtros, termo: e.target.value })}
                    style={{ flex: 1, padding: '8px', borderRadius: '4px', border: '1px solid #ddd' }}
                />
                <button onClick={handleSearch} style={{ padding: '8px 20px', backgroundColor: '#007bff', color: 'white', border: 'none', borderRadius: '4px', cursor: 'pointer' }}>
                    Buscar
                </button>
            </div>

            <div style={{ display: 'grid', gridTemplateColumns: 'repeat(auto-fill, minmax(300px, 1fr))', gap: '20px' }}>
                {pontos.map(ponto => (
                    <div key={ponto.id} style={{ border: '1px solid #ddd', borderRadius: '8px', padding: '15px', cursor: 'pointer' }} onClick={() => onSelect(ponto.id)}>
                        <h3>{ponto.nome}</h3>
                        <p style={{ color: '#666' }}>{ponto.cidade}, {ponto.estado}</p>
                        <p style={{ fontSize: '14px', color: '#888' }}>{ponto.descricao.substring(0, 100)}...</p>
                        <div style={{ marginTop: '10px', display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
                            <span style={{ color: '#ff9800' }}>⭐ {ponto.notaMedia ? ponto.notaMedia.toFixed(1) : 'N/A'}</span>
                            <span style={{ fontSize: '12px', color: '#999' }}>{ponto.totalAvaliacoes} avaliações</span>
                        </div>
                    </div>
                ))}
            </div>

            {totalPages > 1 && (
                <div style={{ marginTop: '20px', display: 'flex', justifyContent: 'center', gap: '10px' }}>
                    <button onClick={() => setPage(p => Math.max(0, p - 1))} disabled={page === 0} style={{ padding: '8px 16px', border: '1px solid #ddd', borderRadius: '4px', cursor: 'pointer' }}>
                        Anterior
                    </button>
                    <span style={{ padding: '8px' }}>Página {page + 1} de {totalPages}</span>
                    <button onClick={() => setPage(p => Math.min(totalPages - 1, p + 1))} disabled={page >= totalPages - 1} style={{ padding: '8px 16px', border: '1px solid #ddd', borderRadius: '4px', cursor: 'pointer' }}>
                        Próxima
                    </button>
                </div>
            )}
        </div>
    );
};

const PontoDetalhes = ({ pontoId, onBack }) => {
    const [ponto, setPonto] = useState(null);
    const [avaliacoes, setAvaliacoes] = useState([]);
    const [comentarios, setComentarios] = useState([]);
    const [hospedagens, setHospedagens] = useState([]);
    const [novaAvaliacao, setNovaAvaliacao] = useState({ nota: 5, comentario: '' });
    const [novoComentario, setNovoComentario] = useState('');
    const { user } = useAuth();

    useEffect(() => {
        loadPonto();
        loadAvaliacoes();
        loadComentarios();
        loadHospedagens();
    }, [pontoId]);

    const loadPonto = async () => {
        try {
            const data = await api.pontos.buscar(pontoId);
            setPonto(data);
        } catch (err) {
            console.error('Erro ao carregar ponto:', err);
        }
    };

    const loadAvaliacoes = async () => {
        try {
            const data = await api.avaliacoes.listar(pontoId);
            setAvaliacoes(data);
        } catch (err) {
            console.error('Erro ao carregar avaliações:', err);
        }
    };

    const loadComentarios = async () => {
        try {
            const data = await api.comentarios.listar(pontoId);
            setComentarios(data);
        } catch (err) {
            console.error('Erro ao carregar comentários:', err);
        }
    };

    const loadHospedagens = async () => {
        try {
            const data = await api.hospedagens.listar(pontoId);
            setHospedagens(data);
        } catch (err) {
            console.error('Erro ao carregar hospedagens:', err);
        }
    };

    const handleAvaliar = async () => {
        if (!user) {
            alert('Faça login para avaliar');
            return;
        }

        try {
            await api.avaliacoes.criar(pontoId, novaAvaliacao);
            setNovaAvaliacao({ nota: 5, comentario: '' });
            loadAvaliacoes();
            loadPonto();
        } catch (err) {
            alert(err.message);
        }
    };

    const handleComentar = async () => {
        if (!user) {
            alert('Faça login para comentar');
            return;
        }

        try {
            await api.comentarios.criar(pontoId, { texto: novoComentario });
            setNovoComentario('');
            loadComentarios();
        } catch (err) {
            alert(err.message);
        }
    };

    if (!ponto) return <div>Carregando...</div>;

    return (
        <div>
            <button onClick={onBack} style={{ marginBottom: '20px', padding: '8px 16px', backgroundColor: '#6c757d', color: 'white', border: 'none', borderRadius: '4px', cursor: 'pointer' }}>
                ← Voltar
            </button>

            <div style={{ marginBottom: '30px' }}>
                <h1>{ponto.nome}</h1>
                <p style={{ color: '#666', fontSize: '18px' }}>{ponto.cidade}, {ponto.estado} - {ponto.pais}</p>
                <div style={{ marginTop: '10px' }}>
                    <span style={{ color: '#ff9800', fontSize: '20px' }}>⭐ {ponto.notaMedia ? ponto.notaMedia.toFixed(1) : 'N/A'}</span>
                    <span style={{ marginLeft: '10px', color: '#999' }}>({ponto.totalAvaliacoes} avaliações)</span>
                </div>
                <p style={{ marginTop: '20px', lineHeight: '1.6' }}>{ponto.descricao}</p>

                {ponto.endereco && (
                    <p style={{ marginTop: '10px' }}><strong>Endereço:</strong> {ponto.endereco}</p>
                )}

                {ponto.comoChegar && (
                    <p style={{ marginTop: '10px' }}><strong>Como chegar:</strong> {ponto.comoChegar}</p>
                )}
            </div>

            {hospedagens.length > 0 && (
                <div style={{ marginBottom: '30px' }}>
                    <h2>Hospedagens</h2>
                    <div style={{ display: 'grid', gap: '15px' }}>
                        {hospedagens.map(hosp => (
                            <div key={hosp.id} style={{ border: '1px solid #ddd', borderRadius: '8px', padding: '15px' }}>
                                <h3>{hosp.nome}</h3>
                                <p>{hosp.tipo}</p>
                                {hosp.precoMedio && <p>Preço médio: R$ {hosp.precoMedio}</p>}
                                {hosp.telefone && <p>Telefone: {hosp.telefone}</p>}
                            </div>
                        ))}
                    </div>
                </div>
            )}

            {user && (
                <div style={{ marginBottom: '30px', padding: '20px', backgroundColor: '#f8f9fa', borderRadius: '8px' }}>
                    <h2>Avaliar</h2>
                    <div>
                        <div style={{ marginBottom: '15px' }}>
                            <label>Nota: </label>
                            <select value={novaAvaliacao.nota} onChange={(e) => setNovaAvaliacao({ ...novaAvaliacao, nota: parseInt(e.target.value) })} style={{ marginLeft: '10px', padding: '5px' }}>
                                {[1, 2, 3, 4, 5].map(n => <option key={n} value={n}>{n} estrela{n > 1 ? 's' : ''}</option>)}
                            </select>
                        </div>
                        <textarea
                            placeholder="Comentário (opcional)"
                            value={novaAvaliacao.comentario}
                            onChange={(e) => setNovaAvaliacao({ ...novaAvaliacao, comentario: e.target.value })}
                            style={{ width: '100%', padding: '8px', borderRadius: '4px', border: '1px solid #ddd', minHeight: '80px' }}
                        />
                        <button onClick={handleAvaliar} style={{ marginTop: '10px', padding: '10px 20px', backgroundColor: '#28a745', color: 'white', border: 'none', borderRadius: '4px', cursor: 'pointer' }}>
                            Enviar Avaliação
                        </button>
                    </div>
                </div>
            )}

            <div style={{ marginBottom: '30px' }}>
                <h2>Avaliações ({avaliacoes.length})</h2>
                {avaliacoes.map(aval => (
                    <div key={aval.id} style={{ marginBottom: '15px', padding: '15px', border: '1px solid #ddd', borderRadius: '8px' }}>
                        <div style={{ display: 'flex', justifyContent: 'space-between', marginBottom: '10px' }}>
                            <span style={{ color: '#ff9800' }}>{'⭐'.repeat(aval.nota)}</span>
                            <span style={{ color: '#999', fontSize: '14px' }}>{new Date(aval.createdAt).toLocaleDateString()}</span>
                        </div>
                        {aval.comentario && <p>{aval.comentario}</p>}
                    </div>
                ))}
            </div>

            {user && (
                <div style={{ marginBottom: '30px', padding: '20px', backgroundColor: '#f8f9fa', borderRadius: '8px' }}>
                    <h2>Adicionar Comentário</h2>
                    <div>
            <textarea
                placeholder="Seu comentário"
                value={novoComentario}
                onChange={(e) => setNovoComentario(e.target.value)}
                style={{ width: '100%', padding: '8px', borderRadius: '4px', border: '1px solid #ddd', minHeight: '80px' }}
            />
                        <button onClick={handleComentar} style={{ marginTop: '10px', padding: '10px 20px', backgroundColor: '#007bff', color: 'white', border: 'none', borderRadius: '4px', cursor: 'pointer' }}>
                            Enviar Comentário
                        </button>
                    </div>
                </div>
            )}

            <div>
                <h2>Comentários ({comentarios.length})</h2>
                {comentarios.map(com => (
                    <div key={com.id} style={{ marginBottom: '15px', padding: '15px', border: '1px solid #ddd', borderRadius: '8px' }}>
                        <div style={{ display: 'flex', justifyContent: 'space-between', marginBottom: '10px' }}>
                            <strong>{com.usuarioLogin}</strong>
                            <span style={{ color: '#999', fontSize: '14px' }}>{new Date(com.createdAt).toLocaleDateString()}</span>
                        </div>
                        <p>{com.texto}</p>
                    </div>
                ))}
            </div>
        </div>
    );
};

const NovoPontoForm = ({ onSuccess, onCancel }) => {
    const [formData, setFormData] = useState({
        nome: '',
        descricao: '',
        cidade: '',
        estado: '',
        pais: 'Brasil',
        endereco: '',
        comoChegar: ''
    });
    const [error, setError] = useState('');

    const handleSubmit = async () => {
        setError('');

        try {
            await api.pontos.criar(formData);
            onSuccess();
        } catch (err) {
            setError(err.message);
        }
    };

    return (
        <div style={{ maxWidth: '600px', margin: '0 auto', padding: '20px', border: '1px solid #ddd', borderRadius: '8px' }}>
            <h2>Novo Ponto Turístico</h2>
            {error && <div style={{ color: 'red', marginBottom: '10px' }}>{error}</div>}

            <div>
                <div style={{ marginBottom: '15px' }}>
                    <input
                        type="text"
                        placeholder="Nome *"
                        value={formData.nome}
                        onChange={(e) => setFormData({ ...formData, nome: e.target.value })}
                        style={{ width: '100%', padding: '8px', borderRadius: '4px', border: '1px solid #ddd' }}
                    />
                </div>

                <div style={{ marginBottom: '15px' }}>
          <textarea
              placeholder="Descrição *"
              value={formData.descricao}
              onChange={(e) => setFormData({ ...formData, descricao: e.target.value })}
              style={{ width: '100%', padding: '8px', borderRadius: '4px', border: '1px solid #ddd', minHeight: '100px' }}
          />
                </div>

                <div style={{ marginBottom: '15px', display: 'grid', gridTemplateColumns: '1fr 1fr', gap: '10px' }}>
                    <input
                        type="text"
                        placeholder="Cidade *"
                        value={formData.cidade}
                        onChange={(e) => setFormData({ ...formData, cidade: e.target.value })}
                        style={{ padding: '8px', borderRadius: '4px', border: '1px solid #ddd' }}
                    />
                    <input
                        type="text"
                        placeholder="Estado"
                        value={formData.estado}
                        onChange={(e) => setFormData({ ...formData, estado: e.target.value })}
                        style={{ padding: '8px', borderRadius: '4px', border: '1px solid #ddd' }}
                    />
                </div>

                <div style={{ marginBottom: '15px' }}>
                    <input
                        type="text"
                        placeholder="Endereço"
                        value={formData.endereco}
                        onChange={(e) => setFormData({ ...formData, endereco: e.target.value })}
                        style={{ width: '100%', padding: '8px', borderRadius: '4px', border: '1px solid #ddd' }}
                    />
                </div>

                <div style={{ marginBottom: '15px' }}>
          <textarea
              placeholder="Como chegar"
              value={formData.comoChegar}
              onChange={(e) => setFormData({ ...formData, comoChegar: e.target.value })}
              style={{ width: '100%', padding: '8px', borderRadius: '4px', border: '1px solid #ddd', minHeight: '60px' }}
          />
                </div>

                <div style={{ display: 'flex', gap: '10px' }}>
                    <button onClick={handleSubmit} style={{ flex: 1, padding: '10px', backgroundColor: '#28a745', color: 'white', border: 'none', borderRadius: '4px', cursor: 'pointer' }}>
                        Criar
                    </button>
                    <button onClick={onCancel} style={{ flex: 1, padding: '10px', backgroundColor: '#6c757d', color: 'white', border: 'none', borderRadius: '4px', cursor: 'pointer' }}>
                        Cancelar
                    </button>
                </div>
            </div>
        </div>
    );
};

// ==================== Main App ====================
