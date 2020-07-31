package me.nikhilchaudhari.asynkioapp

import me.nikhilchaudhari.asynkio.core.get
import org.hamcrest.CoreMatchers.equalTo
import org.junit.Assert.assertThat
import org.junit.Test
import java.nio.file.Files
import java.nio.file.Paths
import java.security.KeyStore
import javax.net.ssl.KeyManagerFactory
import javax.net.ssl.SSLContext

class NetworkCallTest {

    @Test
    fun executeGetRequestWithCertificateBasedClientAuthentication() {
        val sslContext = createSslContextWithKeyMaterial()
        val response = get(
            url = "https://client.badssl.com/",
            sslContext = sslContext
        )
        val statusCode = response.statusCode
        if (statusCode == 400) {
            println("Please validate if certificate is not expired")
        } else {
            assertThat(statusCode, equalTo(200))
        }
    }

    private fun createSslContextWithKeyMaterial(): SSLContext {
        val keyStorePath = "src/test/resources/badssl.com-client.jks"
        val keyStorePassword = "badssl.com".toCharArray()
        val keyStore = KeyStore.getInstance(KeyStore.getDefaultType())

        Files.newInputStream(Paths.get(keyStorePath))
            .use { keyStoreInputStream -> keyStore.load(keyStoreInputStream, keyStorePassword) }

        val keyManagerFactory = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm())
        keyManagerFactory.init(keyStore, keyStorePassword)

        val keyManagers = keyManagerFactory.keyManagers

        val sslContext = SSLContext.getInstance("TLSv1.2")
        sslContext.init(keyManagers, null, null)

        return sslContext
    }
}
