package GameServer

import GameServer.Routing.MainGame
import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.stream.ActorMaterializer
import com.typesafe.config.ConfigFactory

import scala.io.StdIn

object Server {

  object Config {
    val config = ConfigFactory.defaultApplication()
    val port = config.getInt("server.port")
    val host = config.getString("server.host")
  }

  def main(args: Array[String]) {
    implicit val system = ActorSystem()
    implicit val materializer = ActorMaterializer()
    implicit val executionContext = system.dispatcher

    val mainGameRouter = new MainGame()
    val bindingFuture = Http().bindAndHandle(mainGameRouter.websocketRoutes, Config.host,Config.port)
    println(s"Server online at ${Config.host}:${Config.port}\nPress RETURN to stop...")
    StdIn.readLine()
    bindingFuture
      .flatMap(_.unbind())
      .onComplete(_ => system.terminate())
  }
}