-- =========================================================
-- V3: Watchlist copy engine — tabelas de output
--
-- As tabelas de INPUT (watchlist_input_items, watchlist_input_aggregate)
-- são gerenciadas pelo b3_cvm_pipeline.
-- Aqui declaramos apenas as tabelas de OUTPUT que o backend lê,
-- garantindo que existam mesmo que o pipeline ainda não rodou.
--
-- Convenção: user_watchlist_id = users.id::text
-- =========================================================

CREATE TABLE IF NOT EXISTS analiso.watchlist_copy_page (
    id                              BIGSERIAL PRIMARY KEY,
    reference_date                  DATE        NOT NULL,
    user_watchlist_id               TEXT        NOT NULL,
    mode                            TEXT        NOT NULL,

    page_template                   TEXT        NOT NULL,
    header_title                    TEXT        NOT NULL,
    header_subtitle                 TEXT        NOT NULL,
    tab_bar_active                  TEXT        NOT NULL,

    state_block_eyebrow             TEXT,
    state_block_headline            TEXT,
    state_block_body                TEXT,
    state_block_pill                TEXT,

    priority_section_title          TEXT,
    priority_section_body           TEXT,
    priority_section_count_label    TEXT,

    updates_section_title           TEXT,
    updates_section_body            TEXT,

    list_section_title              TEXT,
    list_section_sort_order         TEXT,

    quick_overview_title            TEXT        NOT NULL,
    quick_overview_body             TEXT        NOT NULL,
    quick_overview_metrics          JSONB       NOT NULL DEFAULT '[]',

    alerts_panel_title              TEXT        NOT NULL,
    alerts_panel_body               TEXT        NOT NULL,
    alerts_panel_cta_label          TEXT        NOT NULL,

    session_closing_title           TEXT,
    session_closing_body            TEXT,

    manifest_version                TEXT        NOT NULL,
    rendered_at                     TIMESTAMPTZ NOT NULL DEFAULT now(),
    audit                           JSONB       NOT NULL DEFAULT '{}',

    CONSTRAINT uq_watchlist_copy_page
        UNIQUE (reference_date, user_watchlist_id, mode)
);

CREATE INDEX IF NOT EXISTS idx_watchlist_copy_page_date_watchlist
    ON analiso.watchlist_copy_page (reference_date, user_watchlist_id);

CREATE INDEX IF NOT EXISTS idx_watchlist_copy_page_watchlist_mode_date_desc
    ON analiso.watchlist_copy_page (user_watchlist_id, mode, reference_date DESC);


CREATE TABLE IF NOT EXISTS analiso.watchlist_copy_items (
    id                  BIGSERIAL PRIMARY KEY,
    reference_date      DATE        NOT NULL,
    user_watchlist_id   TEXT        NOT NULL,
    mode                TEXT        NOT NULL,
    section             TEXT        NOT NULL,
    display_order       INT         NOT NULL,

    ticker              TEXT,
    company_name        TEXT,
    sector_label        TEXT,

    item_template       TEXT        NOT NULL,
    badge               TEXT        NOT NULL,
    top_tag             TEXT,
    context_line        TEXT,
    pillar_badge        TEXT,
    what_changed_label  TEXT,
    what_changed        TEXT,
    why_matters_label   TEXT,
    why_matters         TEXT,
    watch_line          TEXT,

    headline            TEXT,
    support_line        TEXT,
    status_chip         TEXT,
    unseen_chip         TEXT,
    pending_data_badge  TEXT,

    meta_line           TEXT,
    cta_label           TEXT,
    cta_primary         TEXT,
    cta_secondary       TEXT,
    overlays            JSONB       NOT NULL DEFAULT '[]',

    manifest_version    TEXT        NOT NULL,
    rendered_at         TIMESTAMPTZ NOT NULL DEFAULT now()
);

CREATE INDEX IF NOT EXISTS idx_watchlist_copy_items_lookup
    ON analiso.watchlist_copy_items (reference_date, user_watchlist_id, mode, section, display_order);


CREATE TABLE IF NOT EXISTS analiso.watchlist_copy_alerts (
    id                  BIGSERIAL PRIMARY KEY,
    reference_date      DATE        NOT NULL,
    user_watchlist_id   TEXT        NOT NULL,
    mode                TEXT        NOT NULL,
    display_order       INT         NOT NULL,

    badge               TEXT        NOT NULL,
    title               TEXT        NOT NULL,
    body                TEXT        NOT NULL,
    time_label          TEXT,

    manifest_version    TEXT        NOT NULL,
    rendered_at         TIMESTAMPTZ NOT NULL DEFAULT now()
);

CREATE INDEX IF NOT EXISTS idx_watchlist_copy_alerts_lookup
    ON analiso.watchlist_copy_alerts (reference_date, user_watchlist_id, mode, display_order);
