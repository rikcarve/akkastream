package ch.carve.akkastream;

import java.util.concurrent.CompletionStage;

import akka.NotUsed;
import akka.actor.ActorSystem;
import akka.stream.ActorMaterializer;
import akka.stream.Materializer;
import akka.stream.javadsl.Flow;
import akka.stream.javadsl.Framing;
import akka.stream.javadsl.FramingTruncation;
import akka.stream.javadsl.Source;
import akka.stream.javadsl.Tcp;
import akka.stream.javadsl.Tcp.IncomingConnection;
import akka.stream.javadsl.Tcp.ServerBinding;
import akka.util.ByteString;

public class Main {

    public static void main(String[] args) {
        final ActorSystem system = ActorSystem.create("rik");
        final Materializer materializer = ActorMaterializer.create(system);

        final Source<IncomingConnection, CompletionStage<ServerBinding>> connections = Tcp.get(system).bind("127.0.0.1", 8888);
        connections.runForeach(connection -> {
            System.out.println("New connection from: " + connection.remoteAddress());

            final Flow<ByteString, ByteString, NotUsed> echo = Flow.of(ByteString.class)
                    .via(Framing.delimiter(ByteString.fromString("\n"), 256, FramingTruncation.DISALLOW))
                    .map(ByteString::utf8String)
                    .map(s -> s + " proxied!\n")
                    .log("bla")
                    .map(ByteString::fromString)
                    .via(Tcp.get(system).outgoingConnection("localhost", 9999));
            connection.handleWith(echo, materializer);
        }, materializer);

        // final Source<Integer, NotUsed> source = Source.range(1, 100);
        // source.runForeach(i -> System.out.println(i), materializer).thenRun(() ->
        // system.terminate());

    }
}
