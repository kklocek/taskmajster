package controllers

import play.api._
import play.api.mvc._

object Calendar extends Controller {

  def index = Action { implicit request =>
    Ok(views.html.calendar())
  }

}