import models._
import org.joda.time.{Period, DateTime}
import org.junit.runner._
import org.specs2.mutable._
import org.specs2.runner._
import play.api.test.Helpers._
import play.api.test._

/**
 * Add your spec here.
 * You can mock out a whole application including requests, plugins etc.
 * For more information, consult the wiki.
 */
@RunWith(classOf[JUnitRunner])
class TaskTest extends Specification {

  "Task" should {

    "have proper kind" in new WithApplication {
      {
        val x: Task = new DeadlineTask(None, "Dummy task", Priority.normal, DateTime.now(), new DateTime())
        x.kind must equalTo("deadline")
      }
      {
        val x: Task = new FrequentTask(None, "Dummy task", Priority.normal, DateTime.now(), new Frequency(DateTime.now, Period.days(10)))
        x.kind must equalTo("frequent")
      }
      {
        val x: Task = new CurrentTask(None, "Dummy task", Priority.normal, DateTime.now())
        x.kind must equalTo("current")
      }
      {
        val x: Task = new LongtermTask(None, "Dummy task", Priority.normal, DateTime.now())
        x.kind must equalTo("longterm")
      }
    }

    "have proper priority" in new WithApplication {
      {
        val x: Task = new DeadlineTask(None, "Dummy task", Priority.high, DateTime.now(), new DateTime())
        x.priority must equalTo(Priority.high)
      }
      {
        val x: Task = new FrequentTask(None, "Dummy task", Priority.high, DateTime.now(), new Frequency(DateTime.now, Period.days(10)))
        x.priority must equalTo(Priority.high)
      }
      {
        val x: Task = new CurrentTask(None, "Dummy task", Priority.low, DateTime.now())
        x.priority must equalTo(Priority.low)
      }
      {
        val x: Task = new LongtermTask(None, "Dummy task", Priority.low, DateTime.now())
        x.priority must equalTo(Priority.low)
      }
    }

    "work properly in pattern matching" in new WithApplication {
      {
        val x: Task = new DeadlineTask(None, "Dummy task", Priority.high, DateTime.now(), new DateTime())
        (x match {
          case y: DeadlineTask => true
          case _ => false
        }) must beTrue
      }
      {
        val x: Task = new FrequentTask(None, "Dummy task", Priority.high, DateTime.now(), new Frequency(DateTime.now, Period.days(10)))
        (x match {
          case y: FrequentTask => true
          case _ => false
        }) must beTrue
      }
      {
        val x: Task = new CurrentTask(None, "Dummy task", Priority.low, DateTime.now())
        (x match {
          case y: CurrentTask => true
          case _ => false
        }) must beTrue
      }
      {
        val x: Task = new LongtermTask(None, "Dummy task", Priority.low, DateTime.now())
        (x match {
          case y: LongtermTask => true
          case _ => false
        }) must beTrue
      }
    }
  }
}
