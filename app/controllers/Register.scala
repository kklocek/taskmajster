package controllers

import play.api.data.Forms._
import play.api.data._
import models.{RegisterData,User}
import play.api.mvc._

/**
 * Created by Konrad on 2015-02-22.
 */
object Register extends Controller {
  def index = Action{
    implicit request => Ok(views.html.register(registerForm))}

  val registerForm = Form(mapping(
  "login" -> nonEmptyText,
  "mail" -> nonEmptyText,
  "password" -> nonEmptyText
  )(RegisterData.apply)(RegisterData.unapply))

  def addUser = Action { implicit request =>
    registerForm.bindFromRequest.fold(
      formWithErrors => {
        // binding failure, you retrieve the form containing errors:
        BadRequest(views.html.index())
      },
      userData => {
        val newUser = User(userData.login, userData.mail, userData.password)
        val res = User.put(newUser)
         res match {
           case true =>  Redirect(routes.Application.index()).withNewSession
           case false => BadRequest(views.html.index())
        }
      }
    )
  }
}
