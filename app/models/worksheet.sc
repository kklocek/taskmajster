import org.joda.time.DateTime
import org.joda.time.format.DateTimeFormat

val dateFormatter = DateTimeFormat.forPattern("YYYYMMdd'T'HHmmss")
dateFormatter.parseDateTime("20140926T080000")
//new DateTime("19970902T090000")

