CREATE EXTENSION IF NOT EXISTS unaccent;

CREATE OR REPLACE FUNCTION slugify(input text)
RETURNS text
LANGUAGE sql
IMMUTABLE
AS $$
  SELECT trim(both '-' from
    regexp_replace(
      lower(unaccent(coalesce(input, ''))),
      '[^a-z0-9]+', '-', 'g'
    )
  );
$$;

CREATE OR REPLACE FUNCTION set_blog_item_slug()
RETURNS trigger
LANGUAGE plpgsql
AS $$
DECLARE
  base text;
  candidate text;
  i int := 2;
BEGIN
  IF NEW.slug IS NULL OR NEW.slug = '' THEN
    base := slugify(NEW.title);

    IF base = '' THEN
      base := 'post';
    END IF;

    candidate := base;

    WHILE EXISTS (
      SELECT 1
      FROM blog_items
      WHERE slug = candidate
        AND id <> COALESCE(NEW.id, '00000000-0000-0000-0000-000000000000')
    ) LOOP
      candidate := base || '-' || i;
      i := i + 1;
    END LOOP;

    NEW.slug := candidate;
  END IF;

  RETURN NEW;
END;
$$;

-- Unique index (als je die nog niet had)
CREATE UNIQUE INDEX IF NOT EXISTS blog_items_slug_uq ON blog_items (slug);

DROP TRIGGER IF EXISTS blog_items_set_slug ON blog_items;

CREATE TRIGGER blog_items_set_slug
BEFORE INSERT OR UPDATE OF title, slug ON blog_items
FOR EACH ROW
EXECUTE FUNCTION set_blog_item_slug();
