package controllers
import Services.TwitterService
import javax.inject._
import play.api._
import play.api.libs.ws.WSClient
import play.api.mvc._
import play.api.libs.json.Json

import scala.concurrent.ExecutionContext

/**
 * This controller creates an `Action` to handle HTTP requests to the
 * application's home page.
 */
@Singleton
class HomeController @Inject()(ws: WSClient,
                               cc: ControllerComponents,
                               twitterService: TwitterService
                              )(implicit ec: ExecutionContext) extends AbstractController(cc) with Logging {

  /**
   * Create an Action to render an HTML page.
   *
   * The configuration in the `routes` file means that this method
   * will be called when the application receives a `GET` request with
   * a path of `/`.
   */

  def index() = Action { implicit request: Request[AnyContent] =>
    Ok("Working")
  }

  def tweets = Action.async { implicit  request: Request[AnyContent] =>
    twitterService.connectToFilteredStream.map {
      case response if response.status.equals(201) => {
        Ok("Okay this works but I now need to stream the data")
      }
      case _ => InternalServerError("Some went wrong")
    }
  }

  def setFilterRules = Action { implicit  request: Request[AnyContent] =>
    twitterService.setRules
    Ok(Json.obj("Info" -> "Check application logs to see if rules set successfully"))
  }
}
