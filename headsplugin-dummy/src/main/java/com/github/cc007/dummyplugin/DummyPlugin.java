package com.github.cc007.dummyplugin;

import com.github.cc007.headsplugin.HeadsPlugin;

import org.bukkit.plugin.java.JavaPlugin;

public class DummyPlugin extends JavaPlugin {


    @Override
    public void onLoad() {
        getLogger().info("Added class loader to HeadsPlugin springClassLoaders");
        HeadsPlugin.addSpringClassLoader(getClassLoader());
    }

    @Override
    public void onEnable() {
        System.out.println("Contains doIExist: " + HeadsPlugin.getSpringContext().containsBean("doIExist"));
        System.out.println("Contains component with autowired field doIExist: " + HeadsPlugin.getSpringContext().getBean("dummyComponent", DummyComponent.class).isDoIExist());
    }
}
