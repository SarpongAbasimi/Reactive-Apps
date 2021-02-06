import GreeterMain.SayHello
import akka.actor.typed.{ActorSystem}

object Main extends App {
  val greeterMain: ActorSystem[GreeterMain.SayHello] = ActorSystem(GreeterMain(), "AkkaQuickStart")
  greeterMain ! SayHello("Charles")
}
