-- =========================
-- USERS
-- =========================
CREATE TABLE IF NOT EXISTS users (
  id            uuid PRIMARY KEY,
  email         varchar(150) NOT NULL,
  username      varchar(100) NOT NULL,
  alias         varchar(100),
  bio           varchar(255),
  role varchar(10) NOT NULL DEFAULT 'USER',
  password      varchar(255) NOT NULL,
  created_at    timestamp    NOT NULL DEFAULT now(),
  updated_at    timestamp    NOT NULL DEFAULT now(),
  deleted_at    timestamp,
  image_url     varchar(255)

  CONSTRAINT users_role_chk CHECK (role IN ('USER','MODERATOR','ADMIN'))
);

CREATE UNIQUE INDEX IF NOT EXISTS users_email_uq    ON users (email);
CREATE UNIQUE INDEX IF NOT EXISTS users_username_uq ON users (username);


-- =========================
-- TIMELINE POSTS
-- =========================
CREATE TABLE IF NOT EXISTS timeline_posts (
  id         uuid PRIMARY KEY,
  created_at timestamp NOT NULL DEFAULT now(),
  updated_at timestamp NOT NULL DEFAULT now(),
  deleted_at timestamp,
  user_id    uuid      NOT NULL,
  content    jsonb     NOT NULL,

  CONSTRAINT timeline_posts_user_fk
    FOREIGN KEY (user_id) REFERENCES users(id)
    ON DELETE CASCADE
);

CREATE INDEX IF NOT EXISTS timeline_posts_user_id_idx    ON timeline_posts (user_id);
CREATE INDEX IF NOT EXISTS timeline_posts_created_at_idx ON timeline_posts (created_at);


-- =========================
-- REFRESH TOKENS
-- =========================
CREATE TABLE IF NOT EXISTS refresh_tokens (
  id            uuid PRIMARY KEY,
  refresh_token varchar(255) NOT NULL,
  user_id       uuid         NOT NULL,
  created_at    timestamp    NOT NULL DEFAULT now(),
  expired_at    timestamp    NOT NULL,
  revoked_at    timestamp,

  CONSTRAINT refresh_tokens_user_fk
    FOREIGN KEY (user_id) REFERENCES users(id)
    ON DELETE CASCADE
);

CREATE UNIQUE INDEX IF NOT EXISTS refresh_tokens_token_uq ON refresh_tokens (refresh_token);
CREATE INDEX IF NOT EXISTS refresh_tokens_user_id_idx     ON refresh_tokens (user_id);


-- =========================
-- FRIENDSHIPS
-- =========================
CREATE TABLE IF NOT EXISTS friendships (
  id           uuid PRIMARY KEY,
  from_user_id uuid             NOT NULL,
  to_user_id   uuid             NOT NULL,
  pair_a       uuid             NOT NULL,
  pair_b       uuid             NOT NULL,
  status varchar(16) NOT NULL DEFAULT 'PENDING',

  created_at   timestamp        NOT NULL DEFAULT now(),
  updated_at   timestamp        NOT NULL DEFAULT now(),

  CONSTRAINT friendships_status_chk CHECK (status IN ('PENDING','ACCEPTED','DECLINED','REMOVED')),
  CONSTRAINT friendships_from_user_fk FOREIGN KEY (from_user_id) REFERENCES users(id) ON DELETE CASCADE,
  CONSTRAINT friendships_to_user_fk   FOREIGN KEY (to_user_id)   REFERENCES users(id) ON DELETE CASCADE,
  CONSTRAINT friendships_pair_a_fk    FOREIGN KEY (pair_a)       REFERENCES users(id) ON DELETE CASCADE,
  CONSTRAINT friendships_pair_b_fk    FOREIGN KEY (pair_b)       REFERENCES users(id) ON DELETE CASCADE
);

CREATE UNIQUE INDEX IF NOT EXISTS friendships_pair_uq          ON friendships (pair_a, pair_b);
CREATE INDEX IF NOT EXISTS friendships_from_user_id_idx        ON friendships (from_user_id);
CREATE INDEX IF NOT EXISTS friendships_to_user_id_idx          ON friendships (to_user_id);
CREATE INDEX IF NOT EXISTS friendships_status_idx              ON friendships (status);


-- =========================
-- FRIENDSHIP EVENTS
-- =========================
CREATE TABLE IF NOT EXISTS friendship_events (
  id             uuid PRIMARY KEY,
  from_user_id   uuid NOT NULL,
  to_user_id     uuid NOT NULL,
  pair_a         uuid NOT NULL,
  pair_b         uuid NOT NULL,
  event_type varchar(32) NOT NULL,
  friendship_id  uuid,
  meta           text,
  created_at     timestamp NOT NULL DEFAULT now(),

  CONSTRAINT friendship_events_event_type_chk CHECK (event_type IN ('SENT','ACCEPTED','DECLINED','CANCELLED','AUTO_REJECTED')),
  CONSTRAINT friendship_events_from_user_fk FOREIGN KEY (from_user_id) REFERENCES users(id) ON DELETE CASCADE,
  CONSTRAINT friendship_events_to_user_fk   FOREIGN KEY (to_user_id)   REFERENCES users(id) ON DELETE CASCADE,
  CONSTRAINT friendship_events_pair_a_fk    FOREIGN KEY (pair_a)       REFERENCES users(id) ON DELETE CASCADE,
  CONSTRAINT friendship_events_pair_b_fk    FOREIGN KEY (pair_b)       REFERENCES users(id) ON DELETE CASCADE,

  CONSTRAINT friendship_events_friendship_fk
    FOREIGN KEY (friendship_id) REFERENCES friendships(id)
    ON DELETE SET NULL
);

