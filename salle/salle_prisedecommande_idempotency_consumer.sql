CREATE EXTENSION IF NOT EXISTS pgcrypto WITH SCHEMA public;
CREATE SCHEMA IF NOT EXISTS salle_prisedecommande;

CREATE TABLE IF NOT EXISTS salle_prisedecommande.t_idempotency (
  target character varying(255) not null,
  from_application character varying(255) not null,
  aggregate_root_type character varying(255) not null,
  aggregate_root_id character varying(255) not null,
  last_consumed_version bigint not null,
  PRIMARY KEY (target, from_application, aggregate_root_type, aggregate_root_id)
)
