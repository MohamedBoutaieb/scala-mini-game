package Client;

import io.circe.Json
import io.circe.syntax.EncoderOps

import java.time.LocalDateTime

class RequestManager {

    def newGameRequest(n:Int ) : Json = {
        val requestGame = Map("message_type" -> "request.play").asJson
        val request = requestGame.deepMerge(Map("players" -> n).asJson)
        request
    }
    def pingPongRequest(id :Int) : Json = {
        val requestGame = Map("message_type" -> "request.ping").asJson
        var request = requestGame.deepMerge(Map("time_stamp" -> LocalDateTime.now().toString()).asJson)
        request = request.deepMerge(Map("id" -> id).asJson)
        request
    }

}
