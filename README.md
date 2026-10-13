# delivery-system — Projeto Diamante 02

Sistema de pedidos de delivery com microsserviços (Java Advanced, FIAP).

## Serviços

| Serviço | Porta | Função |
|---------|-------|--------|
| eureka-server | 8761 | Service discovery |
| order-service | 8080 | Cardápio, pedidos com lock, avaliações (producer) e assistente com Spring AI |
| payment-service | 8081 e 8082 | Pagamento instável (falha em ~50%), duas instâncias |
| review-service | 8083 | Consumer das avaliações, buffer com flush a cada 5 s e ranking |
| RabbitMQ (Docker) | 5672 e 15672 | Mensageria (`delivery.exchange` → `reviews.queue`) |

## Requisitos

- Java 25
- Docker
- (Opcional) chave da OpenAI ou modelo local, só para o `POST /assistant`

## Como subir

Na raiz do projeto, suba o RabbitMQ:

```bash
docker compose up -d
```

Depois, **nesta ordem**, cada serviço em um terminal, dentro da sua pasta:

```bash
cd eureka-server && ./gradlew bootRun
cd payment-service && ./gradlew bootRun
cd payment-service && ./gradlew bootRun --args='--server.port=8082'
cd order-service && ./gradlew bootRun
cd review-service && ./gradlew bootRun
```

Confira no painel do Eureka (http://localhost:8761) se `ORDER-SERVICE`, `REVIEW-SERVICE` e as duas instâncias de `PAYMENT-SERVICE` estão registrados. O RabbitMQ tem painel em http://localhost:15672 (guest/guest).

## Assistente (Spring AI)

A chave nunca fica no repositório: é lida de variáveis de ambiente, definidas antes de subir o `order-service`.

| Variável | Padrão | Uso |
|----------|--------|-----|
| `OPENAI_API_KEY` | `not-set` | Chave da OpenAI (qualquer texto para modelo local) |
| `AI_BASE_URL` | `https://api.openai.com/v1` | Para modelo local: `http://localhost:1234/v1` (LM Studio) ou `http://localhost:11434/v1` (Ollama) |
| `AI_MODEL` | `gpt-4o-mini` | Nome do modelo |

Sem a chave, todos os outros endpoints funcionam normalmente e apenas `POST /assistant` responde 502.

## Endpoints (order-service, porta 8080)

| Método | Rota | Respostas |
|--------|------|-----------|
| GET | `/dishes` | 200 |
| GET | `/dishes/{id}` | 200, 404 |
| POST | `/orders` | 201, 400, 404, 409, 502 |
| GET | `/orders/{id}` | 200, 404 |
| POST | `/reviews` | 202, 400, 404 |
| POST | `/assistant` | 200, 400, 502 |

Ranking (review-service, porta 8083): `GET /reviews/ranking`.

## Decisões de projeto

- **Race condition:** `POST /orders` reserva o estoque com `@Lock(PESSIMISTIC_WRITE)` dentro de `@Transactional`. Com 50 requisições simultâneas no prato da promoção, exatamente 10 pedidos são confirmados.
- **Load balance e retry:** o `PaymentClient` chama `http://PAYMENT-SERVICE/payments` por um `RestTemplate` com `@LoadBalanced`, com `@Retryable` e backoff exponencial com jitter. Se todas as tentativas falham, a transação é desfeita (estoque intacto) e a resposta é 502.
- **Backpressure:** o review-service acumula as avaliações em um `ConcurrentHashMap` e grava no H2 a cada 5 s com `@Scheduled`. O ranking é lido do banco.