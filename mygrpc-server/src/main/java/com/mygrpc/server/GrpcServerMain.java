package com.mygrpc.server;

import io.helidon.grpc.server.GrpcRouting;
import io.helidon.grpc.server.GrpcServer;
import io.helidon.grpc.server.GrpcServerConfiguration;

public class GrpcServerMain {
	public static void main(String[] args) {

		GrpcRouting grpcRouting = GrpcRouting.builder().register(new StringService()) // Register your gRPC services
				.build();

		GrpcServerConfiguration grpcServerConfig = GrpcServerConfiguration.builder().port(8080) // Example port
				.build();

		GrpcServer server = GrpcServer.create(grpcServerConfig, grpcRouting);

		// Start the server
		server.start().whenComplete((result, ex) -> {
			if (ex != null) {
				System.err.println("Failed to start server: " + ex.getMessage());
			} else {
				System.out.println("Server started on port " + server.port());
			}
		});
	}


}
