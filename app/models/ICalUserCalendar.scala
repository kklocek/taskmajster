package models

import net.fortuna.ical4j.data.CalendarBuilder
import net.fortuna.ical4j.model._
import net.fortuna.ical4j.model.property._
import java.io.FileInputStream
import org.joda.time.{LocalDateTime, Period, DateTime}
import scala.annotation.tailrec

/**
 * Created by Konrad on 2014-12-26.
 */
class ICalUserCalendar(private val path: String) extends UserCalendar {

  var events: List[CalendarEvent] = List()

  val in: FileInputStream = new FileInputStream(path)
  val builder: CalendarBuilder = new CalendarBuilder()
  private var calendar: Calendar = builder.build(in)

  this.parseCalendar()


  def parseCalendar(): Unit = {
    val iCalEvents = calendar.getComponents("VEVENT")
    events = convertEvents(iCalEvents)
  }

  //Collection pipeline pattern, yeah
  def getEvents(from: DateTime, to: DateTime): List[EventOccurrence] = events.filter(a => a.compareDates(from, to)).map(a => a.getEventOccurrence(from, to)).flatten


  //Converts from iCal event to app CalendarEvent list
  private def convertEvents(list: ComponentList): List[CalendarEvent] = {
    val size: Int = list.size()
    var iterator = list.iterator()
    var eventList: List[CalendarEvent] = List()
    while (iterator.hasNext) {
      val event = iterator.next().asInstanceOf[Component] //Ugly...
      val desc: String = event.getProperty("SUMMARY").getValue

      val dateStartToPrepare = event.getProperty("DTSTART").getValue
      val dateEndToPrepare = event.getProperty("DTEND").getValue
      val frequency = event.getProperty("RRULE").asInstanceOf[RRule].getRecur.getFrequency //Again ugly :/
      val count = if (event.getProperty("RRULE").asInstanceOf[RRule].getRecur.getCount == -1) 0 else event.getProperty("RRULE").asInstanceOf[RRule].getRecur.getCount
      val interval = if (event.getProperty("RRULE").asInstanceOf[RRule].getRecur.getInterval == -1) 0 else event.getProperty("RRULE").asInstanceOf[RRule].getRecur.getInterval
      val weekStartDate = event.getProperty("RRULE").asInstanceOf[RRule].getRecur.getWeekStartDay

      // :D :D :D
      val dateStart = new DateTime(new net.fortuna.ical4j.model.DateTime(dateStartToPrepare))
      val dateEnd = new DateTime(new net.fortuna.ical4j.model.DateTime(dateEndToPrepare))

      val dateEndFrequency = event.getProperty("RRULE").asInstanceOf[RRule].getRecur.getUntil

      //Very ugly block, could it be better?
      var untilDate:DateTime = new DateTime()
      if(dateEndFrequency != null) {
        untilDate = new DateTime(new net.fortuna.ical4j.model.DateTime(dateEndFrequency))
      }
      else {
        untilDate = null
      }


      val foo = new CalendarEvent(desc, dateStart, dateEnd, frequency, untilDate, weekStartDate, count, interval)
      eventList = foo :: eventList
    }

    eventList
  }

}
