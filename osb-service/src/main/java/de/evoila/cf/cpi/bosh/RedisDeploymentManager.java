package de.evoila.cf.cpi.bosh;

import de.evoila.cf.broker.bean.BoshProperties;
import de.evoila.cf.broker.model.ServiceInstance;
import de.evoila.cf.broker.model.catalog.plan.Plan;
import de.evoila.cf.cpi.bosh.deployment.DeploymentManager;
import de.evoila.cf.cpi.bosh.deployment.manifest.Manifest;
import de.evoila.cf.security.credhub.CredhubClient;
import org.springframework.core.env.Environment;

import java.util.HashMap;
import java.util.Map;

public class RedisDeploymentManager extends DeploymentManager {

    public static final String INSTANCE_GROUP = "redis";
    public static final String REDIS_PASSWORD = "password";
    public static final String PORT = "port";

    private CredhubClient credhubClient;

    RedisDeploymentManager(BoshProperties boshProperties, Environment environment, CredhubClient credhubClient){
        super(boshProperties, environment);
        this.credhubClient = credhubClient;
    }

    @Override
    protected void replaceParameters(ServiceInstance serviceInstance, Manifest manifest, Plan plan, Map<String, Object> customParameters) {
        HashMap<String, Object> properties = new HashMap<>();
        if (customParameters != null && !customParameters.isEmpty())
            properties.putAll(customParameters);

        if (customParameters != null && !customParameters.isEmpty())
            properties.putAll(customParameters);

        Map<String, Object> manifestProperties = manifest.getInstanceGroups()
                .stream()
                .filter(i -> i.getName().equals(INSTANCE_GROUP))
                .findAny().get().getProperties();

        HashMap<String, Object> redis = (HashMap<String, Object>) manifestProperties.get("redis");

        credhubClient.createPassword(serviceInstance.getId(), "redisPassword");

        redis.put(REDIS_PASSWORD, "((redisPassword))");

        this.updateInstanceGroupConfiguration(manifest, plan);
    }
}
