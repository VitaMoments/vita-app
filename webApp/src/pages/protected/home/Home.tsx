import React, { useCallback, useEffect, useRef, useState } from "react";
import { useAuth } from "../../../auth/AuthContext";

import TripleColumnPageLayout from "../../../components/page/base_pages/TripleColumnPageLayout";
import { LeftSideBar } from "./LeftSideBar";
import { DailyQuestionInput } from "../../../composables/input/DailyQuestionInput";
import { TimelineService } from "../../../api/service/TimelineService";
import { FeedEditor } from "../../../components/editor/feed-editor/FeedEditor";
import { TimelineItemCard } from "../../../components/timeline/TimelineItemCard";
import { DailyQuestionItemCard } from "../../../components/timeline/DailyQuestionItemCard";

import type { FeedItem, TimeLineFeed } from "../../../data/types";
import { didAnswerToday } from "../../../data/ui/streakHelpers";
import { isFeedItemDailyquestionitem, isFeedItemTimelineitem } from "../../../data/types";

import styles from "./Home.module.css";

const LIMIT = 20;
const MAX_LOAD_MORE_AUTO_RETRIES = 2;
const LOAD_MORE_RETRY_BASE_DELAY_MS = 1200;

const TABS = [
  { label: "Following", feed: "FRIENDS" },
  { label: "Self", feed: "SELF" },
  { label: "Groups", feed: "GROUPS" },
  { label: "Discovery", feed: "DISCOVERY" },
] as const satisfies ReadonlyArray<{ label: string; feed: TimeLineFeed }>;

