import org.joda.time
import org.specs2.mutable._
import org.specs2.runner._
import org.junit.runner._
import models.ICalUserCalendar

import play.api.test._
import play.api.test.Helpers._
import play.Play
import models._

import org.joda.time.DateTime


/**
 * Created by Konrad on 2015-01-04.
 */
@RunWith(classOf[JUnitRunner])
class SleepUserCalendarTest extends Specification {

  //val path = System.getProperty("user.dir") + "/schedule.ics"

  "SleepUserCalendar" should {

    "Work properly from 5 to 7" in new WithApplication() {
      val a = new SleepUserCalendar(5,7)
      a.getEvents(DateTime.now(), DateTime.now().plusDays(7)).size must beGreaterThan(0)
    }

    "Work properly from 7 to 5" in new WithApplication() {
      val a = new SleepUserCalendar(7, 5)
      a.getEvents(DateTime.now(), DateTime.now().plusDays(7)).size must beGreaterThan(0)
    }

  }
}
