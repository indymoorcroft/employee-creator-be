package user.dtos

import play.api.libs.json.{Json, Reads}

case class LoginRequest(email: String, password: String)

object LoginRequest {
  implicit val reads: Reads[LoginRequest] = Json.reads[LoginRequest]
}