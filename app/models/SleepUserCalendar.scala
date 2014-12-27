package models

import org.joda.time.DateTime
import scala.annotation.tailrec

/**
 * Created by Konrad on 2014-12-26.
 */
class SleepUserCalendar(private val sleepStart: Int, private val sleepEnd: Int) extends UserCalendar {
  assert(sleepStart >= 0 && sleepStart <= 24 || sleepEnd >= 0 && sleepEnd <= 24)
  private var events: List[EventOccurrence] = Nil
  private val desc: String = "Sleep"
  private val isTwoDayDream = sleepStart > sleepEnd

  def parseCalendar(): Unit = {}

  def getEvents(from: DateTime, to: DateTime): List[EventOccurrence] = events match {
    case Nil => {
      def getEventsAcc(startDate: DateTime, acc: List[EventOccurrence]): List[EventOccurrence] = {
        if (startDate.compareTo(to) >= 0)
          acc
        else {
          if (isTwoDayDream) {
            if (startDate.compareTo(startDate.minusDays(1).withHourOfDay(sleepStart)) > 0 && startDate.compareTo(startDate.withHourOfDay(sleepEnd)) > 0)
              getEventsAcc(startDate.plusDays(1), acc)
            else {
              val newEventDate = new DateTime(startDate).withHourOfDay(sleepStart)
              if (startDate.compareTo(newEventDate) <= 0)
                getEventsAcc(startDate.plusDays(1), new EventOccurrence(newEventDate, newEventDate.withHourOfDay(sleepEnd) plusDays (1), desc) :: acc)
              else
                getEventsAcc(startDate.plusDays(1), new EventOccurrence(newEventDate.minusDays(1), newEventDate.withHourOfDay(sleepEnd), desc) :: acc)
            }
          }
          else {
            if (startDate.compareTo(startDate.withHourOfDay(sleepStart)) > 0 && startDate.compareTo(startDate.withHourOfDay(sleepEnd)) > 0)
              getEventsAcc(startDate.plusDays(1), acc)
            else {
              val newEventDate = new DateTime(startDate).withHourOfDay(sleepStart)
              if (startDate.compareTo(newEventDate) <= 0)
                getEventsAcc(startDate.plusDays(1), new EventOccurrence(newEventDate, newEventDate.withHourOfDay(sleepEnd), desc) :: acc)
              else
                getEventsAcc(startDate.plusDays(1), new EventOccurrence(newEventDate, newEventDate.withHourOfDay(sleepEnd), desc) :: acc)
            }
          }
        }

      }
      getEventsAcc(from, Nil)
    }

    case _: List[EventOccurrence] => events
  }
}
