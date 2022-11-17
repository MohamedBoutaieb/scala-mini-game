package Client

import akka.{Done, NotUsed}
import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.model.ws.{Message, TextMessage, WebSocketRequest}
import akka.stream.ActorMaterializer
import akka.stream.scaladsl.{Flow, Keep, Sink, Source}
import com.typesafe.config.ConfigFactory
import io.circe.syntax.EncoderOps
import spray.json._

import java.time.{LocalDate, LocalDateTime}
import scala.concurrent.Future

object Client extends DefaultJsonProtocol {

  var id = 0
  val config = ConfigFactory.defaultApplication()
  private val serverAdress = config.getString("serverAdress")
  implicit val system = ActorSystem()

  def main(args: Array[String]): Unit = {
   startGame(15)
//     while(true){
//      pingPong()
//      Thread.sleep(1000)
//    }
  }


  def startGame(n: Int): Unit = {
    import system.dispatcher
    // Future[Done] is the materialized value of Sink.foreach,
    // emitted when the stream completes
    val printSink: Sink[Message, Future[Done]] =
    Sink.foreach {
      case message: TextMessage =>
        println(message.getStrictText)
      // ignore other message types
    }

    val requestGame = Map("message_type" -> "request.play").asJson
    val request = requestGame.deepMerge(Map("players" -> n).asJson)
    val outgoing = Source.single(TextMessage(request.toString()))

    val flow: Flow[Message, Message, Future[Done]] =
      Flow.fromSinkAndSourceMat(printSink, outgoing)(Keep.left)
    val (upgradeResponse, closed) =
      Http().singleWebSocketRequest(WebSocketRequest(s"ws://$serverAdress/new-game",
      ), flow)
    val connected = upgradeResponse.flatMap { upgrade =>
      if (upgrade.response.status == StatusCodes.SwitchingProtocols) {
        Future.successful(Done)
      } else {
        throw new RuntimeException(s"Connection failed: ${upgrade.response.status}")
      }
    }

    connected.onComplete(a => println(a.get))
    closed.foreach(_ => println("socket closed"))
  }

  def pingPong(): Unit = {
    import system.dispatcher
    // Future[Done] is the materialized value of Sink.foreach,
    // emitted when the stream completes
    val printSink: Sink[Message, Future[Done]] =
    Sink.foreach {
      case message: TextMessage =>
        println(message.getStrictText)
      // ignore other message types
    }
    val requestGame = Map("message_type" -> "request.ping").asJson
    var request = requestGame.deepMerge(Map("time_stamp" -> LocalDateTime.now().toString()).asJson)
    request = request.deepMerge(Map("id" -> id).asJson)
    id += 1;
    val outgoing = Source.single(TextMessage(request.toString()))

    val flow: Flow[Message, Message, Future[Done]] =
      Flow.fromSinkAndSourceMat(printSink, outgoing)(Keep.left)
    val (upgradeResponse, closed) =
      Http().singleWebSocketRequest(WebSocketRequest(s"ws://$serverAdress/ping-pong",
      ), flow)
    val connected = upgradeResponse.flatMap { upgrade =>
      if (upgrade.response.status == StatusCodes.SwitchingProtocols) {
        Future.successful(Done)
      } else {
        throw new RuntimeException(s"Connection failed: ${upgrade.response.status}")
      }
    }

    connected.onComplete(a => println(a.get))
    closed.foreach(_ => println("closed"))
  }

}

