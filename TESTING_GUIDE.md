# 🧪 GUIA DE TESTES - Travel Graph API Reestruturada

Após iniciar a aplicação, use os exemplos abaixo para testar todos os endpoints.

## 🚀 Iniciar a Aplicação

```bash
cd C:\Users\anahe\Downloads\travel-graph-api

# Opção 1: Com Maven
mvn spring-boot:run -Dspring-boot.run.arguments="--spring.profiles.active=dev"

# Opção 2: JAR compilado
java -jar target/travelGraph-0.0.1-SNAPSHOT.jar
```

A API estará disponível em: `http://localhost:8080`

---

## 📍 TESTES - CIDADES (/cidades)

### 1️⃣ Criar Primeira Cidade

**Requisição:**
```bash
curl -X POST http://localhost:8080/cidades \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Rio de Janeiro",
    "latitude": -22.9068,
    "longitude": -43.1729
  }'
```

**Resposta Esperada:**
```json
{
  "id": 1,
  "name": "Rio de Janeiro",
  "latitude": -22.9068,
  "longitude": -43.1729,
  "createdAt": "2026-06-12T21:50:00",
  "updatedAt": null
}
```

---

### 2️⃣ Criar Segunda Cidade

**Requisição:**
```bash
curl -X POST http://localhost:8080/cidades \
  -H "Content-Type: application/json" \
  -d '{
    "name": "São Paulo",
    "latitude": -23.5505,
    "longitude": -46.6333
  }'
```

---

### 3️⃣ Criar Terceira Cidade

**Requisição:**
```bash
curl -X POST http://localhost:8080/cidades \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Belo Horizonte",
    "latitude": -19.8267,
    "longitude": -43.9445
  }'
```

---

### 4️⃣ Listar Todas as Cidades

**Requisição:**
```bash
curl http://localhost:8080/cidades
```

**Resposta Esperada:**
```json
[
  {
    "id": 1,
    "name": "Rio de Janeiro",
    "latitude": -22.9068,
    "longitude": -43.1729,
    "createdAt": "2026-06-12T21:50:00",
    "updatedAt": null
  },
  {
    "id": 2,
    "name": "São Paulo",
    "latitude": -23.5505,
    "longitude": -46.6333,
    "createdAt": "2026-06-12T21:50:05",
    "updatedAt": null
  },
  {
    "id": 3,
    "name": "Belo Horizonte",
    "latitude": -19.8267,
    "longitude": -43.9445,
    "createdAt": "2026-06-12T21:50:10",
    "updatedAt": null
  }
]
```

---

### 5️⃣ Buscar Cidade por ID

**Requisição:**
```bash
curl http://localhost:8080/cidades/1
```

**Resposta Esperada:**
```json
{
  "id": 1,
  "name": "Rio de Janeiro",
  "latitude": -22.9068,
  "longitude": -43.1729,
  "createdAt": "2026-06-12T21:50:00",
  "updatedAt": null
}
```

---

### 6️⃣ Atualizar Cidade

**Requisição:**
```bash
curl -X PUT http://localhost:8080/cidades/1 \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Rio de Janeiro - RJ",
    "latitude": -22.9068,
    "longitude": -43.1729
  }'
```

**Resposta Esperada:**
```json
{
  "id": 1,
  "name": "Rio de Janeiro - RJ",
  "latitude": -22.9068,
  "longitude": -43.1729,
  "createdAt": "2026-06-12T21:50:00",
  "updatedAt": "2026-06-12T21:51:00"
}
```

---

## 🎯 TESTES - PONTOS TURÍSTICOS (/pontos)

### 1️⃣ Criar Ponto Turístico na Cidade 1

**Requisição:**
```bash
curl -X POST http://localhost:8080/pontos \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Cristo Redentor",
    "description": "Estátua icônica do Rio de Janeiro com vista para toda a cidade",
    "category": "Monumento",
    "latitude": -22.9519,
    "longitude": -43.2105,
    "cityId": 1
  }'
```

**Resposta Esperada:**
```json
{
  "id": 1,
  "name": "Cristo Redentor",
  "description": "Estátua icônica do Rio de Janeiro com vista para toda a cidade",
  "category": "Monumento",
  "latitude": -22.9519,
  "longitude": -43.2105,
  "createdAt": "2026-06-12T21:52:00",
  "updatedAt": null,
  "city": {
    "id": 1,
    "name": "Rio de Janeiro - RJ",
    "latitude": -22.9068,
    "longitude": -43.1729,
    "createdAt": "2026-06-12T21:50:00",
    "updatedAt": "2026-06-12T21:51:00"
  }
}
```

---

### 2️⃣ Criar Mais Pontos Turísticos

**Pão de Açúcar - Rio:**
```bash
curl -X POST http://localhost:8080/pontos \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Pão de Açúcar",
    "description": "Montanha de 396 metros com teleférico",
    "category": "Atração Natural",
    "latitude": -22.9432,
    "longitude": -43.1614,
    "cityId": 1
  }'
```

