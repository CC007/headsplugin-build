package com.github.cc007.headsplugin;

import com.github.cc007.headsplugin.config.Application;
import dev.alangomes.springspigot.SpringSpigotInitializer;
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
import java.util.logging.Level;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManagerFactory;
import org.bukkit.plugin.java.JavaPlugin;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.ResourceLoader;

public class HeadsPlugin extends JavaPlugin
{

	public static ConfigurableApplicationContext context;

	private ClassLoader defaultClassLoader;

	@Override
	public void onEnable()
	{
		// Add the Let's Encrypt certificate (More info: https://letsencrypt.org/)
		addRootCA();

		saveDefaultConfig();

		// configure the class loader and run the spring application
		defaultClassLoader = Thread.currentThread().getContextClassLoader();
		Thread.currentThread().setContextClassLoader(getClassLoader());
		ResourceLoader loader = new DefaultResourceLoader(getClassLoader());
		SpringApplication application = new SpringApplication(loader, Application.class);
		application.addInitializers(new SpringSpigotInitializer(this));
		context = application.run();
	}

	@Override
	public void onDisable()
	{
		if (context != null) {
			context.close();
			context = null;
		}

		Thread.currentThread().setContextClassLoader(defaultClassLoader);
	}

	private void addRootCA()
	{
		try (InputStream fis = new BufferedInputStream(getResource("letsencrypt.crt"))) {
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
		}
		catch (CertificateException ex) {
			getLogger().warning("Something went wrong with adding a certificate for freshcoal. This can happen when using the /reload command. In that case the exception can be ignored: " + ex.getMessage());
		}
		catch (IOException | KeyManagementException | KeyStoreException | NoSuchAlgorithmException ex) {
			getLogger().log(Level.SEVERE, null, ex);
		}
	}

}
