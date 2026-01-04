# Design: Mobile-Friendly Counter Dashboard

## Context
The Tracker app currently uses a table-based layout for displaying counters, which works well on desktop but is not optimized for mobile touch interactions. Users want a more intuitive mobile experience where counters are displayed as large tiles that can be tapped to increment and long-pressed to access details/editing.

**Constraints:**
- Must work on mobile browsers (iOS Safari, Chrome, Firefox)
- Keep frontend lightweight (no heavy frameworks)
- Maintain server-rendered HTML approach (Thymeleaf)
- Support touch and mouse interactions (hybrid desktop/mobile)

**Stakeholders:**
- Single-user system; optimizing for personal use on mobile devices

## Goals / Non-Goals

**Goals:**
- Provide a mobile-first, touch-optimized dashboard view
- Enable quick counter increments via tap (single touch) and swipe up gesture
- Enable quick counter decrements via swipe down gesture
- Enable navigation to counter details/edit via long press
- Maintain visual consistency with existing dark theme
- Keep implementation simple (minimal JavaScript)

**Non-Goals:**
- Complex gesture recognition beyond tap, swipe up/down, and long press (e.g., pinch, multi-touch, etc.)
- Real-time updates or WebSocket integration
- Offline support or PWA features
- Multi-user collaboration features

## Decisions

### Decision 1: Tile-Based Grid Layout
**What:** Use CSS Grid to display counters as large, touch-friendly tiles (minimum 120x120px tap target, ideally larger).

