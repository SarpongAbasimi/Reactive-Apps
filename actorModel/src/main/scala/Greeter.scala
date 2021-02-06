import akka.actor.typed.javadsl.Behaviors
import akka.actor.typed.{ActorRef, Behavior}

object Greeter {
  final case class Greet(whom: String, replyTo: ActorRef[Greeted])
  final case class Greeted(whom: String, from: ActorRef[Greet])

  def apply(): Behavior[Greet] = Behaviors.receive {(context, message) =>
    context.getLog.info("Hello", message.whom)
    message.replyTo ! Greeted(message.whom, context.getSelf)
    Behaviors.same
  }
}
