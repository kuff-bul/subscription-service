# Subscription Service

Stores a user and the user's subscription by Keycloak login.

## Data Model

The `users` table stores the application user:

```text
login         varchar(255) primary key
email         varchar(320), nullable
display_name  varchar(255), nullable
created_at    timestamp with time zone
```

The `subscriptions` table uses the same login as the primary key and foreign key:

```text
login              varchar(255) primary key references users(login)
subscription_type  FREE | PAID
expires_at         timestamp with time zone, nullable
```

Users are not created automatically from Keycloak registration. Insert the user
and subscription rows manually and make sure `login` matches the Keycloak
`preferred_username` value.

Paid subscription:

```sql
insert into users (login, email, display_name)
values ('user1', 'user1@example.com', 'User One')
on conflict (login) do nothing;

insert into subscriptions (login, subscription_type, expires_at)
values ('user1', 'PAID', '2026-12-31 23:59:59+00')
on conflict (login) do update
set subscription_type = excluded.subscription_type,
    expires_at = excluded.expires_at;
```

Free subscription:

```sql
insert into users (login, email, display_name)
values ('user1', 'user1@example.com', 'User One')
on conflict (login) do nothing;

insert into subscriptions (login, subscription_type, expires_at)
values ('user1', 'FREE', null)
on conflict (login) do update
set subscription_type = excluded.subscription_type,
    expires_at = excluded.expires_at;
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
