package ch.carve.akkastream;

import java.util.concurrent.CompletionStage;

import akka.actor.ActorSystem;
import akka.stream.ActorMaterializer;
import akka.stream.Materializer;
import akka.stream.javadsl.Flow;
import akka.stream.javadsl.Source;
import akka.stream.javadsl.Tcp;
import akka.stream.javadsl.Tcp.IncomingConnection;
import akka.stream.javadsl.Tcp.ServerBinding;
import akka.util.ByteString;

public class Echo {

    public static void main(String[] args) {
        final ActorSystem system = ActorSystem.create("rik");
        final Materializer materializer = ActorMaterializer.create(system);

        final Source<IncomingConnection, CompletionStage<ServerBinding>> connections = Tcp.get(system).bind("127.0.0.1", 9999);
        connections.runForeach(connection -> {
            System.out.println("New connection from: " + connection.remoteAddress());
            connection.handleWith(Flow.of(ByteString.class), materializer);
        }, materializer);
    }

}
