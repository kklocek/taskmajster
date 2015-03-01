package controllers

import play.api.mvc._
import play.api.data.Forms._
import play.api.data._
import models.User

/**
 * Created by Konrad on 2015-02-23.
 */

case class LoginData(login:String, password:String)

object Login extends  Controller{

  def index = Action {
    implicit request => Ok(views.html.login(loginForm))
  }

  def logout = Action {
    implicit request => Redirect(routes.Application.index()).withNewSession.flashing("success" -> "Log out completed successfully!")
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
            case true =>  Redirect(routes.Application.index()).withSession("login" -> loginData.login).flashing("success" -> "Log in completed!")
            case false => Ok(views.html.index()) //TODO: fix it in better way
          }
        }
      )
  }
}
