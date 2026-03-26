import React, { useEffect, useRef, useState } from "react";
import { MdClose } from "react-icons/md";

import { FeedEditor } from "../../components/editor/feed-editor/FeedEditor";
import { DailyQuestionService } from "../../api/service/DailyQuestionService";
import { FEED_CATEGORY_META } from "../../data/ui/feedCategoryMeta";

import type { FeedItem, RichTextDocument } from "../../data/types";
import type { FeedCategory } from "../../data/types";

import styles from "./DailyQuestionInput.module.css";

type Props = {
  /** Controlled open state. Set to true to show the input and fetch a question. */
  isOpen: boolean;
  onClose: () => void;
  onSubmitted?: (result: { currentStreak: number; longestStreak: number }) => void;
};

const NO_MORE_QUESTIONS_MSG = "no_more_questions_today";

export const DailyQuestionInput: React.FC<Props> = ({ isOpen, onClose, onSubmitted }) => {
  const [question, setQuestion] = useState<FeedItem.DAILYQUESTIONITEM | null>(null);
  const [loading, setLoading] = useState(false);
  const [submitting, setSubmitting] = useState(false);
  const [error, setError] = useState<string | null>(null);
  const [noMoreQuestions, setNoMoreQuestions] = useState(false);

  // Multiple-choice state
  const [selectedAnswer, setSelectedAnswer] = useState<string>("");

  // Track which question UUID we last fetched to avoid re-fetching on re-renders
  const fetchedForOpen = useRef(false);

  useEffect(() => {
    if (!isOpen) {
      fetchedForOpen.current = false;
      return;
    }

    if (fetchedForOpen.current) return;
    fetchedForOpen.current = true;

    void loadNextQuestion();
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [isOpen]);

  const loadNextQuestion = async () => {
    setLoading(true);
    setError(null);
    setNoMoreQuestions(false);
    setQuestion(null);
    setSelectedAnswer("");

    try {
      const q = await DailyQuestionService.getNextQuestion();
      setQuestion(q);
    } catch (e: any) {
      const msg: string =
        e?.response?.data?.message ?? e?.message ?? "unknown";
      if (msg === NO_MORE_QUESTIONS_MSG || e?.response?.status === 404) {
        setNoMoreQuestions(true);
      } else {
        setError("Kon geen vraag ophalen. Probeer het opnieuw.");
      }
    } finally {
      setLoading(false);
    }
  };

  const handleSubmit = async ({
    document,
    text,
  }: {
    document: RichTextDocument;
    text: string;
  }) => {
    if (!question) return;

    if (question.questionType === "MULTIPLE_CHOICE" && !selectedAnswer) {
      setError("Kies eerst een antwoordoptie.");
      return;
    }

    setSubmitting(true);
    setError(null);

    try {
      const result = await DailyQuestionService.submitAnswer({
        questionItemId: question.uuid,
        answerText: question.questionType === "OPEN" ? text || null : null,
        selectedAnswer: question.questionType === "MULTIPLE_CHOICE" ? selectedAnswer : null,
        answerDocument: question.questionType === "OPEN" ? document : null,
      });

      onSubmitted?.({ currentStreak: result.currentStreak, longestStreak: result.longestStreak });
      onClose();
    } catch (e: any) {
      setError(
        e?.response?.data?.message ?? "Antwoord versturen mislukt. Probeer opnieuw."
      );
    } finally {
      setSubmitting(false);
    }
  };

  if (!isOpen) return null;

  return (
    <div className={styles.card}>
      {/* Loading */}
      {loading && (
        <p className={styles.loadingText}>Vraag laden…</p>
      )}

      {/* No more questions */}
      {!loading && noMoreQuestions && (
        <>
          <div className={styles.header}>
            <span className={styles.title}>Vraag van de dag</span>
            <button
              type="button"
              className={styles.closeBtn}
              onClick={onClose}
              aria-label="Sluiten"
            >
              <MdClose size={20} />
            </button>
          </div>
          <div className={styles.stateBox}>
            <div className={styles.stateIcon}>🎉</div>
            <p className={styles.stateTitle}>Alle vragen beantwoord!</p>
            <p className={styles.stateSubtitle}>
              Je hebt vandaag alle beschikbare vragen beantwoord. Kom morgen terug!
            </p>
          </div>
        </>
      )}

      {/* Error */}
      {!loading && error && !question && (
        <>
          <div className={styles.header}>
            <span className={styles.title}>Vraag van de dag</span>
            <button
              type="button"
              className={styles.closeBtn}
              onClick={onClose}
              aria-label="Sluiten"
            >
              <MdClose size={20} />
            </button>
          </div>
          <p className={styles.errorText}>{error}</p>
        </>
      )}

      {/* Question loaded */}
      {!loading && question && (
        <>
          <div className={styles.header}>
            <span className={styles.title}>{question.question}</span>
            <button
              type="button"
              className={styles.closeBtn}
              onClick={onClose}
              aria-label="Sluiten"
              disabled={submitting}
            >
              <MdClose size={20} />
            </button>
          </div>

          {/* Category badges */}
          {question.categories.length > 0 && (
            <div className={styles.categories}>
              {question.categories.map((cat: FeedCategory) => {
                const meta = FEED_CATEGORY_META[cat];
                const Icon = meta?.icon;
                return (
                  <span key={cat} className={styles.categoryBadge}>
                    {Icon && <Icon />}
                    {meta?.label ?? cat}
                  </span>
                );
              })}
            </div>
          )}

          {/* Open question: rich-text editor */}
          {question.questionType === "OPEN" && (
            <FeedEditor
              placeholder="Schrijf je antwoord hier…"
              showCategories={false}
              showDraftButton={false}
              publishLabel={submitting ? "Versturen…" : "Verstuur antwoord"}
              onError={(msg) => setError(msg)}
              onClearError={() => setError(null)}
              onSubmit={async ({ document, text }) => {
                await handleSubmit({ document, text });
              }}
            />
          )}

          {/* Multiple choice question */}
          {question.questionType === "MULTIPLE_CHOICE" && (
            <>
              <div className={styles.choices}>
                {(question.answers ?? []).map((option) => (
                  <label key={option} className={styles.choiceItem}>
                    <input
                      type="radio"
                      name={`dq-choice-${question.uuid}`}
                      value={option}
                      checked={selectedAnswer === option}
                      onChange={(e) => setSelectedAnswer(e.target.value)}
                      disabled={submitting}
                    />
                    <span className={styles.choiceLabel}>{option}</span>
                  </label>
                ))}
              </div>
              <FeedEditor
                placeholder="Optioneel: voeg een toelichting toe…"
                showCategories={false}
                showDraftButton={false}
                publishLabel={submitting ? "Versturen…" : "Verstuur antwoord"}
                onError={(msg) => setError(msg)}
                onClearError={() => setError(null)}
                onSubmit={async ({ document, text }) => {
                  await handleSubmit({ document, text });
                }}
              />
            </>
          )}

          {error && <p className={styles.errorText}>{error}</p>}
        </>
      )}
    </div>
  );
};

