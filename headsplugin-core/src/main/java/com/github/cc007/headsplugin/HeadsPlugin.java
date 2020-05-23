package com.github.cc007.headsplugin;

import com.github.cc007.headsplugin.api.HeadsPluginApi;
import com.github.cc007.headsplugin.config.Application;

import dev.alangomes.springspigot.SpringSpigotInitializer;
import lombok.Getter;
import org.bukkit.plugin.java.JavaPlugin;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.ResourceLoader;
import org.switchyard.common.type.CompoundClassLoader;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManagerFactory;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.logging.Level;

public class HeadsPlugin extends JavaPlugin {

    @Getter
    private static ConfigurableApplicationContext springContext;

    private static List<ClassLoader> springClassLoaders;

    private ClassLoader defaultClassLoader;


    public static void addSpringClassLoader(ClassLoader springClassLoader) {
        springClassLoaders.add(springClassLoader);
    }

    public static HeadsPluginApi getApi(){
        BeanFactory beanFactory = Optional.ofNullable(springContext)
                .orElseThrow(() -> new IllegalStateException(
                        "HeadsPlugin has not been fully initialized yet! Make sure that the HeadsPluginAPI plugin is enabled."));
        return HeadsPluginApi.getInstance(springContext);
    }

    @Override
    public void onLoad() {
        springClassLoaders = new ArrayList<>();
        getLogger().info("Added class loader to HeadsPlugin springClassLoaders");
        springClassLoaders.add(getClassLoader());
    }

    @Override
    public void onEnable() {
        // Add the Let's Encrypt certificate (More info: https://letsencrypt.org/)
        addRootCA();

        saveDefaultConfig();

        // configure the class loader and run the spring application
        defaultClassLoader = Thread.currentThread().getContextClassLoader();
        springClassLoaders.add(defaultClassLoader);
        ClassLoader classLoader = new CompoundClassLoader(springClassLoaders);
        Thread.currentThread().setContextClassLoader(classLoader);
        ResourceLoader loader = new DefaultResourceLoader(classLoader);
        SpringApplication application = new SpringApplication(loader, Application.class);
        application.addInitializers(new SpringSpigotInitializer(this));
        springContext = application.run();
    }

    @Override
    public void onDisable() {
        if (springContext != null && springContext.isActive()) {
            try {
                springContext.close();
            } catch (IllegalStateException ex) {
                getLogger().log(Level.SEVERE, "While stopping HeadsPluginAPI: " + ex.getMessage(), ex);
            }
            springContext = null;
        }

        Thread.currentThread().setContextClassLoader(defaultClassLoader);
    }

    private void addRootCA() {
        try (InputStream fis = new BufferedInputStream(
                Optional.ofNullable(getResource("letsencrypt.crt"))
                        .orElseThrow(() -> new IOException("Unable to find the letsencrypt certificate"))
        )) {
            Certificate ca = CertificateFactory.getInstance("X.509").generateCertificate(fis);
            KeyStore ks = KeyStore.getInstance(KeyStore.getDefaultType());
            Path ksPath = Paths.get(System.getProperty("java.home"), "lib", "security", "cacerts");
            ks.load(Files.newInputStream(ksPath), "changeit".toCharArray());
            ks.setCertificateEntry("LetsEncrypt CA", ca);
            TrustManagerFactory tmf = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
            tmf.init(ks);
            SSLContext ctx = SSLContext.getInstance("TLS");
            ctx.init(null, tmf.getTrustManagers(), null);
            HttpsURLConnection.setDefaultSSLSocketFactory(ctx.getSocketFactory());
        } catch (CertificateException ex) {
            getLogger().warning("Something went wrong with adding a certificate for freshcoal. This can happen when using the /reload command. In that case the exception can be ignored: " + ex.getMessage());
        } catch (IOException | KeyManagementException | KeyStoreException | NoSuchAlgorithmException ex) {
            getLogger().log(Level.SEVERE, null, ex);
        }
    }
}
