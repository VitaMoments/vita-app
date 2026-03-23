import React from "react";
import styles from "./TripleColumnPageLayout.module.css";

type Props = {
  leftSidebar?: React.ReactNode;
  rightSidebar?: React.ReactNode;
  children?: React.ReactNode;
};

const TripleColumnPageLayout: React.FC<Props> = ({
  leftSidebar,
  rightSidebar,
  children,
}) => {
  const hasLeft = !!leftSidebar;
  const hasRight = !!rightSidebar;

  const layoutClassName = [
    styles.layout,
    hasLeft ? styles.hasLeft : "",
    hasRight ? styles.hasRight : "",
  ]
    .filter(Boolean)
    .join(" ");

  return (
    <div className={styles.page}>
      <div className={styles.content}>
        <div className={layoutClassName}>
          {hasLeft ? (
            <aside className={styles.sidebar}>
              {leftSidebar}
            </aside>
          ) : null}

          <main className={styles.main}>
            <div className={styles.mainCard}>{children}</div>
          </main>

          {hasRight ? (
            <aside className={styles.sidebar}>
              <div className={styles.sidebarCard}>{rightSidebar}</div>
            </aside>
          ) : null}
        </div>
      </div>
    </div>
  );
};

export default TripleColumnPageLayout;