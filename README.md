# scala-mini-game

## 1. Description

The game is based on the following steps :
<ol>
<li> choosing a number of players </li>
<li> each players will have a random number between 0 and 999999 </li>
<li> the following formula will be applied for each player : <br> result =  sum(10^(number of occurences of each number-1) * that number)   </li>
<li> a bot will compete against the players and the winners will be the players who had better results than the bot</li>
<li>the winners are ranked by their results</li>
</ol>

## 2. Used Technologies
- Scala
- Akka Http / Akka Actors / Akka Sockets
- Circe

## 3. Endpoints
the server has 2 main endpoints :
- GET /new-game => starts a new game and returns the finals results
- GET /ping-pong => pings the server for a handshake (3 requests are done)

## 4. How to run the project

<ol>
<li> clone the project </li>
<li> build the project </li>
<li> you can either open a terminal at the project directory execute "sbt run" and run both servers <br>
or compile Server and Client on the terminal
</li>

</ol>