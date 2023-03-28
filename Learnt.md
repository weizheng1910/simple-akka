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
## On case object vs case class
``` scala
case object Increment extends Command
final case class GetValue(replyTo: ActorRef[Value]) extends Command
```
* When we look at those which implement the `Command` trait, some are `object` and some are `class`
* In the above example, `Increment` is `object` whilst `GetValue` is `class`
* For `object` there is only one instance of it, while for `class` there can be many instances, and they differ based on the value of the arguments.
* This makes sense because for `Increment`, all we need to know is that it will increase the value by one, but for `GetValue` it depends on which ActorRef we should send the value to.
* 