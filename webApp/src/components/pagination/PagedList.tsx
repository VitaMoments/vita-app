import React, { useCallback, useEffect, useMemo, useState } from "react";
import { Pagination } from "./Pagination";
import type { PagedResult } from "../../data/types"
import styles from "./PagedList.module.css"


export type PagedListCtx<T> = {
  items: T[];
  total: number;
  limit: number;
  offset: number;

  currentPage: number;
  totalPages: number;

  loading: boolean;
  error: string | null;

  goToPage: (page: number) => void;
  setOffset: React.Dispatch<React.SetStateAction<number>>;
  refresh: () => void;
};

export type PagedListProps<T> = {
  limit?: number;
  initialOffset?: number;

  /** Verander deze key om pagina te resetten (bv. query wijzigt) */
  resetKey?: string | number;

  /** Haal data op voor (limit, offset) */
  fetchPage: (args: { limit: number; offset: number; signal?: AbortSignal }) => Promise<PagedResult<T>>;

  /** Render 1 item */
  renderItem: (item: T) => React.ReactNode;

  /** Key extractor */
  getKey: (item: T) => string | number;

  /** Optionele UI boven de lijst (bv. search input) */
  controls?: (ctx: PagedListCtx<T>) => React.ReactNode;

  /** Optionele empty state */
  empty?: React.ReactNode;

  /** Wrapper classnames */
  className?: string;
  listClassName?: string;

  /** Pagination opties */
  showGoTo?: boolean;
  paginationLabels?: {
    prev?: string;
    next?: string;
    go?: string;
    pagePlaceholder?: string;
  };
};

export function PagedList<T>(props: PagedListProps<T>) {
  const limit = props.limit ?? 20;
  const initialOffset = props.initialOffset ?? 0;

  const [items, setItems] = useState<T[]>([]);
  const [total, setTotal] = useState(0);
  const [offset, setOffset] = useState(initialOffset);

  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);

  const [refreshToken, setRefreshToken] = useState(0);

  // reset naar pagina 1 als resetKey verandert
  useEffect(() => {
    setOffset(0);
  }, [props.resetKey]);

  const currentPage = Math.floor(offset / limit) + 1;
  const totalPages = Math.max(1, Math.ceil(total / limit));

  const goToPage = useCallback(
    (page: number) => {
      const clamped = Math.min(Math.max(1, page), totalPages);
      setOffset((clamped - 1) * limit);
    },
    [limit, totalPages]
  );

  const refresh = useCallback(() => {
    setRefreshToken((x) => x + 1);
  }, []);

  const ctx: PagedListCtx<T> = useMemo(
    () => ({
      items,
      total,
      limit,
      offset,
      currentPage,
      totalPages,
      loading,
      error,
      goToPage,
      setOffset,
      refresh,
    }),
    [items, total, limit, offset, currentPage, totalPages, loading, error, goToPage, refresh]
  );

  useEffect(() => {
    const controller = new AbortController();

    (async () => {
      setLoading(true);
      setError(null);

      try {
        const page = await props.fetchPage({
          limit,
          offset,
          signal: controller.signal,
        });

        if (controller.signal.aborted) return;

        setItems(page.items);
        setTotal(page.total);
      } catch (e: any) {
        if (controller.signal.aborted) return;
        setError(e?.message ?? "Failed to load data");
      } finally {
        if (controller.signal.aborted) return;
        setLoading(false);
      }
    })();

    return () => controller.abort();
  }, [props.fetchPage, limit, offset, refreshToken, props.resetKey]);

  const isEmpty = !loading && !error && items.length === 0;

  return (
    <div className={props.className}>
      {props.controls?.(ctx)}

      {loading && <p>Loading…</p>}
      {error && <p>{error}</p>}

      {isEmpty ? (
        props.empty ?? <p>No results</p>
      ) : (
        <ul className={props.listClassName}>
          {items.map((item) => (
            <li key={props.getKey(item)}>{props.renderItem(item)}</li>
          ))}
        </ul>
      )}

      <Pagination
        currentPage={currentPage}
        totalPages={totalPages}
        disabled={loading}
        onPageChange={goToPage}
        showGoTo={props.showGoTo ?? true}
        labels={props.paginationLabels}
      />
    </div>
  );
}
