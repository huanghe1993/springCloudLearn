package com.huanghe.springcloud.config;

import com.netflix.discovery.DiscoveryClient;
import com.netflix.discovery.shared.transport.jersey.EurekaJerseyClientImpl;
import org.apache.http.ssl.SSLContextBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.net.ssl.SSLContext;
import java.io.IOException;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;

@Configuration
public class EurekaClientHttpsCfg {

    @Value("${ssl.trust-store}")
    String trustStoreFileName;

    @Value("${ssl.trust-store-password}")
    String trustStorePassword;

    @Value("${ssl.key-store}")
    String keyStoreFileName;

    @Value("${ssl.key-store-password}")
    String keyStorePassword;

    /**
     * 客户端端与Eureka服务端进行通信的，客户端信任服务端
     *
     * @return
     * @throws CertificateException     CertificateException
     * @throws NoSuchAlgorithmException NoSuchAlgorithmException
     * @throws KeyStoreException        KeyStoreException
     * @throws IOException              IOException
     * @throws KeyManagementException   KeyManagementException
     */
    @Bean
    public DiscoveryClient.DiscoveryClientOptionalArgs discoveryClientOptionalArgs() throws CertificateException, NoSuchAlgorithmException, KeyStoreException, IOException, KeyManagementException, UnrecoverableKeyException {
        EurekaJerseyClientImpl.EurekaJerseyClientBuilder builder = new EurekaJerseyClientImpl.EurekaJerseyClientBuilder();
        builder.withClientName("eureka-client");
        SSLContext sslContext = new SSLContextBuilder()
                .loadTrustMaterial(
                        this.getClass().getClassLoader().getResource(trustStoreFileName), trustStorePassword.toCharArray()
                ).loadKeyMaterial(this.getClass().getClassLoader().getResource(keyStoreFileName), keyStorePassword.toCharArray(), keyStorePassword.toCharArray())
                .build();
        builder.withCustomSSL(sslContext);

        builder.withMaxTotalConnections(10);
        builder.withMaxConnectionsPerHost(10);
        DiscoveryClient.DiscoveryClientOptionalArgs args = new DiscoveryClient.DiscoveryClientOptionalArgs();
        args.setEurekaJerseyClient(builder.build());
        return args;
    }
}
