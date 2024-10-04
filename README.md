1. Initialize servers[] with Server objects(name, weight,
responseTime, Energy Consumption):
2. Initialize variables for total energy consumption, total
elapsed time, and total response time =0
3. For request in 1 to n:
4. For each server in servers:
5. Calculate slackTime = server.weight server.load
//Optimize server's weight based on slackTime and response
time
6. selectedServer = Select server using WRR based on
weight and load
7. Print("Request assigned to", selectedServer.name)
8. responseTime = Simulate random response time
between 0 and 300ms
9. selectedServer.responseTime = responseTime
10. If responseTime <= 200:
11. selectedServer.load += 1
12. Else:
13. selectedServer.load += 2
14. energyConsumption
selectedServer.estimateEnergyConsumption()
15. totalEnergyConsumption += energyConsumption
16. elapsedTime = Calculate time to process request
17. totalElapsedTime += elapsedTime
18. totalResponseTime += responseTime
//After processing all requests, calculate throughput, average
response time, total energy consumed, and total elapsed time
19. throughput = n(totalRequest) / totalElapsedTime
20. averageResponseTime = totalResponseTime / n
// Print throughput, average response time, total energy
consumed, and total elapsed tim
