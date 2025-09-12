package user

import slick.jdbc.MySQLProfile.api._
import user.models._

import java.sql.Timestamp

class Users(tag: Tag) extends Table[User](tag, "users") {

  def id = column[Long]("id", O.PrimaryKey, O.AutoInc)
  def email = column[String]("email")
  def passwordHash = column[String]("password_hash")
  def createdAt = column[Timestamp]("created_at")
  def updatedAt = column[Timestamp]("updated_at")

  def * = (id.?, email, passwordHash, createdAt, updatedAt) <>
    (User.tupled, User.unapply)
}

object Table {
  val users = TableQuery[Users]
}
