package GameServer.Routing

import akka.actor.ActorSystem
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import akka.http.scaladsl.model.ws.{BinaryMessage, Message, TextMessage}
import akka.http.scaladsl.server.{Directives, Route}
import akka.stream.ActorMaterializer
import akka.stream.scaladsl.{Flow, Sink}
import io.circe.syntax.EncoderOps
import spray.json.{DefaultJsonProtocol, RootJsonFormat}
import GameServer.Service.GameService._

import java.io.ByteArrayOutputStream
import java.time.LocalDateTime
import scala.concurrent.ExecutionContextExecutor
import scala.util.parsing.json.JSON


case class Game(messageType: String, players: Int)

trait GameJson extends DefaultJsonProtocol {
  implicit val personFormat: RootJsonFormat[Game] = jsonFormat2(Game)
}

class MainGame extends Directives
  with SprayJsonSupport with GameJson {
  implicit val system: ActorSystem = ActorSystem()
  implicit val materializer: ActorMaterializer = ActorMaterializer()
  implicit val executionContext: ExecutionContextExecutor = system.dispatcher


  val websocketRoute: Route = (path("new-game") & get) {
    handleWebSocketMessages(playGame())
  }
  val websocketRoute2: Route = (path("ping-pong") & get) {
    handleWebSocketMessages(pingPong())
  }
  val websocketRoutes: Route = concat(
    websocketRoute, websocketRoute2
  )
  val stream: ByteArrayOutputStream = new ByteArrayOutputStream()

  def playGame(): Flow[Message, Message, Any] =
    try {
      Flow[Message].mapConcat {

        case tm: TextMessage =>
          println(tm.getStrictText)
          val body = JSON.parseFull(tm.getStrictText).orNull.asInstanceOf[Map[String, Any]]
          val numberOfPlayers = body("players").asInstanceOf[Double]
          val resultJson = Map("results" -> gameFlow(numberOfPlayers.asInstanceOf[Int]).asJson).asJson
          val responseJson = resultJson.deepMerge(Map("message_type" -> "response.results").asJson)
          TextMessage(responseJson.toString()) :: Nil
        case bm: BinaryMessage =>
          // ignore binary messages but drain content to avoid the stream being clogged
          bm.dataStream.runWith(Sink.ignore)
          Nil
      }


    }
    catch {
      case e: Exception => println(e)
        null
    }

  def pingPong(): Flow[Message, Message, Any] = {
    try {
      Flow[Message].mapConcat {
        case tm: TextMessage => {
          println(tm.getStrictText)
          val body = JSON.parseFull(tm.getStrictText).orNull.asInstanceOf[Map[String, Any]]
          val response = Map("message_type" -> "response.pong".asJson,
            "request_id" -> body("id").asInstanceOf[Double].asInstanceOf[Int].asJson,
            "created_at" -> body("time_stamp").asInstanceOf[String].asJson,
            "time_stamp" -> LocalDateTime.now().toString.asJson).asJson
          TextMessage(response.toString()) :: Nil
        }
        case bm: BinaryMessage =>
          // ignore binary messages but drain content to avoid the stream being clogged
          bm.dataStream.runWith(Sink.ignore)
          Nil
      }
    }
    catch {
      case e: Exception => println(e)
        null
    }
  }
}