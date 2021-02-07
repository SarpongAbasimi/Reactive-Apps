package Services

import javax.inject.Inject
import play.api.libs.oauth.{ConsumerKey, RequestToken}
import play.api.{Configuration, Logging}
import play.api.libs.ws.WSClient
import play.api.libs.json.{JsValue, Json}
import scala.concurrent.{ExecutionContext }

trait TwitterAuth {
  def credentials: Option[(ConsumerKey, RequestToken)]
  def setBearerHeader (configuration: Configuration): (String, String)
  def setRules: Unit
}

object SampleRules {
  val add: JsValue = Json.obj("add" -> Json.arr(
    Json.obj("value" -> "COYS")
  ))
}

class TwitterService @Inject()(config: Configuration, ws: WSClient)(implicit ec: ExecutionContext) extends TwitterAuth with Logging{

  def connectToFilteredStream =  {
    ws
      .url(config.get[String]("twitter.streamUrl"))
      .addHttpHeaders(setBearerHeader(config))
      .get()
  }

  def setRules = ws
    .url(config.get[String]("twitter.rulesUrl"))
    .addHttpHeaders(("Accept" -> "application/json"), setBearerHeader(config))
    .post(SampleRules.add)
    .map(response => {
      if(response.status != 201) {
        logger.info("Something went wrong")
        logger.debug(s"Cannot add rules (HTTP ${response.status}): ${response.body}")
      } else {
        logger.info("Rules set successfully")
      }
    })

  def setBearerHeader(configuration: Configuration) = ("Authorization"-> s"Bearer ${configuration.get[String]("twitter.bearerToken")}")

  def credentials: Option[(ConsumerKey, RequestToken)] = for {
      apiKey <- config.getOptional[String]("twitter.apiKey")
      twitterSecretKey <- config.getOptional[String]("twitter.secretKey")
      accessToken <- config.getOptional[String]("accessToken")
      accessTokenSecret <- config.getOptional[String]("accessToken_secret")
    } yield (ConsumerKey(apiKey, twitterSecretKey), RequestToken(accessToken, accessTokenSecret))
}
