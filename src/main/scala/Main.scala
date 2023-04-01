import MergeSortWorker.Split
import akka.actor.typed.{ActorSystem, Behavior}
import akka.actor.typed.scaladsl.Behaviors

object Main {

  object RootActor{
    final case class Spawn()

    def apply(): Behavior[Spawn] =
      Behaviors.setup { context =>
        // Upon creation of the RootActor object, create the Counter actor
        val ms = context.spawn(MergeSortWorker(), "ms")

        /* Returns the behavior object. This object listens for the spawn command, which upon receiving the command,
        performs some logic
        */
        Behaviors.receiveMessage { message =>
          ms ! Split(Array(20,19,18,17,16,15,14,13,12,11,10,9,8,7,6,5,4,3,2,1),ms)
          Behaviors.same
        }
      }
  }

  def main(args: Array[String]): Unit = {
    val system: ActorSystem[RootActor.Spawn] =
      ActorSystem(RootActor(), "root")

    system ! RootActor.Spawn()
  }
}