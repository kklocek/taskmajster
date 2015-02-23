package controllers

import play.api.mvc._
import play.api.data.Forms._
import play.api.data._
import models.{User, LoginData}

/**
 * Created by Konrad on 2015-02-23.
 */
object Login extends  Controller{

  def index = Action {
    implicit request => Ok(views.html.login(loginForm))
  }

  val loginForm = Form(mapping(
  "login" -> nonEmptyText,
  "password" -> nonEmptyText
  )(LoginData.apply)(LoginData.unapply))

  def log = Action {
    implicit request =>
      loginForm.bindFromRequest.fold(
        formWithErrors => BadRequest(views.html.index()),
        loginData => {
          val res = User.login(loginData.login, loginData.password)

          res match {
            case true =>  Redirect(routes.Application.index()).withNewSession
            case false => BadRequest(views.html.index())
          }
        }
      )
  }
}
