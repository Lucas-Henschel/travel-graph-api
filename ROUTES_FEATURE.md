# 🗺️ CÁLCULO DE ROTAS - Travel Graph API

## 📋 Visão Geral

Funcionalidade para calcular a rota mais curta entre duas cidades usando Neo4j GDS (Graph Data Science Library) com algoritmo de Dijkstra.

---

## 🏗️ Arquitetura

### Componentes Criados

#### 1. DTOs (Data Transfer Objects)
- **RoteiroDTO**: Resposta com cidades do roteiro, distância total e tempo total
- **CidadeRotaDTO**: Cidade com seus pontos turísticos no contexto da rota

#### 2. Service
- **RotaService**: Lógica de cálculo de rota usando GDS

#### 3. Controller
- **RotaController**: Endpoint REST `/rotas`

---

## 🔌 Endpoint

### GET /rotas

**URL**: `http://localhost:8080/rotas?origem=1&destino=2&criterio=distancia`

**Query Parameters**:
| Parâmetro | Tipo | Obrigatório | Descrição |
|-----------|------|-----------|-----------|
| `origem` | Long | Sim | ID da cidade de origem |
| `destino` | Long | Sim | ID da cidade de destino |
| `criterio` | String | Não (padrão: distancia) | `distancia` ou `tempo` |

**Resposta (200 OK)**:
```json
{
  "cidades": [
    {
      "id": 1,
      "nome": "Rio de Janeiro",
      "latitude": -22.9068,
      "longitude": -43.1729,
      "pontosTuristicos": [
        {
          "id": 1,
          "name": "Cristo Redentor",
          "description": "Estátua icônica",
          "category": "Monumento",
          "latitude": -22.9519,
          "longitude": -43.2105,
          "createdAt": "2026-06-12T21:50:00",
          "updatedAt": null,
          "city": { ... }
        }
      ]
    },
    {
      "id": 2,
      "nome": "São Paulo",
      "latitude": -23.5505,
      "longitude": -46.6333,
      "pontosTuristicos": [ ... ]
    }
  ],
  "distanciaTotal": 430.5,
  "tempoTotal": 6.0
}
```

---

## 🔧 Fluxo de Funcionamento

### 1. Validação de Entrada
```
Cliente envia: /rotas?origem=1&destino=2&criterio=distancia
    ↓
RotaController recebe requisição
    ↓
Validar origem existe? Sim → Prosseguir
Validar destino existe? Sim → Prosseguir
Validar criterio? ("distancia" ou "tempo") Sim → Prosseguir
```

### 2. Criar Grafo em Memória
```
RotaService.criarGrafo(propriedadePeso)
    ↓
Neo4j GDS cria grafo:
  - Nós: todas as Cidades
  - Relacionamentos: CONECTA
  - Peso: distancia ou tempo
    ↓
Grafo pronto para algoritmo
```

**Cypher**:
```cypher
CALL gds.graph.project(
    'travel_graph',
    'Cidade',
    'CONECTA',
    { relationshipProperties: { weight: { property: 'distancia', defaultValue: 1.0 } } }
)
YIELD graphName, nodeCount, relationshipCount
RETURN graphName, nodeCount, relationshipCount
```

### 3. Executar Dijkstra
```
RotaService.executarDijkstra(origem, destino, propriedadePeso)
    ↓
Neo4j GDS executa Dijkstra:
  - Source: Cidade origem
  - Target: Cidade destino
  - Weight: propriedade de peso (distancia/tempo)
    ↓
Retorna lista de IDs das cidades no caminho
```

**Cypher**:
```cypher
CALL gds.shortestPath.dijkstra.stream(
    'travel_graph',
    { sourceNode: 1, targetNode: 2, relationshipWeightProperty: 'weight' }
)
YIELD nodeIds, costs
RETURN nodeIds, costs
```

### 4. Construir Resposta
```
RotaService.construirRoteiro(caminhoIds, propriedadePeso)
    ↓
Para cada cidade no caminho:
  1. Buscar dados da cidade
  2. Buscar pontos turísticos vinculados
  3. Calcular distancia/tempo até próxima cidade
    ↓
Retornar RoteiroDTO com todas as informações
```

### 5. Limpar Grafo em Memória
```
RotaService.deletarGrafo()
    ↓
Neo4j GDS deleta o grafo da memória
    ↓
Libera recursos
```

**Cypher**:
```cypher
CALL gds.graph.drop('travel_graph')
YIELD graphName
RETURN graphName
```

---

## 📊 DTOs

### RoteiroDTO
```java
@Data
public class RoteiroDTO {
    private List<CidadeRotaDTO> cidades;      // Cidades do caminho
    private Double distanciaTotal;             // Soma distâncias
    private Double tempoTotal;                 // Soma tempos
}
```

