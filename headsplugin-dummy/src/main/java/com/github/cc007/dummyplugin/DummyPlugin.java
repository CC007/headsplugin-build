package com.github.cc007.dummyplugin;

import com.github.cc007.headsplugin.api.HeadsPluginApi;

import org.bukkit.plugin.java.JavaPlugin;

public class DummyPlugin extends JavaPlugin {


    @Override
    public void onLoad() {
        getLogger().info("Added class loader to HeadsPlugin springClassLoaders");
        HeadsPluginApi.addSpringClassLoader(getClassLoader());
    }

    @Override
    public void onEnable() {
        HeadsPluginApi api = HeadsPluginApi.getInstance();
        System.out.println("Contains doIExist: " + api.getBeanFactory().containsBean("doIExist"));
        System.out.println("Contains component with autowired field doIExist: " + api.getBeanFactory().getBean("dummyComponent", DummyComponent.class).isDoIExist());
        System.out.println("Debug time...");
    }
}
