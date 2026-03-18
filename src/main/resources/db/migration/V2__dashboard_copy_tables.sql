-- =========================================================
-- V2: Dashboard copy engine — tabelas de output
--
-- As tabelas de INPUT (dashboard_signal_items, dashboard_day_aggregate)
-- e de user_watchlists são gerenciadas pelo b3_cvm_pipeline.
-- Aqui declaramos apenas as tabelas de OUTPUT que o backend lê,
-- garantindo que existam mesmo que o pipeline ainda não rodou.
--
-- Convenção: user_watchlist_id = users.id::text
-- O pipeline usa o id numérico do usuário como identificador.
-- =========================================================

CREATE TABLE IF NOT EXISTS analiso.dashboard_copy_day (
    id                          BIGSERIAL PRIMARY KEY,
    reference_date              DATE NOT NULL,
    user_watchlist_id           TEXT NOT NULL,
    day_template                TEXT NOT NULL,
    summary_headline            TEXT NOT NULL,
    summary_body                TEXT,
    next_step_headline          TEXT NOT NULL,
    next_step_body              TEXT,
    session_closing_headline    TEXT NOT NULL,
    session_closing_body        TEXT,
    cta_primary                 TEXT NOT NULL,
    manifest_version            TEXT NOT NULL,
    rendered_at                 TIMESTAMPTZ NOT NULL DEFAULT now(),

    CONSTRAINT uq_dashboard_copy_day
        UNIQUE (reference_date, user_watchlist_id)
);

CREATE INDEX IF NOT EXISTS idx_dashboard_copy_day_date_watchlist
    ON analiso.dashboard_copy_day (reference_date, user_watchlist_id);

CREATE INDEX IF NOT EXISTS idx_dashboard_copy_day_watchlist_date_desc
    ON analiso.dashboard_copy_day (user_watchlist_id, reference_date DESC);


CREATE TABLE IF NOT EXISTS analiso.dashboard_copy_items (
    id                      BIGSERIAL PRIMARY KEY,
    reference_date          DATE NOT NULL,
    user_watchlist_id       TEXT NOT NULL,
    ticker                  TEXT NOT NULL,
    pillar                  TEXT NOT NULL,
    priority_score          INT  NOT NULL,
    priority_rank           INT  NOT NULL,
    primary_template        TEXT NOT NULL,
    overlays                JSONB NOT NULL DEFAULT '[]',
    card_badge              TEXT NOT NULL,
    card_title              TEXT,
    card_why_it_matters     TEXT NOT NULL,
    card_cta_label          TEXT NOT NULL,
    detail_entry_reason     TEXT,
    detail_benefit_now      TEXT,
    extra_badge             TEXT,
    extra_line              TEXT,
    manifest_version        TEXT NOT NULL,
    rendered_at             TIMESTAMPTZ NOT NULL DEFAULT now(),

    CONSTRAINT uq_dashboard_copy_items
        UNIQUE (reference_date, user_watchlist_id, ticker, pillar)
);

CREATE INDEX IF NOT EXISTS idx_dashboard_copy_items_date_watchlist
    ON analiso.dashboard_copy_items (reference_date, user_watchlist_id);

CREATE INDEX IF NOT EXISTS idx_dashboard_copy_items_rank
    ON analiso.dashboard_copy_items (reference_date, user_watchlist_id, priority_rank);
