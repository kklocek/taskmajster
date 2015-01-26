package models

import org.joda.time.DateTime
import tasks._
/**
 * Created by Konrad on 2014-12-28.
 */
class Suggestion(private val solution: Map[Task, (DateTime, DateTime)]) {

  private val safetyPin = generateSafetyPin()

  def generateSafetyPin(): SafetyPin = new SafetyPin(null) //temporary

  def getSolution(): Map[Task, (DateTime, DateTime)] = solution

  def getSafetyPin(): SafetyPin = safetyPin
}
