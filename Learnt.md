## On the different ways of writing Actors, OOP V FP
* https://doc.akka.io/docs/akka/current/typed/style-guide.html
## On spawning new Actors
* We might see some documentation spawning actors that implements **Actor** from the ActorSystem directly, but this is outdated v.2.5 impl.
* In the new Akka typed actors, the ActorSystem itself is an Actor(with an ActorRef) responsible for creating other child actors.
```
class HelloActor extends Actor {
    def receive = {
      case "hello" => {
        log.info("{} is saying Hello", context.self.toString())
        println("hello back at you")
      }
      case _ => println("huh?")
    }
  }
    val helloActor = system.actorOf(Props[HelloActor], name = "helloactor")
    helloActor ! "hello"
    helloActor ! "buenos dias"
```