# Subscription Service

Stores a user's subscription by Keycloak login.

## Data Model

The `subscriptions` table uses the Keycloak login as the primary key:

```text
login              varchar(255) primary key
subscription_type  FREE | PAID
expires_at         timestamp with time zone, nullable
```

Users are not created automatically from Keycloak registration. Add the row
manually and make sure `login` matches the Keycloak `preferred_username` value.

Example:

```sql
insert into subscriptions (login, subscription_type, expires_at)
values ('user1', 'PAID', '2026-12-31 23:59:59+00');
```

For a free subscription:

```sql
insert into subscriptions (login, subscription_type, expires_at)
values ('user1', 'FREE', null);
```

## Runtime Behavior

- `GET /api/subscriptions/{login}` returns subscription state for FlowManager.
- The scheduler periodically finds expired `PAID` subscriptions and changes them to `FREE`.
- ShedLock uses the `shedlock` table so only one service instance runs the expiration job.
- When a paid subscription expires, the service publishes `SUBSCRIPTION_EXPIRED` to the configured Kafka topic.

Default Kafka topic:

```text
subscription-events
```

## Local Infrastructure

Start the PostgreSQL database:

```powershell
docker compose up -d
```

The service expects Kafka at `localhost:9094` by default. In the current project,
Kafka is provided by the FlowManager docker compose file.
