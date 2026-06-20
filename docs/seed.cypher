// =============================================================================
// TravelGraph - Seed de dados para Neo4j
// =============================================================================
// Schema (extraído das entities):
//   (:City   {id, name, latitude, longitude, createdAt, updatedAt})
//   (:Attraction {id, name, description, category, latitude, longitude, createdAt, updatedAt})
//   (:User   {id, name, email, password, createdAt})
//   (:City)-[:CONECTA {distancia, tempo, createdAt}]->(:City)
//   (:Attraction)-[:PERTENCE_A]->(:City)
//
// Como rodar:
//   - Pelo Neo4j Browser: cole o script inteiro e execute (:auto não é necessário).
//   - Pelo cypher-shell:
//       cypher-shell -u neo4j -p <senha> -f docs/seed.cypher
//
// Coordenadas (latitude/longitude) são reais (WGS-84) para que apareçam
// corretamente em qualquer mapa (Leaflet, Google Maps, Mapbox, etc).
//
// As conexões CONECTA são geradas automaticamente entre cidades a até
// 900 km de distância, com `distancia` em km (calculada via point.distance)
// e `tempo` em horas (assumindo velocidade média de 80 km/h).
// =============================================================================


// -----------------------------------------------------------------------------
// 0. Limpa dados existentes (CUIDADO: apaga TUDO do banco)
//    Comente esta linha se quiser apenas adicionar dados.
// -----------------------------------------------------------------------------
MATCH (n) DETACH DELETE n;


// -----------------------------------------------------------------------------
// 1. Cidades (40 cidades brasileiras com coordenadas reais)
// -----------------------------------------------------------------------------
UNWIND [
  {name: 'São Paulo',        latitude: -23.5505, longitude: -46.6333},
  {name: 'Rio de Janeiro',   latitude: -22.9068, longitude: -43.1729},
  {name: 'Brasília',         latitude: -15.7975, longitude: -47.8919},
  {name: 'Salvador',         latitude: -12.9714, longitude: -38.5014},
  {name: 'Fortaleza',        latitude:  -3.7172, longitude: -38.5433},
  {name: 'Belo Horizonte',   latitude: -19.9167, longitude: -43.9345},
  {name: 'Manaus',           latitude:  -3.1190, longitude: -60.0217},
  {name: 'Curitiba',         latitude: -25.4284, longitude: -49.2733},
  {name: 'Recife',           latitude:  -8.0476, longitude: -34.8770},
  {name: 'Porto Alegre',     latitude: -30.0346, longitude: -51.2177},
  {name: 'Belém',            latitude:  -1.4554, longitude: -48.5044},
  {name: 'Goiânia',          latitude: -16.6869, longitude: -49.2648},
  {name: 'Guarulhos',        latitude: -23.4538, longitude: -46.5333},
  {name: 'Campinas',         latitude: -22.9099, longitude: -47.0626},
  {name: 'São Luís',         latitude:  -2.5297, longitude: -44.3028},
  {name: 'Maceió',           latitude:  -9.6498, longitude: -35.7089},
  {name: 'Natal',            latitude:  -5.7945, longitude: -35.2110},
  {name: 'Teresina',         latitude:  -5.0892, longitude: -42.8019},
  {name: 'João Pessoa',      latitude:  -7.1195, longitude: -34.8450},
  {name: 'Florianópolis',    latitude: -27.5949, longitude: -48.5482},
  {name: 'Aracaju',          latitude: -10.9472, longitude: -37.0731},
  {name: 'Cuiabá',           latitude: -15.6014, longitude: -56.0979},
  {name: 'Porto Velho',      latitude:  -8.7619, longitude: -63.9039},
  {name: 'Vitória',          latitude: -20.3155, longitude: -40.3128},
  {name: 'Macapá',           latitude:  -0.0349, longitude: -51.0694},
  {name: 'Campo Grande',     latitude: -20.4697, longitude: -54.6201},
  {name: 'Rio Branco',       latitude:  -9.9747, longitude: -67.8243},
  {name: 'Palmas',           latitude: -10.1689, longitude: -48.3317},
  {name: 'Boa Vista',        latitude:   2.8235, longitude: -60.6758},
  {name: 'Blumenau',         latitude: -26.9194, longitude: -49.0661},
  {name: 'Joinville',        latitude: -26.3045, longitude: -48.8487},
  {name: 'Foz do Iguaçu',    latitude: -25.5478, longitude: -54.5882},
  {name: 'Santos',           latitude: -23.9608, longitude: -46.3331},
  {name: 'Búzios',           latitude: -22.7469, longitude: -41.8819},
  {name: 'Paraty',           latitude: -23.2206, longitude: -44.7203},
  {name: 'Ouro Preto',       latitude: -20.3853, longitude: -43.5039},
  {name: 'Petrópolis',       latitude: -22.5050, longitude: -43.1786},
  {name: 'Gramado',          latitude: -29.3788, longitude: -50.8769},
  {name: 'Bonito',           latitude: -21.1248, longitude: -56.4818},
  {name: 'Olinda',           latitude:  -8.0089, longitude: -34.8553}
] AS row
CREATE (c:City {
  name:      row.name,
  latitude:  row.latitude,
  longitude: row.longitude,
  createdAt: localdatetime(),
  updatedAt: localdatetime()
});


