package models

import org.joda.time.DateTime
import scala.annotation.tailrec

/**
 * Created by Konrad on 2015-01-03.
 */
class MultipleUserCalendar(private val calendars: UserCalendar*) {
  private var events: List[EventOccurrence] = Nil

  def getEvents(from: DateTime, to: DateTime): List[EventOccurrence] = events match {
    case Nil => getEventsFromCalendars(from, to)
    case _ => events
    }

  private def getEventsFromCalendars(from: DateTime, to: DateTime): List[EventOccurrence] = {
    val size = calendars.size

    def getEventAcc(index: Int, calendars: List[UserCalendar], acc: List[EventOccurrence]): List[EventOccurrence] = {
      if (index == size)
        acc
      else
        getEventAcc(index + 1, calendars.tail, acc ::: calendars.head.getEvents(from, to))
    }

    getEventAcc(1, calendars.toList, List())
  }

}

