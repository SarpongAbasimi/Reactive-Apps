package controllers
import play.api.Configuration
import javax.inject._
import play.api._
import play.api.libs.ws.WSClient
import play.api.mvc._

import scala.concurrent.ExecutionContext
import scala.concurrent.Future
import play.api.libs.json.{JsValue, Json}
import play.api.libs.oauth.{ConsumerKey, OAuthCalculator, RequestToken}

/**
 * This controller creates an `Action` to handle HTTP requests to the
 * application's home page.
 */
@Singleton
class HomeController @Inject()(ws: WSClient, cc: ControllerComponents, config: Configuration)(implicit ec: ExecutionContext) extends AbstractController(cc) with Logging {

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

  def tweets() = Action.async { implicit request: Request[AnyContent] =>
    credentials.map { case (consumerKey, requestToken) =>
        ws
          .url(config.get[String]("streamUrl"))
          .sign(OAuthCalculator(consumerKey, requestToken))
          .get()
          .map { response => Ok(response.body) }
    } getOrElse Future.successful( InternalServerError("Twitter is mess up -> Credential missing"))
  }

  private def credentials: Option[(ConsumerKey, RequestToken)] = {
    for {
      apiKey <- config.getOptional("twitter.apiKey")
      twitterSecretKey <- config.getOptional("twitter.secretKey")
      accessToken <- config.getOptional("accessToken")
      accessTokenSecret <- config.getOptional("accessToken_secret")
    } yield (ConsumerKey(apiKey, twitterSecretKey), RequestToken(accessToken, accessTokenSecret))
  }
}
