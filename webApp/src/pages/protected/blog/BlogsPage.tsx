// BlogsPage.tsx
import React, { useMemo, useState } from "react";
import { useNavigate } from "react-router-dom";
import styles from "./BlogsPage.module.css";

import Tabs, { type TabItem } from "../../../components/tabs/Tabs";
import Input from "../../../components/input/Input";
import { Button } from "../../../components/buttons/Button";

import { CreateBlogInput } from "./CreateBlogInput";
import BlogsTab from "./BlogsTab";

import type { BlogCategory } from "../../../data/types";
import { BLOG_CATEGORY_META } from "../../../data/ui/blogCategoryMeta";

type TabLabel = "FOLLOWING" | "DISCOVER" | "MY_BLOGS" | "NEW";
type Sort = "NEWEST" | "OLDEST";

const BlogsPage: React.FC = () => {
  const navigate = useNavigate();

  const [activeTab, setActiveTab] = useState<TabLabel>("FOLLOWING");

  // filters
  const [query, setQuery] = useState("");
  const [activeCategory, setActiveCategory] = useState<BlogCategory | null>(null);
  const [sort, setSort] = useState<Sort>("NEWEST");

  const allCategories = useMemo(
    () => Object.keys(BLOG_CATEGORY_META) as BlogCategory[],
    []
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
    <div className={styles.page}>
      <div className={styles.content}>
        <div className={styles.layout}>
          <aside className={styles.leftPane}>
            <div className={styles.leftPaneCard}>
              <h2 className={styles.leftPaneHeader}></h2>

              <Input
                name="query"
                value={query}
                onChange={(e) => setQuery(e.target.value)}
                placeholder="Search..."
              />

              <div style={{ display: "flex", gap: 8, marginTop: 10 }}>
                <Button onClick={() => setActiveTab("NEW")}>Create</Button>

                {/* optioneel sort UI (laat weg als je het niet wil) */}
                <Button
                  onClick={() => setSort((s) => (s === "NEWEST" ? "OLDEST" : "NEWEST"))}
                >
                  Sort: {sort === "NEWEST" ? "Newest" : "Oldest"}
                </Button>
              </div>
            </div>

            <div className={styles.leftPaneCard}>
              <h3 className={styles.leftPaneHeader}>Categories</h3>

              <div className={styles.categoryList}>
                {allCategories.map((c) => {
                  const active = activeCategory === c;
                  const label = BLOG_CATEGORY_META[c]?.label ?? c;

                  return (
                    <button
                      key={c}
                      type="button"
                      onClick={() => setActiveCategory(active ? null : c)}
                      className={`${styles.categoryRow} ${
                        active ? styles.categoryRowActive : ""
                      }`}
                    >
                      <span className={styles.categoryName}>{label}</span>
                      {/* als je later counts uit API wil, kun je hier een getal tonen */}
                      <span className={styles.categoryCount}> </span>
                    </button>
                  );
                })}
              </div>

              {(activeCategory || query.trim()) && (
                <div style={{ marginTop: 10, display: "flex", gap: 8, flexWrap: "wrap" }}>
                  {activeCategory ? (
                    <button
                      type="button"
                      className={styles.chip}
                      onClick={() => setActiveCategory(null)}
                      title="Clear category"
                    >
                      {(BLOG_CATEGORY_META[activeCategory]?.label ?? activeCategory)} ×
                    </button>
                  ) : null}

                  {query.trim() ? (
                    <button
                      type="button"
                      className={styles.chip}
                      onClick={() => setQuery("")}
                      title="Clear search"
                    >
                      Search: {query.trim()} ×
                    </button>
                  ) : null}
                </div>
              )}
            </div>
          </aside>

          <section className={styles.main}>
            <div className={styles.leftPaneCard}>
              <Tabs
                tabs={tabs}
                value={activeTab}
                onChange={(v) => setActiveTab(v as TabLabel)}
                ariaLabel="blogs tabs"
              />
            </div>
          </section>
        </div>
      </div>
    </div>
  );
};

export default BlogsPage;
