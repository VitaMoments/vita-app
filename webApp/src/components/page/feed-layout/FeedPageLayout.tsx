import React from "react";
import Input from "../../input/Input";
import styles from "./FeedPageLayout.module.css";

export type FeedLayoutCategoryItem = {
  value: string;
  label: string;
  count?: number | null;
};

type Props = {
  searchValue: string;
  onSearchChange: (value: string) => void;
  searchPlaceholder?: string;

  sidebarActions?: React.ReactNode;

  categories?: FeedLayoutCategoryItem[];
  activeCategory?: string | null;
  onToggleCategory?: (category: string) => void;
  categoriesTitle?: string;

  activeFilters?: React.ReactNode;

  sidebarTopExtra?: React.ReactNode;
  sidebarBottomExtra?: React.ReactNode;

  children: React.ReactNode;
};

const FeedPageLayout: React.FC<Props> = ({
  searchValue,
  onSearchChange,
  searchPlaceholder = "Search...",
  sidebarActions,
  categories = [],
  activeCategory = null,
  onToggleCategory,
  categoriesTitle = "Categories",
  activeFilters,
  sidebarTopExtra,
  sidebarBottomExtra,
  children,
}) => {
  return (
    <div className={styles.page}>
      <div className={styles.content}>
        <div className={styles.layout}>
          <aside className={styles.leftPane}>
            <div className={styles.leftPaneCard}>
              {sidebarTopExtra}

              <Input
                name="query"
                value={searchValue}
                onChange={(e) => onSearchChange(e.target.value)}
                placeholder={searchPlaceholder}
              />

              {sidebarActions ? (
                <div className={styles.actionsRow}>{sidebarActions}</div>
              ) : null}
            </div>

            <div className={styles.leftPaneCard}>
              <h3 className={styles.leftPaneHeader}>{categoriesTitle}</h3>

              <div className={styles.categoryList}>
                {categories.map((category) => {
                  const active = activeCategory === category.value;

                  return (
                    <button
                      key={category.value}
                      type="button"
                      onClick={() => onToggleCategory?.(category.value)}
                      className={`${styles.categoryRow} ${
                        active ? styles.categoryRowActive : ""
                      }`}
                    >
                      <span className={styles.categoryName}>{category.label}</span>

                      {category.count != null ? (
                        <span className={styles.categoryCount}>{category.count}</span>
                      ) : (
                        <span className={styles.categoryCountEmpty} />
                      )}
                    </button>
                  );
                })}
              </div>

              {activeFilters ? (
                <div className={styles.activeFilters}>{activeFilters}</div>
              ) : null}

              {sidebarBottomExtra}
            </div>
          </aside>

          <section className={styles.main}>
            <div className={styles.mainCard}>{children}</div>
          </section>
        </div>
      </div>
    </div>
  );
};

export default FeedPageLayout;