**Museu do Ipiranga - São Paulo:**
```bash
curl -X POST http://localhost:8080/pontos \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Museu do Ipiranga",
    "description": "Museu histórico dedicado à independência do Brasil",
    "category": "Museu",
    "latitude": -23.5962,
    "longitude": -46.6211,
    "cityId": 2
  }'
```

**Mercadão de São Paulo:**
```bash
curl -X POST http://localhost:8080/pontos \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Mercadão de São Paulo",
    "description": "Mercado tradicional com produtos diversos",
    "category": "Mercado",
    "latitude": -23.5505,
    "longitude": -46.6333,
    "cityId": 2
  }'
```

---

### 3️⃣ Listar Todos os Pontos

**Requisição:**
```bash
curl http://localhost:8080/pontos
```

---

### 4️⃣ Listar Pontos de Uma Cidade (QUERY PARAM)

**Listar pontos do Rio de Janeiro (ID = 1):**
```bash
curl "http://localhost:8080/pontos?cidadeId=1"
```

**Resposta Esperada:**
```json
[
  {
    "id": 1,
    "name": "Cristo Redentor",
    "description": "Estátua icônica...",
    "category": "Monumento",
    "latitude": -22.9519,
    "longitude": -43.2105,
    "createdAt": "2026-06-12T21:52:00",
    "updatedAt": null,
    "city": { ... }
  },
  {
    "id": 2,
    "name": "Pão de Açúcar",
    "description": "Montanha de 396 metros...",
    "category": "Atração Natural",
    "latitude": -22.9432,
    "longitude": -43.1614,
    "createdAt": "2026-06-12T21:52:30",
    "updatedAt": null,
    "city": { ... }
  }
]
```

**Listar pontos de São Paulo (ID = 2):**
```bash
curl "http://localhost:8080/pontos?cidadeId=2"
```

---

### 5️⃣ Buscar Ponto por ID

**Requisição:**
```bash
curl http://localhost:8080/pontos/1
```

---

### 6️⃣ Atualizar Ponto Turístico

**Requisição:**
```bash
curl -X PUT http://localhost:8080/pontos/1 \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Cristo Redentor - Monumento Histórico",
    "description": "Estátua icônica de 38 metros - Patrimônio da Humanidade",
    "category": "Monumento Histórico",
    "latitude": -22.9519,
    "longitude": -43.2105
  }'
```

---

## 🔗 TESTES - CONEXÕES (/conexoes)

### 1️⃣ Criar Conexão entre Cidades

**Conexão: Rio → São Paulo**
```bash
curl -X POST http://localhost:8080/conexoes \
  -H "Content-Type: application/json" \
  -d '{
    "cidadeOrigemId": 1,
    "cidadeDestinoId": 2,
    "distancia": 430.5,
    "tempo": 6.0
  }'
```

**Resposta Esperada:**
```json
{
  "id": 1,
  "cidadeOrigemId": 1,
  "cidadeDestinoId": 2,
  "cidadeOrigemNome": "Rio de Janeiro - RJ",
  "cidadeDestinoNome": "São Paulo",
  "distancia": 430.5,
  "tempo": 6.0,
  "createdAt": "2026-06-12T21:53:00"
}
```

---

### 2️⃣ Criar Mais Conexões

**São Paulo → Belo Horizonte:**
```bash
curl -X POST http://localhost:8080/conexoes \
  -H "Content-Type: application/json" \
  -d '{
    "cidadeOrigemId": 2,
    "cidadeDestinoId": 3,
    "distancia": 586.0,
    "tempo": 8.5
  }'
```

**Rio → Belo Horizonte:**
```bash
curl -X POST http://localhost:8080/conexoes \
  -H "Content-Type: application/json" \
  -d '{
    "cidadeOrigemId": 1,
    "cidadeDestinoId": 3,
    "distancia": 434.0,
    "tempo": 6.5
  }'
```

---

### 3️⃣ Listar Todas as Conexões

**Requisição:**
```bash
curl http://localhost:8080/conexoes
```

**Resposta Esperada:**
```json
[
  {
    "id": 1,
    "cidadeOrigemId": 1,
    "cidadeDestinoId": 2,
    "cidadeOrigemNome": "Rio de Janeiro - RJ",
    "cidadeDestinoNome": "São Paulo",
    "distancia": 430.5,
    "tempo": 6.0,
    "createdAt": "2026-06-12T21:53:00"
  },
  {
    "id": 2,
    "cidadeOrigemId": 2,
    "cidadeDestinoId": 3,
    "cidadeOrigemNome": "São Paulo",
    "cidadeDestinoNome": "Belo Horizonte",
    "distancia": 586.0,
    "tempo": 8.5,
    "createdAt": "2026-06-12T21:53:15"
  },
  {
    "id": 3,
    "cidadeOrigemId": 1,
    "cidadeDestinoId": 3,
    "cidadeOrigemNome": "Rio de Janeiro - RJ",
    "cidadeDestinoNome": "Belo Horizonte",
    "distancia": 434.0,
    "tempo": 6.5,
    "createdAt": "2026-06-12T21:53:30"
  }
]
```

