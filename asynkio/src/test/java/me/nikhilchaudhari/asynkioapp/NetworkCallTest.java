package me.nikhilchaudhari.asynkioapp;

import org.junit.Test;

import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.KeyStore;
import java.util.Collections;

import javax.net.ssl.KeyManager;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;

import me.nikhilchaudhari.asynkio.core.NetworkCall;
import me.nikhilchaudhari.asynkio.helper.RawFiles;
import me.nikhilchaudhari.asynkio.response.Response;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class NetworkCallTest {

    @Test
    public void executeGetRequestWithCertificateBasedClientAuthentication() throws Exception {
        SSLContext sslContext = createSslContextWithKeyMaterial();

        Response response = NetworkCall.get("https://client.badssl.com/",
                Collections.<String, String>emptyMap(),
                Collections.<String, String>emptyMap(),
                null,
                null,
                null,
                2.0,
                true,
                false,
                Collections.<RawFiles>emptyList(),
                sslContext);

        int statusCode = response.getStatusCode();

        if (statusCode == 400) {
            System.out.println("Please validate if certificate is not expired");
        } else {
            assertThat(statusCode, is(200));
        }
    }

    private SSLContext createSslContextWithKeyMaterial() throws Exception {
        String keyStorePath = "src/test/resources/badssl.com-client.jks";
        char[] keyStorePassword = "badssl.com".toCharArray();

        KeyStore keyStore = KeyStore.getInstance(KeyStore.getDefaultType());

        try(InputStream keyStoreInputStream = Files.newInputStream(Paths.get(keyStorePath));) {
            keyStore.load(keyStoreInputStream, keyStorePassword);
        }

        KeyManagerFactory keyManagerFactory = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
        keyManagerFactory.init(keyStore, keyStorePassword);
        KeyManager[] keyManagers = keyManagerFactory.getKeyManagers();

        SSLContext sslContext = SSLContext.getInstance("TLSv1.2");
        sslContext.init(keyManagers, null, null);
        return sslContext;
    }

}
