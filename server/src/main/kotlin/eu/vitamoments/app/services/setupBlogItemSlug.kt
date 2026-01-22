package eu.vitamoments.app.services

import eu.vitamoments.app.modules.DatabaseFactory
import org.jetbrains.exposed.v1.jdbc.transactions.transaction

private fun setupBlogItemSlug() {
    // 1) Probeer unaccent (kan mislukken op sommige hosts)
    transaction(DatabaseFactory.db) {
        try {
            exec("""CREATE EXTENSION IF NOT EXISTS unaccent;""")
        } catch (_: Exception) {
            // Als dit faalt, kun je óf negeren (maar dan faalt slugify door unaccent),
            // óf hieronder een slugify zonder unaccent installeren.
        }

        // 2) slugify (met unaccent)
        exec(
        """
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
        """.trimIndent()
        )

        // 3) trigger-functie + index + trigger
        exec(
            """
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
            """.trimIndent()
        )

        exec("""CREATE UNIQUE INDEX IF NOT EXISTS blog_items_slug_uq ON blog_items (slug);""")
        exec("""DROP TRIGGER IF EXISTS blog_items_set_slug ON blog_items;""")

        exec(
            """
            CREATE TRIGGER blog_items_set_slug
            BEFORE INSERT OR UPDATE OF title, slug ON blog_items
            FOR EACH ROW
            EXECUTE FUNCTION set_blog_item_slug();
            """.trimIndent()
        )
    }
}
