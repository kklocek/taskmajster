package models

/**
 * Created by Konrad on 2014-12-31.
 */
object UserMessageFeedback extends Enumeration {
  type UserMessageFeedback = Value
  val Praise, NotEnoughTime, TooMuchTime, WasteTime, LittleSleep, GoodDoing, NotDevelop = Value
}
