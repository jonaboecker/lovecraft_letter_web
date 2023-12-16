package controllers

import javax.inject._
import play.api._
import play.api.mvc._

import play.api.libs.streams.ActorFlow
import akka.actor.ActorSystem
import akka.stream.Materializer
import akka.actor._

import scala.swing.Reactor
import scala.collection.mutable.Map

import java.util.UUID

import akka.actor.ActorSystem
import play.api.mvc._
import akka.actor.ActorRef
import akka.actor.Props
import akka.stream.Materializer
import akka.actor.Actor
import akka.stream.Materializer
import akka.stream.OverflowStrategy
import akka.stream.scaladsl._

import de.htwg.lovecraftletter.controller.controllerImpl._
import de.htwg.lovecraftletter.model.GameStateImpl.GameState
import de.htwg.lovecraftletter.controller.controllState
import de.htwg.lovecraftletter.aview._
import de.htwg.lovecraftletter.util.Observer
import de.htwg.lovecraftletter.aview.TUI
import java.lang.ProcessBuilder.Redirect
import play.api.libs.json._

//val tui = new de.htwg.lovecraftletter.aview.TUI()


/**
 * This controller creates an `Action` to handle HTTP requests to the
 * application's home page.
 */
@Singleton
class HomeController @Inject()(val controllerComponents: ControllerComponents)(implicit system: ActorSystem, materializer: Materializer) extends BaseController with de.htwg.lovecraftletter.util.Observer {
    var gameController = new Controller(GameState(0, Nil, Nil, 0), (controllState.standard, ""), -999)
    //val tui = new de.htwg.lovecraftletter.aview.TUI(gameController)
    gameController.add(this)
  /**
   * Create an Action to render an HTML page.
   *
   * The configuration in the `routes` file means that this method
   * will be called when the application receives a `GET` request with
   * a path of `/`.
   */
  def index() = Action { implicit request: Request[AnyContent] =>
    Ok(views.html.index())
  }
  def health() = Action {
    Ok("ok")
  }
  override def update = {
    //show(controller.handle)
    println("Update received")
  }
  def tui() = Action {
    val tui = new de.htwg.lovecraftletter.aview.TUI(gameController)
    Ok("TUI started")
  }

  //Websocket
  // Map to store ActorRef objects
  val actorMap: Map[String, ActorRef] = Map()

  def socket = WebSocket.accept[String, String] { request =>
    ActorFlow.actorRef { out =>
      println("Connect received")
      val actor = LovecraftLetterActorFactory.create(out)
      val sessionId = UUID.randomUUID().toString // Generate a unique ID
      actorMap.put(sessionId, out) // Store the ActorRef
      actor
    }
  }

  object LovecraftLetterActorFactory {
    def create(out: ActorRef) = {
      Props(new LovecraftLetterWebSocketActor(out))
    }
  }

  class LovecraftLetterWebSocketActor(out: ActorRef) extends Actor with Reactor{
    override def postStop(): Unit = {
      // Hier wird ausgeführt, wenn die Verbindung geschlossen wird
      println("WebSocket connection closed")
      actorMap.retain((_, v) => v != this)
    }
    //listenTo(gameController)

    def receive = {
      case msg: String =>
        //out ! (gameController.toJson.toString)
        println("Received Message from Client: "+ msg)
    }

    /* reactions += {
      case event: GridSizeChanged => sendJsonToClient
      case event: CellChanged     => sendJsonToClient
      case event: CandidatesChanged => sendJsonToClient
    } */

    def sendJsonToClient(msg: String) = {
      println("Received event from Controller")
      out ! (msg)
    }
  }

