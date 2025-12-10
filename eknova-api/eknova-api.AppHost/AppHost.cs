var builder = DistributedApplication.CreateBuilder(args);

var apiService = builder.AddProject<Projects.eknova_api_ApiService>("apiservice")
    .WithHttpHealthCheck("/health");

builder.AddProject<Projects.eknova_api_Web>("webfrontend")
    .WithExternalHttpEndpoints()
    .WithHttpHealthCheck("/health")
    .WithReference(apiService)
    .WaitFor(apiService);

builder.Build().Run();
