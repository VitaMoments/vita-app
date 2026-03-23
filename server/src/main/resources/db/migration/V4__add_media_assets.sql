-- =========================
-- MEDIA ASSETS
-- =========================
CREATE TABLE IF NOT EXISTS media_assets (
  id                 uuid PRIMARY KEY,
  reference_id       uuid         NOT NULL,
  reference_type     varchar(30)  NOT NULL,
  purpose            varchar(15)  NOT NULL,
  privacy            varchar(15)  NOT NULL,

  original_file_name varchar(255),
  stored_file_name   varchar(255) NOT NULL,
  object_key         varchar(1000) NOT NULL,

  content_type       varchar(100) NOT NULL,
  size_bytes         bigint       NOT NULL,

  width              integer,
  height             integer,

  created_at         timestamp    NOT NULL DEFAULT now(),
  updated_at         timestamp    NOT NULL DEFAULT now(),
  deleted_at         timestamp,

  created_by         uuid,
  updated_by         uuid,
  deleted_by         uuid,

  CONSTRAINT media_assets_reference_type_chk
    CHECK (reference_type IN ('USER','FEED_ITEM','POST','COMMENT')),

  CONSTRAINT media_assets_purpose_chk
    CHECK (purpose IN ('PROFILE','POST','FEED','COVER')),

  CONSTRAINT media_assets_privacy_chk
    CHECK (privacy IN ('OPEN','FRIENDS_ONLY','PRIVATE'))
);

CREATE UNIQUE INDEX IF NOT EXISTS media_assets_object_key_uq
  ON media_assets (object_key);

CREATE INDEX IF NOT EXISTS media_assets_reference_idx
  ON media_assets (reference_id, reference_type);

CREATE INDEX IF NOT EXISTS media_assets_reference_type_purpose_idx
  ON media_assets (reference_type, purpose);

CREATE INDEX IF NOT EXISTS media_assets_privacy_idx
  ON media_assets (privacy);