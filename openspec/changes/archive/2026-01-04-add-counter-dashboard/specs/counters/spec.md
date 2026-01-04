## ADDED Requirements

### Requirement: Counter Dashboard View
The system SHALL provide a mobile-friendly dashboard view that displays counters as large, touch-optimized tiles.

Each tile MUST display:
- Counter name
- Current value (prominently)
- Unit label

The dashboard MUST use a responsive grid layout that adapts to screen size.

#### Scenario: Dashboard displays counters as tiles
- **GIVEN** the user has created multiple counters
- **WHEN** the user navigates to the dashboard
- **THEN** the system displays each counter as a large tile
- **AND** each tile shows the counter name, current value, and unit

#### Scenario: Dashboard is responsive on mobile
- **GIVEN** the user accesses the dashboard on a mobile device
- **WHEN** the viewport width is less than 768px
- **THEN** the tiles are displayed in a single column layout
- **AND** each tile is at least 120px tall for touch accessibility

#### Scenario: Dashboard is responsive on tablet/desktop
- **GIVEN** the user accesses the dashboard on a tablet or desktop device
- **WHEN** the viewport width is 768px or greater
- **THEN** the tiles are displayed in a multi-column grid layout
- **AND** tiles are evenly distributed across available width

### Requirement: Tap-to-Increment Interaction
The system SHALL allow users to increment a counter by tapping its tile on the dashboard.

When a tile is tapped:
- The system MUST increment the counter by its default amount
- The tile MUST update to show the new value immediately (optimistic update)
- The system MUST provide visual feedback for the tap (e.g., animation or ripple effect)

If the increment operation fails:
- The system MUST revert the optimistic update
- The system MUST display an error message to the user

#### Scenario: Tap increments counter
- **GIVEN** a counter with value 5 and default amount 1
- **WHEN** the user taps the counter tile
- **THEN** the counter value increases to 6
- **AND** the tile displays the new value immediately

#### Scenario: Tap provides visual feedback
- **GIVEN** the user is viewing the dashboard
- **WHEN** the user taps a counter tile
- **THEN** the system displays a visual animation or effect on the tile
- **AND** the animation completes within 300ms

#### Scenario: Failed increment reverts optimistic update
- **GIVEN** a counter with value 5
- **WHEN** the user taps the counter tile
- **AND** the increment operation fails (e.g., network error)
- **THEN** the tile reverts to displaying value 5
- **AND** the system displays an error message

### Requirement: Swipe-Up-to-Increment Interaction
The system SHALL allow users to increment a counter by swiping up on its tile on the dashboard.

A swipe up is defined as:
- A touch or mouse movement that starts on the tile
- Moves vertically upward by at least 40px
- With minimal horizontal movement (horizontal drift < 20px)
- Completes within 500ms

When an upward swipe is detected:
- The system MUST increment the counter by its default amount
- The tile MUST update to show the new value immediately (optimistic update)
- The system MUST provide visual feedback for the swipe (e.g., upward slide animation)

If the increment operation fails:
- The system MUST revert the optimistic update
- The system MUST display an error message to the user

The swipe gesture MUST NOT trigger if:
- The movement is primarily horizontal
- The movement distance is less than 40px
- The touch duration exceeds 500ms (to avoid conflicts with long press)

#### Scenario: Swipe up increments counter
- **GIVEN** a counter with value 5 and default amount 1
- **WHEN** the user swipes up on the counter tile
- **THEN** the counter value increases to 6
- **AND** the tile displays the new value immediately

#### Scenario: Swipe up provides visual feedback
- **GIVEN** the user is viewing the dashboard
- **WHEN** the user swipes up on a counter tile
- **THEN** the system displays a visual animation or effect on the tile (e.g., upward slide)
- **AND** the animation completes within 300ms

#### Scenario: Failed increment from swipe reverts optimistic update
- **GIVEN** a counter with value 5
- **WHEN** the user swipes up on the counter tile
- **AND** the increment operation fails (e.g., network error)
- **THEN** the tile reverts to displaying value 5
- **AND** the system displays an error message

#### Scenario: Short upward movement does not trigger swipe
- **GIVEN** the user is viewing the dashboard
- **WHEN** the user moves their finger/cursor up less than 40px on a tile
- **THEN** the swipe gesture is not triggered
- **AND** no increment occurs

#### Scenario: Horizontal movement does not trigger swipe up
- **GIVEN** the user is viewing the dashboard
- **WHEN** the user moves their finger/cursor diagonally with horizontal drift > 20px
- **THEN** the swipe up gesture is not triggered
- **AND** no increment occurs

#### Scenario: Slow movement triggers long press instead of swipe
- **GIVEN** the user is viewing the dashboard
- **WHEN** the user holds on a tile for more than 500ms while moving upward
- **THEN** the long press gesture is triggered (navigates to detail page)
- **AND** the swipe up gesture is not triggered

### Requirement: Swipe-Down-to-Decrement Interaction
The system SHALL allow users to decrement a counter by swiping down on its tile on the dashboard.

A swipe down is defined as:
- A touch or mouse movement that starts on the tile
- Moves vertically downward by at least 40px
- With minimal horizontal movement (horizontal drift < 20px)
- Completes within 500ms

When a downward swipe is detected:
- The system MUST decrement the counter by its default amount
- The tile MUST update to show the new value immediately (optimistic update)
- The system MUST provide visual feedback for the swipe (e.g., downward slide animation)

