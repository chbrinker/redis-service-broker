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
    private static String PASSWORD = "password";

    private RandomString randomStringPassword = new RandomString(15);

    @Override
	protected Map<String, Object> createCredentials(String bindingId, ServiceInstance serviceInstance, Plan plan,
			ServerAddress host) {

        String endpoint = ServiceInstanceUtils.connectionUrl(serviceInstance.getHosts());

        // When host is not empty, it is a service key
        if (host != null)
            endpoint = host.getIp() + ":" + host.getPort();

        String dbURL = String.format("redis://%s", endpoint);

		Map<String, Object> credentials = new HashMap<>();
		credentials.put(URI, dbURL);
        credentials.put(PASSWORD, randomStringPassword.nextString());

		return credentials;
	}

	@Override
	public ServiceInstanceBinding getServiceInstanceBinding(String id) {
		throw new UnsupportedOperationException();
	}

    @Override
    protected void deleteBinding(ServiceInstanceBinding binding, ServiceInstance serviceInstance, Plan plan)  {}

    @Override
    protected RouteBinding bindRoute(ServiceInstance serviceInstance, String route) {
        throw new UnsupportedOperationException();
    }

}
