package com.huanghe.springcloud.config;

import com.netflix.appinfo.ApplicationInfoManager;
import com.netflix.discovery.EurekaClientConfig;
import com.netflix.eureka.cluster.PeerEurekaNodes;
import com.netflix.eureka.registry.PeerAwareInstanceRegistry;
import com.netflix.eureka.resources.ServerCodecs;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.cloud.context.environment.EnvironmentChangeEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Set;

@Configuration
public class EurekaServerConfig {
    @Autowired
    private ApplicationInfoManager applicationInfoManager;

    @Autowired
    private com.netflix.eureka.EurekaServerConfig eurekaServerConfig;

    @Autowired
    private EurekaClientConfig eurekaClientConfig;


    @Bean
    @ConditionalOnMissingBean
    public PeerEurekaNodes peerEurekaNodes(PeerAwareInstanceRegistry registry,
                                           ServerCodecs serverCodecs) {
        return new PeerEurekaNodesRefreshable(registry, this.eurekaServerConfig,
                this.eurekaClientConfig, serverCodecs, this.applicationInfoManager);
    }

    static class PeerEurekaNodesRefreshable extends PeerEurekaNodes
            implements ApplicationListener<EnvironmentChangeEvent> {

        public PeerEurekaNodesRefreshable(
                final PeerAwareInstanceRegistry registry,
                final com.netflix.eureka.EurekaServerConfig serverConfig,
                final EurekaClientConfig clientConfig,
                final ServerCodecs serverCodecs,
                final ApplicationInfoManager applicationInfoManager) {
            super(registry, serverConfig, clientConfig, serverCodecs, applicationInfoManager);
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
    }
}
