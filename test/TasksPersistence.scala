import models.tasks._
import org.joda.time.{DateTime, Period}
import org.junit.runner._
import org.specs2.mutable._
import org.specs2.runner._
import play.api.test._

/**
 * Add your spec here.
 * You can mock out a whole application including requests, plugins etc.
 * For more information, consult the wiki.
 */
@RunWith(classOf[JUnitRunner])
class TasksPersistence extends Specification {

  "Task" should {

    "be properly stored" in new WithApplication {
      {
        val x: Task = new DeadlineTask("Dummy task", Priority.normal, DateTime.now plusDays 3, false, Period.hours(3))
        Task.insert(x) must beTrue
        val tasks = Task.getAll
        tasks.size must equalTo(1) //TODO better matching
      val soleTask: Task = tasks(0)
        soleTask.name must equalTo("Dummy task")
        soleTask.priority.value must equalTo(Priority.normal.value) //TODO override appropriate methods

        Task.delete(soleTask) must beTrue
        val tasks2 = Task.getAll
        tasks2.size must equalTo(0)
      }
    }
  }
}
