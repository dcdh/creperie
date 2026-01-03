CREATE EXTENSION IF NOT EXISTS pgcrypto WITH SCHEMA public;
CREATE SCHEMA IF NOT EXISTS salle_prisedecommande;

CREATE TABLE IF NOT EXISTS salle_prisedecommande.t_idempotency (
  purpose character varying(255) not null,
  from_application character varying(255) not null,
  topic character varying(255) not null,
  aggregate_root_type character varying(255) not null,
  aggregate_root_id character varying(255) not null,
  last_consumed_version bigint not null,
  CONSTRAINT idempotency_pkey PRIMARY KEY (purpose, from_application, topic, aggregate_root_type, aggregate_root_id),
  CONSTRAINT topic_format_chk CHECK (topic = 'EVENT' OR topic = 'AGGREGATE_ROOT')
)