// -----------------------------------------------------------------------------
// 2. Atrações (associadas a cidades pela chave `cityName`)
// -----------------------------------------------------------------------------
UNWIND [
  // --- Rio de Janeiro ---
  {cityName: 'Rio de Janeiro', name: 'Cristo Redentor',         category: 'Monumento',    latitude: -22.9519, longitude: -43.2106, description: 'Estátua símbolo do Brasil no morro do Corcovado.'},
  {cityName: 'Rio de Janeiro', name: 'Pão de Açúcar',           category: 'Natureza',     latitude: -22.9492, longitude: -43.1565, description: 'Morro icônico com bondinho panorâmico.'},
  {cityName: 'Rio de Janeiro', name: 'Praia de Copacabana',     category: 'Praia',        latitude: -22.9714, longitude: -43.1828, description: 'A praia mais famosa do Brasil.'},
  {cityName: 'Rio de Janeiro', name: 'Maracanã',                category: 'Estádio',      latitude: -22.9121, longitude: -43.2302, description: 'Templo do futebol brasileiro.'},
  {cityName: 'Rio de Janeiro', name: 'Jardim Botânico',         category: 'Parque',       latitude: -22.9676, longitude: -43.2238, description: 'Parque histórico com flora tropical.'},

  // --- São Paulo ---
  {cityName: 'São Paulo', name: 'Avenida Paulista',             category: 'Urbano',       latitude: -23.5614, longitude: -46.6562, description: 'Principal avenida cultural e financeira.'},
  {cityName: 'São Paulo', name: 'MASP',                         category: 'Museu',        latitude: -23.5614, longitude: -46.6559, description: 'Museu de Arte de São Paulo.'},
  {cityName: 'São Paulo', name: 'Parque Ibirapuera',            category: 'Parque',       latitude: -23.5874, longitude: -46.6576, description: 'Maior parque urbano da cidade.'},
  {cityName: 'São Paulo', name: 'Mercado Municipal',            category: 'Gastronomia',  latitude: -23.5419, longitude: -46.6294, description: 'Mercadão com sanduíche de mortadela.'},
  {cityName: 'São Paulo', name: 'Theatro Municipal',            category: 'Cultura',      latitude: -23.5454, longitude: -46.6388, description: 'Teatro histórico em estilo eclético.'},

  // --- Brasília ---
  {cityName: 'Brasília', name: 'Catedral de Brasília',          category: 'Arquitetura',  latitude: -15.7986, longitude: -47.8754, description: 'Obra-prima de Oscar Niemeyer.'},
  {cityName: 'Brasília', name: 'Congresso Nacional',            category: 'Arquitetura',  latitude: -15.7997, longitude: -47.8639, description: 'Sede do Poder Legislativo.'},
  {cityName: 'Brasília', name: 'Praça dos Três Poderes',        category: 'Histórico',    latitude: -15.8025, longitude: -47.8616, description: 'Centro político do país.'},

  // --- Salvador ---
  {cityName: 'Salvador', name: 'Pelourinho',                    category: 'Histórico',    latitude: -12.9716, longitude: -38.5076, description: 'Centro histórico colonial.'},
  {cityName: 'Salvador', name: 'Elevador Lacerda',              category: 'Histórico',    latitude: -12.9745, longitude: -38.5111, description: 'Conecta Cidade Alta e Baixa.'},
  {cityName: 'Salvador', name: 'Farol da Barra',                category: 'Histórico',    latitude: -13.0107, longitude: -38.5320, description: 'Primeiro farol das Américas.'},
  {cityName: 'Salvador', name: 'Mercado Modelo',                category: 'Cultura',      latitude: -12.9747, longitude: -38.5117, description: 'Artesanato e cultura baiana.'},

  // --- Fortaleza ---
  {cityName: 'Fortaleza', name: 'Praia de Iracema',             category: 'Praia',        latitude:  -3.7185, longitude: -38.5089, description: 'Pôr do sol e vida noturna.'},
  {cityName: 'Fortaleza', name: 'Beach Park',                   category: 'Parque',       latitude:  -3.8351, longitude: -38.4117, description: 'Maior parque aquático do Brasil.'},
  {cityName: 'Fortaleza', name: 'Mercado Central',              category: 'Gastronomia',  latitude:  -3.7297, longitude: -38.5341, description: 'Artesanato e renda cearense.'},

  // --- Belo Horizonte ---
  {cityName: 'Belo Horizonte', name: 'Lagoa da Pampulha',       category: 'Natureza',     latitude: -19.8635, longitude: -43.9785, description: 'Conjunto arquitetônico de Niemeyer.'},
  {cityName: 'Belo Horizonte', name: 'Mercado Central',         category: 'Gastronomia',  latitude: -19.9211, longitude: -43.9381, description: 'Cachaça, queijos e doces mineiros.'},
  {cityName: 'Belo Horizonte', name: 'Mineirão',                category: 'Estádio',      latitude: -19.8657, longitude: -43.9707, description: 'Estádio do Cruzeiro e seleção.'},

  // --- Manaus ---
  {cityName: 'Manaus', name: 'Teatro Amazonas',                 category: 'Cultura',      latitude:  -3.1300, longitude: -60.0233, description: 'Joia da Belle Époque na Amazônia.'},
  {cityName: 'Manaus', name: 'Encontro das Águas',              category: 'Natureza',     latitude:  -3.1372, longitude: -59.9114, description: 'Rios Negro e Solimões.'},

  // --- Curitiba ---
  {cityName: 'Curitiba', name: 'Jardim Botânico',               category: 'Parque',       latitude: -25.4419, longitude: -49.2399, description: 'Cartão postal da cidade.'},
  {cityName: 'Curitiba', name: 'Ópera de Arame',                category: 'Cultura',      latitude: -25.3826, longitude: -49.2811, description: 'Teatro de estrutura tubular.'},
  {cityName: 'Curitiba', name: 'Museu Oscar Niemeyer',          category: 'Museu',        latitude: -25.4097, longitude: -49.2685, description: 'Museu do Olho.'},

  // --- Recife ---
  {cityName: 'Recife', name: 'Marco Zero',                      category: 'Histórico',    latitude:  -8.0633, longitude: -34.8711, description: 'Ponto de partida da cidade.'},
  {cityName: 'Recife', name: 'Praia de Boa Viagem',             category: 'Praia',        latitude:  -8.1192, longitude: -34.8956, description: 'Praia urbana de Recife.'},
  {cityName: 'Recife', name: 'Instituto Ricardo Brennand',      category: 'Museu',        latitude:  -8.0301, longitude: -34.9408, description: 'Castelo com acervo de arte.'},

  // --- Porto Alegre ---
  {cityName: 'Porto Alegre', name: 'Usina do Gasômetro',        category: 'Cultura',      latitude: -30.0354, longitude: -51.2424, description: 'Centro cultural à beira do Guaíba.'},
  {cityName: 'Porto Alegre', name: 'Mercado Público',           category: 'Gastronomia',  latitude: -30.0277, longitude: -51.2287, description: 'Mercado histórico do centro.'},

  // --- Belém ---
  {cityName: 'Belém', name: 'Mercado Ver-o-Peso',               category: 'Gastronomia',  latitude:  -1.4500, longitude: -48.5036, description: 'Maior feira ao ar livre da AL.'},
  {cityName: 'Belém', name: 'Estação das Docas',                category: 'Gastronomia',  latitude:  -1.4470, longitude: -48.5039, description: 'Complexo gastronômico portuário.'},

  // --- Goiânia ---
  {cityName: 'Goiânia', name: 'Bosque dos Buritis',             category: 'Parque',       latitude: -16.6798, longitude: -49.2667, description: 'Parque urbano com lago.'},
  {cityName: 'Goiânia', name: 'Mercado Central',                category: 'Gastronomia',  latitude: -16.6720, longitude: -49.2625, description: 'Sabores do interior goiano.'},

  // --- São Luís ---
  {cityName: 'São Luís', name: 'Centro Histórico',              category: 'Histórico',    latitude:  -2.5310, longitude: -44.3068, description: 'Patrimônio Mundial da UNESCO.'},

  // --- Maceió ---
  {cityName: 'Maceió', name: 'Praia de Pajuçara',               category: 'Praia',        latitude:  -9.6679, longitude: -35.7027, description: 'Piscinas naturais de jangada.'},
  {cityName: 'Maceió', name: 'Praia do Francês',                category: 'Praia',        latitude:  -9.7531, longitude: -35.8378, description: 'Praia próxima de Marechal Deodoro.'},

  // --- Natal ---
  {cityName: 'Natal', name: 'Ponta Negra',                      category: 'Praia',        latitude:  -5.8826, longitude: -35.1764, description: 'Morro do Careca.'},
  {cityName: 'Natal', name: 'Forte dos Reis Magos',             category: 'Histórico',    latitude:  -5.7506, longitude: -35.1925, description: 'Fortaleza colonial em forma de estrela.'},
  {cityName: 'Natal', name: 'Dunas de Genipabu',                category: 'Natureza',     latitude:  -5.7019, longitude: -35.2025, description: 'Passeio de buggy nas dunas.'},

  // --- João Pessoa ---
  {cityName: 'João Pessoa', name: 'Praia do Jacaré',            category: 'Praia',        latitude:  -7.0606, longitude: -34.8525, description: 'Pôr do sol ao som do Bolero de Ravel.'},
  {cityName: 'João Pessoa', name: 'Ponta do Seixas',            category: 'Natureza',     latitude:  -7.1465, longitude: -34.7906, description: 'Ponto mais oriental das Américas.'},

  // --- Florianópolis ---
  {cityName: 'Florianópolis', name: 'Praia da Joaquina',        category: 'Praia',        latitude: -27.6275, longitude: -48.4525, description: 'Surfe e dunas.'},
  {cityName: 'Florianópolis', name: 'Lagoa da Conceição',       category: 'Natureza',     latitude: -27.6022, longitude: -48.4664, description: 'Cenário natural e gastronômico.'},
  {cityName: 'Florianópolis', name: 'Praça XV de Novembro',     category: 'Histórico',    latitude: -27.5961, longitude: -48.5495, description: 'Centro histórico de Floripa.'},

  // --- Aracaju ---
  {cityName: 'Aracaju', name: 'Orla de Atalaia',                category: 'Praia',        latitude: -10.9881, longitude: -37.0411, description: 'Praia urbana com Oceanário.'},

  // --- Cuiabá ---
  {cityName: 'Cuiabá', name: 'Chapada dos Guimarães',           category: 'Natureza',     latitude: -15.4609, longitude: -55.7497, description: 'Cânions e cachoeiras.'},

  // --- Vitória ---
  {cityName: 'Vitória', name: 'Convento da Penha',              category: 'Histórico',    latitude: -20.3243, longitude: -40.2876, description: 'Santuário do século XVI.'},
  {cityName: 'Vitória', name: 'Praia de Camburi',               category: 'Praia',        latitude: -20.2762, longitude: -40.2890, description: 'Praia urbana da capital capixaba.'},

  // --- Campo Grande ---
  {cityName: 'Campo Grande', name: 'Parque das Nações Indígenas', category: 'Parque',     latitude: -20.4434, longitude: -54.5797, description: 'Maior parque urbano do mundo.'},

  // --- Blumenau ---
  {cityName: 'Blumenau', name: 'Vila Germânica',                category: 'Cultura',      latitude: -26.9165, longitude: -49.0738, description: 'Sede da Oktoberfest brasileira.'},

  // --- Joinville ---
  {cityName: 'Joinville', name: 'Mirante da Boa Vista',         category: 'Natureza',     latitude: -26.3072, longitude: -48.8233, description: 'Vista panorâmica da cidade.'},

  // --- Foz do Iguaçu ---
  {cityName: 'Foz do Iguaçu', name: 'Cataratas do Iguaçu',      category: 'Natureza',     latitude: -25.6953, longitude: -54.4367, description: 'Uma das 7 maravilhas naturais.'},
  {cityName: 'Foz do Iguaçu', name: 'Usina de Itaipu',          category: 'Histórico',    latitude: -25.4081, longitude: -54.5912, description: 'Maior geradora de energia limpa do mundo.'},
  {cityName: 'Foz do Iguaçu', name: 'Marco das Três Fronteiras', category: 'Histórico',   latitude: -25.5945, longitude: -54.5868, description: 'BR, ARG e PAR.'},

  // --- Santos ---
  {cityName: 'Santos', name: 'Jardins da Orla',                 category: 'Urbano',       latitude: -23.9810, longitude: -46.3211, description: 'Maior jardim de orla do mundo.'},
  {cityName: 'Santos', name: 'Museu do Pelé',                   category: 'Museu',        latitude: -23.9322, longitude: -46.3262, description: 'Homenagem ao Rei do Futebol.'},

  // --- Búzios ---
  {cityName: 'Búzios', name: 'Praia de Geribá',                 category: 'Praia',        latitude: -22.7741, longitude: -41.9061, description: 'Praia agitada com ondas.'},
  {cityName: 'Búzios', name: 'Rua das Pedras',                  category: 'Urbano',       latitude: -22.7475, longitude: -41.8814, description: 'Coração turístico de Búzios.'},

  // --- Paraty ---
  {cityName: 'Paraty', name: 'Centro Histórico',                category: 'Histórico',    latitude: -23.2207, longitude: -44.7186, description: 'Vilarejo colonial de pedra.'},
  {cityName: 'Paraty', name: 'Praia do Sono',                   category: 'Praia',        latitude: -23.3239, longitude: -44.6314, description: 'Praia paradisíaca de difícil acesso.'},

  // --- Ouro Preto ---
  {cityName: 'Ouro Preto', name: 'Praça Tiradentes',            category: 'Histórico',    latitude: -20.3858, longitude: -43.5026, description: 'Centro da cidade colonial.'},
  {cityName: 'Ouro Preto', name: 'Igreja São Francisco de Assis', category: 'Histórico',  latitude: -20.3870, longitude: -43.5036, description: 'Obra-prima do Aleijadinho.'},

  // --- Petrópolis ---
  {cityName: 'Petrópolis', name: 'Museu Imperial',              category: 'Museu',        latitude: -22.5093, longitude: -43.1789, description: 'Antigo palácio de D. Pedro II.'},

  // --- Gramado ---
  {cityName: 'Gramado', name: 'Lago Negro',                     category: 'Natureza',     latitude: -29.3886, longitude: -50.8763, description: 'Lago artificial com araucárias.'},
  {cityName: 'Gramado', name: 'Rua Coberta',                    category: 'Urbano',       latitude: -29.3771, longitude: -50.8761, description: 'Centro gastronômico da cidade.'},
  {cityName: 'Gramado', name: 'Mini Mundo',                     category: 'Parque',       latitude: -29.3776, longitude: -50.8745, description: 'Réplicas em miniatura.'},

  // --- Bonito ---
  {cityName: 'Bonito', name: 'Gruta do Lago Azul',              category: 'Natureza',     latitude: -21.1186, longitude: -56.5891, description: 'Caverna com lago de água azul.'},
  {cityName: 'Bonito', name: 'Rio Sucuri',                      category: 'Natureza',     latitude: -21.2122, longitude: -56.5419, description: 'Flutuação em águas cristalinas.'},

  // --- Olinda ---
  {cityName: 'Olinda', name: 'Alto da Sé',                      category: 'Histórico',    latitude:  -8.0095, longitude: -34.8497, description: 'Vista panorâmica e artesanato.'},
  {cityName: 'Olinda', name: 'Mosteiro de São Bento',           category: 'Histórico',    latitude:  -8.0140, longitude: -34.8421, description: 'Mosteiro beneditino do séc. XVI.'}
] AS row
MATCH (c:City {name: row.cityName})
CREATE (a:Attraction {
  name:        row.name,
  description: row.description,
  category:    row.category,
  latitude:    row.latitude,
  longitude:   row.longitude,
  createdAt:   localdatetime(),
  updatedAt:   localdatetime()
})
CREATE (a)-[:PERTENCE_A]->(c);


