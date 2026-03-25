-- =========================
-- USERS
-- =========================
CREATE TABLE IF NOT EXISTS users (
  id                 uuid         PRIMARY KEY,
  email              varchar(150) NOT NULL,
  username           varchar(100) NOT NULL,
  alias              varchar(100),
  bio                varchar(255),
  role               varchar(10)  NOT NULL DEFAULT 'USER',
  password           varchar(255) NOT NULL,

  first_name         varchar(80),
  last_name          varchar(80),
  phone              varchar(32),
  birth_date         date,
  locale             varchar(16),
  time_zone          varchar(64),
  email_verified_at  timestamp,

  image_url          varchar(255),
  cover_image_url    varchar(255),
  details_privacy    varchar(16)  NOT NULL DEFAULT 'PRIVATE',

  created_at         timestamp    NOT NULL DEFAULT now(),
  updated_at         timestamp    NOT NULL DEFAULT now(),
  deleted_at         timestamp,
  created_by         uuid,
  updated_by         uuid,
  deleted_by         uuid,

  CONSTRAINT users_role_chk CHECK (role IN ('USER','MODERATOR','ADMIN'))
);

CREATE UNIQUE INDEX IF NOT EXISTS users_email_uq    ON users (email);
CREATE UNIQUE INDEX IF NOT EXISTS users_username_uq ON users (username);

