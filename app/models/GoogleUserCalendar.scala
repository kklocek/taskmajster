package models

import java.util.Collections

import org.joda.time.DateTime
import com.google.api.client.auth.oauth2.Credential
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow
import com.google.api.client.googleapis.auth.oauth2.GoogleCredential
import com.google.api.client.googleapis.auth.oauth2.GoogleTokenResponse
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport
import com.google.api.client.http.HttpTransport
import com.google.api.client.json.jackson2.JacksonFactory
import com.google.api.services.calendar.Calendar

import scala.annotation.tailrec

/**
 * Created by Konrad on 2014-12-26.
 */
object GoogleUserCalendar {
  def apply() = (new GoogleUserCalendar).setUpConnection()

  def apply(accessToken: String) = new GoogleUserCalendar(accessToken)
}


class GoogleUserCalendar(private val accessToken: String = null) extends UserCalendar {

  private var events: List[CalendarEvent] = List()

  def parseCalendar(): Unit = {}

  def getEvents(from: DateTime, to: DateTime): List[EventOccurrence] = events.filter(a => a.compareDates(from, to)).map(a => new EventOccurrence(a.from, a.to, a.desc))

  def setUpConnection(): Unit = {
    val httpTransport: HttpTransport = GoogleNetHttpTransport.newTrustedTransport()
    val jsonFactory: JacksonFactory = JacksonFactory.getDefaultInstance

    val clientID: String = "513318797389-7eqq4bs0jno1fjiklt9g1353jopsmrj0.apps.googleusercontent.com"
    val clientSecret: String = "12HvaKFzwkWqndXMaWE4RUuw "
    val scope: String = "https://www.googleapis.com/auth/calendar"
    val redirectUrl: String = "https://www.example.com/oauth2callback"
    val flow: GoogleAuthorizationCodeFlow = new GoogleAuthorizationCodeFlow(httpTransport, jsonFactory, clientID, clientSecret, Collections.singleton(scope))
    val authorizationUrl: String = flow.newAuthorizationUrl().setRedirectUri(redirectUrl).build()

    if (accessToken != null) {
      //Ok, we have an connection
    }
    else {
      //We need to connect
    }
    //Below we print authorizationURL, that we have to route user. After that,
    //we need to get credentials e.g. in a JSON file
    //println(authorizationUrl)


  }

}
