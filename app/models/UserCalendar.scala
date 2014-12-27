package models

import org.joda.time.DateTime

/**
 * Created by Konrad on 2014-12-26.
 */
abstract class UserCalendar {

  def parseCalendar(): Unit

  def getEvents(from: DateTime, to: DateTime): List[EventOccurrence]

}
