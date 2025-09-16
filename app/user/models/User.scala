package user.models

import java.sql.Timestamp

case class User (
                id: Option[Long] = None,
                email: String,
                passwordHash: String,
                createdAt: Timestamp,
                updatedAt: Timestamp
                )
