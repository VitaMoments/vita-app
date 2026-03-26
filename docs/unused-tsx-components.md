# Unused TSX Components Audit

Date: 2026-03-26
Scope: `webApp/src/components/**/*.tsx`
Method: import-graph reachability from `webApp/src/index.tsx` including static imports and dynamic `import(...)`.

## Removed component files

- `webApp/src/components/banner/InfoBanner.tsx`
- `webApp/src/components/buttons/TimelineButton.tsx`
- `webApp/src/components/buttons/TimelineButtonBar.tsx`
- `webApp/src/components/input/TimelineInput.tsx`
- `webApp/src/components/page/feed-layout/FeedPageLayout.tsx`
- `webApp/src/components/user_cards/UserCard.tsx`
- `webApp/src/components/user_cards/UserCardIncomingRequest.tsx`

## Notes

- These files were unreachable from the app entrypoint and removed.
- Matching CSS modules were also removed when they were only referenced by the removed components.
- Verification passed with `cd webApp && npm run build`.


