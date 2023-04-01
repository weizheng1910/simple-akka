import akka.actor.typed.{ActorRef, Behavior}
import akka.actor.typed.scaladsl.{AbstractBehavior, ActorContext, Behaviors}
import akka.util.Timeout
import scala.concurrent.duration.DurationInt
import scala.util.{Failure, Success}

object MergeSortWorker {
  sealed trait Order
  final case class Return(sortedArr: Array[Int]) extends Order
  final case class Split(originalArr: Array[Int], replyTo: ActorRef[SortedArr]) extends Order
  final case class SortedArr(sortedArr: Array[Int]) extends Order

  private case class AdaptedResponse(payload: SortedArr, replyTo: ActorRef[SortedArr]) extends Order
  private case class AdaptedErrorResponse(error: String) extends Order

  def apply(): Behavior[Order] = {
    Behaviors.setup(context => new MergeSortWorker(context))
  }
}

class MergeSortWorker(context: ActorContext[MergeSortWorker.Order]) extends AbstractBehavior[MergeSortWorker.Order](context) {
  import MergeSortWorker._

  implicit val timeout: Timeout = 30.seconds
  private var sorted1: Array[Int] = null
  private var sorted2: Array[Int] = null
  private var receive = 0

  override def onMessage(msg: Order): Behavior[MergeSortWorker.Order] = {

    msg match {
      case SortedArr(sortedArr) =>
        context.log.info("Final answer: {} ", sortedArr.mkString(","))
        this

      case Split(originalArr, replyTo) =>
        context.log.info("{}, Split array {}",replyTo.path,originalArr.mkString(" "))
        if(originalArr.length == 1) {
          replyTo ! SortedArr(originalArr)
          return this
        }
        val w1 = context.spawn(MergeSortWorker(),"Left")
        val w2 = context.spawn(MergeSortWorker(), "Right")

        val arrs = originalArr.splitAt(originalArr.length / 2)
        val arr1 = arrs._1
        val arr2 = arrs._2

        context.ask(w1, (ref: ActorRef[SortedArr]) => Split(arr1,ref)){
          case Success(SortedArr(sortedArr)) =>
            AdaptedResponse(SortedArr(sortedArr),replyTo)
          case Failure(exception) =>
            AdaptedErrorResponse(exception.getMessage)
        }

        context.ask(w2, (ref: ActorRef[SortedArr]) => Split(arr2,ref)){
          case Success(SortedArr(sortedArr)) =>
            AdaptedResponse(SortedArr(sortedArr),replyTo)
          case Failure(exception) =>
            AdaptedErrorResponse(exception.getMessage)
        }
        Behaviors.same

      case AdaptedResponse(payload,replyTo) =>
        if(receive == 0){
          sorted1 = payload.sortedArr
          receive+=1
        } else if(receive == 1){
          sorted2 = payload.sortedArr

          var (i,j) = (0,0)
          val output = Array.newBuilder[Int]
          while(i < sorted1.length || j < sorted2.length){
            val takeLeft = (i<sorted1.length , j<sorted2.length) match {
              case(true,false) => true
              case(false,true) => false
              case(true,true) => sorted1(i) < sorted2(j)
            }
            if(takeLeft) {
              output += sorted1(i)
              i += 1
            } else {
              output += sorted2(j)
              j+=1
            }
          }

          replyTo ! SortedArr(output.result())

        }
        Behaviors.same

      case AdaptedErrorResponse(error) =>
        context.log.error(s"There was an error during encoding: $error")
        Behaviors.same


    }
  }
}