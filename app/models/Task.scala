package models.tasks

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

  def possibleOptions(): Seq[(String, String)] = priorities.toSeq.map {
    tuple => (tuple._1.toString, tuple._2)
  }

  val low: Priority = Priority(1)
  val normal: Priority = Priority(2)
  val high: Priority = Priority(3)
}

class Frequency(val begin: DateTime, val period: Period)

class DoabilityInterval(val begin: Option[DateTime], val end: Option[DateTime])

sealed abstract class Task(private var _id: Option[Long], val kind: String, var name: String, var priority: Priority, val createdDate: DateTime) {
  def id = _id
  def nextDoability(lastTimeDone: Option[DateTime] = None): DoabilityInterval
  def extraData: String
}

object Task {

  //TODO: introduce BasicTaskInfo struct
  private def apply(id: Long, kind: String, name: String, priority: Priority, createDate: DateTime, extraData: String): Task = {
    kind match {
      //TODO better implementation
      case "deadline" => new DeadlineTask(Some(id), name, priority, createDate, extraData)
      case "frequent" => new FrequentTask(Some(id), name, priority, createDate, extraData)
      case "current"  => new CurrentTask (Some(id), name, priority, createDate, extraData)
      case "longterm" => new LongtermTask(Some(id), name, priority, createDate)
    }
  }

  import anorm.SQL
  import play.api.Play.current
  def getAll: List[Task] = {
    DB.withConnection {
      implicit connection => {
        SQL("SELECT * FROM tasks")().map( row =>
          Task(row[Long]("id"), row[String]("kind"), row[String]("name"), Priority(row[Int]("priority")), row[DateTime]("createDate"), row[String]("data"))
        ).toList
      }
    }
  }

  def store(task: Task): Boolean = {
    task.id match {
      case None => insert(task)
      case Some(_) => update(task)
    }
  }

  private def insert(task: Task): Boolean = {
    DB.withConnection {
      implicit connection => {
        SQL("INSERT INTO tasks (id, kind, name, priority, createDate, data) " +
          "VALUES ({id}, {kind}, {name}, {priority}, {createDate}, {data})").on(
            "id" -> task.id,
            "kind" -> task.kind,
            "name" -> task.name,
            "priority" -> task.priority.value,
            "createDate" -> task.createdDate,
            "data" -> task.extraData
          ).executeUpdate() == 1 //TODO: store id in Task
      }
    }
  }

  private def update(task: Task): Boolean = {
    DB.withConnection {
      implicit connection => {
        SQL("UPDATE tasks SET kind={kind}, name={name}, priority={priority}, createDate={createDate}, data={data} " +
          "WHERE id={id} LIMIT 1").on(
            "id" -> task.id,
            "kind" -> task.kind,
            "name" -> task.name,
            "priority" -> task.priority.value,
            "createDate" -> task.createdDate,
            "data" -> task.extraData //TODO: deduplicate code
          ).executeUpdate() == 1
      }
    }
  }

  def delete(task: Task): Boolean = {
    DB.withConnection {
      implicit connection => {
        SQL("DELETE FROM tasks WHERE id={id} LIMIT 1").on(
          "id" -> task.id
        ).executeUpdate() == 1
      }
    }
  }
}

class DeadlineTask private[tasks] (id: Option[Long], name: String, priority: Priority, createdDate: DateTime,
                                   var deadline: DateTime, var continuous: Boolean, var length: Period)
  extends Task(id, "deadline", name, priority, createdDate) {

  private[tasks] def this (id: Option[Long], name: String, priority: Priority,
                           createdDate: DateTime, extraData: String) {
    this(id, name, priority, createdDate, DateTime.now, true, Period.hours(1)) //TODO: extract data from string
  }

  def this(name: String, priority: Priority, deadline: DateTime, continuous: Boolean, length: Period) {
    this(None, name, priority, DateTime.now, deadline, continuous, length)
  }

  override def nextDoability(lastTimeDone: Option[DateTime]) = new DoabilityInterval(None, Some(deadline))
  override def extraData: String = "Foo" //TODO: encode data into string
}

class FrequentTask private[tasks] (id: Option[Long], name: String, priority: Priority, createdDate: DateTime,
                                   var frequency: Frequency, var continuous: Boolean, var length: Period)
  extends Task(id, "frequent", name, priority, createdDate) {

  private[tasks] def this (id: Option[Long], name: String, priority: Priority,
                           createdDate: DateTime, extraData: String) {
    this(id, name, priority, createdDate, new Frequency(createdDate, Period.weeks(1)), true, Period.hours(1)) //TODO: extract data from string
  }

  def this(name: String, priority: Priority, frequency: Frequency, continuous: Boolean, length: Period) {
    this(None, name, priority, DateTime.now, frequency, continuous, length)
  }

  override def nextDoability(lastTimeDone: Option[DateTime]) = {
    // TODO write sensible implementation
    new DoabilityInterval(Some(frequency.begin), Some(frequency.begin plus frequency.period))
  }
  override def extraData: String = "Bar" //TODO: encode data into string
}


class CurrentTask private[tasks] (id: Option[Long], name: String, priority: Priority, createdDate: DateTime,
                                  var continuous: Boolean, var length: Period)
  extends Task(id, "current", name, priority, createdDate) {

  private[tasks] def this (id: Option[Long], name: String, priority: Priority,
                           createdDate: DateTime, extraData: String) {
    this(id, name, priority, createdDate, true, Period.hours(1)) //TODO: extract data from string
  }

  def this(name: String, priority: Priority, continuous: Boolean, length: Period) {
    this(None, name, priority, DateTime.now, continuous, length)
  }

  override def nextDoability(lastTimeDone: Option[DateTime]) = new DoabilityInterval(Some(createdDate), None)
  override def extraData: String = "Buz" //TODO: encode data into string
}

object CurrentTask {
  def apply(name: String, priority: Priority, continuous: Boolean, length: Period) = new CurrentTask(name, priority, continuous, length)
  def unapply(task: CurrentTask): Option[(String, Priority, Boolean, Period)] = None //TODO
}


class LongtermTask private[tasks] (id: Option[Long], name: String, priority: Priority, createdDate: DateTime)
  extends Task(id, "longterm", name, priority, createdDate) {

  def this(name: String, priority: Priority) {
    this(None, name, priority, DateTime.now)
  }

  override def nextDoability(lastTimeDone: Option[DateTime]) = new DoabilityInterval(Some(createdDate), None)
  override def extraData: String = ""
}

object LongtermTask {
  def apply(name: String, priority: Priority) = new LongtermTask(name, priority)
  def unapply(task: LongtermTask): Option[(String, Priority)] = None //TODO
}