/**
 * Counter Dashboard - Gesture Detection and Interaction
 * 
 * Supports:
 * - Tap: Increment counter (quick touch <500ms, <10px movement)
 * - Swipe Up: Increment counter (≥40px vertical up, <20px horizontal, <500ms)
 * - Swipe Down: Decrement counter (≥40px vertical down, <20px horizontal, <500ms)
 * - Long Press: Navigate to detail page (≥500ms hold, <10px movement)
 */

(function() {
  'use strict';

  // Gesture thresholds
  const THRESHOLDS = {
    TAP_MAX_DURATION: 500,
    TAP_MAX_MOVEMENT: 10,
    SWIPE_MIN_DISTANCE: 40,
    SWIPE_MAX_HORIZONTAL_DRIFT: 20,
    SWIPE_MAX_DURATION: 500,
    LONG_PRESS_MIN_DURATION: 500,
    LONG_PRESS_MAX_MOVEMENT: 10
  };

  // Gesture state for each tile
  class GestureTracker {
    constructor(element) {
      this.element = element;
      this.reset();
    }

    reset() {
      this.startX = 0;
      this.startY = 0;
      this.startTime = 0;
      this.currentX = 0;
      this.currentY = 0;
      this.isTracking = false;
      this.longPressTimer = null;
      this.longPressTriggered = false;
    }

    start(x, y) {
      this.reset();
      this.startX = x;
      this.startY = y;
      this.currentX = x;
      this.currentY = y;
      this.startTime = Date.now();
      this.isTracking = true;
      
      // Start long press timer
      this.longPressTimer = setTimeout(() => {
        if (this.isTracking && this.getMovement() < THRESHOLDS.LONG_PRESS_MAX_MOVEMENT) {
          this.longPressTriggered = true;
          this.element.classList.add('gesture-long-press');
        }
      }, THRESHOLDS.LONG_PRESS_MIN_DURATION);
    }

    move(x, y) {
      if (!this.isTracking) return;
      
      this.currentX = x;
      this.currentY = y;
      
      // Cancel long press if movement exceeds threshold
      if (this.getMovement() > THRESHOLDS.LONG_PRESS_MAX_MOVEMENT) {
        this.cancelLongPress();
      }
    }

    end() {
      if (!this.isTracking) return;
      
      const gesture = this.detectGesture();
      this.cleanup();
      return gesture;
    }

    cancel() {
      this.cleanup();
      return null;
    }

    cleanup() {
      this.cancelLongPress();
      this.element.classList.remove('gesture-long-press');
      this.isTracking = false;
    }

    cancelLongPress() {
      if (this.longPressTimer) {
        clearTimeout(this.longPressTimer);
        this.longPressTimer = null;
      }
    }

    getMovement() {
      const dx = this.currentX - this.startX;
      const dy = this.currentY - this.startY;
      return Math.sqrt(dx * dx + dy * dy);
    }

    getDuration() {
      return Date.now() - this.startTime;
    }

    detectGesture() {
      const duration = this.getDuration();
      const movement = this.getMovement();
      const dx = this.currentX - this.startX;
      const dy = this.currentY - this.startY;
      const absDx = Math.abs(dx);
      const absDy = Math.abs(dy);

      // Long press (highest priority if triggered)
      if (this.longPressTriggered) {
        return { type: 'long-press' };
      }

      // Check for swipe gestures (vertical movement with minimal horizontal drift)
      if (duration < THRESHOLDS.SWIPE_MAX_DURATION &&
          absDy >= THRESHOLDS.SWIPE_MIN_DISTANCE &&
          absDx < THRESHOLDS.SWIPE_MAX_HORIZONTAL_DRIFT) {
        
        if (dy < 0) {
          return { type: 'swipe-up' };
        } else {
          return { type: 'swipe-down' };
        }
      }

      // Tap (quick touch with minimal movement)
      if (duration < THRESHOLDS.TAP_MAX_DURATION &&
          movement < THRESHOLDS.TAP_MAX_MOVEMENT) {
        return { type: 'tap' };
      }

      // No recognized gesture
      return null;
    }
  }

  // Counter tile manager
  class CounterTile {
    constructor(element) {
      this.element = element;
      this.counterId = element.dataset.counterId;
      this.tracker = new GestureTracker(element);
      this.setupEventListeners();
    }

    setupEventListeners() {
      // Touch events
      this.element.addEventListener('touchstart', (e) => this.handleStart(e), { passive: false });
      this.element.addEventListener('touchmove', (e) => this.handleMove(e), { passive: false });
      this.element.addEventListener('touchend', (e) => this.handleEnd(e), { passive: false });
      this.element.addEventListener('touchcancel', (e) => this.handleCancel(e));

      // Mouse events (for desktop testing)
      this.element.addEventListener('mousedown', (e) => this.handleStart(e));
      this.element.addEventListener('mousemove', (e) => this.handleMove(e));
      this.element.addEventListener('mouseup', (e) => this.handleEnd(e));
      this.element.addEventListener('mouseleave', (e) => this.handleCancel(e));

      // Prevent context menu
      this.element.addEventListener('contextmenu', (e) => e.preventDefault());
    }

    handleStart(e) {
      e.preventDefault();
      
      const point = this.getPoint(e);
      this.tracker.start(point.x, point.y);
    }

    handleMove(e) {
      if (!this.tracker.isTracking) return;
      
      const point = this.getPoint(e);
      this.tracker.move(point.x, point.y);
    }

    async handleEnd(e) {
      if (!this.tracker.isTracking) return;
      
      e.preventDefault();
      
      const gesture = this.tracker.end();
      if (!gesture) return;

      // Handle the gesture
      switch (gesture.type) {
        case 'tap':
          await this.handleTap();
          break;
        case 'swipe-up':
          await this.handleSwipeUp();
          break;
        case 'swipe-down':
          await this.handleSwipeDown();
          break;
        case 'long-press':
          this.handleLongPress();
          break;
      }
    }

    handleCancel(e) {
      this.tracker.cancel();
    }

    getPoint(e) {
      if (e.touches && e.touches.length > 0) {
        return { x: e.touches[0].clientX, y: e.touches[0].clientY };
      }
      return { x: e.clientX, y: e.clientY };
    }

    async handleTap() {
      this.showFeedback('gesture-tap');
      await this.incrementCounter();
    }

    async handleSwipeUp() {
      this.showFeedback('gesture-swipe-up');
      await this.incrementCounter();
    }

    async handleSwipeDown() {
      this.showFeedback('gesture-swipe-down');
      await this.decrementCounter();
    }

    handleLongPress() {
      // Navigate to detail/edit page
      window.location.href = `/counters/${this.counterId}/edit`;
    }

    showFeedback(className) {
      this.element.classList.add(className);
      setTimeout(() => {
        this.element.classList.remove(className);
      }, 300);
    }

    async incrementCounter() {
      const currentValue = parseInt(this.element.dataset.counterValue);
      const newValue = currentValue + 1;
      
      // Optimistic update
      this.updateValue(newValue);
      
      try {
        const response = await fetch(`/counters/${this.counterId}/increment`, {
          method: 'POST',
          headers: {
            'Content-Type': 'application/x-www-form-urlencoded',
          }
        });
        
        if (!response.ok) {
          throw new Error('Increment failed');
        }
        
        // Update succeeded, keep the optimistic value
      } catch (error) {
        // Revert on error
        this.updateValue(currentValue);
        this.showError('Failed to increment counter');
      }
    }

    async decrementCounter() {
      const currentValue = parseInt(this.element.dataset.counterValue);
      const newValue = currentValue - 1;
      
      // Optimistic update
      this.updateValue(newValue);
      
      try {
        const response = await fetch(`/counters/${this.counterId}/decrement`, {
          method: 'POST',
          headers: {
            'Content-Type': 'application/x-www-form-urlencoded',
          }
        });
        
        if (!response.ok) {
          throw new Error('Decrement failed');
        }
        
        // Update succeeded, keep the optimistic value
      } catch (error) {
        // Revert on error
        this.updateValue(currentValue);
        this.showError('Failed to decrement counter');
      }
    }

    updateValue(newValue) {
      this.element.dataset.counterValue = newValue;
      const valueElement = this.element.querySelector('.counter-tile-value');
      if (valueElement) {
        valueElement.textContent = newValue;
      }
    }

    showError(message) {
      const toast = document.createElement('div');
      toast.className = 'error-toast';
      toast.textContent = message;
      document.body.appendChild(toast);
      
      setTimeout(() => {
        toast.remove();
      }, 3000);
    }
  }

  // Initialize all counter tiles
  function initDashboard() {
    const tiles = document.querySelectorAll('.counter-tile');
    tiles.forEach(tile => new CounterTile(tile));
  }

  // Initialize when DOM is ready
  if (document.readyState === 'loading') {
    document.addEventListener('DOMContentLoaded', initDashboard);
  } else {
    initDashboard();
  }
})();