const Home: React.FC = () => {
  const [activeIndex, setActiveIndex] = useState(0);
  const [items, setItems] = useState<FeedItem[]>([]);
  const [loading, setLoading] = useState(false);
  const [loadingMore, setLoadingMore] = useState(false);
  const [hasMore, setHasMore] = useState(true);
  const [offset, setOffset] = useState(0);
  const [loadMoreError, setLoadMoreError] = useState<string | null>(null);
  const [error, setError] = useState<string | null>(null);
  const [info, setInfo] = useState<string | null>(null);
  const { user, streak } = useAuth();
  const myUserId = user?.uuid;

  const loadMoreRef = useRef<HTMLDivElement | null>(null);
  const loadMoreRetryCountRef = useRef(0);
  const loadMoreRetryTimerRef = useRef<number | null>(null);

  // Daily question panel: open by default on first visit; hidden after first submit
  const [dailyQuestionOpen, setDailyQuestionOpen] = useState(!didAnswerToday(streak));

  const clearLoadMoreRetryTimer = useCallback(() => {
    if (loadMoreRetryTimerRef.current != null) {
      window.clearTimeout(loadMoreRetryTimerRef.current);
      loadMoreRetryTimerRef.current = null;
    }
  }, []);

  const resetLoadMoreRetryState = useCallback(() => {
    clearLoadMoreRetryTimer();
    loadMoreRetryCountRef.current = 0;
    setLoadMoreError(null);
  }, [clearLoadMoreRetryTimer]);

  const mergeUnique = (prev: FeedItem[], next: FeedItem[]) => {
    if (next.length === 0) return prev;
    const seen = new Set(prev.map((item) => item.uuid));
    const uniqueNew = next.filter((item) => !seen.has(item.uuid));
    return [...prev, ...uniqueNew];
  };

  const loadInitialPosts = useCallback(async (index: number) => {
    const feed = TABS[index].feed;
    try {
      setLoading(true);
      setError(null);
      resetLoadMoreRetryState();

      const data = await TimelineService.getTimeline({
        feed,
        offset: 0,
        limit: LIMIT,
      });

      setItems(data);
      setOffset(data.length);
      setHasMore(data.length === LIMIT);
    } catch (e) {
      console.error(e);
      setError("Het ophalen van posts is mislukt.");
    } finally {
      setLoading(false);
    }
  }, [resetLoadMoreRetryState]);

  const loadMorePosts = useCallback(async (options?: { manualRetry?: boolean; isAutoRetry?: boolean }) => {
    const manualRetry = options?.manualRetry ?? false;
    const isAutoRetry = options?.isAutoRetry ?? false;

    if (loading || loadingMore || !hasMore) return;
    if (loadMoreError && !manualRetry && !isAutoRetry) return;

    const feed = TABS[activeIndex].feed;
    try {
      setLoadingMore(true);
      if (manualRetry) setLoadMoreError(null);

      const data = await TimelineService.getTimeline({
        feed,
        offset,
        limit: LIMIT,
      });

      setItems((prev) => mergeUnique(prev, data));
      setOffset((prev) => prev + data.length);
      setHasMore(data.length === LIMIT);
      clearLoadMoreRetryTimer();
      loadMoreRetryCountRef.current = 0;
      setLoadMoreError(null);
    } catch (e) {
      console.error(e);
      setLoadMoreError("Meer posts laden is mislukt.");

      const canAutoRetry = loadMoreRetryCountRef.current < MAX_LOAD_MORE_AUTO_RETRIES;
      if (!manualRetry && !isAutoRetry && canAutoRetry) {
        const attempt = loadMoreRetryCountRef.current;
        const delay = LOAD_MORE_RETRY_BASE_DELAY_MS * Math.pow(2, attempt);
        loadMoreRetryCountRef.current += 1;
        clearLoadMoreRetryTimer();
        loadMoreRetryTimerRef.current = window.setTimeout(() => {
          void loadMorePosts({ isAutoRetry: true });
        }, delay);
      }
    } finally {
      setLoadingMore(false);
    }
  }, [activeIndex, clearLoadMoreRetryTimer, hasMore, loadMoreError, loading, loadingMore, offset]);

  const handleTabChange = (index: number) => {
    if (index === activeIndex) return;
    setActiveIndex(index);
  };

  useEffect(() => {
    setItems([]);
    setOffset(0);
    setHasMore(true);
    setLoadMoreError(null);
    clearLoadMoreRetryTimer();
    loadMoreRetryCountRef.current = 0;
    void loadInitialPosts(activeIndex);
  }, [activeIndex, clearLoadMoreRetryTimer, loadInitialPosts]);

  useEffect(() => {
    const sentinel = loadMoreRef.current;
    if (!sentinel) return;

    const observer = new IntersectionObserver(
      (entries) => {
        const entry = entries[0];
        if (entry.isIntersecting) {
          void loadMorePosts();
        }
      },
      {
        root: null,
        rootMargin: "300px 0px",
        threshold: 0.01,
      }
    );

    observer.observe(sentinel);
    return () => observer.disconnect();
  }, [loadMorePosts]);

  useEffect(() => {
    return () => {
      clearLoadMoreRetryTimer();
    };
  }, [clearLoadMoreRetryTimer]);

  return (
    <TripleColumnPageLayout
      leftSidebar={
        user ? (
          <LeftSideBar
            user={user}
            streak={streak ?? null}
            activitiesCount={streak?.currentStreak ?? 0}
            onOpenDailyQuestion={() => setDailyQuestionOpen(true)}
          />
        ) : null
      }
    >
      <div>
        {/* Daily question panel – shown above the feed editor */}
        <DailyQuestionInput
          isOpen={dailyQuestionOpen}
          onClose={() => setDailyQuestionOpen(false)}
          onSubmitted={({ currentStreak, longestStreak }) => {
            setInfo(`Top! Streak: ${currentStreak} (record: ${longestStreak})`);
            setDailyQuestionOpen(false);
          }}
        />

        <FeedEditor
          placeholder="Share something with your timeline..."
          showCategories={true}
          showDraftButton={false}
          publishLabel="Post"
          onError={(msg) => setError(msg)}
          onClearError={() => setError(null)}
          onSubmit={async ({ document }) => {
            await TimelineService.postContent({ document });
            await loadInitialPosts(activeIndex);
            setInfo("Post geplaatst.");
          }}
        />

        <div className={styles.tabsCard}>
          <div className={styles.tabRow}>
            {TABS.map((tab, index) => (
              <button
                key={tab.feed}
                type="button"
                className={`${styles.tabButton} ${index === activeIndex ? styles.tabButtonActive : ""}`}
                onClick={() => handleTabChange(index)}
              >
                {tab.label}
              </button>
            ))}
          </div>
        </div>

        {loading && <p className={styles.stateText}>Loading…</p>}
        {error && <p className={styles.stateText}>{error}</p>}
        {info && <p className={styles.stateText}>{info}</p>}

        {!loading && !error && items.length === 0 && (
          <div className={styles.emptyState}>
            <h3 className={styles.emptyTitle}>Nog geen feed items</h3>
            <p className={styles.emptyText}>Er zijn nog geen posts beschikbaar voor deze timeline.</p>
          </div>
        )}

        {!loading && items.length > 0 && (
          <>
            <ul className={styles.timelineList}>
              {items.map((item) => (
                <li key={item.uuid} className={styles.timelineListItem}>
                  {isFeedItemTimelineitem(item) ? (
                    <TimelineItemCard item={item} isUserItem={item.author.uuid === myUserId} />
                  ) : isFeedItemDailyquestionitem(item) ? (
                    <DailyQuestionItemCard item={item} />
                  ) : null}
                </li>
              ))}
            </ul>

            <div ref={loadMoreRef} className={styles.loadMoreSentinel} aria-hidden="true" />

            {loadingMore && <p className={styles.loadMoreText}>Meer posts laden…</p>}
            {loadMoreError && hasMore && !loadingMore && (
              <div className={styles.loadMoreErrorBox}>
                <p className={styles.loadMoreErrorText}>{loadMoreError}</p>
                <button
                  type="button"
                  className={styles.retryButton}
                  onClick={() => {
                    void loadMorePosts({ manualRetry: true });
                  }}
                >
                  Probeer opnieuw
                </button>
              </div>
            )}
            {!hasMore && <p className={styles.loadMoreText}>Geen extra posts meer.</p>}
          </>
        )}
      </div>
    </TripleColumnPageLayout>
  );
};

export default Home;

