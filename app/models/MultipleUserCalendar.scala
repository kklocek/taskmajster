package models

import org.joda.time.DateTime
import scala.annotation.tailrec

/**
 * Created by Konrad on 2015-01-03.
 */
class MultipleUserCalendar(private val calendars: UserCalendar*) {
  def getEvents(from: DateTime, to: DateTime) = calendars.map(_.getEvents(from, to)).flatten.toList
}

