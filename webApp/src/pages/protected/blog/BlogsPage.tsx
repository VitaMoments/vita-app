import React, { useMemo, useState } from "react";
import { useNavigate } from "react-router-dom";

import Tabs, { type TabItem } from "../../../components/tabs/Tabs";
import { Button } from "../../../components/buttons/Button";
import FeedPageLayout from "../../../components/page/feed-layout/FeedPageLayout";
import layoutStyles from "../../../components/page/feed-layout/FeedPageLayout.module.css";

import { CreateBlogInput } from "./CreateBlogInput";
import BlogsTab from "./BlogsTab";

import type { FeedCategory } from "../../../data/types";
import { FEED_CATEGORY_META } from "../../../data/ui/feedCategoryMeta";

type TabLabel = "FOLLOWING" | "DISCOVER" | "MY_BLOGS" | "NEW";
type Sort = "NEWEST" | "OLDEST";

const BlogsPage: React.FC = () => {
  const navigate = useNavigate();

  const [activeTab, setActiveTab] = useState<TabLabel>("FOLLOWING");
  const [query, setQuery] = useState("");
  const [activeCategory, setActiveCategory] = useState<FeedCategory | null>(null);
  const [sort, setSort] = useState<Sort>("NEWEST");

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

  const tabs: TabItem[] = useMemo(
    () => [
      {
        value: "FOLLOWING",
        label: "Following",
        content: (
          <BlogsTab
            isActive={activeTab === "FOLLOWING"}
            scope="FOLLOWING"
            query={query}
            activeCategory={activeCategory}
            sort={sort}
            onOpenBlog={(slug) => navigate(`/blogs/${slug}`)}
          />
        ),
      },
      {
        value: "MY_BLOGS",
        label: "My blogs",
        content: (
          <BlogsTab
            isActive={activeTab === "MY_BLOGS"}
            scope="MY_BLOGS"
            query={query}
            activeCategory={activeCategory}
            sort={sort}
            onOpenBlog={(slug) => navigate(`/blogs/${slug}`)}
          />
        ),
      },
      {
        value: "DISCOVER",
        label: "Discover",
        content: (
          <BlogsTab
            isActive={activeTab === "DISCOVER"}
            scope="DISCOVER"
            query={query}
            activeCategory={activeCategory}
            sort={sort}
            onOpenBlog={(slug) => navigate(`/blogs/${slug}`)}
          />
        ),
      },
      {
        value: "NEW",
        label: "New",
        content: (
          <CreateBlogInput
            onCreated={(idOrSlug) => navigate(`/blogs/${idOrSlug}`)}
          />
        ),
      },
    ],
    [activeTab, query, activeCategory, sort, navigate]
  );

  return (
    <FeedPageLayout
      searchValue={query}
      onSearchChange={setQuery}
      searchPlaceholder="Search blogs..."
      categories={categoryItems}
      activeCategory={activeCategory}
      onToggleCategory={(category) =>
        setActiveCategory((prev) =>
          prev === category ? null : (category as FeedCategory)
        )
      }
      activeFilters={
        <>
          {activeCategory ? (
            <button
              type="button"
              className={layoutStyles.chip}
              onClick={() => setActiveCategory(null)}
              title="Clear category"
            >
              {FEED_CATEGORY_META[activeCategory]?.label ?? activeCategory} ×
            </button>
          ) : null}

          {query.trim() ? (
            <button
              type="button"
              className={layoutStyles.chip}
              onClick={() => setQuery("")}
              title="Clear search"
            >
              Search: {query.trim()} ×
            </button>
          ) : null}
        </>
      }
      sidebarActions={
        <>
          <Button onClick={() => setActiveTab("NEW")}>Create</Button>
          <Button
            onClick={() =>
              setSort((s) => (s === "NEWEST" ? "OLDEST" : "NEWEST"))
            }
          >
            Sort: {sort === "NEWEST" ? "Newest" : "Oldest"}
          </Button>
        </>
      }
    >
      <Tabs
        tabs={tabs}
        value={activeTab}
        onChange={(v) => setActiveTab(v as TabLabel)}
        ariaLabel="blogs tabs"
      />
    </FeedPageLayout>
  );
};

export default BlogsPage;