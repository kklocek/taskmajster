package controllers

import play.api._
import play.api.mvc._

object About extends Controller {

  def index = Action {
    Ok(views.html.about())
  }

}