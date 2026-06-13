# TravelGraph API - Reestruturada

Uma aplicação backend que modela cidades e pontos turísticos como uma rede conectada, permitindo calcular o melhor roteiro entre dois pontos usando Neo4j (banco de dados orientado a grafos).

## 🏗️ Arquitetura

- **Banco de Dados**: Neo4j (banco orientado a grafos)
- **Framework**: Spring Boot 3.5.6
- **Linguagem**: Java 17
- **Autenticação**: JWT
- **Validação**: Jakarta Validation

## 📦 Estrutura Reestruturada

### CRUD de Cidades

#### Model
- **Node**: `Cidade`
- **Campos**: 
  - `id`: Identificador único
  - `nome`: Nome da cidade
  - `latitude`: Coordenada de latitude (double)
  - `longitude`: Coordenada de longitude (double)

#### Repository
- `CidadeRepository extends Neo4jRepository`
- Método: `findByName(String name)` - para queries de rota

#### Endpoints
- `GET /cidades` — lista todas as cidades
- `GET /cidades/{id}` — busca por ID
- `POST /cidades` — cria nova cidade
- `PUT /cidades/{id}` — atualiza cidade
- `DELETE /cidades/{id}` — remove cidade

**Exemplo de requisição POST /cidades:**
```json
{
  "name": "Rio de Janeiro",
  "latitude": -22.9068,
  "longitude": -43.1729
}
```

---

### CRUD de Pontos Turísticos

#### Model
- **Node**: `PontoTuristico`
- **Campos**:
  - `id`: Identificador único
  - `nome`: Nome do ponto
  - `descricao`: Descrição do ponto
  - `categoria`: Categoria (museu, parque, etc.)
  - `latitude`: Coordenada de latitude (double)
  - `longitude`: Coordenada de longitude (double)
- **Relacionamento**: `@Relationship("PERTENCE_A")` apontando para Cidade

#### Repository
- `PontoTuristicoRepository extends Neo4jRepository`
- Método: `findByCidadeId(Long cityId)` - para listar pontos de uma cidade

#### Endpoints
- `GET /pontos` — lista todos os pontos
- `GET /pontos?cidadeId=X` — lista pontos de uma cidade
- `GET /pontos/{id}` — busca ponto por ID
- `POST /pontos` — cria ponto vinculado a uma cidade
- `PUT /pontos/{id}` — atualiza ponto
- `DELETE /pontos/{id}` — remove ponto

**Exemplo de requisição POST /pontos:**
```json
{
  "name": "Cristo Redentor",
  "description": "Estátua icônica do Rio de Janeiro",
  "category": "Monumento",
  "latitude": -22.9519,
  "longitude": -43.2105,
  "cityId": 1
}
```

---

### Conexões entre Cidades

#### Model
- **Relacionamento**: `CONECTA` entre dois nós Cidade
- **Classe**: `CityConnection` com `@RelationshipProperties`
- **Propriedades**:
  - `id`: Identificador único
  - `distancia`: Distância em km (double)
  - `tempo`: Tempo de viagem em horas (double)
  - `targetCity`: Cidade de destino

#### Endpoints
- `GET /conexoes` — lista todas as conexões
- `GET /conexoes/{id}` — busca conexão por ID
- `POST /conexoes` — cria conexão entre duas cidades
- `DELETE /conexoes/{id}` — remove conexão

**Exemplo de requisição POST /conexoes:**
```json
{
  "cidadeOrigemId": 1,
  "cidadeDestinoId": 2,
  "distancia": 430.5,
  "tempo": 6.0
}
```

**Response GET /conexoes:**
```json
[
  {
    "id": 1,
    "cidadeOrigemId": 1,
    "cidadeDestinoId": 2,
    "cidadeOrigemNome": "Rio de Janeiro",
    "cidadeDestinoNome": "São Paulo",
    "distancia": 430.5,
    "tempo": 6.0,
    "createdAt": "2026-06-12T21:45:00"
  }
]
```

---

## 🚀 Configuração e Execução

### Pré-requisitos
- Java 17+
- Maven
- Neo4j (local ou container Docker)

### Instalar e Executar Neo4j (Docker)
```bash
docker run -d -p 7687:7687 -p 7474:7474 -e NEO4J_AUTH=neo4j/Flamengo29. neo4j:latest
```

### Configurar Ambiente
Edite `src/main/resources/application-dev.properties`:
```properties
environment.name=dev

# Neo4j properties
spring.neo4j.uri=neo4j://127.0.0.1:7687
spring.neo4j.authentication.username=neo4j
spring.neo4j.authentication.password=Flamengo29.
spring.data.neo4j.database=travel-graph

logging.level.org.springframework.data.neo4j=DEBUG
```

