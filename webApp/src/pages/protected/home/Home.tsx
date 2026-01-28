import React, { useEffect, useMemo, useState } from "react";

import { TimelineService } from "../../../api/service/TimelineService";

import type { FeedItem, TimeLineFeed } from "../../../data/types";

import { TimelinePostCard } from "../../../components/timeline/TimelinePostCard";
import { TimelineInput } from "../../../components/input/TimelineInput";
import { TimelineButtonBar } from "../../../components/buttons/TimelineButtonBar";
import {
  ErrorBanner,
  WarningBanner,
  InfoBanner,
} from "../../../components/banner/InfoBanner";

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
  const [warning, setWarning] = useState<string | null>(null);
  const [info, setInfo] = useState<string | null>(null);

  const labels = useMemo(() => TABS.map((t) => t.label), []);

  const loadPosts = async (index: number) => {
    const feed = TABS[index].feed;

    try {
      setLoading(true);
      setError(null);

      const data = await TimelineService.getTimeline({
        feed,
        offset: 0,
        limit: LIMIT,
      });

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
    <div className="timeline">
      <ErrorBanner message={error} />
      <WarningBanner message={warning} />
      <InfoBanner message={info} />

      <TimelineInput
        onPosted={() => handleTabChange(activeIndex)}
        onError={(msg) => setError(msg)}
        onClearError={() => setError(null)}
      />

      <TimelineButtonBar
        activeIndex={activeIndex}
        onChange={handleTabChange}
        labels={labels}
      />

      {loading && <p>Loading…</p>}

      <ul className="timeline-list">
        {items.map((item) => (
          <li key={item.uuid}>
            <TimelinePostCard item={item} />
          </li>
        ))}
      </ul>
    </div>
  );
};

export default Home;
