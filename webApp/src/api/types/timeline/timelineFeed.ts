export type TimelineFeed = "FRIENDS" | "SELF" | "DISCOVERY" | "GROUPS"

export const toApiFeedParam = (feed: TimeLineFeed): string =>
  feed.toLowerCase();
