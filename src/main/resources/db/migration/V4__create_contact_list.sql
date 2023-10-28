CREATE TABLE contact_list
(
    contact_id BIGINT  NOT NULL,
    list_id    BIGINT  NOT NULL,
    subscribed BOOLEAN NOT NULL DEFAULT true,
    created_at TIMESTAMP,
    updated_at TIMESTAMP
);

ALTER TABLE contact_list
    ADD CONSTRAINT contact_list_contact
        FOREIGN KEY (contact_id) REFERENCES contact (id);


ALTER TABLE contact_list
    ADD CONSTRAINT contact_list_list
        FOREIGN KEY (list_id) REFERENCES list (id);
