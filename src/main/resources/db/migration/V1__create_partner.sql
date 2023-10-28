CREATE TABLE partner
(
    id         SERIAL PRIMARY KEY,
    name       VARCHAR(255) NOT NULL,
    domainName VARCHAR(255),
    created_at TIMESTAMP,
    updated_at TIMESTAMP
)
