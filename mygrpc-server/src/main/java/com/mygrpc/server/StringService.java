package com.mygrpc.server;

import static io.helidon.grpc.core.ResponseHelper.complete;
import static io.helidon.grpc.core.ResponseHelper.stream;

import java.util.stream.Collectors;
import java.util.stream.Stream;

import io.grpc.stub.StreamObserver;
import io.helidon.grpc.examples.common.StringServiceOuterClass;
import io.helidon.grpc.examples.common.StringServiceOuterClass.StringMessage;
import io.helidon.grpc.server.CollectingObserver;
import io.helidon.grpc.server.GrpcService;
import io.helidon.grpc.server.ServiceDescriptor;

public class StringService implements GrpcService {

	@Override
	public void update(ServiceDescriptor.Rules rules) {
		rules.proto(StringServiceOuterClass.getDescriptor()).unary("Upper", this::upper).unary("Lower", this::lower)
				.serverStreaming("Split", this::split).clientStreaming("Join", this::join)
				.bidirectional("Echo", this::echo);
	}

	private void upper(StringMessage request, StreamObserver<StringMessage> observer) {
		complete(observer, response(request.getText().toUpperCase()));
	}

	private void lower(StringMessage request, StreamObserver<StringMessage> observer) {
		complete(observer, response(request.getText().toLowerCase()));
	}

	private void split(StringMessage request, StreamObserver<StringMessage> observer) {
		String[] parts = request.getText().split(" ");
		stream(observer, Stream.of(parts).map(this::response));
	}

	private StreamObserver<StringMessage> join(StreamObserver<StringMessage> observer) {
		return new CollectingObserver<>(Collectors.joining(" "), observer, StringMessage::getText, this::response);
	}

	private StreamObserver<StringMessage> echo(StreamObserver<StringMessage> observer) {
		return new StreamObserver<StringMessage>() {
			public void onNext(StringMessage value) {
				observer.onNext(value);
			}

			public void onError(Throwable t) {
				t.printStackTrace();
			}

			public void onCompleted() {
				observer.onCompleted();
			}
		};
	}

	private StringMessage response(String text) {
		return StringMessage.newBuilder().setText(text).build();
	}

}