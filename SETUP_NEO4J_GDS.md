# 🔧 SETUP - Neo4j GDS para Travel Graph API

Para usar a funcionalidade de **Cálculo de Rotas**, é necessário instalar a biblioteca **Neo4j Graph Data Science (GDS)**.

---

## 📋 Requisitos

- Neo4j 4.3+
- Graph Data Science Library (GDS)
- Java 17+

---

## 🚀 Opção 1: Neo4j com Docker (Recomendado)

### 1. Instalar Docker
Baixe em: https://www.docker.com/products/docker-desktop

### 2. Executar Neo4j com GDS

```bash
docker run -d \
  -p 7687:7687 \
  -p 7474:7474 \
  -e NEO4J_AUTH=neo4j/Flamengo29. \
  -e NEO4J_PLUGINS='["graph-data-science"]' \
  -e NEO4J_dbms__security__procedures__unrestricted=gds.* \
  neo4j:5.0-enterprise
```

**Explicação:**
- `-p 7687:7687`: Bolt protocol (driver Java)
- `-p 7474:7474`: Web interface
- `NEO4J_AUTH`: User/password
- `NEO4J_PLUGINS`: Ativa GDS
- `NEO4J_dbms__security__procedures__unrestricted`: Permite GDS procedures

### 3. Verificar Instalação

```bash
# Acessar Neo4j Browser
http://localhost:7474

# User: neo4j
# Password: Flamengo29.

# Executar no browser
CALL gds.version()
```

Deve retornar algo como:
```
{
  "gdsVersion": "2.3.0",
  "graphQLVersion": "2.3.0"
}
```

---

## 📦 Opção 2: Neo4j Local (Windows)

### 1. Download

Acesse: https://neo4j.com/download-center/

Selecione:
- Produto: Neo4j Enterprise (requer licença, mas trial funciona)
- Versão: 5.0+

### 2. Instalar GDS

1. Abra o Neo4j Desktop
2. Clique no banco de dados
3. "Manage" → "Plugins"
4. Procure "Graph Data Science"
5. Instale

### 3. Configuração

Edite o arquivo `neo4j.conf`:
```
# Ativar GDS
dbms.security.procedures.unrestricted=gds.*
```

### 4. Reiniciar Neo4j

```
Clique em "Start" no Neo4j Desktop
```

---

## ✅ Verificar Instalação

### Via Neo4j Browser

1. Acesse: http://localhost:7474
2. Execute:
```cypher
CALL gds.version()
```

### Via aplicação Travel Graph

Execute uma rota:
```bash
curl "http://localhost:8080/rotas?origem=1&destino=2&criterio=distancia"
```

Se funcionar: ✅ GDS está instalado!

---

## 🔐 Configuração da Aplicação

### application-dev.properties

```properties
# Neo4j
spring.neo4j.uri=neo4j://127.0.0.1:7687
spring.neo4j.authentication.username=neo4j
spring.neo4j.authentication.password=Flamengo29.
spring.data.neo4j.database=travel-graph

# Logging
logging.level.org.springframework.data.neo4j=DEBUG
logging.level.com.travelGraph=DEBUG
```

---

## 🧪 Teste Completo

### 1. Criar Dados

```bash
# Terminal 1: Iniciar aplicação
mvn spring-boot:run

# Terminal 2: Criar cidades
curl -X POST http://localhost:8080/cidades \
  -H "Content-Type: application/json" \
  -d '{"name":"Cidade A","latitude":0,"longitude":0}'

curl -X POST http://localhost:8080/cidades \
  -H "Content-Type: application/json" \
  -d '{"name":"Cidade B","latitude":1,"longitude":1}'

curl -X POST http://localhost:8080/cidades \
  -H "Content-Type: application/json" \
  -d '{"name":"Cidade C","latitude":2,"longitude":2}'
```

### 2. Conectar Cidades

```bash
curl -X POST http://localhost:8080/conexoes \
  -H "Content-Type: application/json" \
  -d '{"cidadeOrigemId":1,"cidadeDestinoId":2,"distancia":100,"tempo":1.5}'

curl -X POST http://localhost:8080/conexoes \
  -H "Content-Type: application/json" \
  -d '{"cidadeOrigemId":2,"cidadeDestinoId":3,"distancia":150,"tempo":2.0}'
```

### 3. Calcular Rota

```bash
curl "http://localhost:8080/rotas?origem=1&destino=3&criterio=distancia"
```

**Resposta Esperada:**
```json
{
  "cidades": [
    {"id": 1, "nome": "Cidade A", "latitude": 0, "longitude": 0, "pontosTuristicos": []},
    {"id": 2, "nome": "Cidade B", "latitude": 1, "longitude": 1, "pontosTuristicos": []},
    {"id": 3, "nome": "Cidade C", "latitude": 2, "longitude": 2, "pontosTuristicos": []}
  ],
  "distanciaTotal": 250.0,
  "tempoTotal": 3.5
}
```

---

## 🐛 Troubleshooting

### "GDS not available" ou erro 404

**Problema**: Neo4j rodando sem GDS

**Solução**:
1. Parar container: `docker stop <container_id>`
2. Remover: `docker rm <container_id>`
3. Rodar novamente com `-e NEO4J_PLUGINS='["graph-data-science"]'`

---

### "Connection refused"

**Problema**: Neo4j não está rodando

**Solução**:
```bash
# Verificar status
docker ps

# Se não aparecer, iniciar
docker run -d -p 7687:7687 -p 7474:7474 \
  -e NEO4J_AUTH=neo4j/Flamengo29. \
  neo4j:5.0
```

---

### "Invalid credentials"

**Problema**: User/password incorreto

**Solução**:
- User padrão: `neo4j`
- Senha padrão (se mudar): verificar `docker run` ou Neo4j Desktop

---

### "Procedure not found: gds.graph.project"

**Problema**: GDS não instalado corretamente

**Solução**:
```cypher
# Verificar no Neo4j Browser
SHOW PROCEDURES LIKE 'gds.*'

# Se vazio, reinstalar GDS:
# 1. Docker: adicione Neo4j_PLUGINS
# 2. Local: use Neo4j Desktop → Plugins
```

---

## 📊 Algoritmos Disponíveis

### Dijkstra (Usado)
```cypher
CALL gds.shortestPath.dijkstra.stream(...)
```
- Melhor caminho por peso
- O(E log V)

### A* (Futuro)
```cypher
CALL gds.shortestPath.astar.stream(...)
```
- Com coordenadas (latitude/longitude)
- Mais rápido que Dijkstra

### Outros
- `all-pairs-shortest-path`
- `floyd-warshall`
- `bellman-ford`

---

## 📚 Referências

- [Neo4j GDS Documentation](https://neo4j.com/docs/graph-data-science/)
- [Dijkstra Algorithm](https://en.wikipedia.org/wiki/Dijkstra%27s_algorithm)
- [Neo4j Docker Hub](https://hub.docker.com/_/neo4j)

---

## ✅ Checklist

- [ ] Neo4j instalado e rodando
- [ ] GDS plugin ativo
- [ ] Verificou `gds.version()` com sucesso
- [ ] Application-dev.properties configurado
- [ ] Criou dados de teste
- [ ] Conectou cidades
- [ ] Testou endpoint `/rotas`
- [ ] Recebeu resposta com sucesso

---

**Setup Neo4j GDS Concluído!** ✅

Para questões, consulte `ROUTES_FEATURE.md` ou `TESTING_GUIDE.md`

