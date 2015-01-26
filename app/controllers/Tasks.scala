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

  def index = Action { implicit request =>
    Ok(views.html.tasks.list(Task.getAll))
  }

  def add = Action { implicit request =>
    Ok(views.html.tasks.add())
  }

  implicit val priorityFormatter = new Formatter[Priority] {
    def bind(key: String, data: Map[String, String]) =
      data.get(key) map { value =>
        Try {
          Right(Priority(value))
        } getOrElse Left(Seq(FormError(key, "error.priority", Nil)))
      } getOrElse Left(Seq(FormError(key, "error.required", Nil)))

    def unbind(key: String, priority: Priority) = Map(key -> priority.value.toString)
  }

  implicit val periodFormatter = new Formatter[Period] {
    def bind(key: String, data: Map[String, String]) =
      data.get(key) map { value =>
        Try {
          Right(Period.seconds((value.toDouble*3600).toInt))
        } getOrElse Left(Seq(FormError(key, "error.period", Nil)))
      } getOrElse Left(Seq(FormError(key, "error.required", Nil)))

    def unbind(key: String, period: Period) = Map(key -> (period.toStandardSeconds.getSeconds.toDouble/3600).toString)
  }


  val deadlineForm = Form(mapping(
    "name" -> nonEmptyText,
    "priority" -> of[Priority],
    "deadline" -> jodaDate("dd.MM.yyyy HH:mm"),
    "continuous" -> boolean,
    "length" -> of[Period]
  )(DeadlineTask.apply)(DeadlineTask.unapply))

  def addDeadlineForm = Action { implicit request =>
    Ok(views.html.tasks.edit_deadline(None, deadlineForm))
  }

  def addDeadline = Action { implicit request =>
    deadlineForm.bindFromRequest.fold(
      formWithErrors => Ok(views.html.tasks.edit_deadline(None, formWithErrors)),
      value => {
        Task.insert(value)
        Redirect(routes.Tasks.index).flashing("success" -> "Added new task")
      }
    )
  }


  val frequentForm = Form(mapping(
    "name" -> nonEmptyText,
    "priority" -> of[Priority],
    "period" -> of[Period],
    "continuous" -> boolean,
    "length" -> of[Period]
  )(FrequentTask.apply)(FrequentTask.unapply))

  def addFrequentForm = Action { implicit request =>
    Ok(views.html.tasks.edit_frequent(None, frequentForm))
  }

  def addFrequent = Action { implicit request =>
    frequentForm.bindFromRequest.fold(
      formWithErrors => Ok(views.html.tasks.edit_frequent(None, formWithErrors)),
      value => {
        Task.insert(value)
        Redirect(routes.Tasks.index).flashing("success" -> "Added new task")
      }
    )
  }


  val longtermForm = Form(mapping(
    "name" -> nonEmptyText,
    "priority" -> of[Priority]
  )(LongtermTask.apply)(LongtermTask.unapply))

  def addLongtermForm = Action { implicit request =>
    Ok(views.html.tasks.edit_longterm(None, longtermForm))
  }

  def addLongterm = Action { implicit request =>
    longtermForm.bindFromRequest.fold(
      formWithErrors => Ok(views.html.tasks.edit_longterm(None, formWithErrors)),
      value => {
        Task.insert(value)
        Redirect(routes.Tasks.index).flashing("success" -> "Added new task")
      }
    )
  }


  val currentForm = Form(mapping(
    "name" -> nonEmptyText,
    "priority" -> of[Priority],
    "continuous" -> boolean,
    "length" -> of[Period]
  )(CurrentTask.apply)(CurrentTask.unapply))

  def addCurrentForm = Action { implicit request =>
    Ok(views.html.tasks.edit_current(None, currentForm))
  }

  def addCurrent = Action { implicit request =>
    currentForm.bindFromRequest.fold(
      formWithErrors => Ok(views.html.tasks.edit_current(None, formWithErrors)),
      value => {
        Task.insert(value)
        Redirect(routes.Tasks.index).flashing("success" -> "Added new task")
      }
    )
  }


  def edit(id: Long) = Action { implicit request =>
    Task.get(id) match {
      case Some(task) => {
        task match {
          case deadlineTask: DeadlineTask => {
            deadlineForm.bindFromRequest.fold(
              formWithErrors => Ok(views.html.tasks.edit_deadline(task.id, deadlineForm.fill(deadlineTask))),
              value => {
                Task.update(deadlineTask.id.get, value)
                Redirect(routes.Tasks.index).flashing("success" -> "Task updated")
              }
            )
          }
          case frequentTask: FrequentTask => {
            frequentForm.bindFromRequest.fold(
              formWithErrors => Ok(views.html.tasks.edit_frequent(task.id, frequentForm.fill(frequentTask))),
              value => {
                Task.update(frequentTask.id.get, value)
                Redirect(routes.Tasks.index).flashing("success" -> "Task updated")
              }
            )
          }
          case currentTask: CurrentTask => {
            currentForm.bindFromRequest.fold(
              formWithErrors => Ok(views.html.tasks.edit_current(task.id, currentForm.fill(currentTask))),
              value => {
                Task.update(currentTask.id.get, value)
                Redirect(routes.Tasks.index).flashing("success" -> "Task updated")
              }
            )
          }
          case longtermTask: LongtermTask => {
            longtermForm.bindFromRequest.fold(
              formWithErrors => Ok(views.html.tasks.edit_longterm(task.id, longtermForm.fill(longtermTask))),
              value => {
                Task.update(longtermTask.id.get, value)
                Redirect(routes.Tasks.index).flashing("success" -> "Task updated")
              }
            )
          }
        }
      }
      case None =>
        Redirect(routes.Tasks.index).flashing("error" -> "Task does not exist")
    }
  }


  def editForm(id: Long) = Action { implicit request =>
    Task.get(id) match {
      case Some(task) => {
        task match {
          case deadlineTask: DeadlineTask => Ok(views.html.tasks.edit_deadline(deadlineTask.id, deadlineForm.fill(deadlineTask)))
          case frequentTask: FrequentTask => Ok(views.html.tasks.edit_frequent(frequentTask.id, frequentForm.fill(frequentTask)))
          case  currentTask:  CurrentTask => Ok(views.html.tasks.edit_current ( currentTask.id,  currentForm.fill( currentTask)))
          case longtermTask: LongtermTask => Ok(views.html.tasks.edit_longterm(longtermTask.id, longtermForm.fill(longtermTask)))
        }
      }
      case None =>
        Redirect(routes.Tasks.index).flashing("error" -> "Task does not exist")
    }
  }


  def delete(id: Long) = Action { implicit request =>
    Task.delete(id)
    Redirect(routes.Tasks.index).flashing("success" -> "Deleted task")
  }


}