If the decrement operation fails:
- The system MUST revert the optimistic update
- The system MUST display an error message to the user

The swipe gesture MUST NOT trigger if:
- The movement is primarily horizontal
- The movement distance is less than 40px
- The touch duration exceeds 500ms (to avoid conflicts with long press)

#### Scenario: Swipe down decrements counter
- **GIVEN** a counter with value 10 and default amount 1
- **WHEN** the user swipes down on the counter tile
- **THEN** the counter value decreases to 9
- **AND** the tile displays the new value immediately

#### Scenario: Swipe down provides visual feedback
- **GIVEN** the user is viewing the dashboard
- **WHEN** the user swipes down on a counter tile
- **THEN** the system displays a visual animation or effect on the tile (e.g., downward slide)
- **AND** the animation completes within 300ms

#### Scenario: Failed decrement reverts optimistic update
- **GIVEN** a counter with value 10
- **WHEN** the user swipes down on the counter tile
- **AND** the decrement operation fails (e.g., network error)
- **THEN** the tile reverts to displaying value 10
- **AND** the system displays an error message

#### Scenario: Short downward movement does not trigger swipe
- **GIVEN** the user is viewing the dashboard
- **WHEN** the user moves their finger/cursor down less than 40px on a tile
- **THEN** the swipe gesture is not triggered
- **AND** no decrement occurs

#### Scenario: Horizontal movement does not trigger swipe down
- **GIVEN** the user is viewing the dashboard
- **WHEN** the user moves their finger/cursor diagonally with horizontal drift > 20px
- **THEN** the swipe down gesture is not triggered
- **AND** no decrement occurs

#### Scenario: Slow movement triggers long press instead of swipe
- **GIVEN** the user is viewing the dashboard
- **WHEN** the user holds on a tile for more than 500ms while moving downward
- **THEN** the long press gesture is triggered (navigates to detail page)
- **AND** the swipe down gesture is not triggered

### Requirement: Long-Press-to-Edit Navigation
The system SHALL allow users to navigate to a counter's detail/edit page by long-pressing its tile on the dashboard.

A long press is defined as:
- A touch or mouse press held for at least 500ms
- Without significant movement (drag threshold < 10px)

When a long press is detected:
- The system MUST navigate to the counter's detail/edit page
- The system MUST prevent the native browser context menu from appearing

The system SHOULD provide visual feedback during the long press to indicate progress.

#### Scenario: Long press navigates to detail page
- **GIVEN** the user is viewing the dashboard
- **WHEN** the user presses and holds on a counter tile for 500ms
- **THEN** the system navigates to the counter's detail/edit page

#### Scenario: Long press prevents context menu
- **GIVEN** the user is viewing the dashboard on a mobile device
- **WHEN** the user presses and holds on a counter tile
- **THEN** the native browser context menu does not appear

#### Scenario: Long press shows visual feedback
- **GIVEN** the user is viewing the dashboard
- **WHEN** the user presses and holds on a counter tile
- **THEN** the system displays a visual indicator (e.g., scale or border animation)
- **AND** the indicator progresses during the hold duration

#### Scenario: Movement cancels long press
- **GIVEN** the user is viewing the dashboard
- **WHEN** the user presses on a counter tile and moves their finger/cursor more than 10px
- **THEN** the long press is cancelled
- **AND** no navigation occurs

#### Scenario: Short tap does not trigger long press
- **GIVEN** the user is viewing the dashboard
- **WHEN** the user taps a counter tile for less than 500ms
- **THEN** the counter increments (tap-to-increment behavior)
- **AND** no navigation to detail page occurs

### Requirement: Counter Detail/Edit Page
The system SHALL provide a detail/edit page for each counter, accessible via long press from the dashboard.

The detail/edit page MUST display:
- Counter name (editable)
- Counter unit (editable)
- Counter default amount (editable)
- Current value (read-only)

The detail/edit page MUST provide actions for:
- Decrement counter
- Delete counter
- Save changes to counter metadata
- Navigate back to dashboard

#### Scenario: Detail page displays counter information
- **GIVEN** a counter with name "Steps", unit "steps", value 1000, and default amount 1
- **WHEN** the user navigates to the counter's detail page
- **THEN** the page displays the counter name "Steps"
- **AND** the page displays the unit "steps"
- **AND** the page displays the current value 1000
- **AND** the page displays the default amount 1

#### Scenario: User edits counter metadata
- **GIVEN** the user is on a counter's detail page
- **WHEN** the user changes the counter name to "Daily Steps"
- **AND** the user submits the form
- **THEN** the counter name is updated to "Daily Steps"
- **AND** the user is redirected to the dashboard
- **AND** the tile displays the new name "Daily Steps"

#### Scenario: User decrements counter from detail page
- **GIVEN** a counter with value 10 on the detail page
- **WHEN** the user clicks the decrement button
- **THEN** the counter value decreases to 9
- **AND** the page refreshes to show the new value

#### Scenario: User deletes counter from detail page
- **GIVEN** the user is on a counter's detail page
- **WHEN** the user clicks the delete button
- **AND** confirms the deletion
- **THEN** the counter is soft-deleted
- **AND** the user is redirected to the dashboard
- **AND** the counter tile no longer appears on the dashboard

#### Scenario: User navigates back to dashboard
- **GIVEN** the user is on a counter's detail page
- **WHEN** the user clicks the "Back to Dashboard" link
- **THEN** the user is navigated to the dashboard

