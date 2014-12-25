package models

import anorm.Column
import org.joda.time.{Period, DateTime}
import play.api.db.DB

class Priority (val value: Int) {
  override def toString = Priority.stringify(value)
}

object Priority {
  def apply(value: Int) = new Priority(value)
  def apply(value: String) = new Priority(value.toInt)

  private val priorities = Map(1 -> "low", 2->"normal", 3->"high")

  def stringify(value: Int) = priorities(value)

  val low: Priority = Priority(1)
  val normal: Priority = Priority(2)
  val high: Priority = Priority(3)
}

class Frequency(val begin: DateTime, val period: Period)

class DoabilityInterval(val begin: Option[DateTime], val end: Option[DateTime])

sealed abstract class Task(var id: Option[Long], val kind: String, var name: String, var priority: Priority, val createdDate: DateTime) {
  def nextDoability(lastTimeDone: Option[DateTime] = None): DoabilityInterval
}

object Task {

  def apply(id: Long, kind: String, name: String, priority: Priority, createDate: DateTime, data: String): Task = {
    import DeadlineTask._
    import FrequentTask._
    kind match {
      //TODO better implementation
      case "deadline" => new DeadlineTask(Some(id), name, priority, createDate, data)
      case "frequent" => new FrequentTask(Some(id), name, priority, createDate, data)
      case "current"  => new CurrentTask (Some(id), name, priority, createDate)
      case "longterm" => new LongtermTask(Some(id), name, priority, createDate)
    }
  }

  import anorm.SQL
  def getAll: List[Task] = {
    import play.api.Play.current
    DB.withConnection {
      implicit connection => {
        SQL("SELECT * FROM tasks")().map( row =>
          Task(row[Long]("id"), row[String]("kind"), row[String]("name"), Priority(row[Int]("priority")), row[DateTime]("createDate"), row[String]("data"))
        ).toList
      }
    }
  }
}

class DeadlineTask(id: Option[Long], name: String, priority: Priority, createdDate: DateTime, var deadline: DateTime)
  extends Task(id, "deadline", name, priority, createdDate) {

  override def nextDoability(lastTimeDone: Option[DateTime]) = new DoabilityInterval(None, Some(deadline))
}

object DeadlineTask {
  implicit def decodeDateTime(encoded: String): DateTime = DateTime.now
  implicit def encodeDateTime(decoded: DateTime): String = "Foo"
}

class FrequentTask(id: Option[Long], name: String, priority: Priority, createdDate: DateTime, var frequency: Frequency)
  extends Task(id, "frequent", name, priority, createdDate) {
  override def nextDoability(lastTimeDone: Option[DateTime]) = {
    // TODO write sensible implementation
    new DoabilityInterval(Some(frequency.begin), Some(frequency.begin plus frequency.period))
  }
}

object FrequentTask {
  implicit def decodeFrequency(encoded: String): Frequency = new Frequency(DateTime.now, Period.days(8))
  implicit def encodeFrequency(decoded: Frequency): String = "Bar"
}

class CurrentTask(id: Option[Long], name: String, priority: Priority, createdDate: DateTime)
  extends Task(id, "current", name, priority, createdDate) {
  override def nextDoability(lastTimeDone: Option[DateTime]) = new DoabilityInterval(Some(createdDate), None)
}

class LongtermTask(id: Option[Long], name: String, priority: Priority, createdDate: DateTime)
  extends Task(id, "longterm", name, priority, createdDate) {
  override def nextDoability(lastTimeDone: Option[DateTime]) = new DoabilityInterval(Some(createdDate), None)
}