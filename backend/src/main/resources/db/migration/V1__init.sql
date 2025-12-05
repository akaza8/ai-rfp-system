-- V1__init.sql
-- Initial schema for AI-powered RFP system

-- 1) VENDOR
CREATE TABLE vendor (
    id          BIGSERIAL PRIMARY KEY,
    name        VARCHAR(255) NOT NULL,
    email       VARCHAR(255) NOT NULL UNIQUE
);

-- 2) RFP
CREATE TABLE rfp (
    id                      BIGSERIAL PRIMARY KEY,
    title                   VARCHAR(255) NOT NULL,
    budget                  INTEGER,
    delivery_timeline_days  INTEGER,
    payment_terms           TEXT,
    warranty_terms          TEXT
);

-- 3) RFP_ITEM
CREATE TABLE rfp_item (
    id              BIGSERIAL PRIMARY KEY,
    item_type       VARCHAR(255) NOT NULL,
    quantity        INTEGER,
    required_specs  TEXT,
    rfp_id          BIGINT NOT NULL,
    CONSTRAINT fk_rfp_item_rfp
        FOREIGN KEY (rfp_id)
        REFERENCES rfp (id)
        ON DELETE CASCADE
);

CREATE INDEX idx_rfp_item_rfp_id ON rfp_item (rfp_id);

-- 4) RFP_VENDOR_INVITATION
CREATE TABLE rfp_vendor_invitation (
    id          BIGSERIAL PRIMARY KEY,
    rfp_id      BIGINT NOT NULL,
    vendor_id   BIGINT NOT NULL,
    status      VARCHAR(50),        -- INVITED, SENT, RESPONDED, NO_RESPONSE, etc.
    sent_at     TIMESTAMP,

    CONSTRAINT fk_rvi_rfp
        FOREIGN KEY (rfp_id)
        REFERENCES rfp (id)
        ON DELETE CASCADE,

    CONSTRAINT fk_rvi_vendor
        FOREIGN KEY (vendor_id)
        REFERENCES vendor (id)
        ON DELETE CASCADE
);

CREATE INDEX idx_rvi_rfp_id ON rfp_vendor_invitation (rfp_id);
CREATE INDEX idx_rvi_vendor_id ON rfp_vendor_invitation (vendor_id);

-- 5) PROPOSAL
CREATE TABLE proposal (
    id              BIGSERIAL PRIMARY KEY,
    rfp_id          BIGINT NOT NULL,
    vendor_id       BIGINT NOT NULL,

    total_price     NUMERIC(15,2),
    currency        VARCHAR(10),
    delivery_days   INTEGER,
    payment_terms   TEXT,
    warranty_terms  TEXT,

    CONSTRAINT fk_proposal_rfp
        FOREIGN KEY (rfp_id)
        REFERENCES rfp (id)
        ON DELETE CASCADE,

    CONSTRAINT fk_proposal_vendor
        FOREIGN KEY (vendor_id)
        REFERENCES vendor (id)
        ON DELETE CASCADE
);

CREATE INDEX idx_proposal_rfp_id ON proposal (rfp_id);
CREATE INDEX idx_proposal_vendor_id ON proposal (vendor_id);

-- 6) PROPOSAL_ITEM
CREATE TABLE proposal_item (
    id               BIGSERIAL PRIMARY KEY,
    proposal_id      BIGINT NOT NULL,
    for_rfp_item_id  BIGINT,   -- optional mapping to RFP line item

    quantity         INTEGER,
    unit_price       NUMERIC(15,2),
    total_price      NUMERIC(15,2),

    CONSTRAINT fk_prop_item_proposal
        FOREIGN KEY (proposal_id)
        REFERENCES proposal (id)
        ON DELETE CASCADE
);

CREATE INDEX idx_proposal_item_proposal_id ON proposal_item (proposal_id);

-- optional FK link to rfp_item, commented for now:
-- ALTER TABLE proposal_item
--   ADD CONSTRAINT fk_prop_item_rfp_item
--   FOREIGN KEY (for_rfp_item_id)
--   REFERENCES rfp_item (id);

-- 7) PROPOSAL_SCORE
CREATE TABLE proposal_score (
    id              BIGSERIAL PRIMARY KEY,
    proposal_id     BIGINT NOT NULL,

    overall_score   DOUBLE PRECISION,
    price_score     DOUBLE PRECISION,
    timeline_score  DOUBLE PRECISION,
    quality_score   DOUBLE PRECISION,
    explanation     TEXT,

    CONSTRAINT fk_prop_score_proposal
        FOREIGN KEY (proposal_id)
        REFERENCES proposal (id)
        ON DELETE CASCADE
);

CREATE INDEX idx_prop_score_proposal_id ON proposal_score (proposal_id);
