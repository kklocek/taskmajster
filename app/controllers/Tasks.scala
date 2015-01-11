package controllers

import models.tasks._
import org.joda.time.Period
import play.api._
import play.api.data.format.Formatter
import play.api.data.{FormError, Form}
import play.api.data.Forms._
import play.api.mvc._
import play.twirl.api.Html

import scala.util.Try

object Tasks extends Controller {

  def index = Action {
    Ok(views.html.tasks.list(Task.getAll))
  }

  def add = Action {
    Ok(views.html.tasks.add())
  }

  implicit val priorityFormatter = new Formatter[Priority] {
    def bind(key: String, data: Map[String, String]) =
      data.get(key) map { value =>
        Try {
          Right(Priority(value))
        } getOrElse Left(Seq(FormError(key, "error.priority", Nil)))
      } getOrElse Left(Seq(FormError(key, "error.required", Nil)))

    def unbind(key: String, priority: Priority) = Map(key -> priority.toString)
  }

  implicit val periodFormatter = new Formatter[Period] {
    def bind(key: String, data: Map[String, String]) =
      data.get(key) map { value =>
        Try {
          Right(Period.hours(value.toInt))
        } getOrElse Left(Seq(FormError(key, "error.period", Nil)))
      } getOrElse Left(Seq(FormError(key, "error.required", Nil)))

    def unbind(key: String, period: Period) = Map(key -> period.getHours.toString)
  }


  val longtermForm = Form(mapping(
    "name" -> nonEmptyText,
    "priority" -> of[Priority]
  )(LongtermTask.apply)(LongtermTask.unapply))

  def addLongtermForm = Action {
    Ok(views.html.tasks.add_longterm(longtermForm))
  }

  def addLongterm = Action { implicit request =>
    longtermForm.bindFromRequest.fold(
      formWithErrors => Ok(views.html.tasks.add_longterm(formWithErrors)),
      value => {
        Task.store(value)
        Redirect(routes.Tasks.index)
      }
    )
  }


  val currentForm = Form(mapping(
    "name" -> nonEmptyText,
    "priority" -> of[Priority],
    "continuous" -> boolean,
    "length" -> of[Period]
  )(CurrentTask.apply)(CurrentTask.unapply))

  def addCurrentForm = Action {
    Ok(views.html.tasks.add_current(currentForm))
  }

  def addCurrent = Action { implicit request =>
    currentForm.bindFromRequest.fold(
      formWithErrors => Ok(views.html.tasks.add_current(formWithErrors)),
      value => {
        Task.store(value)
        Redirect(routes.Tasks.index)
      }
    )
  }


  def delete(id: Long) = Action {
    Redirect(routes.Tasks.index)
  }

}