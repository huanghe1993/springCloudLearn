package com.huanghe.springcloud.config;

import com.netflix.appinfo.ApplicationInfoManager;
import com.netflix.discovery.EurekaClientConfig;
import com.netflix.discovery.EurekaIdentityHeaderFilter;
import com.netflix.discovery.shared.transport.jersey.EurekaJerseyClient;
import com.netflix.discovery.shared.transport.jersey.EurekaJerseyClientImpl;
import com.netflix.eureka.EurekaServerConfig;
import com.netflix.eureka.EurekaServerIdentity;
import com.netflix.eureka.cluster.DynamicGZIPContentEncodingFilter;
import com.netflix.eureka.cluster.HttpReplicationClient;
import com.netflix.eureka.cluster.PeerEurekaNode;
import com.netflix.eureka.cluster.PeerEurekaNodes;
import com.netflix.eureka.registry.PeerAwareInstanceRegistry;
import com.netflix.eureka.resources.ServerCodecs;
import com.netflix.eureka.transport.JerseyReplicationClient;
import com.sun.jersey.client.apache4.ApacheHttpClient4;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.cloud.context.environment.EnvironmentChangeEvent;
import org.springframework.cloud.netflix.eureka.server.EurekaServerAutoConfiguration;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.Set;

/**
 * EurekaServerAutoConfiguration
 */
@Configuration
public class EurekaServerConfigMy {

    private static final Logger logger = LoggerFactory.getLogger(EurekaServerConfigMy.class);

    @Autowired
    private ApplicationInfoManager applicationInfoManager;
    @Autowired
    private EurekaServerConfig eurekaServerConfig;
    @Autowired
    private EurekaClientConfig eurekaClientConfig;

    @Bean
    public PeerEurekaNodes peerEurekaNodes(PeerAwareInstanceRegistry registry,
                                           ServerCodecs serverCodecs) {
        return new EurekaServerConfigMy.RefreshablePeerEurekaNodes(registry, this.eurekaServerConfig,
                this.eurekaClientConfig, serverCodecs, this.applicationInfoManager);
    }

    static class RefreshablePeerEurekaNodes extends PeerEurekaNodes
            implements ApplicationListener<EnvironmentChangeEvent> {

        public RefreshablePeerEurekaNodes(
                final PeerAwareInstanceRegistry registry,
                final EurekaServerConfig serverConfig,
                final EurekaClientConfig clientConfig,
                final ServerCodecs serverCodecs,
                final ApplicationInfoManager applicationInfoManager) {
            super(registry, serverConfig, clientConfig, serverCodecs, applicationInfoManager);
            // TODO
            updatePeerEurekaNodes(resolvePeerUrls());
        }

        @Override
        public void onApplicationEvent(final EnvironmentChangeEvent event) {
            if (shouldUpdate(event.getKeys())) {
                updatePeerEurekaNodes(resolvePeerUrls());
            }
        }

        /*
         * Check whether specific properties have changed.
         */
        protected boolean shouldUpdate(final Set<String> changedKeys) {
            assert changedKeys != null;

            // if eureka.client.use-dns-for-fetching-service-urls is true, then
            // service-url will not be fetched from environment.
            if (clientConfig.shouldUseDnsForFetchingServiceUrls()) {
                return false;
            }

            if (changedKeys.contains("eureka.client.region")) {
                return true;
            }

            for (final String key : changedKeys) {
                // property keys are not expected to be null.
                if (key.startsWith("eureka.client.service-url.") ||
                        key.startsWith("eureka.client.availability-zones.")) {
                    return true;
                }
            }

            return false;
        }

        protected PeerEurekaNode createPeerEurekaNode(String peerEurekaNodeUrl) {
            HttpReplicationClient replicationClient = createReplicationClient(serverConfig, serverCodecs, peerEurekaNodeUrl);
            String targetHost = hostFromUrl(peerEurekaNodeUrl);
            if (targetHost == null) {
                targetHost = "host";
            }
            return new PeerEurekaNode(registry, targetHost, peerEurekaNodeUrl, replicationClient, serverConfig);
        }

        public static JerseyReplicationClient createReplicationClient(EurekaServerConfig config, ServerCodecs serverCodecs, String serviceUrl) {
            String name = JerseyReplicationClient.class.getSimpleName() + ": " + serviceUrl + "apps/: ";

            EurekaJerseyClient jerseyClient;
            try {
                String hostname;
                try {
                    hostname = new URL(serviceUrl).getHost();
                } catch (MalformedURLException e) {
                    hostname = serviceUrl;
                }

                String jerseyClientName = "Discovery-PeerNodeClient-" + hostname;
                EurekaJerseyClientImpl.EurekaJerseyClientBuilder clientBuilder = new EurekaJerseyClientImpl.EurekaJerseyClientBuilder()
                        .withClientName(jerseyClientName)
                        .withUserAgent("Java-EurekaClient-Replication")
                        .withEncoderWrapper(serverCodecs.getFullJsonCodec())
                        .withDecoderWrapper(serverCodecs.getFullJsonCodec())
                        .withConnectionTimeout(config.getPeerNodeConnectTimeoutMs())
                        .withReadTimeout(config.getPeerNodeReadTimeoutMs())
                        .withMaxConnectionsPerHost(config.getPeerNodeTotalConnectionsPerHost())
                        .withMaxTotalConnections(config.getPeerNodeTotalConnections())
                        .withConnectionIdleTimeout(config.getPeerNodeConnectionIdleTimeoutSeconds());

                if (serviceUrl.startsWith("https://") &&
                        "true".equals(System.getProperty("com.netflix.eureka.shouldSSLConnectionsUseSystemSocketFactory"))) {
                    clientBuilder.withSystemSSLConfiguration();
                }
                jerseyClient = clientBuilder.build();
            } catch (Throwable e) {
                throw new RuntimeException("Cannot Create new Replica Node :" + name, e);
            }

            String ip = null;
            try {
                ip = InetAddress.getLocalHost().getHostAddress();
            } catch (UnknownHostException e) {
                logger.warn("Cannot find localhost ip", e);
            }

            ApacheHttpClient4 jerseyApacheClient = jerseyClient.getClient();
            jerseyApacheClient.addFilter(new DynamicGZIPContentEncodingFilter(config));

            EurekaServerIdentity identity = new EurekaServerIdentity(ip);
            jerseyApacheClient.addFilter(new EurekaIdentityHeaderFilter(identity));

            return new JerseyReplicationClient(jerseyClient, serviceUrl);
        }

    }


}
