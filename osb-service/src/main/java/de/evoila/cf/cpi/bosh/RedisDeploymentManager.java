package de.evoila.cf.cpi.bosh;

import de.evoila.cf.broker.bean.BoshProperties;
import de.evoila.cf.broker.model.ServiceInstance;
import de.evoila.cf.broker.model.catalog.plan.Plan;
import de.evoila.cf.broker.model.credential.PasswordCredential;
import de.evoila.cf.broker.util.MapUtils;
import de.evoila.cf.cpi.bosh.deployment.DeploymentManager;
import de.evoila.cf.cpi.bosh.deployment.manifest.Manifest;
import de.evoila.cf.security.credentials.CredentialStore;
import org.springframework.core.env.Environment;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Johannes Hiemer.
 */
public class RedisDeploymentManager extends DeploymentManager {

    public static final String INSTANCE_GROUP = "redis";
    public static final String REDIS_PASSWORD = "password";
    public static final String PORT = "port";

    private CredentialStore credentialStore;

    RedisDeploymentManager(BoshProperties boshProperties, Environment environment, CredentialStore credentialStore){
        super(boshProperties, environment);
        this.credentialStore = credentialStore;
    }

    @Override
    protected void replaceParameters(ServiceInstance serviceInstance, Manifest manifest, Plan plan,
                                     Map<String, Object> customParameters, boolean isUpdate) {
        HashMap<String, Object> properties = new HashMap<>();
        if (customParameters != null && !customParameters.isEmpty())
            properties.putAll(customParameters);

        if (!isUpdate) {
            if (customParameters != null && !customParameters.isEmpty())
                properties.putAll(customParameters);

            if (customParameters != null && !customParameters.isEmpty())
                properties.putAll(customParameters);

            Map<String, Object> manifestProperties = manifest.getInstanceGroups()
                    .stream()
                    .filter(i -> i.getName().equals(INSTANCE_GROUP))
                    .findAny().get().getProperties();

            HashMap<String, Object> redis = (HashMap<String, Object>) manifestProperties.get("redis");

            PasswordCredential passwordCredential = credentialStore.createPassword(serviceInstance, CredentialConstants.REDIS_PASSWORD);

            redis.put(REDIS_PASSWORD, passwordCredential.getPassword());
        } else if (isUpdate && customParameters != null && !customParameters.isEmpty()) {
            for (Map.Entry parameter : customParameters.entrySet()) {
                Map<String, Object> manifestProperties = manifestProperties(parameter.getKey().toString(), manifest);

                if (manifestProperties != null)
                    MapUtils.deepMerge(manifestProperties, customParameters);
            }

        }

        this.updateInstanceGroupConfiguration(manifest, plan);
    }

    private Map<String, Object> manifestProperties(String instanceGroup, Manifest manifest) {
        return manifest
                .getInstanceGroups()
                .stream()
                .filter(i -> {
                    if (i.getName().equals(instanceGroup))
                        return true;
                    return false;
                }).findFirst().get().getProperties();
    }
}
