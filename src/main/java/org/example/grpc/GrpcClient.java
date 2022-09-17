package org.example.grpc;

import com.google.protobuf.ByteString;
import io.grpc.ClientCall;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.stub.StreamObserver;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

import static io.grpc.stub.ClientCalls.asyncBidiStreamingCall;
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
        StreamObserver<Message> requestObserver = asyncBidiStreamingCall(call, new StreamObserver<Message>() {
            @Override
            public void onNext(Message value) {
                System.out.println("响应："
                        + value.getMessage().toString(StandardCharsets.UTF_8));
            }

            @Override
            public void onError(Throwable t) {
                t.printStackTrace();

            }

            @Override
            public void onCompleted() {
                System.out.println("onCompleted");

            }
        });
        Message req = Message.newBuilder().setMessage(ByteString.copyFrom("Hello World".getBytes()))
                .build();
        requestObserver.onNext(req);
        requestObserver.onNext(req);
        requestObserver.onNext(req);
        requestObserver.onNext(req);
        call.cancel("hello ex", new RuntimeException("ex"));
        requestObserver.onNext(req);
        requestObserver.onNext(req);
        requestObserver.onCompleted();
        System.in.read();
    }
}