**Why:**
- CSS Grid provides responsive layout without JavaScript
- Large tap targets meet accessibility guidelines (44x44px minimum, but we'll go larger)
- Tiles provide visual hierarchy and clear affordance for interaction

**Alternatives considered:**
- Flexbox: Less control over responsive grid behavior
- Table layout: Not touch-friendly, poor mobile UX

### Decision 2: JavaScript for Long Press Detection
**What:** Use minimal vanilla JavaScript to detect long press (e.g., 500ms touch hold) and navigate to edit page.

**Why:**
- HTML/CSS alone cannot distinguish tap vs long press
- Vanilla JS keeps bundle size minimal
- Touch events are well-supported across modern mobile browsers

**Alternatives considered:**
- Pure CSS `:active` pseudo-class: Cannot distinguish tap vs long press
- Heavy gesture library: Overkill for simple long press detection

### Decision 3: Progressive Enhancement
**What:** Ensure tiles work without JavaScript (tap navigates to detail page), then enhance with JS for increment-on-tap and long-press-to-edit.

**Why:**
- Maintains accessibility and fallback behavior
- Aligns with "keep frontend lightweight" principle

**Alternatives considered:**
- JavaScript-required approach: Breaks without JS, poor accessibility

### Decision 4: Single Dashboard View (Replace Table)
**What:** Replace the existing table view with the dashboard view, rather than maintaining both.

**Why:**
- Simplifies maintenance (single UI to update)
- Mobile-first approach benefits all users
- Table view can be restored later if needed

**Alternatives considered:**
- Maintain both views with toggle: Adds complexity, splits maintenance effort
- Add dashboard as separate route: Confusing UX with two list views

### Decision 5: Tile Content and Visual Design
**What:** Each tile displays:
- Counter name (large, bold)
- Current value (very large, prominent)
- Unit label (smaller, muted)
- Visual feedback on tap (ripple or scale animation)

**Why:**
- Prioritizes the most important information (value)
- Clear visual hierarchy
- Tap feedback confirms interaction

### Decision 6: Vertical Swipe Gestures for Increment and Decrement
**What:** Use vanilla JavaScript to detect vertical swipe gestures (≥40px vertical movement, <20px horizontal drift, <500ms duration):
- **Swipe up** → Increment counter
- **Swipe down** → Decrement counter

**Why:**
- Symmetric, intuitive gestures (up = increase, down = decrease)
- Provides alternative to tap for users who prefer swiping
- Complements tap-to-increment for flexible interaction
- Gesture parameters prevent conflicts with scrolling and long press
- Vertical swipes feel more intentional than taps, reducing accidental increments

**Alternatives considered:**
- Secondary buttons on tiles: Clutters UI, reduces tap target size
- Increment/decrement only on detail page: Requires two actions (long press + tap), slower workflow
- Swipe left/right: Conflicts with potential future horizontal scrolling or swipe-to-delete patterns
- Swipe only (no tap): Taps are faster for quick increments, both should be supported

### Decision 7: Dual Increment Methods (Tap and Swipe Up)
**What:** Support both tap and swipe up for incrementing counters.

**Why:**
- **Tap** is faster for quick, repeated increments (lower cognitive load)
- **Swipe up** is more intentional and reduces accidental increments
- **Flexibility** allows users to choose their preferred interaction method
- **Symmetry** with swipe down makes the gesture language more learnable
- Common pattern in mobile UIs (e.g., pull-to-refresh has both swipe and button alternatives)

**Trade-off:** Slightly more complex implementation, but provides better UX flexibility.

### Decision 8: Gesture Priority and Conflict Resolution
**What:** Implement gesture detection with clear priority order:
1. **Long press** (≥500ms hold, <10px movement) → Navigate to detail page
2. **Swipe up** (≥40px vertical up, <20px horizontal, <500ms) → Increment
3. **Swipe down** (≥40px vertical down, <20px horizontal, <500ms) → Decrement
4. **Tap** (quick touch, <500ms, <10px movement) → Increment

**Why:**
- Clear disambiguation prevents accidental triggers
- Duration thresholds separate tap/swipe from long press
- Movement thresholds separate tap from swipe
- Directional constraints distinguish swipe up from swipe down
- Distance thresholds keep swipes distinct from page scroll

**Implementation approach:**
- Track touch start position and timestamp
- Monitor movement during touch
- On touch end, determine which gesture occurred based on duration, distance, and direction
- Cancel long press if significant movement detected (triggers swipe instead)
- Cancel swipe if duration exceeds 500ms (triggers long press instead)
- Classify swipe direction based on primary axis (vertical vs horizontal) and sign (up vs down)

## Risks / Trade-offs

**Risk:** Long press may conflict with native browser context menus on mobile.
- **Mitigation:** Use `preventDefault()` on `contextmenu` event to suppress native menu during long press.

**Risk:** JavaScript-based increment may feel slower than native form submission.
- **Mitigation:** Use `fetch()` API with optimistic UI update (update tile immediately, revert on error).

**Risk:** Gesture conflicts between tap, swipe, long press, and native scroll.
- **Mitigation:** Use carefully tuned thresholds (distance, duration, direction) to disambiguate gestures. Test extensively on real devices.

**Risk:** Swipe down may conflict with pull-to-refresh on mobile browsers.
- **Mitigation:** Use `preventDefault()` on touch events within tiles to prevent pull-to-refresh. Ensure page-level scroll still works outside tiles.

**Trade-off:** Removing table view loses quick access to delete action on dashboard.
- **Mitigation:** Provide delete action on the detail/edit page accessible via long press. Swipe down now provides quick decrement access.

## Migration Plan

1. Implement new dashboard view in parallel (new template or modify existing)
2. Add tile CSS and JavaScript for interaction
3. Test on iOS Safari, Chrome mobile, Firefox mobile
4. Replace existing list view route with dashboard view
5. Remove old table-based template (or keep as commented reference)

**Rollback:** Revert template changes and restore table view if issues arise.

## Open Questions

- **Q:** Should we show a visual indicator for long press (e.g., progress ring)?
  - **Proposed answer:** Yes, add a subtle visual cue (e.g., scaling or border animation) to indicate long press is being detected.

- **Q:** What happens if a counter is deleted while viewing the dashboard?
  - **Proposed answer:** On next page load, tile disappears. No real-time updates needed for single-user system.

- **Q:** What if the user accidentally triggers a swipe while scrolling the page?
  - **Proposed answer:** Horizontal drift threshold (<20px) and vertical movement requirement (≥40px) should prevent most accidents. May need to adjust thresholds based on user testing.

- **Q:** With both tap and swipe up for increment, will users be confused about which to use?
  - **Proposed answer:** No - this is a common pattern (e.g., pull-to-refresh, like/favorite buttons). Visual feedback and animations will make both interactions discoverable. Users naturally gravitate to their preferred method.

