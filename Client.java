package org.cloudbus.cloudsim.src;
import java.util.ArrayList;
import java.util.List;
import java.util.Comparator;

class Server {
    String name;
    int weight;
    int currentLoad;
    int responseTime;
    int timeConsumed;
    double energyConsumption;

    public Server(String name, int weight) {
        this.name = name;
        this.weight = weight;
        this.currentLoad = 0;
        this.responseTime = 0;
        this.energyConsumption = weight * 10.0;  // Energy based on weight
        this.timeConsumed = 0;  // Initialize time consumed
    }

    public double estimateEnergyConsumption() {
        return weight * 10.0;
    }

    public void increaseLoad(int load) {
        currentLoad += load;
    }

    public int getCurrentLoad() {
        return currentLoad;
    }

    public boolean isSLAAdhered(int responseTime) {
        return responseTime <= 200;  // Define SLA as 200ms
    }

    public int calculateSlackTime() {
        return (weight - currentLoad);
    }

    public double calculateFitness() {
        return calculateSlackTime() / energyConsumption;  // Slack time/energy efficiency
    }

    public void adjustWeight(int newWeight) {
        this.weight = newWeight;
    }

    public void updateResponseTime(int newResponseTime) {
        this.responseTime = newResponseTime;
    }

    public void updateEnergyConsumption(double newEnergyConsumption) {
        this.energyConsumption = newEnergyConsumption;
    }

    public void updateTimeConsumed(int elapsedTime) {
        this.timeConsumed += elapsedTime;
    }
}

class LoadBalancer {
    private List<Server> servers;

    public LoadBalancer() {
        servers = new ArrayList<>();
        // Add servers with initial weights
        servers.add(new Server("Server1", 3));
        servers.add(new Server("Server2", 4));
        servers.add(new Server("Server3", 7));
    }

    public Server getNextServer() {
        // Implement Weighted Round Robin (WRR) with MSJF
        Server selectedServer = servers.stream()
                .min(Comparator.comparingInt(Server::calculateSlackTime))  // Min Slack Job First (MSJF)
                .orElse(null);

        if (selectedServer != null) {
            selectedServer.increaseLoad(1);  // Increase load on the selected server
            return selectedServer;
        }
        return null;
    }

    public void optimizeServerWeights() {
        // Periodically adjust server weights based on response times and energy consumption
        for (Server server : servers) {
            int startTime = (int) System.currentTimeMillis();

            // Simulate evaluation and weight adjustment
            int newResponseTime = (int) (Math.random() * 300);  // Simulated response time
            double newEnergyConsumption = server.estimateEnergyConsumption() * (1.0 - (newResponseTime / 300.0));

            server.updateResponseTime(newResponseTime);
            server.updateEnergyConsumption(newEnergyConsumption);

            // Adjust weight dynamically based on response time and energy consumption
            int adjustedWeight = (int) (server.weight * (1.0 / (1.0 + (newResponseTime / 300.0))));
            server.adjustWeight(adjustedWeight);

            int endTime = (int) System.currentTimeMillis();
            int elapsedTime = endTime - startTime;
            server.updateTimeConsumed(elapsedTime);
        }
    }
}

public class Client {
    public static void main(String[] args) {
        LoadBalancer loadBalancer = new LoadBalancer();
        double totalEnergyConsumption = 0;
        int totalElapsedTime = 0;
        int totalResponseTime = 0;
        int totalRequests = 10;  // Number of requests
        int successfulRequests = 0;

        long simulationStartTime = System.nanoTime();  // To calculate throughput

        for (int i = 0; i < totalRequests; i++) {
            long requestStartTime = System.nanoTime();
            loadBalancer.optimizeServerWeights();  // Optimize server weights periodically

            Server server = loadBalancer.getNextServer();  // Get the next server for handling the request

            System.out.println("Request " + (i + 1) + " routed to: " + server.name);
            int responseTime = (int) (Math.random() * 300);  // Simulated response time
            totalResponseTime += responseTime;

            if (server.isSLAAdhered(responseTime)) {
                server.increaseLoad(1);
                System.out.println("SLA Adhered for Request " + (i + 1));
                successfulRequests++;
            } else {
                server.increaseLoad(2);
                System.out.println("SLA Not Adhered for Request " + (i + 1));
            }

            totalEnergyConsumption += server.estimateEnergyConsumption();
            totalElapsedTime += System.nanoTime() - requestStartTime;
        }

        long simulationEndTime = System.nanoTime();
        double totalTimeInSeconds = (simulationEndTime - simulationStartTime) / 1_000_000_000.0;
        double throughput = totalRequests / totalTimeInSeconds;
        double averageResponseTime = totalResponseTime / (double) totalRequests;

        System.out.println("\n=== Performance Metrics ===");
        System.out.println("Total Energy Consumption: " + totalEnergyConsumption);
        System.out.println("Total Elapsed Time: " + (totalElapsedTime / 1_000_000) + " milliseconds");
        System.out.println("Average Response Time: " + averageResponseTime + " milliseconds");
        System.out.println("Throughput: " + throughput + " requests/second");
        System.out.println("Total Successful Requests: " + successfulRequests);
    }
}
