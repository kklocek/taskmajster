package models

import models.tasks._
import org.joda.time.{Period, DateTime}

class AspasiaSolver(private val tasks: List[Task], private val calendar: MultipleUserCalendar) extends Solver {

  val farAway = DateTime.now plus Period.years(1)

  val calendarEventsSorted = calendar.getEvents(DateTime.now minus Period.days(1), farAway).sortWith { // minus 1 days is kind of hack for SUC
    (e1, e2) => e1.dateFrom.compareTo(e2.dateFrom) < 0
  }

  def solve(from: DateTime, to: DateTime): Suggestion = {
    val willingnessOrdering: List[(Task, (DateTime, DateTime))] = tasks.map {
      event => event match {
        case deadlineTask: DeadlineTask =>
          deadlineTask ->(deadlineTask.nextDoability(None).end.get minus deadlineTask.length, deadlineTask.nextDoability(None).end.get)
        case currentTask: CurrentTask =>
          currentTask ->(farAway, farAway plus currentTask.length)
      }
    }

    val prioritySorted = willingnessOrdering.sortWith{ (e1, e2) =>
      if (e1._1.priority.value != e2._1.priority.value) {
        e1._1.priority.value > e2._1.priority.value
      } else {
        e1._2._1.compareTo(e2._2._1) < 0
      }
    }

    val broughtToNow = greedyAppend(prioritySorted, calendarEventsSorted)

    new Suggestion(broughtToNow.toMap)
  }

  def greedyAppend(prioritySortedList: List[(Task, (DateTime, DateTime))], actualCalendar: List[EventOccurrence]): List[(Task, (DateTime, DateTime))] = {
    val sortedActualCalendar = actualCalendar.sortWith {
      (e1, e2) => e1.dateFrom.compareTo(e2.dateFrom) < 0
    }

    var newActualCalendar = actualCalendar
    prioritySortedList match {
      case (task: Task, (from: DateTime, to: DateTime)) :: tail => {
        task match {
          case deadlineTask: DeadlineTask => {
            val start = nextFreeTimeWindow(DateTime.now, deadlineTask.length, sortedActualCalendar)._1
            newActualCalendar = new EventOccurrence(start, start plus deadlineTask.length, "Task") :: actualCalendar
            deadlineTask ->(start, start plus deadlineTask.length)
          }
          case currentTask: CurrentTask => {
            val start = nextFreeTimeWindow(DateTime.now, currentTask.length, sortedActualCalendar)._1
            newActualCalendar = new EventOccurrence(start, start plus currentTask.length, "Task") :: actualCalendar
            currentTask ->(start, start plus currentTask.length)
          }
        }
      } :: greedyAppend(tail, newActualCalendar)
      case Nil => Nil
    }
  }

  def nextFreeTime(from: DateTime, sortedEvents: List[EventOccurrence]): (DateTime, DateTime) = {
    var actualFreeFrom = from
    for (eventOccurrence <- sortedEvents.filter { _.dateTo.compareTo(from) >= 0 }) {
      if (eventOccurrence.dateFrom.compareTo(actualFreeFrom) > 0) {
        return (actualFreeFrom, eventOccurrence.dateFrom)
      }
      else if (eventOccurrence.dateTo.compareTo(actualFreeFrom) >= 0) {
        actualFreeFrom = eventOccurrence.dateTo
      }
    }
    (actualFreeFrom, farAway)
  }

  def nextFreeTimeWindow(from: DateTime, length: Period, sortedEvents: List[EventOccurrence]): (DateTime, DateTime) = {
    val nft = nextFreeTime(from, sortedEvents)
    val foundLength = new Period(nft._1, nft._2)
    if (foundLength.toDurationFrom(nft._1).getStandardSeconds < length.toDurationFrom(nft._1).getStandardSeconds)
      nextFreeTimeWindow(nft._2, length, sortedEvents)
    else
      nft
  }
}
