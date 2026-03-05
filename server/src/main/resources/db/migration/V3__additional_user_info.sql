ALTER TABLE users
  ADD COLUMN IF NOT EXISTS created_by uuid,
  ADD COLUMN IF NOT EXISTS updated_by uuid,
  ADD COLUMN IF NOT EXISTS deleted_by uuid,
  ADD COLUMN IF NOT EXISTS first_name        varchar(80),
  ADD COLUMN IF NOT EXISTS last_name         varchar(80),
  ADD COLUMN IF NOT EXISTS phone             varchar(32),
  ADD COLUMN IF NOT EXISTS birth_date        date,
  ADD COLUMN IF NOT EXISTS locale            varchar(16),
  ADD COLUMN IF NOT EXISTS time_zone         varchar(64),
  ADD COLUMN IF NOT EXISTS email_verified_at timestamp,
  ADD COLUMN IF NOT EXISTS cover_image_url   varchar(255),
  ADD COLUMN IF NOT EXISTS details_privacy   varchar(16) NOT NULL DEFAULT 'PRIVATE';

CREATE TABLE IF NOT EXISTS addresses (
  id uuid PRIMARY KEY,
  owner_type varchar(24) NOT NULL,
  owner_id uuid NOT NULL,

  type varchar(16) NOT NULL DEFAULT 'OTHER',
  label varchar(80),
  is_primary boolean NOT NULL DEFAULT false,
  privacy varchar(16) NOT NULL DEFAULT 'PRIVATE',

  line1 varchar(120) NOT NULL,
  line2 varchar(120),
  postal_code varchar(32),
  city varchar(80),
  region varchar(80),
  country_code varchar(2) NOT NULL,

  created_at timestamp NOT NULL DEFAULT now(),
  updated_at timestamp NOT NULL DEFAULT now(),
  deleted_at timestamp,

  created_by uuid,
  updated_by uuid,
  deleted_by uuid
);

CREATE INDEX IF NOT EXISTS addresses_owner_idx ON addresses (owner_type, owner_id);
CREATE INDEX IF NOT EXISTS addresses_country_idx ON addresses (country_code);
CREATE INDEX IF NOT EXISTS addresses_owner_type_idx ON addresses (owner_type, owner_id, type);

-- max 1 primary per owner (soft delete aware)
CREATE UNIQUE INDEX IF NOT EXISTS addresses_one_primary_per_owner
ON addresses (owner_type, owner_id)
WHERE is_primary = true AND deleted_at IS NULL;