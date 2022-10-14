package net.immortal.modules;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Holds the state of our modules and hooks.
 */
@AllArgsConstructor
@Getter
public enum State {
    ENABLED,
    DISABLED
}