### CidadeRotaDTO
```java
@Data
public class CidadeRotaDTO {
    private Long id;                           // ID da cidade
    private String nome;                       // Nome da cidade
    private Double latitude;                   // Latitude
    private Double longitude;                  // Longitude
    private List<AttractionResponseDTO> pontosTuristicos;  // Pontos turísticos
}
```

---

## 🧪 Exemplos de Uso

### 1. Calcular Rota Otimizada por Distância

**Requisição:**
```bash
curl "http://localhost:8080/rotas?origem=1&destino=3&criterio=distancia"
```

**Resposta:**
```json
{
  "cidades": [
    {
      "id": 1,
      "nome": "Rio de Janeiro",
      "latitude": -22.9068,
      "longitude": -43.1729,
      "pontosTuristicos": [
        {
          "id": 1,
          "name": "Cristo Redentor",
          "description": "Estátua icônica",
          "category": "Monumento",
          "latitude": -22.9519,
          "longitude": -43.2105,
          "createdAt": "2026-06-12T21:50:00",
          "updatedAt": null,
          "city": null
        },
        {
          "id": 2,
          "name": "Pão de Açúcar",
          "description": "Montanha com teleférico",
          "category": "Atração Natural",
          "latitude": -22.9432,
          "longitude": -43.1614,
          "createdAt": "2026-06-12T21:50:30",
          "updatedAt": null,
          "city": null
        }
      ]
    },
    {
      "id": 3,
      "nome": "Belo Horizonte",
      "latitude": -19.8267,
      "longitude": -43.9445,
      "pontosTuristicos": [
        {
          "id": 5,
          "name": "Pampulha",
          "description": "Lagoa artificial",
          "category": "Atração Natural",
          "latitude": -19.8704,
          "longitude": -43.9799,
          "createdAt": "2026-06-12T21:52:00",
          "updatedAt": null,
          "city": null
        }
      ]
    }
  ],
  "distanciaTotal": 868.0,
  "tempoTotal": 13.0
}
```

---

### 2. Calcular Rota Otimizada por Tempo

**Requisição:**
```bash
curl "http://localhost:8080/rotas?origem=1&destino=2&criterio=tempo"
```

**Resposta:**
```json
{
  "cidades": [
    {
      "id": 1,
      "nome": "Rio de Janeiro",
      "latitude": -22.9068,
      "longitude": -43.1729,
      "pontosTuristicos": [ ... ]
    },
    {
      "id": 2,
      "nome": "São Paulo",
      "latitude": -23.5505,
      "longitude": -46.6333,
      "pontosTuristicos": [ ... ]
    }
  ],
  "distanciaTotal": 430.5,
  "tempoTotal": 6.0
}
```

---

### 3. Usar Critério Padrão (distancia)

**Requisição:**
```bash
curl "http://localhost:8080/rotas?origem=1&destino=2"
```

Equivalente a:
```bash
curl "http://localhost:8080/rotas?origem=1&destino=2&criterio=distancia"
```

---

## ❌ Erros e Tratamento

### 404 - Cidade Não Encontrada

**Requisição:**
```bash
curl "http://localhost:8080/rotas?origem=999&destino=2"
```

**Resposta:**
```json
{
  "timestamp": "2026-06-12T21:55:00",
  "status": 404,
  "error": "Not Found",
  "message": "Cidade de origem não encontrada: 999"
}
```

---

### 404 - Nenhuma Rota Encontrada

**Requisição:**
```bash
curl "http://localhost:8080/rotas?origem=1&destino=2"
```
(Se não há conexão entre cidades)

**Resposta:**
```json
{
  "timestamp": "2026-06-12T21:55:30",
  "status": 404,
  "error": "Invalid Route",
  "message": "Nenhuma rota encontrada entre as cidades informadas"
}
```

---

### 400 - Critério Inválido

**Requisição:**
```bash
curl "http://localhost:8080/rotas?origem=1&destino=2&criterio=preco"
```

**Resposta:**
```json
{
  "timestamp": "2026-06-12T21:55:45",
  "status": 400,
  "error": "Bad Request",
  "message": "Critério inválido. Use 'distancia' ou 'tempo'"
}
```

---

### 400 - Parâmetro Obrigatório Ausente

**Requisição:**
```bash
curl "http://localhost:8080/rotas?origem=1"
```
(Falta destino)

**Resposta:**
```json
{
  "timestamp": "2026-06-12T21:56:00",
  "status": 400,
  "error": "Bad Request",
  "message": "O ID da cidade de destino é obrigatório"
}
```

---

## 🎯 Casos de Uso

### 1. Roteiro Turístico Rápido
```
Usuário quer ir do Rio para São Paulo no menor tempo possível
GET /rotas?origem=1&destino=2&criterio=tempo

Resultado: Rota rápida com pontos turísticos em cada cidade
```

