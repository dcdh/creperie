CREATE EXTENSION IF NOT EXISTS pgcrypto WITH SCHEMA public;
CREATE SCHEMA IF NOT EXISTS gestion_statistics;

CREATE TABLE IF NOT EXISTS gestion_statistics.idempotency (
  purpose character varying(255) not null,
  from_application character varying(255) not null,
  table_name character varying(255) not null,
  aggregate_root_type character varying(255) not null,
  aggregate_root_id character varying(255) not null,
  last_consumed_version bigint not null,
  CONSTRAINT idempotency_pkey PRIMARY KEY (purpose, from_application, table_name, aggregate_root_type, aggregate_root_id),
  CONSTRAINT table_name_format_chk CHECK (table_name = 'EVENT' OR table_name = 'AGGREGATE_ROOT')
)
