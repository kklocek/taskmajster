package controllers

import org.joda.time.DateTime
import play.api._
import play.api.mvc._
//Hardcode
import play.Play
import models.ICalUserCalendar

object Calendar extends Controller {

  val path = Play.application().path().getAbsolutePath + "/test/schedule.ics"
  val userCalendar = new ICalUserCalendar(path)
  def index = Action { implicit request =>
    Ok(views.html.calendar())
  }

  def getCalendar(from:String, to:String) = Action { implicit request =>
    //formatter?
    val dateStart:DateTime = new DateTime(from)
    val dateEnd:DateTime = new DateTime(to)
    Ok(userCalendar.getEvents(dateStart, dateEnd))
  }

}