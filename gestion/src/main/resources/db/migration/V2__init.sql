CREATE TABLE audit_event
(
    functional_domain VARCHAR(255) NOT NULL,
    component_name    VARCHAR(255) NOT NULL,
    aggregate_type    VARCHAR(255) NOT NULL,
    aggregate_id      VARCHAR(255) NOT NULL,
    version           INTEGER      NOT NULL,
    creation_date     TIMESTAMPTZ  NOT NULL,
    event_type        VARCHAR(255) NOT NULL,
    encrypted_payload BYTEA        NOT NULL,
    owned_by          VARCHAR(255) NOT NULL,
    PRIMARY KEY (aggregate_id, creation_date)
);

CREATE TABLE statistic_commandes
(
    date_de_service DATE PRIMARY KEY,
    payload         JSONB NOT NULL
);

CREATE TABLE statistic_frequentation
(
    date_de_service DATE PRIMARY KEY,
    payload         JSONB NOT NULL
);