// -----------------------------------------------------------------------------
// 3. Conexões entre cidades (CONECTA)
//    Cria relação bidirecional entre todo par de cidades cuja distância
//    em linha reta seja menor que 900 km. Distância em km, tempo em horas.
// -----------------------------------------------------------------------------
MATCH (a:City), (b:City)
WHERE id(a) < id(b)
WITH a, b,
     point({latitude: a.latitude, longitude: a.longitude}) AS pa,
     point({latitude: b.latitude, longitude: b.longitude}) AS pb
WITH a, b, point.distance(pa, pb) / 1000.0 AS distKm
WHERE distKm < 900.0
WITH a, b, round(distKm * 100) / 100.0 AS dist
CREATE (a)-[:CONECTA {distancia: dist, tempo: round((dist / 80.0) * 100) / 100.0, createdAt: localdatetime()}]->(b)
CREATE (b)-[:CONECTA {distancia: dist, tempo: round((dist / 80.0) * 100) / 100.0, createdAt: localdatetime()}]->(a);


// -----------------------------------------------------------------------------
// 4. Usuários de teste
//    Senha em texto plano para TODOS os usuários abaixo: "senha1234"
//    (hash BCrypt cost 10 já calculado)
// -----------------------------------------------------------------------------
UNWIND [
  {name: 'Lucas Henschel',  email: 'lucas@travelgraph.com'},
  {name: 'Maria Silva',     email: 'maria@travelgraph.com'},
  {name: 'João Souza',      email: 'joao@travelgraph.com'},
  {name: 'Ana Oliveira',    email: 'ana@travelgraph.com'},
  {name: 'Pedro Almeida',   email: 'pedro@travelgraph.com'}
] AS row
CREATE (u:User {
  id:        randomUUID(),
  name:      row.name,
  email:     row.email,
  password:  '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy',
  createdAt: localdatetime()
});


// -----------------------------------------------------------------------------
// 5. Resumo final
// -----------------------------------------------------------------------------
MATCH (c:City) WITH count(c) AS cidades
MATCH (a:Attraction) WITH cidades, count(a) AS atracoes
MATCH ()-[r:CONECTA]->() WITH cidades, atracoes, count(r) AS conexoes
MATCH (u:User) WITH cidades, atracoes, conexoes, count(u) AS usuarios
RETURN cidades, atracoes, conexoes, usuarios;
