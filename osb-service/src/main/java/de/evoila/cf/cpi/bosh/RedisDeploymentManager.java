package de.evoila.cf.cpi.bosh;

import de.evoila.cf.broker.bean.BoshProperties;
import de.evoila.cf.broker.model.Plan;
import de.evoila.cf.broker.model.ServiceInstance;
import de.evoila.cf.cpi.bosh.deployment.DeploymentManager;
import de.evoila.cf.cpi.bosh.deployment.manifest.Manifest;
import org.springframework.core.env.Environment;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class RedisDeploymentManager extends DeploymentManager {

    public static final String INSTANCE_GROUP = "redis";
    public static final String REDIS_PASSWORD = "password";
    public static final String PORT = "port";

    RedisDeploymentManager(BoshProperties boshProperties, Environment environment){
        super(boshProperties, environment);
    }

    @Override
    protected void replaceParameters(ServiceInstance serviceInstance, Manifest manifest, Plan plan, Map<String, Object> customParameters) {
        HashMap<String, Object> properties = new HashMap<>();
        properties.putAll(plan.getMetadata().getCustomParameters());

        if (customParameters != null && !customParameters.isEmpty())
            properties.putAll(customParameters);

        Map<String, Object> manifestProperties = manifest.getInstanceGroups()
                .stream()
                .filter(i -> i.getName().equals(INSTANCE_GROUP))
                .findAny().get().getProperties();
        HashMap<String, Object> redis = (HashMap<String, Object>) manifestProperties.get("redis");

        String randomPassword = UUID.randomUUID().toString().replace("-", "");
        serviceInstance.setPassword(randomPassword);
        redis.put(REDIS_PASSWORD, randomPassword);

        this.updateInstanceGroupConfiguration(manifest, plan);
    }
}
