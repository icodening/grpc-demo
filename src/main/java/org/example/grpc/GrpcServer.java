package org.example.grpc;

import com.google.protobuf.ByteString;
import io.grpc.Server;
import io.grpc.ServerBuilder;
import io.grpc.stub.StreamObserver;

import java.nio.charset.StandardCharsets;

/**
 * @author icodening
 * @date 2022.09.16
 */
public class GrpcServer {

    public static void main(String[] args) throws Throwable {
        int port = 40000;
        Server server = ServerBuilder.forPort(port).maxInboundMessageSize(96 * 1024 * 1024)
                .addService(new ExchangerImpl()).build().start();
        System.out.println("Server started, listening on " + port);
        server.awaitTermination();
    }

    private static class ExchangerImpl extends ExchangerGrpc.ExchangerImplBase {

        @Override
        public void exchange(Message request, StreamObserver<Message> responseObserver) {
            System.out.println("收到请求：" + request.getMessage().toString(StandardCharsets.UTF_8));
            String response = "response: ";
            response += request.getMessage().toString(StandardCharsets.UTF_8);
            Message resp = Message.newBuilder()
                    .setMessage(ByteString.copyFrom(response.getBytes())).build();
            responseObserver.onNext(resp);
            responseObserver.onCompleted();
        }
    }
}
