package org.example.grpc;

import com.google.protobuf.ByteString;
import io.grpc.ClientCall;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.stub.StreamObserver;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

import static org.example.grpc.ExchangerGrpc.getBiStreamMethod;
import static org.example.grpc.ExchangerGrpc.newStub;

/**
 * @author icodening
 * @date 2022.09.16
 */
public class GrpcClient {

    public static void main(String[] args) throws IOException {
        ManagedChannel channel = ManagedChannelBuilder.forAddress("localhost", 40000)
                .usePlaintext().build();
        ExchangerGrpc.ExchangerStub stub = newStub(channel);
        ClientCall<Message, Message> call = channel.newCall(getBiStreamMethod(), stub.getCallOptions());
        StreamObserver<Message> requestObserver = stub.biStream(new StreamObserver<Message>() {
            @Override
            public void onNext(Message value) {
                System.out.println("响应："
                        + value.getMessage().toString(StandardCharsets.UTF_8));
            }

            @Override
            public void onError(Throwable t) {

            }

            @Override
            public void onCompleted() {

            }
        });
        Message req = Message.newBuilder().setMessage(ByteString.copyFrom("Hello World".getBytes()))
                .build();
        requestObserver.onNext(req);
        requestObserver.onNext(req);
        requestObserver.onNext(req);
        requestObserver.onNext(req);
        call.cancel("hello ex", new RuntimeException("ex"));
        requestObserver.onCompleted();
        requestObserver.onNext(req);
        requestObserver.onNext(req);
        System.in.read();
    }
}
