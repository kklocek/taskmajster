package models

import org.joda.time.{Period, DateTime}

import scala.annotation.tailrec

/**
 * Created by Konrad on 2014-12-26.
 * CalendarEvent is a task in calendar
 */
class CalendarEvent(val desc: String, val from: DateTime, val to: DateTime, val freq: String,
                    val until: DateTime, val weekStartDay: String, var count: Int, val interval: Int) {

  val mapInterval = Map("DAILY" -> 1, "WEEKLY" -> 7, "MONTHLY" -> 30, "YEARLY" -> 365)
  val isFrequent: Boolean = this.freq.compareTo("DAILY") == 0 || this.freq.compareTo("WEEKLY") == 0 ||
    this.freq.compareTo("MONTHLY") == 0 || this.freq.compareTo("YEARLY") == 0

  //Gives an information if our calendar event matching to @from and @to date parameters
  def compareDates(from: DateTime, to: DateTime): Boolean = {
    val x = nextOccurrence(from)

    val res = x match {
      case Some(x) => if(x.dateFrom.compareTo(to) < 0) true else false
      case None => false
    }
    res
  }

  //Returns list of occurrences of calendar event in some period of time
  def getEventOccurrence(from:DateTime, to:DateTime):List[EventOccurrence] = {

    //For countable events
    @tailrec
    def getPeriodCountableEventOccurrence(date:DateTime, counter:Int, acc:List[EventOccurrence]):List[EventOccurrence] = {
      if(counter == 0)
        acc
      else {
        val x = nextOccurrence(date)
        x match {
          case None => acc
          case Some(p) =>
            if(p.dateTo.compareTo(to) > 0)
              acc
            else
              getPeriodCountableEventOccurrence(date.plusDays(mapInterval(freq)), counter - 1, p :: acc)
        }
      }
    }

    //For interval events
    @tailrec
    def getPeriodIntervalEventOccurrence(date:DateTime, acc:List[EventOccurrence]):List[EventOccurrence] = {
      val x = nextOccurrence(date)
      x match {
        case None => acc
        case Some(p) =>
          if (p.dateTo.compareTo(to) > 0)
            acc
          else
            getPeriodIntervalEventOccurrence(date.plusDays(mapInterval(freq)), p :: acc)
      }
    }

    if(!isFrequent)
      List(new EventOccurrence(this.from, this.to, this.desc))
    else if(count > 0)
      getPeriodCountableEventOccurrence(from, count, List())
    else
      getPeriodIntervalEventOccurrence(from, List())
  }

  //Returns next event occurrence after @date parameter
  def nextOccurrence(date: DateTime): Option[EventOccurrence] = {
    if(!isFrequent && to.compareTo(date) < 0) //single event before date
      None
    else if(from.compareTo(date) >= 0) //single event or first event occurence after date
      Some(new EventOccurrence(from, to, desc))
    else if(isFrequent) { //frequent
      if(count > 0)
        getCountableOccurrence(date)
      else if(interval >= 0)
        getIntervalOccurrence(date)
      else
        None
    }
    else //None of above
      None

  }

  //Returns previous occurence before @date parameter
  def prevOccurrence(date:DateTime): Option[EventOccurrence] = {
    if(from.compareTo(date) > 0) //Event or first occurrence is after @date
      None
    else if(from.compareTo(date) < 0 && !isFrequent) //Single event is before @date
      Some(new EventOccurrence(from, to, desc))
    else if(isFrequent) {
      if(until.compareTo(date) < 0) //Frequent event is ended, so we need the last event
        Some(getLastEvent(date))
      else if(count > 0) //We have countable event
        getCountableOccurrence(date.minusDays(mapInterval(freq)))
      else if(interval >= 0) //We have interval event
        getIntervalOccurrence(date.minusDays(mapInterval(freq)))
      else
        None
    }
    else
      None
  }

  //Gives an occurrence of an event if it is countable
  private def getCountableOccurrence(date:DateTime):Option[EventOccurrence] = {

    def getOccurrence(dateStart:DateTime, dateEnd:DateTime, counter:Int):Option[EventOccurrence] = {
      if(counter == 0) //We don't have occurrences
        None
      else if(dateEnd.compareTo(date) >= 0) //We match event
        Some(new EventOccurrence(dateStart, dateEnd, desc))
      else
        getOccurrence(dateStart.plusDays(mapInterval(freq)), dateEnd.plusDays(mapInterval(freq)), counter - 1)
    }

    getOccurrence(from, to, count)
  }


  //Gives an occurrence of an event if it is frequent
  private def getIntervalOccurrence(date:DateTime):Option[EventOccurrence] = {

    def getOccurrence(dateStart:DateTime, dateEnd:DateTime):Option[EventOccurrence] = {
      if(until.compareTo(date) < 0) //Event ended
        None
      else if(dateEnd.compareTo(date) >= 0) //We match event
        Some(new EventOccurrence(dateStart, dateEnd, desc))
      else
        getOccurrence(dateStart.plusDays(mapInterval(freq)), dateEnd.plusDays(mapInterval(freq)))
    }

    getOccurrence(from, to)
  }

  //Gives the last event before @date
  private def getLastEvent(borderDate:DateTime):EventOccurrence = {

    def getLastCountableEvent(dateStart:DateTime, dateEnd:DateTime, counter:Int):EventOccurrence = {
      //if we don't have occurences of event, or if event in next days will exceed borderdate we matched it
      if(counter == 1 || (dateEnd.compareTo(borderDate) < 0 && dateEnd.plusDays(mapInterval(freq)).compareTo(borderDate) > 0))
        new EventOccurrence(dateStart, dateEnd, desc)
      else
        getLastCountableEvent(dateStart.plusDays(mapInterval(freq)), dateEnd.plusDays(mapInterval(freq)), counter - 1)
    }


    def getLastIntervalEvent(dateStart: DateTime, dateEnd:DateTime):EventOccurrence = {
      if((dateEnd.compareTo(borderDate) < 0 && dateEnd.plusDays(mapInterval(freq)).compareTo(borderDate) > 0)) //same as in above function
        new EventOccurrence(dateStart, dateEnd, desc)
      else
        getLastIntervalEvent(dateStart.plusDays(mapInterval(freq)), dateEnd.plusDays(mapInterval(freq)))
    }

    if(count > 0)
      getLastCountableEvent(from, to, count)
    else
      getLastIntervalEvent(from, to)
  }
}
