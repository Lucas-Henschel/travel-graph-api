# TravelGraph API

Uma aplicação backend completa que modela cidades e pontos turísticos como uma rede conectada, permitindo calcular o melhor roteiro entre dois pontos usando Neo4j (banco de dados orientado a grafos).

## 🏗️ Arquitetura

- **Banco de Dados**: Neo4j (banco orientado a grafos)
- **Framework**: Spring Boot 3.5.6
- **Linguagem**: Java 17
- **Autenticação**: JWT
- **Validação**: Jakarta Validation

## 📦 Entidades Principais

### CityNode
Representa uma cidade no grafo:
- `id`: Identificador único
- `name`: Nome da cidade
- `description`: Descrição
- `latitude/longitude`: Coordenadas geográficas
- `country`: País
- `state`: Estado/Província
- `connections`: Conexões com outras cidades
- `attractions`: Atrações turísticas da cidade

### AttractionNode
Representa um ponto turístico:
- `id`: Identificador único
- `name`: Nome da atração
- `description`: Descrição
- `category`: Categoria (museu, parque, etc.)
- `latitude/longitude`: Coordenadas geográficas
- `rating`: Avaliação
- `address`: Endereço
- `city`: Cidade a que pertence
- `connections`: Conexões com outras atrações

### CityConnection & AttractionConnection
Relacionamentos ponderados entre nós:
- `distanceKm`: Distância em quilômetros
- `durationMinutes`: Tempo de viagem em minutos
- `transportType`: Tipo de transporte (carro, ônibus, trem, etc.)

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

## 📚 Documentação da API

### 1. **Cidades (Cities)**

#### Listar todas as cidades
```http
GET /cities
```

#### Buscar cidade por ID
```http
GET /cities/{id}
```

#### Criar nova cidade
```http
POST /cities
Content-Type: application/json

{
  "name": "Rio de Janeiro",
  "description": "A cidade maravilhosa",
  "latitude": -22.9068,
  "longitude": -43.1729,
  "country": "Brasil",
  "state": "RJ"
}
```

#### Atualizar cidade
```http
PUT /cities/{id}
Content-Type: application/json

{
  "name": "Rio de Janeiro",
  "description": "Atualizado",
  "latitude": -22.9068,
  "longitude": -43.1729,
  "country": "Brasil",
  "state": "RJ"
}
```

#### Deletar cidade
```http
DELETE /cities/{id}
```

#### Buscar cidades por nome (busca)
```http
GET /cities/search/{search}
```
Exemplo: `GET /cities/search/rio`

#### Buscar cidades por país
```http
GET /cities/country/{country}
```
Exemplo: `GET /cities/country/Brasil`

### 2. **Atrações Turísticas (Attractions)**

#### Listar todas as atrações
```http
GET /attractions
```

#### Buscar atração por ID
```http
GET /attractions/{id}
```

#### Criar nova atração
```http
POST /attractions
Content-Type: application/json

{
  "name": "Cristo Redentor",
  "description": "Estátua icônica",
  "category": "Monumento",
  "latitude": -22.9519,
  "longitude": -43.2105,
  "rating": 4.8,
  "address": "Corcovado, Rio de Janeiro",
  "cityId": 1
}
```

#### Atualizar atração
```http
PUT /attractions/{id}
Content-Type: application/json

{
  "name": "Cristo Redentor",
  "description": "Atualizado",
  "category": "Monumento",
  "latitude": -22.9519,
  "longitude": -43.2105,
  "rating": 4.9,
  "address": "Corcovado, Rio de Janeiro"
}
```

#### Deletar atração
```http
DELETE /attractions/{id}
```

#### Atrações de uma cidade
```http
GET /attractions/city/{cityId}
```

#### Buscar atrações por nome
```http
GET /attractions/search/{search}
```
Exemplo: `GET /attractions/search/cristo`

#### Atrações por categoria
```http
GET /attractions/category/{category}
```
Exemplo: `GET /attractions/category/Museu`

#### Atrações com avaliação mínima
```http
GET /attractions/rating/{minRating}
```
Exemplo: `GET /attractions/rating/4.0`

### 3. **Grafo e Roteiros (Graph)**

#### Caminho mais curto entre duas cidades
```http
GET /graph/shortest-path?source={sourceId}&target={targetId}
```

Exemplo:
```http
GET /graph/shortest-path?source=1&target=3
```

