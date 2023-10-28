CREATE TABLE contact
(
    id         SERIAL PRIMARY KEY,
    name       VARCHAR(255),
    email      VARCHAR(255) NOT NULL,
    telephone  VARCHAR(255),
    partner_id BIGINT,
    created_at TIMESTAMP,
    updated_at TIMESTAMP
);


ALTER TABLE contact
    ADD CONSTRAINT contact_partner
        FOREIGN KEY (partner_id) REFERENCES partner (id);