### Executar Aplicação
```bash
mvn clean install
mvn spring-boot:run -Dspring-boot.run.arguments="--spring.profiles.active=dev"
```

A API estará disponível em `http://localhost:8080`

---

## 📚 DTOs da API

### City DTOs
- `CreateCityRequestDTO`: Campos (name, latitude, longitude)
- `UpdateCityRequestDTO`: Campos (name, latitude, longitude)
- `CityResponseDTO`: Campos (id, name, latitude, longitude, createdAt, updatedAt)

### Attraction DTOs
- `CreateAttractionRequestDTO`: Campos (name, description, category, latitude, longitude, cityId)
- `UpdateAttractionRequestDTO`: Campos (name, description, category, latitude, longitude)
- `AttractionResponseDTO`: Campos (id, name, description, category, latitude, longitude, city, createdAt, updatedAt)

### Connection DTOs
- `ConexaoRequestDTO`: Campos (cidadeOrigemId, cidadeDestinoId, distancia, tempo)
- `ConexaoResponseDTO`: Campos (id, cidadeOrigemId, cidadeDestinoId, cidadeOrigemNome, cidadeDestinoNome, distancia, tempo, createdAt)

---

## 🔍 Exemplos de Uso Prático

### Cenário 1: Criar Rede de Cidades
```bash
# 1. Criar cidade 1
curl -X POST http://localhost:8080/cidades \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Rio de Janeiro",
    "latitude": -22.9068,
    "longitude": -43.1729
  }'

# 2. Criar cidade 2
curl -X POST http://localhost:8080/cidades \
  -H "Content-Type: application/json" \
  -d '{
    "name": "São Paulo",
    "latitude": -23.5505,
    "longitude": -46.6333
  }'

# 3. Conectar cidades
curl -X POST http://localhost:8080/conexoes \
  -H "Content-Type: application/json" \
  -d '{
    "cidadeOrigemId": 1,
    "cidadeDestinoId": 2,
    "distancia": 430.5,
    "tempo": 6.0
  }'
```

### Cenário 2: Adicionar Pontos Turísticos
```bash
# Criar ponto turístico na cidade 1
curl -X POST http://localhost:8080/pontos \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Cristo Redentor",
    "description": "Estátua icônica",
    "category": "Monumento",
    "latitude": -22.9519,
    "longitude": -43.2105,
    "cityId": 1
  }'

# Listar pontos da cidade 1
curl http://localhost:8080/pontos?cidadeId=1
```

### Cenário 3: Listar Todas as Conexões
```bash
curl http://localhost:8080/conexoes
```

---

## 🛠️ Tecnologias Utilizadas

- **Spring Boot**: Framework web
- **Spring Data Neo4j**: ORM para Neo4j
- **Neo4j**: Banco de dados em grafo
- **Lombok**: Redução de boilerplate
- **Jakarta Validation**: Validação de dados
- **JWT**: Autenticação

---

## 📝 Estrutura de Pastas

```
src/main/java/com/travelGraph/
├── controller/
│   ├── CityController.java
│   ├── AttractionController.java
│   ├── ConexaoController.java
│   └── MainController.java
├── services/
│   ├── CityService.java
│   ├── AttractionService.java
│   ├── ConexaoService.java
│   └── GraphQueryService.java
├── entities/
│   ├── CityNode.java
│   ├── AttractionNode.java
│   └── CityConnection.java
├── repositories/
│   ├── CityRepository.java
│   ├── AttractionRepository.java
│   └── ConexaoRepository.java
├── dto/
│   ├── city/
│   ├── attraction/
│   └── connection/
└── mapper/
    ├── CityMapper.java
    └── AttractionMapper.java
```

---

## ✅ Mudanças Realizadas na Reestruturação

1. ✅ **CityNode** simplificado para apenas: id, name, latitude, longitude
2. ✅ **AttractionNode** ajustado com relacionamento `PERTENCE_A`
3. ✅ **CityConnection** com propriedades: distancia (km) e tempo (horas)
4. ✅ Removidos campos extras: description, country, state, rating, address
5. ✅ Endpoints de cidades mudados para `/cidades`
6. ✅ Endpoints de pontos mudados para `/pontos` com query param `cidadeId`
7. ✅ Novo controller `/conexoes` para gerenciar conexões
8. ✅ DTOs simplificados conforme o novo padrão
9. ✅ Repositórios simplificados, removendo métodos extras

---

## 📄 Licença

MIT

## 👨‍💻 Autor

Travel Graph API - Sistema de Roteiros Turísticos com Neo4j