Resposta:
```json
{
  "path": [
    {
      "id": 1,
      "name": "Rio de Janeiro",
      "type": "CITY",
      "latitude": -22.9068,
      "longitude": -43.1729,
      "order": 0
    },
    {
      "id": 2,
      "name": "São Paulo",
      "type": "CITY",
      "latitude": -23.5505,
      "longitude": -46.6333,
      "order": 1
    }
  ],
  "totalDistanceKm": 430.5,
  "totalDurationMinutes": 360,
  "stepCount": 2
}
```

#### Cidades próximas com raio específico
```http
GET /graph/nearby-cities?cityId={cityId}&radius={radiusKm}
```

Exemplo:
```http
GET /graph/nearby-cities?cityId=1&radius=200
```

#### Atrações próximas a uma cidade
```http
GET /graph/nearby-attractions?cityId={cityId}
```

#### Roteiro recomendado
```http
GET /graph/itinerary?start={startCityId}&end={endCityId}&maxStops={maxStops}
```

Exemplo:
```http
GET /graph/itinerary?start=1&end=3&maxStops=5
```

#### Criar conexão entre cidades ou atrações
```http
POST /graph/connections
Content-Type: application/json

{
  "sourceId": 1,
  "targetId": 2,
  "distanceKm": 430.5,
  "durationMinutes": 360,
  "transportType": "Carro",
  "connectionType": "CITY"
}
```

## 🔍 Exemplos de Uso Prático

### Cenário 1: Criar rede de cidades
```bash
# 1. Criar cidades
curl -X POST http://localhost:8080/cities \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Rio de Janeiro",
    "latitude": -22.9068,
    "longitude": -43.1729,
    "country": "Brasil",
    "state": "RJ"
  }'

# 2. Criar mais cidades (repetir para cada cidade)

# 3. Conectar as cidades
curl -X POST http://localhost:8080/graph/connections \
  -H "Content-Type: application/json" \
  -d '{
    "sourceId": 1,
    "targetId": 2,
    "distanceKm": 430.5,
    "durationMinutes": 360,
    "transportType": "Carro",
    "connectionType": "CITY"
  }'
```

### Cenário 2: Buscar melhor rota
```bash
curl http://localhost:8080/graph/shortest-path?source=1&target=5
```

### Cenário 3: Listar atrações de uma cidade
```bash
curl http://localhost:8080/attractions/city/1
```

## 📊 Consultas Cypher Nativamente Suportadas

A aplicação utiliza as seguintes consultas Cypher:

1. **Caminho Mais Curto**:
   ```cypher
   MATCH path = shortestPath((source:City {id: $sourceId})-[*]-(target:City {id: $targetId}))
   WITH nodes(path) as nodes, relationships(path) as rels
   RETURN nodes, rels
   ```

2. **Locais Próximos**:
   ```cypher
   MATCH (source:City {id: $cityId})-[connection:CONNECTS_TO]->(target:City)
   WHERE connection.distanceKm <= $radius
   RETURN target, connection.distanceKm as distance
   ```

3. **Atrações por Cidade**:
   ```cypher
   MATCH (a:Attraction)-[:HAS_ATTRACTION]-(c:City)
   WHERE c.id = $cityId
   RETURN a
   ```

## 🔐 Segurança

- Endpoints de **leitura** (GET) de cidades, atrações e grafo são **públicos**
- Endpoints de **escrita** (POST, PUT, DELETE) requerem **autenticação JWT**
- Todas as requisições são validadas com `@Valid`

## 🧪 Testes

```bash
# Compilar e rodar testes
mvn clean test
```

## 📝 Estrutura de Pastas

```
src/main/java/com/travelGraph/
├── controller/
│   ├── CityController.java
│   ├── AttractionController.java
│   ├── GraphController.java
│   └── exceptions/
│       └── ResourceExceptionHandler.java
├── services/
│   ├── CityService.java
│   ├── AttractionService.java
│   ├── GraphQueryService.java
│   └── exceptions/
├── entities/
│   ├── CityNode.java
│   ├── AttractionNode.java
│   ├── CityConnection.java
│   └── AttractionConnection.java
├── repositories/
│   ├── CityRepository.java
│   └── AttractionRepository.java
├── dto/
│   ├── city/
│   ├── attraction/
│   ├── connection/
│   └── route/
└── mapper/
    ├── CityMapper.java
    └── AttractionMapper.java
```

## 🛠️ Tecnologias Utilizadas

- **Spring Boot**: Framework web
- **Spring Data Neo4j**: ORM para Neo4j
- **Neo4j**: Banco de dados em grafo
- **Lombok**: Redução de boilerplate
- **Jakarta Validation**: Validação de dados
- **JWT**: Autenticação

## 📄 Licença

MIT

## 👨‍💻 Autor

Travel Graph API - Sistema de Roteiros Turísticos com Neo4j