---

### 4️⃣ Buscar Conexão por ID

**Requisição:**
```bash
curl http://localhost:8080/conexoes/1
```

---

## ❌ TESTES - ERROS ESPERADOS

### 1️⃣ Tentar Criar Cidade Duplicada

**Requisição:**
```bash
curl -X POST http://localhost:8080/cidades \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Rio de Janeiro - RJ",
    "latitude": -22.9068,
    "longitude": -43.1729
  }'
```

**Resposta Esperada (409):**
```json
{
  "timestamp": "2026-06-12T21:54:00",
  "status": 409,
  "error": "Conflict",
  "message": "Já existe uma cidade com esse nome: Rio de Janeiro - RJ"
}
```

---

### 2️⃣ Buscar Recurso Inexistente

**Requisição:**
```bash
curl http://localhost:8080/cidades/999
```

**Resposta Esperada (404):**
```json
{
  "timestamp": "2026-06-12T21:54:30",
  "status": 404,
  "error": "Not Found",
  "message": "Cidade não encontrada com ID: 999"
}
```

---

### 3️⃣ Criar Ponto com Cidade Inexistente

**Requisição:**
```bash
curl -X POST http://localhost:8080/pontos \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Ponto Fantasma",
    "description": "Teste",
    "category": "Teste",
    "latitude": 0,
    "longitude": 0,
    "cityId": 999
  }'
```

**Resposta Esperada (404):**
```json
{
  "timestamp": "2026-06-12T21:54:45",
  "status": 404,
  "error": "Not Found",
  "message": "Cidade não encontrada com ID: 999"
}
```

---

### 4️⃣ Validação - Nome Vazio

**Requisição:**
```bash
curl -X POST http://localhost:8080/cidades \
  -H "Content-Type: application/json" \
  -d '{
    "name": "",
    "latitude": -22.9068,
    "longitude": -43.1729
  }'
```

**Resposta Esperada (400):**
```json
{
  "timestamp": "2026-06-12T21:55:00",
  "status": 400,
  "error": "Bad Request",
  "message": "Validation failed: O nome da cidade não pode estar vazio"
}
```

---

## 🗑️ TESTES - DELEÇÃO

### 1️⃣ Deletar Ponto Turístico

**Requisição:**
```bash
curl -X DELETE http://localhost:8080/pontos/1
```

**Resposta Esperada (204):**
```
(sem corpo)
```

---

### 2️⃣ Verificar Deleção

**Requisição:**
```bash
curl http://localhost:8080/pontos/1
```

**Resposta Esperada (404):**
```json
{
  "timestamp": "2026-06-12T21:55:30",
  "status": 404,
  "error": "Not Found",
  "message": "Ponto turístico não encontrado com ID: 1"
}
```

---

### 3️⃣ Deletar Conexão

**Requisição:**
```bash
curl -X DELETE http://localhost:8080/conexoes/1
```

**Resposta Esperada (204):**
```
(sem corpo)
```

---

## ✅ CHECKLIST DE TESTES

- [ ] ✅ Criar 3 cidades
- [ ] ✅ Listar todas as cidades
- [ ] ✅ Buscar cidade por ID
- [ ] ✅ Atualizar cidade
- [ ] ✅ Criar 4+ pontos turísticos
- [ ] ✅ Listar todos os pontos
- [ ] ✅ Listar pontos por cidade (query param)
- [ ] ✅ Buscar ponto por ID
- [ ] ✅ Atualizar ponto
- [ ] ✅ Criar 3+ conexões
- [ ] ✅ Listar todas as conexões
- [ ] ✅ Buscar conexão por ID
- [ ] ✅ Teste de erro: cidade duplicada
- [ ] ✅ Teste de erro: recurso inexistente
- [ ] ✅ Teste de erro: validação vazia
- [ ] ✅ Deletar ponto
- [ ] ✅ Deletar conexão
- [ ] ✅ Verificar deleção

---

## 📊 Resumo da Estrutura Testada

```
Neo4j Graph:
├── Cidade (Rio de Janeiro) [ID: 1]
│   ├── PERTENCE_A
│   │   ├── PontoTuristico: Cristo Redentor [ID: 1]
│   │   └── PontoTuristico: Pão de Açúcar [ID: 2]
│   └── CONECTA → Cidade (São Paulo) [distancia: 430.5, tempo: 6.0]
│
├── Cidade (São Paulo) [ID: 2]
│   ├── PERTENCE_A
│   │   ├── PontoTuristico: Museu do Ipiranga [ID: 3]
│   │   └── PontoTuristico: Mercadão [ID: 4]
│   ├── CONECTA → Cidade (Rio de Janeiro)
│   └── CONECTA → Cidade (Belo Horizonte)
│
└── Cidade (Belo Horizonte) [ID: 3]
    └── CONECTA ← Cidades anteriores
```

---

**Testes Concluídos! ✅**

