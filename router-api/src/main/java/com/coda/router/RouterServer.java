package com.coda.router;

import com.coda.router.api.RouterHttpHandler;
import com.coda.router.config.TargetConfigLoader;
import com.coda.router.core.InstanceHealthMonitor;
import com.coda.router.core.RoundRobinLoadBalancer;
import com.coda.router.service.RouterService;
import com.sun.net.httpserver.HttpServer;

import java.net.InetSocketAddress;

public class RouterServer {
    public static void main(String[] args) throws Exception {
        var targets = TargetConfigLoader.loadTargets("targets.json");
        InstanceHealthMonitor monitor = new InstanceHealthMonitor();
        RoundRobinLoadBalancer loadBalancer = new RoundRobinLoadBalancer(targets, monitor);
        RouterService service = new RouterService(loadBalancer, monitor);

        int port = Integer.parseInt(System.getProperty("PORT", "8080"));
        HttpServer server = HttpServer.create(new InetSocketAddress(port), 0);
        server.createContext("/forward", new RouterHttpHandler(service));
        server.setExecutor(null);
        System.out.println("Router API running on port " + port);
        server.start();
    }
}