CREATE INDEX IF NOT EXISTS friendship_events_lookup_idx
  ON friendship_events (pair_a, pair_b, event_type, created_at);


-- =========================
-- BLOG ITEMS
-- =========================
CREATE TABLE IF NOT EXISTS blog_items (
  id              uuid PRIMARY KEY,
  user_id         uuid NOT NULL,

  title           varchar(200) NOT NULL,
  subtitle        varchar(255),

  slug            varchar(250) NOT NULL,
  cover_image_url varchar(500),
  cover_image_alt varchar(200),

  categories text[] NOT NULL DEFAULT '{}',
  privacy_status varchar(15) NOT NULL DEFAULT 'FRIENDS_ONLY',
  blog_status varchar(15) NOT NULL DEFAULT 'DRAFT',

  published_at    timestamp,

  created_at      timestamp NOT NULL DEFAULT now(),
  updated_at      timestamp NOT NULL DEFAULT now(),
  deleted_at      timestamp,

  content         jsonb NOT NULL,

  CONSTRAINT blog_items_categories_chk CHECK (categories <@ ARRAY[
                                         'MENTAL','PHYSICAL','FOOD','LIFESTYLE',
                                         'MINDFULNESS','HABITS','SLEEP','ENERGY',
                                         'RELATIONSHIPS','COMMUNITY','PURPOSE',
                                         'PERSONAL_GROWTH','REFLECTION'
                                       ]::text[]),
  CONSTRAINT blog_items_privacy_chk CHECK (privacy_status IN ('OPEN','FRIENDS_ONLY','PRIVATE')),
  CONSTRAINT blog_items_status_chk CHECK (blog_status IN ('DRAFT','PUBLISHED','ARCHIVED')),

  CONSTRAINT blog_items_user_fk
    FOREIGN KEY (user_id) REFERENCES users(id)
    ON DELETE CASCADE
);

CREATE UNIQUE INDEX IF NOT EXISTS blog_items_slug_uq   ON blog_items (slug);
CREATE INDEX IF NOT EXISTS blog_items_user_id_idx      ON blog_items (user_id);
CREATE INDEX IF NOT EXISTS blog_items_status_idx       ON blog_items (blog_status);
CREATE INDEX IF NOT EXISTS blog_items_published_at_idx ON blog_items (published_at);

CREATE INDEX IF NOT EXISTS blog_items_categories_gin_idx ON blog_items USING GIN (categories);

-- =========================
-- NEVO TABLES
-- =========================
CREATE TABLE IF NOT EXISTS food_groups (
  id       uuid PRIMARY KEY,
  group_no integer      NOT NULL UNIQUE,
  name_nl  varchar(255) NOT NULL UNIQUE,
  name_en  varchar(255) UNIQUE
);

CREATE TABLE IF NOT EXISTS nutrients (
  id   uuid PRIMARY KEY,
  code varchar(32) NOT NULL UNIQUE,
  unit varchar(16) NOT NULL
);

CREATE TABLE IF NOT EXISTS products (
  id                uuid PRIMARY KEY,
  nevo_code         integer      NOT NULL UNIQUE,
  nevo_version      varchar(64)  NOT NULL,
  food_group_id     uuid         NOT NULL,

  name_nl           varchar(512) NOT NULL,
  name_en           varchar(512),
  synonym           text,
  quantity          varchar(64),
  remark            text,
  contains_traces_of text,
  fortified_with    text,

  CONSTRAINT products_food_group_fk
    FOREIGN KEY (food_group_id) REFERENCES food_groups(id)
    ON DELETE RESTRICT
);

CREATE INDEX IF NOT EXISTS products_food_group_id_idx ON products (food_group_id);

CREATE TABLE IF NOT EXISTS product_nutrients (
  id           uuid PRIMARY KEY,
  product_id   uuid NOT NULL,
  nutrient_id  uuid NOT NULL,
  value_scaled bigint,

  CONSTRAINT product_nutrients_product_fk
    FOREIGN KEY (product_id) REFERENCES products(id)
    ON DELETE CASCADE,

  CONSTRAINT product_nutrients_nutrient_fk
    FOREIGN KEY (nutrient_id) REFERENCES nutrients(id)
    ON DELETE RESTRICT
);

CREATE UNIQUE INDEX IF NOT EXISTS product_nutrients_product_nutrient_uq
  ON product_nutrients (product_id, nutrient_id);

CREATE INDEX IF NOT EXISTS product_nutrients_nutrient_id_idx
  ON product_nutrients (nutrient_id);
