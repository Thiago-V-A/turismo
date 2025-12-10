# Sistema Web de Turismo - MVP

Sistema completo de gerenciamento de pontos turísticos com múltiplas formas de persistência de dados.

## 🏗️ Arquitetura

### Backend
- **Framework**: Spring Boot 3.2.x
- **Linguagem**: Java 17+
- **Bancos de Dados**:
  - PostgreSQL (dados relacionais)
  - MongoDB (comentários e fotos)
  - Redis (cache)
- **Segurança**: Spring Security + JWT

### Frontend
- **Framework**: React 18
- **Estilização**: Tailwind CSS
- **HTTP Client**: Axios

## 📋 Funcionalidades MVP

### Implementadas
✅ Autenticação (registro/login) com JWT  
✅ CRUD de Pontos Turísticos  
✅ Upload de fotos (filesystem)  
✅ Sistema de avaliações (1-5 estrelas)  
✅ Comentários com MongoDB  
✅ Cadastro de hospedagens  
✅ Filtros e busca  
✅ Cache com Redis  
✅ Exportação de dados (JSON/CSV/XML)  

## 🚀 Pré-requisitos

- Java 17 ou superior
- Node.js 16 ou superior
- PostgreSQL 14+
- MongoDB 6+
- Redis 7+
- Maven 3.8+

## ⚙️ Configuração

### 1. Backend

#### Criar banco de dados PostgreSQL
```sql
CREATE DATABASE tourism_db;
CREATE USER tourism_user WITH PASSWORD 'tourism123';
GRANT ALL PRIVILEGES ON DATABASE tourism_db TO tourism_user;
```

#### Configurar application.properties
```properties
# PostgreSQL
spring.datasource.url=jdbc:postgresql://localhost:5432/tourism_db
spring.datasource.username=tourism_user
spring.datasource.password=tourism123

# MongoDB
spring.data.mongodb.host=localhost
spring.data.mongodb.port=27017
spring.data.mongodb.database=tourism_comments

# Redis
spring.data.redis.host=localhost
spring.data.redis.port=6379

# JWT
jwt.secret=seu-secret-key-muito-seguro-aqui-min-256-bits
jwt.expiration=86400000

# Upload
upload.dir=./uploads
```

#### Executar backend
```bash
cd backend
mvn clean install
mvn spring-boot:run
```

O backend estará disponível em: http://localhost:8080

### 2. Frontend

```bash
cd frontend
npm install
npm start
```

O frontend estará disponível em: http://localhost:3000

## 📁 Estrutura do Projeto

```
tourism-system/
├── backend/
│   ├── src/main/java/com/tourism/
│   │   ├── config/           # Configurações (Security, MongoDB, Redis)
│   │   ├── controller/       # REST Controllers
│   │   ├── model/            # Entidades JPA e MongoDB
│   │   ├── repository/       # Repositories (JPA e MongoDB)
│   │   ├── service/          # Regras de negócio
│   │   ├── dto/              # Data Transfer Objects
│   │   ├── security/         # JWT e autenticação
│   │   └── exception/        # Tratamento de exceções
│   └── pom.xml
├── frontend/
│   ├── src/
│   │   ├── components/       # Componentes React
│   │   ├── pages/            # Páginas
│   │   ├── services/         # API calls
│   │   ├── context/          # Context API (Auth)
│   │   └── App.js
│   └── package.json
└── README.md
```

## 🔑 API Endpoints

### Autenticação
- `POST /api/auth/register` - Registrar usuário
- `POST /api/auth/login` - Login

### Pontos Turísticos
- `GET /api/pontos` - Listar (com filtros e paginação)
- `GET /api/pontos/{id}` - Buscar por ID
- `POST /api/pontos` - Criar (requer auth)
- `PUT /api/pontos/{id}` - Atualizar (requer auth)
- `DELETE /api/pontos/{id}` - Deletar (requer ADMIN)
- `GET /api/pontos/export?format=json|csv|xml` - Exportar

