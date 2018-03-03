package de.evoila.cf.cpi.bosh;

import de.evoila.cf.broker.bean.BoshProperties;
import de.evoila.cf.broker.model.Plan;
import de.evoila.cf.broker.model.ServiceInstance;
import de.evoila.cf.cpi.bosh.deployment.DeploymentManager;
import de.evoila.cf.cpi.bosh.deployment.manifest.Manifest;

import java.util.HashMap;
import java.util.Map;

public class RedisDeploymentManager extends DeploymentManager {

    public static final String NODES = "nodes";
    public static final String PORT = "port";
    public static final String VM_TYPE = "vm_type";
    public static final String DISK_TYPE = "disk_type";

    RedisDeploymentManager(BoshProperties boshProperties){
        super(boshProperties);
    }

    protected void replaceParameters(ServiceInstance instance, Manifest manifest, Plan plan, Map<String, String> customParameters) {
        HashMap<String, Object> properties = new HashMap<>();
        properties.putAll(plan.getMetadata());
        properties.putAll(customParameters);

        if(properties.containsKey(NODES)){
            manifest.getInstance_groups().get(0).setInstances((Integer) properties.get(NODES));
        }

        if(properties.containsKey(VM_TYPE)){
            manifest.getInstance_groups().get(0).setVm_type((String) properties.get(VM_TYPE));
        }

        if(properties.containsKey(DISK_TYPE)){
            manifest.getInstance_groups().get(0).setVm_type((String) properties.get(DISK_TYPE));
        }

        if(properties.containsKey(DISK_TYPE)){
            manifest.getInstance_groups().get(0).setVm_type((String) properties.get(DISK_TYPE));
        }
    }


}
