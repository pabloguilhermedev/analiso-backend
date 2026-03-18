# CONTEXT.md

## Objetivo
Este projeto deve seguir boas práticas de engenharia de software em Java, com foco em código de produção, arquitetura clara, baixo acoplamento, alta coesão e facilidade de manutenção.

A prioridade não é apenas "funcionar".
A prioridade é entregar uma solução:
- legível
- testável
- extensível
- segura para evolução
- simples de operar em produção

---

## Princípios de engenharia
- Clareza é mais importante que esperteza.
- Simplicidade é preferível a abstrações prematuras.
- Código deve ser fácil de entender por outro desenvolvedor experiente.
- Toda decisão de arquitetura deve ser justificada por necessidade real.
- Evitar complexidade acidental.
- Favorecer composição em vez de herança.
- Favorecer coesão alta e acoplamento baixo.
- Fazer o simples bem feito antes de generalizar.

---

## Regras gerais de arquitetura
- Separar claramente responsabilidades.
- Controllers apenas recebem requisições, validam entrada básica e delegam.
- Regras de negócio não devem ficar em controller.
- Regras de negócio devem ficar em casos de uso, services de domínio ou componentes equivalentes.
- Repositórios devem cuidar apenas de persistência.
- Integrações externas devem ser isoladas atrás de interfaces ou clients bem definidos.
- DTOs de entrada e saída não devem ser usados como entidades de domínio.
- Entidades de persistência não devem vazar para a API.
- Infraestrutura não deve contaminar o domínio com detalhes técnicos desnecessários.

---

## Estrutura esperada
A solução deve seguir uma organização lógica e previsível, por exemplo:

- controller: entrada HTTP
- dto: contratos de entrada e saída
- service ou usecase: orquestração e regra de negócio
- domain: entidades, value objects e regras centrais
- repository: acesso a dados
- client ou gateway: integração com sistemas externos
- config: configuração técnica
- exception: exceções customizadas e tratamento global
- mapper: conversões explícitas entre camadas, quando necessário

A estrutura pode variar, mas deve preservar separação clara de responsabilidades.

---

## Convenções de código Java
- Usar nomes explícitos e sem abreviações desnecessárias.
- Métodos devem ter responsabilidade única.
- Classes não devem acumular múltiplos papéis.
- Evitar classes utilitárias genéricas sem contexto claro.
- Evitar métodos longos demais.
- Evitar encadeamentos confusos.
- Preferir retorno claro a lógica excessivamente compactada.
- Comentários devem existir apenas quando agregarem contexto que o código sozinho não expressa.
- Não escrever comentários óbvios.
- Evitar "magic numbers" e strings soltas.
- Extrair constantes apenas quando isso realmente melhora entendimento.

---

## Design de APIs e contratos
- Os contratos devem ser claros e estáveis.
- Requests e responses devem ser específicos ao caso de uso.
- Validar entrada cedo.
- Mensagens de erro devem ser claras e previsíveis.
- Não expor detalhes internos desnecessários na API.
- Modelos externos não devem refletir diretamente o modelo do banco.

---

## Tratamento de erros
- Não engolir exceções.
- Não usar try/catch sem propósito claro.
- Exceções de negócio devem ser diferenciadas de falhas técnicas.
- Mapear erros para respostas apropriadas.
- Toda falha relevante deve ser observável.
- Não retornar null como estratégia silenciosa de erro quando Optional, exceção ou resposta explícita forem melhores.

---

## Persistência
- Repositórios devem ser simples e objetivos.
- Evitar regra de negócio em queries ou adapters de persistência.
- Pensar em consistência transacional com pragmatismo.
- Usar transação apenas onde necessário.
- Evitar carregar mais dados do que o necessário.
- Entidades JPA não devem concentrar lógica indevida de aplicação.
- Modelagem deve priorizar clareza e consistência.

---

## Integrações externas
- Toda integração externa deve ser tratada como falha potencial.
- Isolar chamadas externas em componentes específicos.
- Tratar timeout, erro de contrato e indisponibilidade.
- Evitar espalhar lógica de integração pelo código.
- Contratos externos devem ser traduzidos para modelos internos adequados.

---

## Testes
- Toda regra de negócio relevante deve ter teste.
- Priorizar testes unitários para regras centrais.
- Usar testes de integração para fluxos importantes entre camadas.
- Evitar testes frágeis e excessivamente acoplados à implementação.
- Testes devem validar comportamento, não detalhes irrelevantes.
- Não gerar testes superficiais apenas para aumentar cobertura.

---

## Legibilidade e manutenção
- O código deve ser escrito para ser mantido por outra pessoa.
- Quem ler a classe deve entender rapidamente:
  - o que ela faz
  - o que ela não faz
  - de quem ela depende
  - por que ela existe

Sempre preferir um design previsível e compreensível.

---

## O que evitar
- Overengineering
- Interfaces sem necessidade real
- Generalização precoce
- Padrões aplicados só por estética
- Services que apenas repassam chamada sem agregar valor
- Controllers gordos
- Classes gigantes
- Métodos com múltiplas responsabilidades
- Regras de negócio espalhadas
- Acoplamento direto entre API, domínio e infraestrutura
- Uso excessivo de annotations sem necessidade
- Complexidade para parecer sofisticado

---

## Como responder às tasks
Ao implementar qualquer solução neste projeto, siga este formato:

1. Resuma o problema em termos técnicos.
2. Explique rapidamente a abordagem escolhida.
3. Aponte trade-offs de arquitetura.
4. Gere código pronto para produção.
5. Inclua testes relevantes.
6. Faça uma revisão crítica do resultado.
7. Diga o que melhoraria em uma v2, sem reinventar tudo.

---

## Postura esperada da IA
A IA deve agir como um engenheiro sênior pragmático.
Não deve gerar código apenas "bonito".
Deve gerar código:
- claro
- robusto
- coeso
- fácil de manter
- adequado para produção

Quando houver dúvida entre uma solução sofisticada e uma solução simples bem estruturada, preferir a solução simples bem estruturada.