package models

import org.joda.time.DateTime
import scala.collection.mutable
import scala.collection.immutable.Map
import scala.annotation.tailrec
import tasks._

/**
 * Created by Konrad on 2014-12-28.
 */
class ConcreteSolver(private val tasks: List[Task], private val calendar: MultipleUserCalendar) extends Solver {

  //Dummy solver
  def solve(from: DateTime, to: DateTime): Suggestion = {
    val events = calendar.getEvents(from, to)
    val sortedEvents = events.sortWith((a, b) => a.dateFrom.compareTo(b.dateFrom) < 0) //Sort according to date
    //Not functional version
    var solution: Map[Task, DateTime] = solveFunctional(tasks, sortedEvents)
    new Suggestion(solution) //TODO: Ask Adam about needing for Map[Task, From, To]
  }

  private def solveFunctional(tasks: List[Task], calendar: List[EventOccurrence]): Map[Task, DateTime] = {

    @tailrec
    def solveWithAcc(tasks: List[Task], calendar: List[EventOccurrence], acc: Map[Task, DateTime]): Map[Task, DateTime] = {
      if (tasks.size == 0)
        acc
      else if (calendar.size == 1)
        solveWithAcc(tasks.tail, List(new EventOccurrence(calendar.head.dateFrom, calendar.head.dateTo.plusDays(tasks.head.duration._1).plusMinutes(tasks.head.duration._2), calendar.head.desc)), acc.+((tasks.head, calendar.head.dateTo))) //...
      else {
        if (calendar.head.dateTo.plusDays(tasks.head.duration._1).plusMinutes(tasks.head.duration._2).compareTo(calendar.tail.head.dateFrom) <= 0)
          solveWithAcc(tasks.tail, calendar.tail, acc.+((tasks.head, calendar.head.dateTo)))
        else
          solveWithAcc(tasks, calendar.tail, acc)
      }
    }

    solveWithAcc(tasks, calendar, Map[Task, DateTime]())
  }
}