### Fotos
- `POST /api/pontos/{id}/fotos` - Upload de foto
- `GET /api/pontos/{id}/fotos` - Listar fotos

### Avaliações
- `POST /api/pontos/{id}/avaliacoes` - Criar avaliação
- `GET /api/pontos/{id}/avaliacoes` - Listar avaliações

### Comentários (MongoDB)
- `POST /api/pontos/{id}/comentarios` - Criar comentário
- `GET /api/pontos/{id}/comentarios` - Listar comentários
- `POST /api/comentarios/{id}/respostas` - Responder comentário

### Hospedagens
- `POST /api/pontos/{id}/hospedagens` - Criar hospedagem
- `GET /api/pontos/{id}/hospedagens` - Listar hospedagens

## 🧪 Testando o Sistema

### Criar usuário admin
```bash
curl -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "login": "admin",
    "email": "admin@tourism.com",
    "senha": "admin123",
    "role": "ADMIN"
  }'
```

### Login
```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "login": "admin",
    "senha": "admin123"
  }'
```

### Criar ponto turístico
```bash
curl -X POST http://localhost:8080/api/pontos \
  -H "Authorization: Bearer SEU_TOKEN_JWT" \
  -H "Content-Type: application/json" \
  -d '{
    "nome": "Cristo Redentor",
    "descricao": "Monumento icônico do Rio de Janeiro",
    "cidade": "Rio de Janeiro",
    "estado": "RJ",
    "pais": "Brasil",
    "latitude": -22.9519,
    "longitude": -43.2105,
    "endereco": "Parque Nacional da Tijuca"
  }'
```

## 🐳 Docker (Opcional)

```bash
# Subir bancos de dados
docker-compose up -d
```

```yaml
# docker-compose.yml
version: '3.8'
services:
  postgres:
    image: postgres:15
    environment:
      POSTGRES_DB: tourism_db
      POSTGRES_USER: tourism_user
      POSTGRES_PASSWORD: tourism123
    ports:
      - "5432:5432"
  
  mongodb:
    image: mongo:6
    ports:
      - "27017:27017"
  
  redis:
    image: redis:7
    ports:
      - "6379:6379"
```

## 📚 Conceitos de Persistência Demonstrados

1. **JPA/Hibernate**: Mapeamento objeto-relacional com PostgreSQL
2. **MongoDB**: Persistência de documentos NoSQL
3. **Redis**: Cache em memória para performance
4. **File System**: Armazenamento de arquivos binários
5. **Transações**: Controle transacional com @Transactional
6. **Relacionamentos**: OneToMany, ManyToOne
7. **Queries**: JPQL, Query Methods, MongoDB Queries
8. **Cache**: @Cacheable, @CacheEvict

## 📚 Documentação Adicional

- **QUICK_START.md** - Guia passo a passo de instalação e execução
- **ESTRUTURA_PROJETO.md** - Estrutura completa de diretórios e arquivos
- **Todos os arquivos de código** - Totalmente comentados e documentados

## ✅ Checklist de Entrega MVP

- [x] Autenticação JWT completa
- [x] CRUD de pontos turísticos
- [x] Sistema de avaliações (1-5 estrelas)
- [x] Comentários com MongoDB
- [x] Upload de fotos (metadados)
- [x] Hospedagens
- [x] Cache com Redis
- [x] Exportação (JSON/CSV/XML)
- [x] Frontend React funcional
- [x] Paginação e filtros
- [x] Validação de dados
- [x] Tratamento de erros
- [x] Documentação completa


## 📝 Notas

- As senhas são criptografadas com BCrypt
- JWT expira em 24 horas
- Fotos são armazenadas em `./uploads/`
- Cache Redis: TTL de 1 hora para pontos turísticos
- MongoDB: comentários com respostas aninhadas

## 🤝 Contribuindo

Este é um projeto educacional para demonstração de conceitos de persistência de dados.

## 📄 Licença

MIT License - Projeto Educacional
