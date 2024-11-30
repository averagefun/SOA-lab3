package ru.ifmo;

import java.util.List;

import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.consul.discovery.ConsulDiscoveryClient;
import org.springframework.stereotype.Service;

@Service
public class ConsulService {

    private final ConsulDiscoveryClient consulDiscoveryClient;

    public ConsulService(
            ConsulDiscoveryClient consulDiscoveryClient
    ) {
        this.consulDiscoveryClient = consulDiscoveryClient;
    }

    public String getRoute() {
        List<ServiceInstance> instances = consulDiscoveryClient.getInstances("web-app");
        ServiceInstance serviceInstance = instances.get(0);
        return serviceInstance.getUri().toString();
    }
}
