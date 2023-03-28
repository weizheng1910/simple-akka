import Main.Counter.Increment
import akka.actor.typed.{ActorRef, ActorSystem, Behavior}
import akka.actor.typed.scaladsl.{AbstractBehavior, ActorContext, Behaviors}
import akka.actor.{Actor, Props, TypedActor, TypedProps}
import akka.event.Logging

object Main {

  class HelloActor extends Actor {
    val log = Logging(context.system, this)

    def receive = {
      case "hello" => {
        log.info("{} is saying Hello", context.self.toString())
        println("hello back at you")
      }
      case _ => println("huh?")
    }
  }

  object RootActor{

    final case class Spawn()

    def apply(): Behavior[Spawn] =

      Behaviors.setup { context =>
        // Upon creation of the RootActor object, create the Counter actor
        val counter = context.spawn(Counter(), "Counter")

        /* Returns the behavior object. This object listens for the spawn command, which upon receiving the command,
        performs some logic
        */
        Behaviors.receiveMessage { message =>
          counter ! Counter.Increment
          Behaviors.same
        }
      }

  }

  object Counter {
    sealed trait Command
    case object Increment extends Command
    final case class GetValue(replyTo: ActorRef[Value]) extends Command
    final case class Value(n: Int)

    def apply(): Behavior[Command] =
      counter(0)

    private def counter(n: Int): Behavior[Command] =
      Behaviors.receive { (context, message) =>
        message match {
          case Increment =>
            val newValue = n + 1
            context.log.info("Incremented counter to [{}]", newValue)
            counter(newValue)

          case GetValue(replyTo) =>
            replyTo ! Value(n)
            Behaviors.same
        }
      }
  }

  def split(arr: Array[Int]): Unit ={
    var arr = Array(6,5,4,3,2,1)
    var arrs = arr.splitAt(arr.length/2)
    var arr1 = arrs._1
    var arr2 = arrs._2
  }

  def main(args: Array[String]): Unit = {

    val system: ActorSystem[RootActor.Spawn] =
      ActorSystem(RootActor(), "hello")

    system ! RootActor.Spawn()



  }
}