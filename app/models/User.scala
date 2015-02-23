package models

import anorm._
import play.api.db.DB
import play.api.Play.current
/**
 * Created by Konrad on 2015-02-22.
 */
class User(val login:String,val mail:String, val password:String) {

}

object User {
  def apply(login:String, mail:String, password:String) = new User(login, mail, password)
  def put(user:User):Boolean = DB.withConnection {
    implicit connection =>
      SQL(
      "INSERT INTO registered_users (login, mail, password) " + " VALUES ({login}, {mail}, {password})").on(
      "login" -> user.login,
      "mail" -> user.mail,
      "password" -> user.password
      ).executeUpdate() == 1
  }

  def login(login:String, password:String):Boolean = {
    DB.withConnection {
      implicit connection =>
        SQL("SELECT * FROM registered_users " + "WHERE login={login} AND password={password}").on(
          "login" -> login,
          "password" -> password
        )().head match {
          case x:anorm.Row => true
          //case None => false
        }
    }
  }

}
