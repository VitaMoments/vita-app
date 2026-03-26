import React, { useEffect, useMemo, useState } from "react";
import { useAuth } from "../../../auth/AuthContext";

import TripleColumnPageLayout from "../../../components/page/base_pages/TripleColumnPageLayout";
import { LeftSideBar } from "./LeftSideBar";
import { DailyQuestionInput } from "../../../composables/input/DailyQuestionInput";
import { TimelineService } from "../../../api/service/TimelineService";
import { FeedEditor } from "../../../components/editor/feed-editor/FeedEditor";

import type { FeedItem, TimeLineFeed } from "../../../data/types";

import styles from "./Home.module.css";

const LIMIT = 20;

const TABS = [
  { label: "Following", feed: "FRIENDS" },
  { label: "Self", feed: "SELF" },
  { label: "Groups", feed: "GROUPS" },
  { label: "Discovery", feed: "DISCOVERY" },
] as const satisfies ReadonlyArray<{ label: string; feed: TimeLineFeed }>;

const Home: React.FC = () => {
  const [activeIndex, setActiveIndex] = useState(0);
  const [items, setItems] = useState<FeedItem.TIMELINEITEM[]>([]);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);
  const [info, setInfo] = useState<string | null>(null);

  // Daily question panel: open by default on first visit; hidden after first submit
  const [dailyQuestionOpen, setDailyQuestionOpen] = useState(true);

  const { user } = useAuth();

  const labels = useMemo(() => TABS.map((t) => t.label), []);

  const loadPosts = async (index: number) => {
    const feed = TABS[index].feed;
    try {
      setLoading(true);
      setError(null);
      const data = await TimelineService.getTimeline({ feed, offset: 0, limit: LIMIT });
      setItems(data);
    } catch (e) {
      console.error(e);
      setError("Het ophalen van posts is mislukt.");
    } finally {
      setLoading(false);
    }
  };

  const handleTabChange = (index: number) => {
    setActiveIndex(index);
    void loadPosts(index);
  };

  useEffect(() => {
    void loadPosts(0);
  }, []);

  return (
    <TripleColumnPageLayout
      leftSidebar={
        user ? (
          <LeftSideBar
            user={user}
            activitiesCount={0}
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
          onSubmit={async ({ categories, document }) => {
            await TimelineService.postContent({
              feed: TABS[activeIndex].feed,
              categories,
              document,
            });
            await loadPosts(activeIndex);
            setInfo("Post geplaatst.");
          }}
        />

        {loading && <p className={styles.stateText}>Loading…</p>}
        {error && <p className={styles.stateText}>{error}</p>}
        {info && <p className={styles.stateText}>{info}</p>}
      </div>
    </TripleColumnPageLayout>
  );
};

export default Home;