  // Method to send a message to a specific session
  def sendMessageToWebsockets(message: JsValue): Unit = {
    actorMap.foreach { case (sessionId, actor) =>
      actor ! message.toString
    }
  }

  
  def board(player: Int) = Action {
    //Content html = views.html.Application.index.render(customer, orders);
    //views.html.index.scala.render(gameController.handle);
    val state = gameController.getVarControllerState
    val board = gameController.handle
    var temp = ""
    var numbers: Option[Vector[Int]]= None

    if(state == (controllState.standard, "")) {
      val lines = board.split("\n")
      val firstLine = lines(1)
      val lastLine = lines(lines.length - 1)
      var ablageStapel = -1
      if(gameController.state.player(gameController.state.currentPlayer).discardPile.length != 0)
      {
        ablageStapel = gameController.state.player(gameController.state.currentPlayer).discardPile(0)
        //println("Ablagestapel= " + ablageStapel)
      }
      Ok(views.html.board(firstLine, lastLine, gameController.state.currentCard, gameController.state.player(gameController.state.currentPlayer).hand, ablageStapel))
    } else {    
      if(state == (controllState.getEffectedPlayer, "")) {
        // boardtext = "Waehle einen Spieler auf den du deine Aktion anwenden willst Vector(1, 3)"
        numbers = Some(board.split(", ").map(_.replaceAll("[^\\d.]", "").toInt).toVector)
        temp = "selectPlayer"
        //Ok(views.html.boardNoCards(board, temp, numbers))
      }
      if(state == (controllState.getInvestigatorGuess, ""))
        temp = "getInvestigatorGuess"
      if(state == (controllState.getInputToPlayAnotherCard, ""))
        temp = "notImplementedYet"

      Ok(views.html.boardNoCards(board, temp))
    }
  }
  def getGameStartedPage(player: Int) = Action {
    Ok(views.html.gameStarted(player))
  }
  def initNewGame() = Action {
    gameController = new Controller(GameState(0, Nil, Nil, 0), (controllState.standard, ""), -999)
    gameController.runLL
    Ok("Game initialized")
  }
  def playCard(card: Int) = Action {
    gameController.setVarUserInput(card)
    gameController.makeTurn
    val json: JsValue = JsObject(Seq(
      "player" -> JsNumber(gameController.state.currentPlayer),
    ))
    sendMessageToWebsockets(json)
    //LovecraftLetterWebSocketActor.sendJsonToClient(gameController.gameState.currentPlayer.toString)
    Ok("Card " + card + " played")
  }
  def playCardRed(card: Int, player: Int) = Action {
    gameController.setVarUserInput(card)
    gameController.makeTurn
    val json: JsValue = JsObject(Seq(
      "player" -> JsNumber(gameController.state.currentPlayer),
    ))
    sendMessageToWebsockets(json)
    Redirect(routes.HomeController.board(player))
  }
  def save() = Action {
    //gameController.save
    Ok("Game saved")
  }
  def undoStep() = Action {
    gameController.undoStep
    Redirect(routes.HomeController.board(gameController.state.currentPlayer + 1))
  }
  def redoStep() = Action {
    gameController.redoStep
    Redirect(routes.HomeController.board(gameController.state.currentPlayer + 1))
  }
  def getSelectablePlayers() = Action {
    val board = gameController.handle
    if (gameController.getVarControllerState != (controllState.getEffectedPlayer, "")) {
    val json2: JsValue = JsObject(Seq(
      "player" -> JsObject(Seq(
        "numbers" -> JsArray()
      ))
    ))
    Ok(json2)
    } else {

      // boardtext = "Waehle einen Spieler auf den du deine Aktion anwenden willst Vector(1, 3)"
      val numbers = board.split(", ").map(_.replaceAll("[^\\d.]", "").toInt).toVector

      val json: JsValue = JsObject(Seq(
        "player" -> JsObject(Seq(
          "numbers" -> JsArray(numbers.map(JsNumber(_))),
        ))
      ))
      Ok(json)
    }
  }
  def setPlayerName() = Action(parse.json) { request =>
    val playerName = (request.body \ "playerName").asOpt[String]
    println("PlayerName: " + playerName)
    playerName match {
      case Some(name) =>
        // Hier kannst du die playerName-Funktion aufrufen oder verwenden, wie du es brauchst
        // Zum Beispiel:
        gameController.playerName(name)
        Ok(s"Player name set to $name")
      case None =>
        BadRequest("Player name not found in the request")
    }
  }
  def setPlayerAmount() = Action(parse.json) { request =>
    val playerAmount = (request.body \ "playerAmount").asOpt[String]
    //println("PlayerAmount: " + playerAmount)
    playerAmount match {
      case Some(player) =>
        // Hier kannst du die playerName-Funktion aufrufen oder verwenden, wie du es brauchst
        // Zum Beispiel:
        gameController.playerAmount(player.toInt)
        Ok(s"Player Amount set to $player")
      case None =>
        BadRequest("Player Amount not found in the request")
    }
  }
  def getCurrentPlayer() = Action {
    val json: JsValue = JsObject(Seq(
      "player" -> JsObject(Seq(
        "currentPlayer" -> JsNumber(gameController.state.currentPlayer),
      ))
    ))
    Ok(json)
  }
  def selectPlayer() = Action(parse.json) { request =>
    val player = (request.body \ "player").asOpt[Int]
    println("Player: " + player)
    player match {
      case Some(irgendwas) =>
        gameController.playerChoosed(irgendwas)
        val json: JsValue = JsObject(Seq(
          "player" -> JsNumber(gameController.state.currentPlayer),
        ))
        sendMessageToWebsockets(json)
        Ok(s"Player $irgendwas choosed")
      case None =>
        BadRequest("Player not found in the request")
    }
  }
  def getInvestigatorGuess() = Action(parse.json) { request =>
    val guess = (request.body \ "guess").asOpt[Int]
    println("Guess: " + guess)
    guess match {
      case Some(irgendwas) =>
        gameController.investgatorGuessed(irgendwas)
        val json: JsValue = JsObject(Seq(
          "player" -> JsNumber(gameController.state.currentPlayer),
        ))
        sendMessageToWebsockets(json)
        Ok(s"$irgendwas guessed")
      case None =>
        BadRequest("Guess not found in the request")
    }
  }
  def getCurrentCards() = Action {
    val json: JsValue = JsObject(Seq(
      "cards" -> JsObject(Seq(
        "card1" -> JsNumber(gameController.state.player(gameController.state.currentPlayer).hand),
        "card2" -> JsNumber(gameController.state.currentCard),
        "card3" -> JsNumber(gameController.state.player(gameController.state.currentPlayer).discardPile(0)),
      ))
    ))
    Ok(json)
  }
  def getFirstLineOfBoard() = Action {
    val json: JsValue = JsObject(Seq(
          "firstLine" -> JsString(gameController.handle.split("\n")(1)),
        ))
    Ok(json)
  }
  def test() = Action { implicit request: Request[AnyContent] =>
    Ok(views.html.test())
  }
}
