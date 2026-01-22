// BlogsPage.tsx
import React, { useEffect, useMemo, useState } from "react";
import { useNavigate } from "react-router-dom";
import styles from "./BlogsPage.module.css";

import { CreateBlogInput } from "./CreateBlogInput"

// 👉 pas deze imports aan naar jouw projectstructuur
import type { BlogItemDto } from "../feed/feedDto";
import Tabs, { TabItem }  from "../../../components/tabs/Tabs"
import Input  from "../../../components/input/Input"
import { Button } from "../../../components/buttons/Button"

import BlogsTab from "./BlogsTab"

type Category = { name: string; count: number };
type TabLabel = "FOLLOWING" | "DISCOVER" | "MY_BLOGS" | "NEW";
type Sort = "NEWEST" | "OLDEST";


// ===== Helpers =====
const formatDate = (iso: string) => {
  const d = new Date(iso);
  return d.toLocaleDateString(undefined, { year: "numeric", month: "short", day: "2-digit" });
};

const isPublished = (b: BlogItemDto) => b.status === "PUBLISHED";

function matchesQuery(b: BlogItemDto, q: string) {
  const hay = `${b.title} ${b.subtitle ?? ""} ${b.slug}`.toLowerCase();
  return hay.includes(q);
}

const Chip: React.FC<{ label: string; onClear: () => void }> = ({ label, onClear }) => (
  <span className={styles.chip}>
    {label}
    <button
      type="button"
      onClick={onClear}
      className={styles.chipClear}
      aria-label="Clear filter"
      title="Clear"
    >
      ×
    </button>
  </span>
);

const BlogCard: React.FC<{ blog: BlogItemDto; onOpen: (slug: string) => void }> = ({
  blog,
  onOpen,
}) => {
  const date = blog.publishedAt ?? blog.createdAt;

  return (
    <article className={styles.blogCard}>
      <div className={styles.blogCardRow}>
        <div className={styles.blogCardMedia}>
          {blog.coverImageUrl ? (
            <img
              src={blog.coverImageUrl}
              alt={blog.coverImageAlt ?? blog.title}
              className={styles.blogCardImage}
              loading="lazy"
            />
          ) : (
            <div className={styles.blogCardImagePlaceholder} />
          )}
        </div>

        <div className={styles.blogCardBody}>
          <h3 className={styles.blogCardTitle}>{blog.title}</h3>

          <div className={styles.blogCardMeta}>
            <div className={styles.blogCardAuthor}>
              <span className={styles.avatar}>
                {blog.author.avatarUrl ? (
                  <img src={blog.author.avatarUrl} alt="" className={styles.avatarImg} />
                ) : null}
              </span>
              <span className={styles.authorName}>{blog.author.displayName}</span>
            </div>

            <span className={styles.dot}>·</span>
            <span>{formatDate(date)}</span>

            {"readingTimeMinutes" in blog && (blog as any).readingTimeMinutes ? (
              <>
                <span className={styles.dot}>·</span>
                <span>{(blog as any).readingTimeMinutes} min</span>
              </>
            ) : null}
          </div>

          {"excerpt" in blog && ((blog as any).excerpt || blog.subtitle) ? (
            <p className={styles.blogCardExcerpt}>{(blog as any).excerpt ?? blog.subtitle}</p>
          ) : blog.subtitle ? (
            <p className={styles.blogCardExcerpt}>{blog.subtitle}</p>
          ) : null}

          <div className={styles.blogCardFooter}>
            <div className={styles.badges}>
              {(blog.categories ?? []).slice(0, 2).map((c) => (
                <span key={c} className={styles.badgeCategory}>
                  {c}
                </span>
              ))}
              {!isPublished(blog) ? <span className={styles.badgeStatus}>{blog.status}</span> : null}
            </div>

            <button type="button" onClick={() => onOpen(blog.slug)} className={styles.readMore}>
              Read More <span className={styles.readMoreArrow} aria-hidden>→</span>
            </button>
          </div>
        </div>
      </div>
    </article>
  );
};

