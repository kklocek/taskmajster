package models

import org.joda.time.DateTime

/**
 * Created by Konrad on 2014-12-28.
 */
abstract class Solver {

  def solve(from: DateTime, to: DateTime): Suggestion
}
