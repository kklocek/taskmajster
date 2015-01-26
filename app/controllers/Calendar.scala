package controllers

import org.joda.time._
import org.joda.time.format.{DateTimeFormatter, DateTimeFormat}
import play.api._
import play.api.mvc._
import play.api.libs.json.Json
//Hardcode
import play.Play
import models.ICalUserCalendar

object Calendar extends Controller {

  val path = Play.application().path().getAbsolutePath + "/test/schedule.ics"
  val userCalendar = new ICalUserCalendar(path)
  def index = Action { implicit request =>
    Ok(views.html.calendar())
  }

  def getCalendar(start:String, end:String) = Action { implicit request =>
    //date are in ISO-8601 format, so we need formatter
    val dateFormatter = DateTimeFormat.forPattern("YYYY-MM-dd")

    val dateStart:DateTime = dateFormatter.parseDateTime(start)
    val dateEnd:DateTime = dateFormatter.parseDateTime(end)
    val list = userCalendar.getEvents(dateStart, dateEnd).map(e => Json.obj("title" -> e.desc, "start" -> e.dateFrom.toString, "end" -> e.dateTo.toString))
    Ok(Json.toJson(list))
  }

}