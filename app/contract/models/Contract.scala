package contract.models

import java.sql.{Date, Timestamp}

case class Contract(
                     id: Option[Long] = None,
                     employeeId: Option[Long],
                     startDate: Date,
                     endDate: Option[Date],
                     contractType: String,
                     employmentType: String,
                     hoursPerWeek: BigDecimal,
                     createdAt: Timestamp,
                     updatedAt: Timestamp
                   )