// ===== Page =====
const BlogsPage: React.FC = () => {
  // TODO: vervang met echte user id uit jouw auth store
  const meUserId = "me";
  const [activeTab, setActiveTab] = useState<TabLabel>("FOLLOWING")
  const navigate = useNavigate()

  // TODO: vervang met echte API data per tab
  const [followingBlogs, setFollowingBlogs] = useState<BlogItemDto[]>([]);
  const [discoverBlogs, setDiscoverBlogs] = useState<BlogItemDto[]>([]);
  const [myBlogs, setMyBlogs] = useState<BlogItemDto[]>([]);

  // TODO: vervang met echte API categorie counts
  const [categories, setCategories] = useState<Category[]>([
    { name: "MENTAL", count: 0 },
    { name: "PHYSICAL", count: 0 },
    { name: "FOOD", count: 0 },
    { name: "LIFESTYLE", count: 0 },
  ]);

    const tabs: TabItem<TabLabel>[] = useMemo(
    () => [
            { value: "FOLLOWING", label: "Following", content: <BlogsTab /> },
            { value: "MY_BLOGS", label: "My blogs", content: <div>My blogs</div>  },
            { value: "DISCOVER", label: "Discover", content: <div>Discover</div>  },
            { value: "NEW", label: "New", content: <CreateBlogInput />  },
        ],
        [activeTab]
    );

  // UI state
  const [tab, setTab] = useState<Tab>("FOLLOWING");
  const [query, setQuery] = useState("");
  const [activeCategory, setActiveCategory] = useState<string | null>(null);
  const [sort, setSort] = useState<Sort>("NEWEST");
  const [page, setPage] = useState(1);

  // TODO: laad je API hier (voorbeeld)
  useEffect(() => {
    // setFollowingBlogs(...)
    // setDiscoverBlogs(...)
    // setMyBlogs(...)
    // setCategories(...)
  }, []);

  const onCreate = () => {
    // TODO: route naar editor (bijv. /blogs/new)
    console.log("create blog");
  };

  const onOpenBlog = (slug: string) => {
    // TODO: route naar detail (bijv. /blogs/:slug)
    console.log("open blog:", slug);
  };

  const source = tab === "FOLLOWING" ? followingBlogs : tab === "DISCOVER" ? discoverBlogs : myBlogs;

  const filtered = useMemo(() => {
    const q = query.trim().toLowerCase();
    let list = [...source];

    // buiten My Blogs alleen PUBLISHED tonen
    if (tab !== "MY_BLOGS") list = list.filter((b) => b.status === "PUBLISHED");

    if (activeCategory) list = list.filter((b) => (b.categories ?? []).includes(activeCategory));
    if (q) list = list.filter((b) => matchesQuery(b, q));

    list.sort((a, b) => {
      const aT = new Date((a.publishedAt ?? a.createdAt) as any).getTime();
      const bT = new Date((b.publishedAt ?? b.createdAt) as any).getTime();
      return sort === "NEWEST" ? bT - aT : aT - bT;
    });

    return list;
  }, [source, tab, query, activeCategory, sort]);

  const pageSize = 6;
  const totalPages = Math.max(1, Math.ceil(filtered.length / pageSize));
  const paged = filtered.slice((page - 1) * pageSize, page * pageSize);

  useEffect(() => setPage(1), [tab, query, activeCategory, sort]);

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
               placeholder="Search..."/>
               <Button onClick={() => navigate("/blogs/create")}>Create</Button>
            </div>

            <div className={styles.leftPaneCard}>
              <h3 className={styles.leftPaneHeader}>Categories</h3>
              <div className={styles.categoryList}>
                {categories.map((c) => {
                  const active = activeCategory === c.name;
                  return (
                    <button
                      key={c.name}
                      type="button"
                      onClick={() => setActiveCategory(active ? null : c.name)}
                      className={`${styles.categoryRow} ${active ? styles.categoryRowActive : ""}`}
                    >
                      <span className={styles.categoryName}>{c.name}</span>
                      <span className={styles.categoryCount}>{c.count}</span>
                    </button>
                  );
                })}
              </div>
            </div>
          </aside>
            <section className={styles.main}>
                <div className={styles.leftPaneCard}>
                    <Tabs
                    tabs={tabs}
                    value={activeTab}
                    onChange={setActiveTab}
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