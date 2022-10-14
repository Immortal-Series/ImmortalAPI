package net.immortalapi.modules;

import lombok.Getter;

import java.util.HashMap;

/**
 * Used to cache and register our modules.
 */
public class ModuleRegistry {

    @Getter
    private static final ModuleRegistry instance = new ModuleRegistry();

    /**
     * Holds and registers the state of our modules.
     */
    public HashMap<Module, State> stateCache = new HashMap<>();

    /**
     * Register our module
     *
     * @param module
     */
    public void registerModule(Module module) {
        stateCache.put(module, module.getModuleState());
    }

    /**
     * Update the state of our module.
     *
     * @param module
     */
    public void updateState(Module module) {
        stateCache.put(module, module.getModuleState());
    }

    /**
     * Get then module state.
     *
     * @param module
     * @return
     */
    public State getState(Module module) {
        return stateCache.get(module);
    }

}
