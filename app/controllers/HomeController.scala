package controllers

import javax.inject._
import play.api._
import play.api.mvc._

import de.htwg.lovecraftletter.controller.controllerImpl._
import de.htwg.lovecraftletter.model.GameStateImpl.GameState
import de.htwg.lovecraftletter.controller.controllState
import de.htwg.lovecraftletter.aview._
import java.lang.ProcessBuilder.Redirect

/**
 * This controller creates an `Action` to handle HTTP requests to the
 * application's home page.
 */
@Singleton
class HomeController @Inject()(val controllerComponents: ControllerComponents) extends BaseController {
    val gameController = new Controller(GameState(0, Nil, Nil, 0), (controllState.standard, ""), -999)
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
  def board() = Action {
    //Content html = views.html.Application.index.render(customer, orders);
    //views.html.index.scala.render(gameController.handle);
    val board = gameController.handle
    //Ok(board)
    Ok(views.html.board(board))
  }
  def runLL() = Action {
    gameController.runLL
    Ok("Game started")
  }
  def initNewGame() = Action {
    gameController.runLL
    Redirect(routes.HomeController.board())
    //board()
  }
  def getPlayerAmount(player: Int) = Action {
    gameController.playerAmount(player)
    Ok("Game started with " + player + " players")
  }
  def setPlayerName(name: String) = Action {
    gameController.playerName(name)
    Ok("Player name set to " + name)
  }
  def setPlayerNameRed(name: String) = Action {
    gameController.playerName(name)
    Redirect(routes.HomeController.board())
  }
  def playCard(card: Int) = Action {
    gameController.setVarUserInput(card)
    gameController.makeTurn
    Ok("Card " + card + " played")
  }
  def playCardRed(card: Int) = Action {
    gameController.setVarUserInput(card)
    gameController.makeTurn
    Redirect(routes.HomeController.board())
  }
  def selectPlayer(player: Int) = Action {
    gameController.playerChoosed(player)
    Ok("Player " + player + " selected")
  }
  def selectPlayerRed(player: Int) = Action {
    gameController.playerChoosed(player)
    Redirect(routes.HomeController.board())
  }
}
