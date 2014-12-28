package controllers

import models.tasks.Task
import play.api._
import play.api.mvc._

object Application extends Controller {

  def index = Action {
    Ok(views.html.tasks(Task.getAll))
  }

}