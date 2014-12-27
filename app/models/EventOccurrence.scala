package models

import org.joda.time.DateTime

/**
 * Created by Konrad on 2014-12-31.
 */
//Event occurence will be created by Calendar, so it don't have to know about calendar
class EventOccurrence(val dateFrom: DateTime, val dateTo: DateTime, val desc: String) {

}
