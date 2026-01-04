# Change: Add Mobile-Friendly Counter Dashboard

## Why
The current counter interface uses a table layout optimized for desktop, with small action buttons. Users need a mobile-friendly dashboard with large, touch-optimized tiles that allow quick counter increments (tap or swipe up), decrements (swipe down), and access to details/editing via long press.

## What Changes
- Add a new dashboard view with large, touch-friendly counter tiles
- Implement tap-to-increment interaction on tiles
- Implement swipe-up-to-increment interaction on tiles (alternative to tap)
- Implement swipe-down-to-decrement interaction on tiles
- Implement long-press-to-edit navigation on tiles
- Preserve the existing table view as an alternative (or replace it based on preference)
- Add responsive CSS for mobile-first design with tiles
- Add gesture detection logic to disambiguate tap, swipe up, swipe down, and long press
- Ensure touch event handling works across mobile browsers (iOS Safari, Chrome, etc.)

## Impact
- Affected specs: `counters`
- Affected code:
  - `src/main/resources/templates/counters/list.html` (new dashboard layout)
  - `src/main/resources/static/app.css` (tile styles and mobile-first CSS)
  - `src/main/kotlin/com/tracker/counters/web/CounterWebController.kt` (potentially add dashboard route or modify existing list view)
  - May require JavaScript for long-press detection and tap handling

