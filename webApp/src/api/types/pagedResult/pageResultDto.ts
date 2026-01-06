export type PagedResultDto<T> = {
  items: T[];
  limit: number;
  offset: number;
  total: number;
  hasMore: boolean;
  nextOffset: number | null;
};
