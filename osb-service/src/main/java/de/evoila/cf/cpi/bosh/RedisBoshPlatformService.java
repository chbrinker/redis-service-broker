package de.evoila.cf.cpi.bosh;

import de.evoila.cf.broker.bean.BoshProperties;
import de.evoila.cf.broker.exception.PlatformException;
import de.evoila.cf.broker.model.DashboardClient;
import de.evoila.cf.broker.model.Plan;
import de.evoila.cf.broker.model.ServiceInstance;
import de.evoila.cf.broker.repository.PlatformRepository;
import de.evoila.cf.broker.service.CatalogService;
import de.evoila.cf.broker.service.availability.ServicePortAvailabilityVerifier;
import de.evoila.cf.config.security.credhub.CredhubClient;
import io.bosh.client.deployments.Deployment;
import io.bosh.client.errands.ErrandSummary;
import io.bosh.client.tasks.Task;
import io.bosh.client.vms.Vm;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;
import rx.Observable;

import java.util.List;
import java.util.Optional;

@Service
@ConditionalOnBean(BoshProperties.class)
public class RedisBoshPlatformService extends BoshPlatformService {

    private static final int defaultPort = 6379;

    private CredhubClient credhubClient;

    RedisBoshPlatformService(PlatformRepository repository, CatalogService catalogService,
                             ServicePortAvailabilityVerifier availabilityVerifier,
                             BoshProperties boshProperties, Optional<DashboardClient> dashboardClient, Environment environment, CredhubClient credhubClient) {
        super(repository, catalogService,
                availabilityVerifier, boshProperties,
                dashboardClient, new RedisDeploymentManager(boshProperties, environment, credhubClient));
        this.credhubClient = credhubClient;
    }

    public void runCreateErrands(ServiceInstance instance, Plan plan, Deployment deployment, Observable<List<ErrandSummary>> errands) throws PlatformException {
        Task task = super.connection.connection().errands().runErrand(deployment.getName(), "cluster-config").toBlocking().first();
        super.waitForTaskCompletion(task);

    }

    protected void runUpdateErrands(ServiceInstance instance, Plan plan, Deployment deployment, Observable<List<ErrandSummary>> errands) throws PlatformException {
        Task task = super.connection.connection().errands().runErrand(deployment.getName(), "cluster-config").toBlocking().first();
        super.waitForTaskCompletion(task);
    }

    protected void runDeleteErrands(ServiceInstance instance, Deployment deployment, Observable<List<ErrandSummary>> errands) { }

    @Override
    protected void updateHosts(ServiceInstance serviceInstance, Plan plan, Deployment deployment) {
        List<Vm> vms = super.getVms(serviceInstance);
        serviceInstance.getHosts().clear();

        vms.forEach(vm -> serviceInstance.getHosts().add(super.toServerAddress(vm, defaultPort)));
    }

    @Override
    public void postDeleteInstance(ServiceInstance serviceInstance) {
        credhubClient.deleteCredentials(serviceInstance.getId(), "redisPassword");
    }

}
