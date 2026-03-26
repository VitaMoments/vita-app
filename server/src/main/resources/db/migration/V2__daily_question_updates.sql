-- =====================================================================
-- V2: Daily-question feature expansion
--   1. Add min_days_between + categories to daily_questions template
--   2. Change daily_question_items unique constraint: date -> (question_id, date)
--      so multiple questions can be served per day (one per question per day)
--   3. Add answer_document (rich text) to daily_question_answers
-- =====================================================================

-- 1. daily_questions table
ALTER TABLE daily_questions
    ADD COLUMN IF NOT EXISTS min_days_between integer,
    ADD COLUMN IF NOT EXISTS categories       jsonb;

-- 2. daily_question_items: drop old unique-by-date, add unique (question_id, date)
DROP INDEX IF EXISTS daily_question_items_question_date_uq;

CREATE UNIQUE INDEX IF NOT EXISTS daily_question_items_question_id_date_uq
    ON daily_question_items (question_id, question_date);

-- 3. daily_question_answers: add rich-text answer document
ALTER TABLE daily_question_answers
    ADD COLUMN IF NOT EXISTS answer_document jsonb;

