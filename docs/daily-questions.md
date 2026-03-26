# Daily Questions (Phase 1)

This phase adds the core data foundation for daily questions, feed integration, and streak tracking.

## What is implemented

- New feed type: `DAILY_QUESTION`
- New shared enum: `QuestionType` (`OPEN`, `MULTIPLE_CHOICE`)
- New feed model: `DailyQuestionItem : FeedItem`
- New DB schema in `V1__init.sql`:
  - `daily_questions`
  - `daily_question_items`
  - `daily_question_answers`
  - `user_streaks`
- `daily_question_answers` stores a full question snapshot (`question_*_snapshot`) so answers stay traceable even when the source question changes or is deleted
- New Exposed tables/entities for the same structures
- Optional startup JSON import via `DAILY_QUESTIONS_JSON_PATH`

## JSON import format

Top-level object:

```json
{
  "questions": [
    {
      "id": 1,
      "question": "Question text",
      "type": "multiple_choice",
      "minTime": "08:00",
      "maxTime": "12:00",
      "answers": ["A", "B", "C"]
    }
  ]
}
```

Notes:
- `id` accepts either:
  - UUID string
  - number (mapped to a stable name-based UUID)
- `type` accepts `open` or `multiple_choice`
- `answers` can be `null` for open questions

## Enable import on startup

Set this environment variable to the JSON file path:

```bash
export DAILY_QUESTIONS_JSON_PATH="/absolute/path/to/daily-questions.json"
```

Then run server startup as usual:

```bash
./gradlew :server:run
```

## Next phase

- Add repository/service for "question of the day"
- Add answer submission endpoint
- Compute and update streaks from answer dates
- Extend timeline query to return `FeedItem` union with `DailyQuestionItem`


