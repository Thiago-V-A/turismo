function App() {
    const [view, setView] = useState('login');
    const [selectedPonto, setSelectedPonto] = useState(null);
    const { user, logout } = useAuth();

    useEffect(() => {
        if (user) {
            setView('home');
        }
    }, [user]);

    const handleLogout = () => {
        logout();
        setView('login');
    };

    return (
        <div style={{ minHeight: '100vh', backgroundColor: '#f5f5f5' }}>
            {user && (
                <header style={{ backgroundColor: '#007bff', color: 'white', padding: '15px 20px', display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
                    <h1 style={{ margin: 0, fontSize: '24px' }}>🗺️ Sistema de Turismo</h1>
                    <div style={{ display: 'flex', gap: '15px', alignItems: 'center' }}>
                        <span>Olá, {user.login}</span>
                        {view !== 'home' && (
                            <button onClick={() => { setView('home'); setSelectedPonto(null); }} style={{ padding: '8px 16px', backgroundColor: 'white', color: '#007bff', border: 'none', borderRadius: '4px', cursor: 'pointer' }}>
                                Home
                            </button>
                        )}
                        {view === 'home' && !selectedPonto && (
                            <>
                                <button onClick={() => setView('novo')} style={{ padding: '8px 16px', backgroundColor: '#28a745', color: 'white', border: 'none', borderRadius: '4px', cursor: 'pointer' }}>
                                    + Novo Ponto
                                </button>
                                <div style={{ display: 'flex', gap: '5px' }}>
                                    <button onClick={() => api.pontos.exportar('json')} style={{ padding: '8px 12px', backgroundColor: 'white', color: '#007bff', border: 'none', borderRadius: '4px', cursor: 'pointer', fontSize: '12px' }}>
                                        JSON
                                    </button>
                                    <button onClick={() => api.pontos.exportar('csv')} style={{ padding: '8px 12px', backgroundColor: 'white', color: '#007bff', border: 'none', borderRadius: '4px', cursor: 'pointer', fontSize: '12px' }}>
                                        CSV
                                    </button>
                                    <button onClick={() => api.pontos.exportar('xml')} style={{ padding: '8px 12px', backgroundColor: 'white', color: '#007bff', border: 'none', borderRadius: '4px', cursor: 'pointer', fontSize: '12px' }}>
                                        XML
                                    </button>
                                </div>
                            </>
                        )}
                        <button onClick={handleLogout} style={{ padding: '8px 16px', backgroundColor: '#dc3545', color: 'white', border: 'none', borderRadius: '4px', cursor: 'pointer' }}>
                            Sair
                        </button>
                    </div>
                </header>
            )}

            <main style={{ padding: '20px', maxWidth: '1200px', margin: '0 auto' }}>
                {!user && view === 'login' && (
                    <LoginForm onSuccess={() => setView('home')} />
                )}

                {user && view === 'home' && !selectedPonto && (
                    <PontosList onSelect={(id) => setSelectedPonto(id)} />
                )}

                {user && view === 'home' && selectedPonto && (
                    <PontoDetalhes pontoId={selectedPonto} onBack={() => setSelectedPonto(null)} />
                )}

                {user && view === 'novo' && (
                    <NovoPontoForm
                        onSuccess={() => setView('home')}
                        onCancel={() => setView('home')}
                    />
                )}
            </main>
        </div>
    );
}

// ==================== Root ====================
export default function Root() {
    return (
        <AuthProvider>
            <App />
        </AuthProvider>
    );
}