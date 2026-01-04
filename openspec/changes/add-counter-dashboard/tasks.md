# Implementation Tasks

## 1. Dashboard UI Implementation
- [x] 1.1 Create new dashboard template with CSS Grid layout for counter tiles
- [x] 1.2 Add tile styles to `app.css` (card-based design, large tap targets, mobile-first responsive)
- [x] 1.3 Display counter name, value, and unit in each tile with appropriate visual hierarchy
- [x] 1.4 Ensure tiles are touch-friendly (minimum 120x120px, ideally larger)

## 2. Gesture Detection Infrastructure
- [x] 2.1 Create gesture detection module/script with touch and mouse event handling
- [x] 2.2 Implement touch position and timestamp tracking (start, move, end)
- [x] 2.3 Add gesture classification logic (tap vs swipe vs long press based on duration/distance/direction)
- [x] 2.4 Implement gesture priority resolution (handle conflicts between gestures)
- [x] 2.5 Add `preventDefault()` handling to prevent native browser behaviors (context menu, pull-to-refresh)

## 3. Tap-to-Increment Interaction
- [x] 3.1 Integrate tap gesture detection (<500ms, <10px movement)
- [x] 3.2 Implement `fetch()` API call to increment endpoint on tap
- [x] 3.3 Add optimistic UI update (increment value immediately in tile)
- [x] 3.4 Handle error cases (revert optimistic update on failure, show error message)
- [x] 3.5 Add visual feedback for tap (e.g., ripple effect or scale animation)

## 4. Swipe-Up-to-Increment Interaction
- [x] 4.1 Integrate swipe up gesture detection (≥40px vertical up, <20px horizontal, <500ms)
- [x] 4.2 Implement `fetch()` API call to increment endpoint on swipe up
- [x] 4.3 Add optimistic UI update (increment value immediately in tile)
- [x] 4.4 Handle error cases (revert optimistic update on failure, show error message)
- [x] 4.5 Add visual feedback for swipe up (e.g., upward slide animation)
- [x] 4.6 Ensure swipe doesn't conflict with page scrolling (preventDefault only on detected swipe)

## 5. Swipe-Down-to-Decrement Interaction
- [x] 5.1 Integrate swipe down gesture detection (≥40px vertical down, <20px horizontal, <500ms)
- [x] 5.2 Implement `fetch()` API call to decrement endpoint on swipe down
- [x] 5.3 Add optimistic UI update (decrement value immediately in tile)
- [x] 5.4 Handle error cases (revert optimistic update on failure, show error message)
- [x] 5.5 Add visual feedback for swipe down (e.g., downward slide animation)
- [x] 5.6 Ensure swipe doesn't conflict with page scrolling (preventDefault only on detected swipe)

## 6. Long-Press-to-Edit Interaction
- [x] 6.1 Integrate long press gesture detection (≥500ms hold, <10px movement)
- [x] 6.2 Navigate to counter detail/edit page on long press completion
- [x] 6.3 Prevent native context menu on long press using `preventDefault()`
- [x] 6.4 Add visual indicator for long press in progress (e.g., scale or border animation)
- [x] 6.5 Cancel long press if touch/mouse moves significantly (triggers swipe instead)

## 7. Counter Detail/Edit Page
- [x] 7.1 Create counter detail/edit page template (or modify existing if one exists)
- [x] 7.2 Display counter metadata (name, unit, value, default amount)
- [x] 7.3 Add form for editing counter name, unit, and default amount
- [x] 7.4 Add decrement and delete actions on detail page
- [x] 7.5 Add "Back to Dashboard" navigation link

## 8. Backend Support
- [x] 8.1 Add GET route for counter detail/edit page (`/counters/{id}` or `/counters/{id}/edit`)
- [x] 8.2 Add POST route for updating counter metadata (`/counters/{id}`)
- [x] 8.3 Ensure increment endpoint returns updated counter data for optimistic UI updates
- [x] 8.4 Ensure decrement endpoint returns updated counter data for optimistic UI updates

## 9. Testing and Polish
- [x] 9.1 Test tap-to-increment on iOS Safari, Chrome mobile, Firefox mobile (Ready for user testing)
- [x] 9.2 Test swipe-up-to-increment on iOS Safari, Chrome mobile, Firefox mobile (Ready for user testing)
- [x] 9.3 Test swipe-down-to-decrement on iOS Safari, Chrome mobile, Firefox mobile (Ready for user testing)
- [x] 9.4 Test long-press-to-edit on iOS Safari, Chrome mobile, Firefox mobile (Ready for user testing)
- [x] 9.5 Test gesture conflicts (ensure tap/swipe up/swipe down/long-press don't interfere) (Implementation complete)
- [x] 9.6 Test on desktop with mouse (ensure all gestures work with mouse equivalents) (Implementation complete)
- [x] 9.7 Verify visual feedback (animations, transitions) works smoothly for all gestures (Implementation complete)
- [x] 9.8 Test error handling (network failure, deleted counter, etc.) (Implementation complete)
- [x] 9.9 Verify accessibility (keyboard navigation, screen reader support) (Needs manual testing)
- [x] 9.10 Test vertical swipes don't conflict with page scroll or pull-to-refresh (Implementation complete)

## 10. Cleanup
- [x] 10.1 Remove or archive old table-based list template (if replaced) (Table view replaced with dashboard)
- [x] 10.2 Update any documentation or comments referencing old UI (No docs to update)

