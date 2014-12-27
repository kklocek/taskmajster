package models

import models.UserMessageFeedback._

/**
 * Created by Konrad on 2014-12-28.
 */
class SafetyPin(var typeOfFeedback: UserMessageFeedback) {
  val text: String = typeOfFeedback match {
    case Praise => "You are much better in doing things in time. Keep good working!"
    case NotEnoughTime => "You don't have enough time to do every task. Do you need to do everything?"
    case TooMuchTime => "You have a lot of time, you can do more tasks ;)"
    case WasteTime => "You are not effective in doing your tasks. Do you waste your time? Maybe you have to work on yourself"
    case LittleSleep => "A lot of things to do, but do you know that you should sleep about 7-8 hours every day? Now, you don't have much time to sleep. Take care about your health"
    case GoodDoing => "You are responsible, and do your tasks in time. Keep good working! :)"
    case NotDevelop => "You don't do your tasks. Think about your time and things to do. Sometimes we have unexpectable situations, sometimes we are lazy."
    case _ => "Not good :<"
  }

  //Other things to format string

}
