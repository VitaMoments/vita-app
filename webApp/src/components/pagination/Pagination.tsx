// File: src/components/pagination/Pagination.tsx
import React, { useMemo, useState, useCallback } from "react";
import styles from "./Pagination.module.css";

type PageButton = number | "…";

function buildPageButtons(current: number, totalPages: number, windowSize = 2): PageButton[] {
  if (totalPages <= 1) return [1];

  const pages: PageButton[] = [];
  const first = 1;
  const last = totalPages;

  const start = Math.max(2, current - windowSize);
  const end = Math.min(last - 1, current + windowSize);

  pages.push(first);

  if (start > 2) pages.push("…");
  for (let p = start; p <= end; p++) pages.push(p);
  if (end < last - 1) pages.push("…");

  pages.push(last);

  return pages;
}

export type PaginationProps = {
  currentPage: number;          // 1-based
  totalPages: number;           // >= 1
  disabled?: boolean;

  onPageChange: (page: number) => void;

  showGoTo?: boolean;
  labels?: {
    prev?: string;
    next?: string;
    go?: string;
    pagePlaceholder?: string;
  };
};

export const Pagination: React.FC<PaginationProps> = ({
  currentPage,
  totalPages,
  disabled = false,
  onPageChange,
  showGoTo = true,
  labels = {},
}) => {
  const [pageInput, setPageInput] = useState("");

  const pageButtons = useMemo(
    () => buildPageButtons(currentPage, totalPages, 2),
    [currentPage, totalPages]
  );

  const canPrev = currentPage > 1;
  const canNext = currentPage < totalPages;

  const goPrev = useCallback(() => {
    if (!canPrev || disabled) return;
    onPageChange(currentPage - 1);
  }, [canPrev, disabled, onPageChange, currentPage]);

  const goNext = useCallback(() => {
    if (!canNext || disabled) return;
    onPageChange(currentPage + 1);
  }, [canNext, disabled, onPageChange, currentPage]);

  const submitGoTo = useCallback(() => {
    if (disabled) return;
    const p = Number(pageInput);
    if (!Number.isFinite(p)) return;
    const clamped = Math.min(Math.max(1, p), totalPages);
    onPageChange(clamped);
    setPageInput("");
  }, [disabled, pageInput, totalPages, onPageChange]);

  return (
    <div className={styles.paginationBar}>
      <button className={styles.pageBtn} onClick={goPrev} disabled={disabled || !canPrev}>
        {labels.prev ?? "← Terug"}
      </button>

      <div className={styles.pageNumbers}>
        {pageButtons.map((p, idx) =>
          p === "…" ? (
            <span key={`ellipsis-${idx}`} className={styles.ellipsis}>…</span>
          ) : (
            <button
              key={p}
              className={`${styles.pageBtn} ${p === currentPage ? styles.pageBtnActive : ""}`}
              onClick={() => onPageChange(p)}
              disabled={disabled}
            >
              {p}
            </button>
          )
        )}
      </div>

      {showGoTo && (
        <div className={styles.goto}>
          <input
            className={styles.gotoInput}
            value={pageInput}
            onChange={(e) => setPageInput(e.target.value)}
            onKeyDown={(e) => { if (e.key === "Enter") submitGoTo(); }}
            placeholder={labels.pagePlaceholder ?? "Page"}
            inputMode="numeric"
            disabled={disabled}
          />
          <button className={styles.pageBtn} onClick={submitGoTo} disabled={disabled}>
            {labels.go ?? "Go"}
          </button>
        </div>
      )}

      <button className={styles.pageBtn} onClick={goNext} disabled={disabled || !canNext}>
        {labels.next ?? "Volgende →"}
      </button>
    </div>
  );
};
