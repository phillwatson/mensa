CREATE SCHEMA IF NOT EXISTS ${flyway:defaultSchema};

CREATE TABLE IF NOT EXISTS ${flyway:defaultSchema}.events (
  id uuid PRIMARY KEY,
  correlation_id varchar(256) NOT NULL,
  "timestamp" timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  delivered_at timestamp NULL,
  topic text NOT NULL,
  payload_class text NOT NULL,
  payload text NOT NULL
)
