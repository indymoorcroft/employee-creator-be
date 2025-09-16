package user.dtos

import play.api.libs.json.{Json, Writes}
import user.models.User

case class UserResponse(id: Long, email: String)

object UserResponse {
  implicit val writes: Writes[UserResponse] = Json.writes[UserResponse]

  def fromModel(model: User): UserResponse = {
    UserResponse(id = model.id.getOrElse(0L), email = model.email)
  }
}