### 2. Roteiro Econômico
```
Usuário quer minimizar distância (menor combustível)
GET /rotas?origem=1&destino=2&criterio=distancia

Resultado: Rota com menor quilometragem
```

### 3. Exploração de Cidades Intermediárias
```
Usuário vê as cidades intermediárias no caminho
GET /rotas?origem=1&destino=3&criterio=distancia

Resultado: Lista todas as cidades, não apenas origem e destino
```

---

## 🔐 Neo4j GDS - Conceitos

### O que é GDS (Graph Data Science)?
Biblioteca do Neo4j para algoritmos de grafos em larga escala

### gds.graph.project()
- Cria um grafo em memória
- Mais rápido que rodar diretamente no Cypher
- Suporta algoritmos otimizados

### gds.shortestPath.dijkstra.stream()
- Algoritmo de caminho mais curto
- Usa propriedade de peso (distancia/tempo)
- Retorna o caminho com menor custo

### gds.graph.drop()
- Remove grafo da memória
- Libera recursos
- Importante para limpeza

---

## 📈 Performance

### Optimizações Implementadas

1. **Grafo em Memória**: GDS cria grafo otimizado uma única vez
2. **Algoritmo Eficiente**: Dijkstra é O(E log V)
3. **Limpeza**: Grafo é deletado após uso
4. **Validações**: Verificar existência de cidades antecipadamente

### Complexidade
- **Criar Grafo**: O(V + E)
- **Dijkstra**: O(E log V)
- **Deletar Grafo**: O(1)
- **Total**: O(V + E + E log V)

Onde:
- V = número de cidades
- E = número de conexões

---

## 🛠️ Requisitos Neo4j

### Versão Mínima
- Neo4j 4.3+ (com GDS)

### GDS Plugin
Deve estar instalado:
```
neo4j.conf: dbms.security.procedures.unrestricted=gds.*
```

### Verificar Instalação
```cypher
CALL gds.version()
```

---

## 📝 Logging

### Logs Gerados

```
[INFO] Calculando rota de 1 para 2 com critério: distancia
[INFO] Criando grafo em memória para critério: distancia
[INFO] Grafo criado com sucesso
[INFO] Executando Dijkstra de 1 para 2
[INFO] Caminho encontrado com 3 cidades
[INFO] Construindo roteiro com 3 cidades
[INFO] Deletando grafo em memória
[INFO] Grafo deletado com sucesso
```

---

## 🧪 Teste Completo

### 1. Setup Inicial
```bash
# Criar cidades
curl -X POST http://localhost:8080/cidades -H "Content-Type: application/json" -d '{"name":"A","latitude":0,"longitude":0}'
curl -X POST http://localhost:8080/cidades -H "Content-Type: application/json" -d '{"name":"B","latitude":1,"longitude":1}'
curl -X POST http://localhost:8080/cidades -H "Content-Type: application/json" -d '{"name":"C","latitude":2,"longitude":2}'

# Conectar cidades
curl -X POST http://localhost:8080/conexoes -H "Content-Type: application/json" -d '{"cidadeOrigemId":1,"cidadeDestinoId":2,"distancia":100,"tempo":1.5}'
curl -X POST http://localhost:8080/conexoes -H "Content-Type: application/json" -d '{"cidadeOrigemId":2,"cidadeDestinoId":3,"distancia":150,"tempo":2.0}'

# Criar pontos turísticos
curl -X POST http://localhost:8080/pontos -H "Content-Type: application/json" -d '{"name":"P1","description":"Ponto 1","category":"Tour","latitude":0,"longitude":0,"cityId":1}'
curl -X POST http://localhost:8080/pontos -H "Content-Type: application/json" -d '{"name":"P2","description":"Ponto 2","category":"Tour","latitude":1,"longitude":1,"cityId":2}'
```

### 2. Testar Rota
```bash
curl "http://localhost:8080/rotas?origem=1&destino=3&criterio=distancia"
```

### 3. Validar Resposta
```json
{
  "cidades": [
    { "id": 1, "nome": "A", ... "pontosTuristicos": [...] },
    { "id": 2, "nome": "B", ... "pontosTuristicos": [...] },
    { "id": 3, "nome": "C", ... "pontosTuristicos": [...] }
  ],
  "distanciaTotal": 250.0,
  "tempoTotal": 3.5
}
```

---

## 📚 Referências

- [Neo4j GDS Docs](https://neo4j.com/docs/graph-data-science/current/)
- [Dijkstra Algorithm](https://en.wikipedia.org/wiki/Dijkstra%27s_algorithm)
- [Spring Data Neo4j](https://spring.io/projects/spring-data-neo4j)

---

**Funcionalidade de Rotas - Travel Graph API v1.0**  
*Última atualização: 12 de Junho de 2026*

