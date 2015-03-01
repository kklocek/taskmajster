package controllers

import org.joda.time._
import org.joda.time.format.{DateTimeFormatter, DateTimeFormat}
import play.api._
import play.api.mvc._
import play.api.libs.json.Json
//Hardcode
import play.Play
import models._
import models.tasks.Task


object Calendar extends Controller {

  val path = Play.application().path().getAbsolutePath + "/test/schedule.ics"
  val icalCalendar = new ICalUserCalendar(path)
  val sleepCalendar = new SleepUserCalendar(23, 7)
  val userCalendar = new MultipleUserCalendar(icalCalendar, sleepCalendar)
  def index = Action { implicit request =>
    //Ok(views.html.calendar())
    request.session.get("login").map {
      user => Ok(views.html.calendar())
    }.getOrElse {
      Redirect(routes.Application.index()).flashing("error" -> "You have to log in firstly")
    }
  }

  def getCalendar(start:String, end:String) = Action { implicit request =>
    //date are in ISO-8601 format, so we need formatter
    val dateFormatter = DateTimeFormat.forPattern("YYYY-MM-dd")

    val dateStart:DateTime = dateFormatter.parseDateTime(start)
    val dateEnd:DateTime = dateFormatter.parseDateTime(end)
    val list = userCalendar.getEvents(dateStart, dateEnd).map(e => Json.obj("title" -> e.desc, "start" -> e.dateFrom.toString, "end" -> e.dateTo.toString))
    Ok(Json.toJson(list))
  }

  def getTasks(start:String, end:String) = Action { implicit request =>
    val tasks = Task.getAll
    val solver = new AspasiaSolver(tasks, userCalendar)

    val dateFormatter = DateTimeFormat.forPattern("YYYY-MM-dd")

    val dateStart:DateTime = dateFormatter.parseDateTime(start)
    val dateEnd:DateTime = dateFormatter.parseDateTime(end)

    val suggestion = solver.solve(dateStart, dateEnd)
    val list = suggestion.getSolution().toList.map(p => Json.obj("title" -> p._1.name, "start" -> p._2._1.toString, "end" -> p._2._2.toString))

    Ok(Json.toJson(list))
  }

}