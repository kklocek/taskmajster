package models

import org.joda.time.DateTime
import scala.annotation.tailrec

/**
 * Created by Konrad on 2014-12-26.
 */
class SleepUserCalendar(private val sleepStart: Int, private val sleepEnd: Int) extends UserCalendar {

  private var events: List[EventOccurrence] = List()
  private val desc: String = "Sleep"
  private val isTwoDayDream = sleepStart > sleepEnd

  def parseCalendar(): Unit = {}

  def getEvents(from: DateTime, to: DateTime): List[EventOccurrence] = {

    def getEventsAcc(startDate: DateTime, acc: List[EventOccurrence]): List[EventOccurrence] = {
      if (startDate.compareTo(to) > 0) {
        acc
      }
      else {
        if (isTwoDayDream) {
            val newEventDate = startDate.withHourOfDay(sleepStart)
            if (startDate.compareTo(newEventDate) <= 0)
              getEventsAcc(startDate.plusDays(1), new EventOccurrence(newEventDate, newEventDate.withHourOfDay(sleepEnd) plusDays (1), desc) :: acc)
            else
              getEventsAcc(startDate.plusDays(1), new EventOccurrence(newEventDate.minusDays(1), newEventDate.withHourOfDay(sleepEnd), desc) :: acc)

        }
        else {
            val newEventDate = startDate.withHourOfDay(sleepStart)
            getEventsAcc(startDate.plusDays(1), new EventOccurrence(newEventDate, newEventDate.withHourOfDay(sleepEnd), desc) :: acc)
        }
      }

    }
    getEventsAcc(from, List())
  }
}
