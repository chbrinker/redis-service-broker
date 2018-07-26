/**
 * 
 */
package de.evoila.cf.broker.custom.redis;

import de.evoila.cf.broker.exception.ServiceBrokerException;
import de.evoila.cf.broker.model.*;
import de.evoila.cf.broker.service.impl.BindingServiceImpl;
import de.evoila.cf.broker.util.RandomString;
import de.evoila.cf.broker.util.ServiceInstanceUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * @author Johannes Hiemer.
 *
 */
@Service
public class RedisBindingService extends BindingServiceImpl {

	private Logger log = LoggerFactory.getLogger(getClass());

    private static String URI = "uri";

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
        Map<String, Object> configurations = new HashMap<>();
        configurations.put(URI, String.format("redis://%s@%s", serviceInstance.getPassword(), endpoint));

        Map<String, Object> credentials = ServiceInstanceUtils.bindingObject(serviceInstance.getHosts(),
                null,
                serviceInstance.getPassword(),
                configurations);

        return credentials;
	}

	@Override
	public ServiceInstanceBinding getServiceInstanceBinding(String id) {
		throw new UnsupportedOperationException();
	}

    @Override
    protected void unbindService(ServiceInstanceBinding binding, ServiceInstance serviceInstance, Plan plan)  {}

    @Override
    protected RouteBinding bindRoute(ServiceInstance serviceInstance, String route) {
        throw new UnsupportedOperationException();
    }

}
