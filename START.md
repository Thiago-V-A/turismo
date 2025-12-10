# 🚀 Guia de Início Rápido - Sistema de Turismo MVP

## Pré-requisitos

- Java 17+ ([Download](https://adoptium.net/))
- Node.js 16+ ([Download](https://nodejs.org/))
- Maven 3.8+ ([Download](https://maven.apache.org/))
- Docker e Docker Compose ([Download](https://www.docker.com/))

## Passo 1: Clonar o Projeto

```bash
# Criar estrutura de diretórios
mkdir tourism-system
cd tourism-system
mkdir backend frontend
```

## Passo 2: Subir os Bancos de Dados com Docker

```bash
# Criar arquivo docker-compose.yml na raiz
# (copiar o conteúdo do arquivo docker-compose.yml fornecido)

# Subir os containers
docker-compose up -d

# Verificar se estão rodando
docker-compose ps
```

**Aguarde 30 segundos** para os bancos iniciarem completamente.

## Passo 3: Configurar e Executar o Backend

```bash
cd backend

# Criar estrutura de pastas
mkdir -p src/main/java/com/tourism
mkdir -p src/main/resources
mkdir uploads

# Copiar todos os arquivos Java para src/main/java/com/tourism/
# seguindo a estrutura de pacotes:
# - config/
# - controller/
# - dto/
# - exception/
# - model/
# - repository/
# - security/
# - service/
# - TourismSystemApplication.java

# Copiar pom.xml para a raiz do backend
# Copiar application.properties para src/main/resources/

# Instalar dependências e executar
mvn clean install
mvn spring-boot:run
```

O backend estará disponível em: **http://localhost:8080**

### Verificar se o backend está funcionando:

```bash
curl http://localhost:8080/api/pontos
```

## Passo 4: Configurar e Executar o Frontend

```bash
cd ../frontend

# Copiar package.json
# Criar pasta src/
# Copiar App.js para src/

# Criar arquivo public/index.html
cat > public/index.html << 'EOF'
<!DOCTYPE html>
<html lang="pt-BR">
  <head>
    <meta charset="utf-8" />
    <meta name="viewport" content="width=device-width, initial-scale=1" />
    <title>Sistema de Turismo</title>
  </head>
  <body>
    <div id="root"></div>
  </body>
</html>
EOF

# Criar arquivo src/index.js
cat > src/index.js << 'EOF'
import React from 'react';
import ReactDOM from 'react-dom/client';
import App from './App';

const root = ReactDOM.createRoot(document.getElementById('root'));
root.render(<App />);
EOF

# Instalar dependências
npm install

# Executar
npm start
```

O frontend estará disponível em: **http://localhost:3000**

## Passo 5: Testar o Sistema

### 1. Registrar um usuário

Acesse http://localhost:3000 e clique em "Não tem conta? Registre-se"

- Login: admin
- Email: admin@tourism.com
- Senha: admin123

### 2. Criar um ponto turístico

Após login, clique em "+ Novo Ponto" e preencha:

- Nome: Cristo Redentor
- Descrição: Monumento icônico do Rio de Janeiro, uma das Sete Maravilhas do Mundo Moderno
- Cidade: Rio de Janeiro
- Estado: RJ
- Endereço: Parque Nacional da Tijuca

### 3. Avaliar o ponto

Clique no ponto criado e adicione uma avaliação:

- Nota: 5 estrelas
- Comentário: Vista incrível e experiência inesquecível!

### 4. Adicionar comentário

Na mesma tela, adicione um comentário detalhado sobre sua experiência.

### 5. Exportar dados

Volte para a lista e clique nos botões JSON, CSV ou XML para exportar os dados.

## 🔍 Testando as APIs com cURL

### Registrar usuário:

```bash
curl -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "login": "testuser",
    "email": "test@example.com",
    "senha": "test123",
    "role": "USER"
  }'
```

### Login:

```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "login": "testuser",
    "senha": "test123"
  }'
```

Salve o token retornado!

### Criar ponto turístico:

```bash
TOKEN="seu_token_aqui"

curl -X POST http://localhost:8080/api/pontos \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "nome": "Pão de Açúcar",
    "descricao": "Complexo de morros com teleférico e vista panorâmica",
    "cidade": "Rio de Janeiro",
    "estado": "RJ",
    "pais": "Brasil"
  }'
```

### Listar pontos:

```bash
curl http://localhost:8080/api/pontos
```

### Buscar por cidade:

```bash
curl "http://localhost:8080/api/pontos?cidade=Rio"
```

### Criar avaliação:

```bash
curl -X POST http://localhost:8080/api/pontos/1/avaliacoes \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "nota": 5,
    "comentario": "Experiência maravilhosa!"
  }'
```

### Criar comentário:

```bash
curl -X POST http://localhost:8080/api/pontos/1/comentarios \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "texto": "Recomendo ir de manhã cedo para evitar filas"
  }'
```

### Exportar dados:

```bash
# JSON
curl http://localhost:8080/api/pontos/export?format=json > pontos.json

# CSV
curl http://localhost:8080/api/pontos/export?format=csv > pontos.csv

# XML
curl http://localhost:8080/api/pontos/export?format=xml > pontos.xml
```

## 🛠️ Troubleshooting

### Erro de conexão com PostgreSQL

```bash
# Verificar se o container está rodando
docker ps | grep postgres

# Ver logs
docker logs tourism-postgres

# Recriar banco (CUIDADO: apaga dados)
docker-compose down -v
docker-compose up -d
```

### Erro de conexão com MongoDB

```bash
# Verificar container
docker ps | grep mongodb

# Testar conexão
docker exec -it tourism-mongodb mongosh
```

### Erro de porta já em uso

```bash
# Verificar portas em uso
lsof -i :8080
lsof -i :3000

# Parar processo (substituir PID)
kill -9 PID
```

### Backend não inicia

```bash
# Limpar e recompilar
mvn clean
rm -rf target/
mvn clean install -DskipTests

# Verificar Java
java -version  # Deve ser 17+
```

### Frontend não inicia

```bash
# Limpar cache e reinstalar
rm -rf node_modules package-lock.json
npm cache clean --force
npm install
```

## 📊 Monitorando os Bancos

### PostgreSQL

```bash
# Conectar ao banco
docker exec -it tourism-postgres psql -U tourism_user -d tourism_db

# Ver tabelas
\dt

# Ver dados
SELECT * FROM pontos_turisticos;
SELECT * FROM usuarios;

# Sair
\q
```

### MongoDB

```bash
# Conectar ao MongoDB
docker exec -it tourism-mongodb mongosh

# Usar database
use tourism_comments

# Ver coleções
show collections

# Ver documentos
db.comentarios.find().pretty()
db.fotos.find().pretty()

# Sair
exit
```

### Redis

```bash
# Conectar ao Redis
docker exec -it tourism-redis redis-cli

# Ver chaves
KEYS *

# Ver valor
GET pontos::1

# Limpar cache
FLUSHALL

# Sair
exit
```

## 🎯 Conceitos de Persistência Demonstrados

1. **JPA/Hibernate**: Mapeamento objeto-relacional com PostgreSQL
2. **Relacionamentos**: @OneToMany, @ManyToOne entre entidades
3. **Transações**: @Transactional para operações atômicas
4. **Queries**: JPQL, Query Methods, Criteria API
5. **MongoDB**: Persistência de documentos NoSQL
6. **Documentos Aninhados**: Respostas dentro de comentários
7. **Redis**: Cache distribuído com TTL
8. **Cache**: @Cacheable e @CacheEvict
9. **File System**: Upload e armazenamento de arquivos
10. **Validação**: Bean Validation com anotações

## 📝 Próximos Passos

- Adicionar mais pontos turísticos
- Testar upload de fotos (requer implementação adicional no frontend)
- Criar hospedagens para os pontos
- Testar filtros e paginação
- Explorar o cache Redis
- Experimentar com exportações

## 🆘 Suporte

Se encontrar problemas:

1. Verifique os logs do backend: `mvn spring-boot:run`
2. Verifique os logs do frontend: console do navegador (F12)
3. Verifique os logs do Docker: `docker-compose logs`
4. Reinicie tudo: `docker-compose restart`

---

**Desenvolvido para fins educacionais - Disciplina de Persistência de Dados**
