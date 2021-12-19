package me.lukeben.modules;

import lombok.Getter;
import lombok.Setter;
import me.lukeben.ImmortalAPI;
import me.lukeben.utils.Methods;
import org.bukkit.event.Listener;
import org.bukkit.plugin.PluginManager;

@Getter
public abstract class Module implements ModuleController {

    /**
     * Contains the of the current module name.
     */
    protected String moduleName;

    @Setter
    /**
     * Contains the state of the current module.
     */
    protected State moduleState = State.ENABLED;

    protected Module(final String moduleName) {
        this.moduleName = moduleName;
        ModuleRegistry.getInstance().registerModule(this);
        Methods.log("&e[Module] Starting " + getModuleName() + " module.");
    }

    public void registerEvents(final Listener... listeners) {
        final PluginManager pm = ImmortalAPI.getInstance().getServer().getPluginManager();
        for (final Listener l : listeners)
            pm.registerEvents(l, ImmortalAPI.getInstance());
    }

    public void loadByClass(final Class... classes) {
        for (final Class clazz : classes)
            loadByClass(clazz);
    }

    public <T> T loadByClass(final Class clazz) {
        try {
            return (T) clazz.newInstance();
        } catch (final Exception ex) {
            throw new IllegalStateException(ex.getClass().getSimpleName() + " : " + ex.getMessage());
        }
    }


}
