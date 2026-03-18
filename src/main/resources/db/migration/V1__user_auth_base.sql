CREATE SCHEMA IF NOT EXISTS analiso;

CREATE TABLE IF NOT EXISTS analiso.users (
    id BIGSERIAL PRIMARY KEY,
    google_sub VARCHAR(128) NOT NULL,
    email VARCHAR(320) NOT NULL,
    name VARCHAR(200) NOT NULL,
    avatar_url VARCHAR(2048),
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    CONSTRAINT uk_users_google_sub UNIQUE (google_sub),
    CONSTRAINT uk_users_email UNIQUE (email)
);

CREATE TABLE IF NOT EXISTS analiso.user_watchlist_items (
    user_id BIGINT NOT NULL,
    ticker VARCHAR(20) NOT NULL,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    CONSTRAINT pk_user_watchlist_items PRIMARY KEY (user_id, ticker),
    CONSTRAINT fk_watchlist_user FOREIGN KEY (user_id) REFERENCES analiso.users (id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS analiso.user_dashboard_preferences (
    user_id BIGINT PRIMARY KEY,
    mode VARCHAR(50) NOT NULL DEFAULT 'balanced',
    period VARCHAR(50) NOT NULL DEFAULT '12m',
    severities JSONB NOT NULL DEFAULT '[]'::jsonb,
    pillars JSONB NOT NULL DEFAULT '[]'::jsonb,
    sources JSONB NOT NULL DEFAULT '[]'::jsonb,
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    CONSTRAINT fk_dashboard_preferences_user FOREIGN KEY (user_id) REFERENCES analiso.users (id) ON DELETE CASCADE
);

CREATE INDEX IF NOT EXISTS idx_watchlist_user_id ON analiso.user_watchlist_items (user_id);
