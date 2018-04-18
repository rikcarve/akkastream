package ch.carve.akkastream;

import java.util.concurrent.TimeUnit;

import akka.actor.ActorSystem;
import akka.stream.ActorMaterializer;
import akka.stream.Materializer;
import akka.stream.javadsl.Source;
import akka.stream.javadsl.Tcp;
import akka.util.ByteString;
import scala.concurrent.duration.FiniteDuration;

public class Sender {
    public static void main(String[] args) {
        final ActorSystem system = ActorSystem.create("rik");
        final Materializer materializer = ActorMaterializer.create(system);

        Source.tick(FiniteDuration.create(1, TimeUnit.SECONDS), FiniteDuration.create(500, TimeUnit.MILLISECONDS), ByteString.fromString("Hello\n"))
                .via(Tcp.get(system).outgoingConnection("localhost", 8888))
                .runForeach(f -> System.out.println(f.utf8String()), materializer);

    }

}
