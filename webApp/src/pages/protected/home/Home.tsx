import React, { useEffect, useMemo, useState } from "react";
import { useAuth } from "../../../auth/AuthContext";

import TripleColumnPageLayout from "../../../components/page/base_pages/TripleColumnPageLayout";

import { LeftSideBar } from "./LeftSideBar"

// oud

import { TimelineService } from "../../../api/service/TimelineService";

import type { FeedItem, TimeLineFeed } from "../../../data/types";

import type { FeedCategory } from "../../../data/types";
import { FEED_CATEGORY_META } from "../../../data/ui/feedCategoryMeta";

import { Button } from "../../../components/buttons/Button";
import { TimelineItemCard } from "../../../components/timeline/TimelineItemCard";
import { FeedEditor } from "../../../components/editor/feed-editor/FeedEditor"
import { TimelineButtonBar } from "../../../components/buttons/TimelineButtonBar";
import {
  ErrorBanner,
  WarningBanner,
  InfoBanner,
} from "../../../components/banner/InfoBanner";

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

  const [query, setQuery] = useState("");
  const [activeCategory, setActiveCategory] = useState<FeedCategory | null>(null);

  const [error, setError] = useState<string | null>(null);
  const [warning, setWarning] = useState<string | null>(null);
  const [info, setInfo] = useState<string | null>(null);

  const { user } = useAuth();
  const myUserId = user?.uuid;

  const labels = useMemo(() => TABS.map((t) => t.label), []);

  const allCategories = useMemo(
    () => Object.keys(FEED_CATEGORY_META) as FeedCategory[],
    []
  );

  const categoryItems = useMemo(
    () =>
      allCategories.map((category) => ({
        value: category,
        label: FEED_CATEGORY_META[category]?.label ?? category,
      })),
    [allCategories]
  );

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

    const filteredItems = useMemo(() => {
      let result = [...items];

      if (query.trim()) {
        const q = query.trim().toLowerCase();

        result = result.filter((item) => {
          const title = item.title?.toLowerCase?.() ?? "";
          const text = item.text?.toLowerCase?.() ?? "";
          const author =
            item.author?.displayName?.toLowerCase?.() ??
            item.author?.email?.toLowerCase?.() ??
            "";

          return title.includes(q) || text.includes(q) || author.includes(q);
        });
      }

      if (activeCategory) {
        result = result.filter((item) =>
          Array.isArray(item.categories) && item.categories.includes(activeCategory)
        );
      }

      return result;
    }, [items, query, activeCategory]);

    return (
        <TripleColumnPageLayout
            leftSidebar={
                <LeftSideBar user={user} />
                }
        >
          <div>
            <FeedEditor
                placeholder="Share something with your timeline..."
                showCategories={true}
                showDraftButton={false}
                publishLabel="Post"
                onError={(msg) => setError(msg)}
                onClearError={() => setError(null)}
                onSubmit={
                    async ({ categories, document }) => {
                        await TimelineService.createPost({
                        feed: TABS[activeIndex].feed,
                        categories,
                        document,
                    });
                    await loadPosts(activeIndex);
                    setInfo("Post geplaatst.");
                }}/>

                {loading && <p className={styles.stateText}>Loading…</p>}

                {!loading && filteredItems.length === 0 ? (
                  <div className={styles.emptyState}>
                    <h3 className={styles.emptyTitle}>No timeline items found</h3>
                    <p className={styles.emptyText}>
                      Er zijn geen timeline items gevonden voor deze selectie.
                    </p>
                  </div>
                ) : (
                  <ul className={styles.timelineList}>
                    {filteredItems.map((item) => {
                      const isUserItem = item.author.uuid === myUserId;
                      return (
                        <li key={item.uuid} className={styles.timelineListItem}>
                          <TimelineItemCard item={item} isUserItem={isUserItem} />
                        </li>
                      );
                    })}
                  </ul>
                )}
          </div>
        </TripleColumnPageLayout>
        );

//   return (
//     <FeedPageLayout
//       searchValue={query}
//       onSearchChange={setQuery}
//       searchPlaceholder="Search timeline..."
//       categories={categoryItems}
//       activeCategory={activeCategory}
//       onToggleCategory={(value) =>
//         setActiveCategory((prev) => (prev === value ? null : (value as FeedCategory)))
//       }
//       sidebarActions={
//         <>
//           <Button onClick={() => void loadPosts(activeIndex)}>Refresh</Button>
//         </>
//       }
//       activeFilters={
//         <>
//           {activeCategory ? (
//             <button
//               type="button"
//               className={layoutStyles.chip}
//               onClick={() => setActiveCategory(null)}
//               title="Clear category"
//             >
//               {activeCategory} ×
//             </button>
//           ) : null}
//
//           {query.trim() ? (
//             <button
//               type="button"
//               className={layoutStyles.chip}
//               onClick={() => setQuery("")}
//               title="Clear search"
//             >
//               Search: {query.trim()} ×
//             </button>
//           ) : null}
//         </>
//       }
//     >
//       <div className={styles.mainContent}>
//         <ErrorBanner message={error} />
//         <WarningBanner message={warning} />
//         <InfoBanner message={info} />
//
//         <div className={styles.inputCard}>
//          <FeedEditor
//            placeholder="Share something with your timeline..."
//            showCategories={true}
//            showDraftButton={false}
//            publishLabel="Post"
//            onError={(msg) => setError(msg)}
//            onClearError={() => setError(null)}
//            onSubmit={async ({ categories, document }) => {
//              await TimelineService.createPost({
//                feed: TABS[activeIndex].feed,
//                categories,
//                document,
//              });
//
//              await loadPosts(activeIndex);
//              setInfo("Post geplaatst.");
//            }}
//          />
//         </div>
//
//         <div className={styles.tabsCard}>
//           <TimelineButtonBar
//             activeIndex={activeIndex}
//             onChange={handleTabChange}
//             labels={labels}
//           />
//         </div>
//
//         {loading && <p className={styles.stateText}>Loading…</p>}
//
//         {!loading && filteredItems.length === 0 ? (
//           <div className={styles.emptyState}>
//             <h3 className={styles.emptyTitle}>No timeline items found</h3>
//             <p className={styles.emptyText}>
//               Er zijn geen timeline items gevonden voor deze selectie.
//             </p>
//           </div>
//         ) : (
//           <ul className={styles.timelineList}>
//             {filteredItems.map((item) => {
//               const isUserItem = item.author.uuid === myUserId;
//               return (
//                 <li key={item.uuid} className={styles.timelineListItem}>
//                   <TimelineItemCard item={item} isUserItem={isUserItem} />
//                 </li>
//               );
//             })}
//           </ul>
//         )}
//       </div>
//     </FeedPageLayout>
//   );
};

export default Home;