import akka.actor.typed.{ActorRef, Behavior}
import akka.actor.typed.scaladsl.{AbstractBehavior, ActorContext, Behaviors}

object MSWorker {
  sealed trait Order
  final case class Return(sortedArr: Array[Int]) extends Order
  final case class Split(originalArr: Array[Int], replyTo: ActorRef[MSWorker]) extends Order

  def apply(): Behavior[Order] = {
    Behaviors.setup(context => new MSWorker(context))
  }
}

class MSWorker(context: ActorContext[MSWorker.Order]) extends AbstractBehavior[MSWorker.Order](context) {
  import MSWorker._

  private var sorted1: Array[Int] = null
  private var sorted2: Array[Int] = null
  private var receive = 0
  private var parent: ActorRef[Return] = null


  override def onMessage(msg: Order): Behavior[MSWorker.Order] = {
    msg match {
      case Return(sortedArr) =>
        receive+=1
        if(receive == 1){
          sorted1 = sortedArr
        } else if(receive == 2){
          sorted2 = sortedArr
          var ans = Array.concat(sorted1,sorted2).sorted
          parent ! MSWorker.Return(ans)
        }
        this

      case Split(originalArr, replyTo) =>
        val w1 = context.spawn(MSWorker(),"dd")
        val w2 = context.spawn(MSWorker(), "EE")


        var arrs = originalArr.splitAt(originalArr.length/2)
        var arr1 = arrs._1
        var arr2 = arrs._2


        this
    }
  }
}