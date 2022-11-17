package GameServer.Service

import Client.Player
import io.circe.{Encoder, Json}
import io.circe.syntax.EncoderOps

import scala.math.pow

object GameService {
  implicit val encodeFoo: Encoder[Player] = new Encoder[Player] {
    final def apply(player: Player): Json = Json.obj(
      ("position", Json.fromInt(player.position)),
      ("id", Json.fromInt(player.id)),
      ("number", Json.fromLong(player.number)),
      ("result", Json.fromLong(player.result))
    )
  }
  def gameFlow(n: Int): Json = {
    var players = List[Player]()
    for (i <- 1 to n) {
      val player = new Player(i)
      players = players :+ player
    }
    for (player <- players) {
      player.number = getRandom()
      player.result = getResult(player.number)
    }
    val bot = botPlay()
    var winners = List[Player]()
    for (player <- players) {
      if (player.result > bot) {
        winners = winners :+ player
      }
    }
    winners = winners.sortWith(_.result > _.result)
    for (i <- 0 until winners.size) {
      winners(i).position = i+1
    }
    if(winners.size== 0){
      return "No one has won !".asJson
    }
    winners.asJson
  }

  def getResult(numb: Long): Long = {
    var numbersOcc: Map[Int, Int] = Map()
    for (i <- 0 to 10) {
      numbersOcc += (i -> 0)
    }
    val num = numb.toString
    for (letter <- num) {
      val n = letter.toInt - '0'.toInt
      numbersOcc += (n -> (numbersOcc(n) + 1))
    }
    var result: Long = 0
    for ((k, v) <- numbersOcc) {
      result += (pow(10, v - 1).toLong * k)
    }
    result
  }

  def botPlay(): Long = {
    val num = getRandom()
    getResult(num)
  }

  def getRandom(): Long = {
    val rand = new scala.util.Random
    rand.nextLong(999999);
  }
}
