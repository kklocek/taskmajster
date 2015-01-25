import org.joda.time
import org.specs2.mutable._
import org.specs2.runner._
import org.junit.runner._
import models.ICalUserCalendar

import play.api.test._
import play.api.test.Helpers._
import play.Play

import org.joda.time.DateTime


/**
 * Created by Konrad on 2015-01-04.
 */
@RunWith(classOf[JUnitRunner])
class ICalUserCalendarTest extends Specification {

  //val path = System.getProperty("user.dir") + "/schedule.ics"

  "ICalUserCalendar" should {

    "Parse an ical without exception" in new WithApplication() {
      val path = Play.application().path().getAbsolutePath + "/test/schedule.ics"
        val a = new ICalUserCalendar(path)
    }

    "Get events from schedule.ics" in new WithApplication() {
      val path = Play.application().path().getAbsolutePath + "/test/schedule.ics"
      val a = new ICalUserCalendar(path)

      a.events.size must beEqualTo(19)
      val x = new DateTime(2014, 1, 1, 12, 0, 1)
      val list = a.getEvents( x, new DateTime())
      list.size must  beEqualTo(19)

      //list.foreach(e => println(e.dateFrom + " " + e.dateTo + " " +  e.desc))
    }

    "Gives good information about first calendar event with frequency and without it in list" in new WithApplication() {
      val path = Play.application().path().getAbsolutePath + "/test/schedule.ics"
      val a = new ICalUserCalendar(path)

      val calendarEvent = a.events.head
      val calendarEventWithFrequency = a.events.filter(p => p.isFrequent).head
      val calendarEventsWithFrequency = a.events.filter(p => p.isFrequent)
      calendarEvent must not be null
      calendarEventWithFrequency must not be null
    }

    "Get proper next occurrence of recursive event" in new WithApplication() {
      val path = Play.application().path().getAbsolutePath + "/test/schedule.ics"
      val a = new ICalUserCalendar(path)
      val x = new DateTime(2014, 1, 1, 0, 0, 1)
      //First event from list
      val event = a.events.head.nextOccurrence(x) match {
        case Some(p) => p
        case None => None
      }

      event must not be None
    }

    "Gives some event in the beginning of 3rd semester in schedule.ics" in new WithApplication() {
      val path = Play.application().path().getAbsolutePath + "/test/schedule.ics"
      val a = new ICalUserCalendar(path)

       val x = new DateTime(2014, 1, 1, 12, 0, 1)
       val y = new DateTime(2014, 9, 23, 14, 0, 0)
       val list = a.getEvents( x, y)
       list.size must  beEqualTo(7)
    }

    "Gives some events in the middle of 3rd semester in schedule.ics" in new WithApplication() {
      val path = Play.application().path().getAbsolutePath + "/test/schedule.ics"
      val a = new ICalUserCalendar(path)

       val x = new DateTime(2014, 12, 1, 8, 0, 1)
       val y = new DateTime(2014, 12, 3, 14, 0, 0)
       val list = a.getEvents( x, y)
       //list.foreach(e => println(e.dateFrom + " " + e.dateTo + " " +  e.desc))
       list.size must  beEqualTo(12)
    }

    "Gives only one event - Erlang lecture in schedule.ics" in new WithApplication() {
      val path = Play.application().path().getAbsolutePath + "/test/schedule.ics"
      val a = new ICalUserCalendar(path)

      val x = new DateTime(2014, 12, 3, 12, 52, 1)
      val y = new DateTime(2014, 12, 3, 14, 0, 0)
      val list = a.getEvents( x, y)
      //list.foreach(e => println(e.dateFrom + " " + e.dateTo + " " +  e.desc))
      list.size must  beEqualTo(1)
    }

    "From list of all events gives Erlang lecture one week earlier" in new WithApplication() {
      val path = Play.application().path().getAbsolutePath + "/test/schedule.ics"
      val a = new ICalUserCalendar(path)
      val erlangCalendarEvent = a.events.filter(p => p.desc.contentEquals("Erlang - dr inż. W. Turek, D17 1.38 - wykład")).head
      erlangCalendarEvent must not be null

      val x = new DateTime(2014, 12, 3, 12, 52, 1)
      val prev = erlangCalendarEvent.prevOccurrence(x) match {
        case Some(e) => println(e.dateFrom + " " + e.dateTo + " " +  e.desc)
                        e
        case None => None
      }

      prev must not be None

    }
  }

}
