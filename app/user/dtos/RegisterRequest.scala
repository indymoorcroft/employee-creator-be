package user.dtos

import play.api.libs.json.{Json, Reads, Writes}

case class RegisterRequest(email: String, password: String)

object RegisterRequest {
  implicit val reads: Reads[RegisterRequest] = Json.reads[RegisterRequest]
}