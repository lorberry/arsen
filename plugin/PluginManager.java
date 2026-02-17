package de.lorberry.arsen.plugin;

import de.lorberry.arsen.util.Logger;

import java.io.File;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.List;

public class PluginManager {
    private final List<Plugin> plugins;
    private final PluginContext context;

    public PluginManager(PluginContext context) {
        this.plugins = new ArrayList<>();
        this.context = context;
    }

    public void loadPlugin(String jarPath) {
        try {
            File jarFile = new File(jarPath);
            URL[] urls = {jarFile.toURI().toURL()};
            URLClassLoader classLoader = new URLClassLoader(urls, getClass().getClassLoader());

            Logger.info("Plugin loaded from: " + jarPath);
        } catch (Exception e) {
            Logger.error("Failed to load plugin: " + e.getMessage());
        }
    }

    public void registerPlugin(Plugin plugin) {
        plugins.add(plugin);
        plugin.initialize(context);
        Logger.info("Plugin registered: " + plugin.getName());
    }

    public void unregisterPlugin(Plugin plugin) {
        plugin.shutdown();
        plugins.remove(plugin);
        Logger.info("Plugin unregistered: " + plugin.getName());
    }

    public List<Plugin> getPlugins() {
        return new ArrayList<>(plugins);
    }

    public void shutdownAll() {
        for (Plugin plugin : plugins) {
            plugin.shutdown();
        }
        plugins.clear();
        Logger.info("All plugins shut down");
    }
}
