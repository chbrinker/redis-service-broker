package de.evoila.cf.broker.custom.redis;

import de.evoila.cf.broker.exception.ServiceBrokerException;
import de.evoila.cf.broker.model.RouteBinding;
import de.evoila.cf.broker.model.ServiceInstance;
import de.evoila.cf.broker.model.ServiceInstanceBinding;
import de.evoila.cf.broker.model.ServiceInstanceBindingRequest;
import de.evoila.cf.broker.model.catalog.ServerAddress;
import de.evoila.cf.broker.model.catalog.plan.Plan;
import de.evoila.cf.broker.repository.*;
import de.evoila.cf.broker.service.AsyncBindingService;
import de.evoila.cf.broker.service.HAProxyService;
import de.evoila.cf.broker.service.impl.BindingServiceImpl;
import de.evoila.cf.broker.util.ServiceInstanceUtils;
import de.evoila.cf.cpi.bosh.CredentialConstants;
import de.evoila.cf.security.credentials.CredentialStore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Johannes Hiemer.
 */
@Service
public class RedisBindingService extends BindingServiceImpl {

	private Logger log = LoggerFactory.getLogger(getClass());

    private static String URI = "uri";

    private CredentialStore credentialStore;

    public RedisBindingService(BindingRepository bindingRepository, ServiceDefinitionRepository serviceDefinitionRepository,
                               ServiceInstanceRepository serviceInstanceRepository, RouteBindingRepository routeBindingRepository,
                               HAProxyService haProxyService, CredentialStore credentialStore,
                               JobRepository jobRepository, AsyncBindingService asyncBindingService,
                               PlatformRepository platformRepository) {
        super(bindingRepository, serviceDefinitionRepository,
                serviceInstanceRepository, routeBindingRepository,
                haProxyService, jobRepository,
                asyncBindingService, platformRepository);
        this.credentialStore = credentialStore;
    }

    @Override
	protected Map<String, Object> createCredentials(String bindingId, ServiceInstanceBindingRequest serviceInstanceBindingRequest,
                                                    ServiceInstance serviceInstance, Plan plan, ServerAddress host) throws ServiceBrokerException {

        List<ServerAddress> serverAddresses = null;
        if (plan.getMetadata().getIngressInstanceGroup() != null && host == null)
            serverAddresses = ServiceInstanceUtils.filteredServerAddress(serviceInstance.getHosts(),
                    plan.getMetadata().getIngressInstanceGroup());
        else if (host != null)
            serverAddresses = Arrays.asList(new ServerAddress("service-key-haproxy", host.getIp(), host.getPort()));
        else
            serverAddresses = serviceInstance.getHosts();

        if (serverAddresses == null || serverAddresses.size() == 0)
            throw new ServiceBrokerException("Could not find any Service Backends to create Service Binding");

        String endpoint = ServiceInstanceUtils.connectionUrl(serverAddresses);

        // This needs to be done here and can't be generalized due to the fact that each backend
        // may have a different URL setup
        String password = credentialStore.getPassword(serviceInstance, CredentialConstants.REDIS_PASSWORD);
        Map<String, Object> configurations = new HashMap<>();
        configurations.put(URI, String.format("redis://%s@%s", password, endpoint));

        Map<String, Object> credentials = ServiceInstanceUtils.bindingObject(serviceInstance.getHosts(),
                null, password, configurations);

        return credentials;
	}

    @Override
    protected void unbindService(ServiceInstanceBinding binding, ServiceInstance serviceInstance, Plan plan)  {}

    @Override
    protected RouteBinding bindRoute(ServiceInstance serviceInstance, String route) {
        throw new UnsupportedOperationException();
    }

}
