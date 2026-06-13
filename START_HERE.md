# 🎉 BEM-VINDO - Travel Graph API Reestruturada

## ⚡ Quick Start (5 minutos)

### 1. Compilar o Projeto
```bash
cd C:\Users\anahe\Downloads\travel-graph-api
mvn clean install
```

### 2. Iniciar a Aplicação
```bash
mvn spring-boot:run -Dspring-boot.run.arguments="--spring.profiles.active=dev"
```

### 3. Testar um Endpoint
```bash
# Terminal (PowerShell/CMD)
curl -X POST http://localhost:8080/cidades `
  -H "Content-Type: application/json" `
  -d '{"name":"Rio de Janeiro","latitude":-22.9068,"longitude":-43.1729}'
```

---

## 📚 Documentação Disponível

| Arquivo | Descrição | Para Quem |
|---------|-----------|----------|
| **FINAL_SUMMARY.md** | 📊 Visão geral executiva | Gerentes, Arquitetos |
| **RESTRUCTURED.md** | 🔧 Documentação técnica completa | Desenvolvedores |
| **CHANGELOG_RESTRUCTURE.md** | 📝 Mudanças detalhadas | Revisores, Code Reviewers |
| **DIRECTORY_STRUCTURE.md** | 📁 Árvore do projeto | Novatos, Exploradores |
| **TESTING_GUIDE.md** | 🧪 Exemplos de testes | QA, Testers |
| **Este arquivo (START_HERE.md)** | ⚡ Quick start | Todos |

---

## 🎯 O Que Mudou?

### Antes ❌
```
Endpoints: /cities, /attractions
Campos: muitos campos desnecessários
Estrutura: complexa com muitos métodos auxiliares
```

### Depois ✅
```
Endpoints: /cidades, /pontos, /conexoes
Campos: apenas o essencial
Estrutura: limpa e modular
```

---

## 🚀 Endpoints Disponíveis

### Cidades
```
GET    /cidades              # Listar todas
GET    /cidades/{id}         # Por ID
POST   /cidades              # Criar
PUT    /cidades/{id}         # Atualizar
DELETE /cidades/{id}         # Deletar
```

### Pontos Turísticos
```
GET    /pontos               # Listar todos
GET    /pontos?cidadeId=X    # Por cidade
GET    /pontos/{id}          # Por ID
POST   /pontos               # Criar
PUT    /pontos/{id}          # Atualizar
DELETE /pontos/{id}          # Deletar
```

### Conexões (NOVO)
```
GET    /conexoes             # Listar todas
GET    /conexoes/{id}        # Por ID
POST   /conexoes             # Criar
DELETE /conexoes/{id}        # Deletar
```

---

## 💡 Exemplos Práticos

### 1️⃣ Criar uma Cidade
```bash
curl -X POST http://localhost:8080/cidades \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Rio de Janeiro",
    "latitude": -22.9068,
    "longitude": -43.1729
  }'
```

**Resposta:**
```json
{
  "id": 1,
  "name": "Rio de Janeiro",
  "latitude": -22.9068,
  "longitude": -43.1729,
  "createdAt": "2026-06-12T22:00:00",
  "updatedAt": null
}
```

---

### 2️⃣ Criar um Ponto Turístico
```bash
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
```

---

### 3️⃣ Listar Pontos de Uma Cidade
```bash
curl "http://localhost:8080/pontos?cidadeId=1"
```

---

### 4️⃣ Conectar Duas Cidades
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

---

## 🏗️ Arquitetura em 30 Segundos

```
Cliente HTTP
    ↓
Controller
    ↓
Service (lógica de negócio)
    ↓
Repository (acesso a dados)
    ↓
Neo4j (banco de grafo)
```

**Exemplo**: `CityController` → `CityService` → `CityRepository` → `CityNode`

---

## 🔧 Arquivos Principais

### Controllers (Recebem requisições HTTP)
- `CityController.java` - `/cidades`
- `AttractionController.java` - `/pontos`
- `ConexaoController.java` - `/conexoes`

### Services (Lógica de negócio)
- `CityService.java`
- `AttractionService.java`
- `ConexaoService.java`

### Repositories (Acesso a dados)
- `CityRepository.java`
- `AttractionRepository.java`
- `ConexaoRepository.java`