-- =========================
-- FEED ITEMS (root table)
-- =========================
CREATE TABLE IF NOT EXISTS feed_items (
  id         uuid      PRIMARY KEY,
  type       varchar(20)  NOT NULL,
  user_id    uuid         NOT NULL,
  privacy    varchar(15)  NOT NULL DEFAULT 'FRIENDS_ONLY',
  created_at timestamp    NOT NULL DEFAULT now(),
  updated_at timestamp    NOT NULL DEFAULT now(),
  deleted_at timestamp,

  CONSTRAINT feed_items_type_chk    CHECK (type IN ('TIMELINE')),
  CONSTRAINT feed_items_privacy_chk CHECK (privacy IN ('OPEN','FRIENDS_ONLY','PRIVATE')),
  CONSTRAINT feed_items_user_fk     FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

CREATE INDEX IF NOT EXISTS feed_items_user_id_idx    ON feed_items (user_id);
CREATE INDEX IF NOT EXISTS feed_items_type_idx       ON feed_items (type);
CREATE INDEX IF NOT EXISTS feed_items_created_at_idx ON feed_items (created_at);

-- =========================
-- TIMELINE POSTS
-- =========================
CREATE TABLE IF NOT EXISTS timeline_items (
  id           uuid PRIMARY KEY,
  feed_item_id uuid  NOT NULL,
  content      jsonb NOT NULL,

  CONSTRAINT timeline_items_feed_item_fk
    FOREIGN KEY (feed_item_id) REFERENCES feed_items(id)
    ON DELETE CASCADE
);

-- =========================
-- CATEGORIES
-- =========================
DROP TABLE IF EXISTS feed_item_categories;

CREATE TABLE feed_item_categories (
  feed_item_id uuid    NOT NULL REFERENCES feed_items(id) ON DELETE CASCADE,
  category     varchar(32) NOT NULL,
  PRIMARY KEY (feed_item_id, category)
);

CREATE INDEX IF NOT EXISTS feed_item_categories_feed_item_idx ON feed_item_categories (feed_item_id);
CREATE INDEX IF NOT EXISTS feed_item_categories_category_idx  ON feed_item_categories (category);

-- =========================
-- REFRESH TOKENS
-- =========================
CREATE TABLE IF NOT EXISTS refresh_tokens (
  id            uuid         PRIMARY KEY,
  refresh_token varchar(255) NOT NULL,
  user_id       uuid         NOT NULL,
  created_at    timestamp    NOT NULL DEFAULT now(),
  expired_at    timestamp    NOT NULL,
  revoked_at    timestamp,

  CONSTRAINT refresh_tokens_user_fk
    FOREIGN KEY (user_id) REFERENCES users(id)
    ON DELETE CASCADE
);

CREATE UNIQUE INDEX IF NOT EXISTS refresh_tokens_token_uq  ON refresh_tokens (refresh_token);
CREATE INDEX        IF NOT EXISTS refresh_tokens_user_id_idx ON refresh_tokens (user_id);

-- =========================
-- FRIENDSHIPS
-- =========================
CREATE TABLE IF NOT EXISTS friendships (
  id           uuid PRIMARY KEY,
  from_user_id uuid NOT NULL,
  to_user_id   uuid NOT NULL,
  pair_a       uuid NOT NULL,
  pair_b       uuid NOT NULL,
  status       varchar(16) NOT NULL DEFAULT 'PENDING',
  deleted_at   timestamp,
  created_at   timestamp   NOT NULL DEFAULT now(),
  updated_at   timestamp   NOT NULL DEFAULT now(),

  CONSTRAINT friendships_status_chk    CHECK (status IN ('PENDING','ACCEPTED','DECLINED','REMOVED')),
  CONSTRAINT friendships_from_user_fk  FOREIGN KEY (from_user_id) REFERENCES users(id) ON DELETE CASCADE,
  CONSTRAINT friendships_to_user_fk    FOREIGN KEY (to_user_id)   REFERENCES users(id) ON DELETE CASCADE,
  CONSTRAINT friendships_pair_a_fk     FOREIGN KEY (pair_a)       REFERENCES users(id) ON DELETE CASCADE,
  CONSTRAINT friendships_pair_b_fk     FOREIGN KEY (pair_b)       REFERENCES users(id) ON DELETE CASCADE
);

CREATE UNIQUE INDEX IF NOT EXISTS friendships_pair_uq       ON friendships (pair_a, pair_b);
CREATE INDEX        IF NOT EXISTS friendships_from_user_id_idx ON friendships (from_user_id);
CREATE INDEX        IF NOT EXISTS friendships_to_user_id_idx   ON friendships (to_user_id);
CREATE INDEX        IF NOT EXISTS friendships_status_idx       ON friendships (status);

-- =========================
-- FRIENDSHIP EVENTS
-- =========================
CREATE TABLE IF NOT EXISTS friendship_events (
  id            uuid PRIMARY KEY,
  from_user_id  uuid NOT NULL,
  to_user_id    uuid NOT NULL,
  pair_a        uuid NOT NULL,
  pair_b        uuid NOT NULL,
  event_type    varchar(32) NOT NULL,
  friendship_id uuid,
  meta          text,
  created_at    timestamp   NOT NULL DEFAULT now(),

  CONSTRAINT friendship_events_event_type_chk CHECK (event_type IN ('SENT','ACCEPTED','DECLINED','CANCELLED','AUTO_REJECTED')),
  CONSTRAINT friendship_events_from_user_fk   FOREIGN KEY (from_user_id)  REFERENCES users(id)       ON DELETE CASCADE,
  CONSTRAINT friendship_events_to_user_fk     FOREIGN KEY (to_user_id)    REFERENCES users(id)       ON DELETE CASCADE,
  CONSTRAINT friendship_events_pair_a_fk      FOREIGN KEY (pair_a)        REFERENCES users(id)       ON DELETE CASCADE,
  CONSTRAINT friendship_events_pair_b_fk      FOREIGN KEY (pair_b)        REFERENCES users(id)       ON DELETE CASCADE,
  CONSTRAINT friendship_events_friendship_fk  FOREIGN KEY (friendship_id) REFERENCES friendships(id) ON DELETE SET NULL
);

CREATE INDEX IF NOT EXISTS friendship_events_lookup_idx
  ON friendship_events (pair_a, pair_b, event_type, created_at);

-- =========================
-- ADDRESSES
-- =========================
CREATE TABLE IF NOT EXISTS addresses (
  id           uuid        PRIMARY KEY,
  owner_type   varchar(24) NOT NULL,
  owner_id     uuid        NOT NULL,
  type         varchar(16) NOT NULL DEFAULT 'OTHER',
  label        varchar(80),
  is_primary   boolean     NOT NULL DEFAULT false,
  privacy      varchar(16) NOT NULL DEFAULT 'PRIVATE',

  line1        varchar(120) NOT NULL,
  line2        varchar(120),
  postal_code  varchar(32),
  city         varchar(80),
  region       varchar(80),
  country_code varchar(2)  NOT NULL,

  created_at   timestamp   NOT NULL DEFAULT now(),
  updated_at   timestamp   NOT NULL DEFAULT now(),
  deleted_at   timestamp,
  created_by   uuid,
  updated_by   uuid,
  deleted_by   uuid
);

CREATE INDEX        IF NOT EXISTS addresses_owner_idx            ON addresses (owner_type, owner_id);
CREATE INDEX        IF NOT EXISTS addresses_country_idx          ON addresses (country_code);
CREATE INDEX        IF NOT EXISTS addresses_owner_type_idx       ON addresses (owner_type, owner_id, type);
CREATE UNIQUE INDEX IF NOT EXISTS addresses_one_primary_per_owner
  ON addresses (owner_type, owner_id)
  WHERE is_primary = true AND deleted_at IS NULL;

-- =========================
-- MEDIA ASSETS
-- =========================
CREATE TABLE IF NOT EXISTS media_assets (
  id                 uuid          PRIMARY KEY,
  reference_id       uuid          NOT NULL,
  reference_type     varchar(30)   NOT NULL,
  purpose            varchar(15)   NOT NULL,
  privacy            varchar(15)   NOT NULL,

  original_file_name varchar(255),
  stored_file_name   varchar(255)  NOT NULL,
  object_key         varchar(1000) NOT NULL,

  content_type       varchar(100)  NOT NULL,
  size_bytes         bigint        NOT NULL,

  width              integer,
  height             integer,

  created_at         timestamp     NOT NULL DEFAULT now(),
  updated_at         timestamp     NOT NULL DEFAULT now(),
  deleted_at         timestamp,
  created_by         uuid,
  updated_by         uuid,
  deleted_by         uuid,

  CONSTRAINT media_assets_reference_type_chk CHECK (reference_type IN ('USER','FEED_ITEM','POST','COMMENT')),
  CONSTRAINT media_assets_purpose_chk        CHECK (purpose  IN ('PROFILE','POST','FEED','COVER')),
  CONSTRAINT media_assets_privacy_chk        CHECK (privacy  IN ('OPEN','FRIENDS_ONLY','PRIVATE'))
);

CREATE UNIQUE INDEX IF NOT EXISTS media_assets_object_key_uq            ON media_assets (object_key);
CREATE INDEX        IF NOT EXISTS media_assets_reference_idx             ON media_assets (reference_id, reference_type);
CREATE INDEX        IF NOT EXISTS media_assets_reference_type_purpose_idx ON media_assets (reference_type, purpose);
CREATE INDEX        IF NOT EXISTS media_assets_privacy_idx               ON media_assets (privacy);

-- =========================
-- NEVO TABLES
-- =========================
CREATE TABLE IF NOT EXISTS food_groups (
  id       uuid         PRIMARY KEY,
  group_no integer      NOT NULL UNIQUE,
  name_nl  varchar(255) NOT NULL UNIQUE,
  name_en  varchar(255) UNIQUE
);

CREATE TABLE IF NOT EXISTS nutrients (
  id   uuid        PRIMARY KEY,
  code varchar(32) NOT NULL UNIQUE,
  unit varchar(16) NOT NULL
);

CREATE TABLE IF NOT EXISTS products (
  id               uuid         PRIMARY KEY,
  nevo_code        integer      NOT NULL UNIQUE,
  nevo_version     varchar(64)  NOT NULL,
  food_group_id    uuid         NOT NULL,
  name_nl          varchar(512) NOT NULL,
  name_en          varchar(512),
  synonym          text,
  quantity         varchar(64),
  remark           text,
  contains_traces_of text,
  fortified_with   text,

  CONSTRAINT products_food_group_fk
    FOREIGN KEY (food_group_id) REFERENCES food_groups(id)
    ON DELETE RESTRICT
);

CREATE INDEX IF NOT EXISTS products_food_group_id_idx ON products (food_group_id);

CREATE TABLE IF NOT EXISTS product_nutrients (
  id          uuid   PRIMARY KEY,
  product_id  uuid   NOT NULL,
  nutrient_id uuid   NOT NULL,
  value_scaled bigint,

  CONSTRAINT product_nutrients_product_fk
    FOREIGN KEY (product_id)  REFERENCES products(id)  ON DELETE CASCADE,
  CONSTRAINT product_nutrients_nutrient_fk
    FOREIGN KEY (nutrient_id) REFERENCES nutrients(id) ON DELETE RESTRICT
);

CREATE UNIQUE INDEX IF NOT EXISTS product_nutrients_product_nutrient_uq
  ON product_nutrients (product_id, nutrient_id);

CREATE INDEX IF NOT EXISTS product_nutrients_nutrient_id_idx
  ON product_nutrients (nutrient_id);
