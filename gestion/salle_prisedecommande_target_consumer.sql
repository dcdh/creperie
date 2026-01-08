CREATE EXTENSION IF NOT EXISTS pgcrypto WITH SCHEMA public;
CREATE SCHEMA IF NOT EXISTS salle_prisedecommande;

CREATE TABLE IF NOT EXISTS salle_prisedecommande.aggregate_root (
  aggregate_root_type character varying(255) not null,
  aggregate_root_id character varying(255) not null,
  last_version bigint not null,
  aggregate_root_payload bytea NOT NULL CHECK (octet_length(aggregate_root_payload) <= 1000 * 1024),
  owned_by character varying(255) not null,
  belongs_to character varying(255) not null,
  CONSTRAINT aggregate_root_pkey PRIMARY KEY (aggregate_root_id, aggregate_root_type)
);