### Entities (Modelos Neo4j)
- `CityNode.java`
- `AttractionNode.java`
- `CityConnection.java`

---

## 🔍 Estrutura de Dados

### CityNode
```
Propriedades:
- id: Long
- name: String
- latitude: Double
- longitude: Double

Relacionamentos:
- CONECTA → Outra Cidade (com distancia, tempo)
```

### AttractionNode
```
Propriedades:
- id: Long
- name: String
- description: String
- category: String
- latitude: Double
- longitude: Double

Relacionamentos:
- PERTENCE_A → Cidade
```

### CityConnection (Propriedades do Relacionamento)
```
- id: Long
- distancia: Double (em km)
- tempo: Double (em horas)
```

---

## 📋 Checklist para Começar

- [ ] Clonar/acessar o repositório
- [ ] Rodar `mvn clean install`
- [ ] Iniciar Neo4j (Docker ou local)
- [ ] Rodar `mvn spring-boot:run`
- [ ] Testar com `curl` ou Postman
- [ ] Ler `TESTING_GUIDE.md` para mais exemplos
- [ ] Explorar o código nos Controllers
- [ ] Modificar um endpoint para praticar

---

## 🆘 Problemas Comuns

### "Connection refused" (Neo4j)
**Solução**: 
```bash
# Iniciar Neo4j com Docker
docker run -d -p 7687:7687 -p 7474:7474 \
  -e NEO4J_AUTH=neo4j/Flamengo29. \
  neo4j:latest
```

### "Port 8080 already in use"
**Solução**:
```bash
# Mudar porta no application-dev.properties
server.port=8081
```

### "Compilation error"
**Solução**:
```bash
mvn clean compile
```

---

## 💻 IDE Recomendada

- **IntelliJ IDEA** (Community Edition gratuita)
- **VS Code** com Extension Pack for Java
- **Eclipse** (IDE clássica Java)

---

## 📖 Próxima Leitura

Após este Quick Start, leia nesta ordem:

1. **FINAL_SUMMARY.md** - Entender o escopo completo
2. **DIRECTORY_STRUCTURE.md** - Navegar o código
3. **TESTING_GUIDE.md** - Testar todos os endpoints
4. **RESTRUCTURED.md** - Documentação técnica profunda
5. **CHANGELOG_RESTRUCTURE.md** - Entender cada mudança

---

## 🎓 Aprenda por Tópico

### Quero entender Controllers
→ Abra `src/main/java/com/travelGraph/controller/CityController.java`

### Quero entender Services
→ Abra `src/main/java/com/travelGraph/services/CityService.java`

### Quero entender DTOs
→ Abra `src/main/java/com/travelGraph/dto/city/`

### Quero entender Neo4j
→ Abra `src/main/java/com/travelGraph/entities/CityNode.java`

### Quero adicionar um endpoint
→ Copie o padrão de `CityController`

---

## 🐛 Debug

### Ativar logs detalhados
Edite `application-dev.properties`:
```properties
logging.level.root=INFO
logging.level.com.travelGraph=DEBUG
logging.level.org.springframework.data.neo4j=DEBUG
```

### Verificar conexão Neo4j
```
http://localhost:7474
User: neo4j
Password: Flamengo29.
```

---

## ✅ Sucesso!

Você agora tem:
- ✅ Projeto compilado
- ✅ API rodando
- ✅ Documentação completa
- ✅ Exemplos de testes
- ✅ Estrutura clara do código

**Próximo passo**: Leia `TESTING_GUIDE.md` para testar todos os endpoints!

---

## 📞 Documentos de Referência Rápida

```
📖 FINAL_SUMMARY.md          ← Leia PRIMEIRO (visão geral)
📖 TESTING_GUIDE.md          ← Leia SEGUNDO (exemplos práticos)
📖 RESTRUCTURED.md           ← Leia TERCEIRO (detalhes técnicos)
📖 DIRECTORY_STRUCTURE.md    ← Use para navegação
📖 CHANGELOG_RESTRUCTURE.md  ← Use para referência de mudanças
```

---

**Travel Graph API v1.0**  
Reestruturada em 12 de Junho de 2026  
Status: ✅ PRONTA PARA DESENVOLVIMENTO

