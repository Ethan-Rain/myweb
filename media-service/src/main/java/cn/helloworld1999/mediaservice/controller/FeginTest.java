package cn.helloworld1999.mediaservice.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.cloud.client.loadbalancer.LoadBalancerClient;
import org.springframework.context.annotation.Bean;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@RestController
public class FeginTest {

    @Autowired
    DiscoveryClient discoveryClient;
    @Autowired
    RestTemplate restTemplate;
    @Autowired
    LoadBalancerClient loadBalancerClient;

    @GetMapping("/fegintest")
    public String fegintest() {
        return "fegintest";
    }

    @GetMapping("/restTemplateTest")
    public String restTemplateTest() {
        List<ServiceInstance> instances = discoveryClient.getInstances("user-service");
        if (instances.size() > 0) {
            ServiceInstance serviceInstance = instances.get(0);
            String url = serviceInstance.getUri() + "/hi";
            ResponseEntity<String> responseEntity = restTemplate.getForEntity(url, String.class);
            return responseEntity.getBody();
        }
        return "error";
    }

    @GetMapping("/loadBalancerClientTest")
    public String loadBalancerClientTest() {
        ServiceInstance serviceInstance = loadBalancerClient.choose("user-service");
        String url = serviceInstance.getUri() + "/hi";
        ResponseEntity<String> responseEntity = restTemplate.getForEntity(url, String.class);
        return responseEntity.getBody();
    }
    @GetMapping("/loadBalancerClientTest2")
    public String loadBalancerClientTest2() {
        String rootUrl = "http://user-service";
        String url = rootUrl + "/hi";
        ResponseEntity<String> responseEntity = restTemplate.getForEntity(url, String.class);
        return responseEntity.getBody();
    }
}
