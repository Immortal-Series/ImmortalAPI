package net.immortalapi.modules;

public interface ModuleController {
    /**
     * When a module is started.
     */
    void moduleStart();

    /**
     * When a module is stopped.
     */
    void moduleDisable();
}
