package api.utils

import org.scalatest.{BeforeAndAfterAll, Suite}
import org.scalatestplus.play.guice.GuiceOneAppPerSuite
import play.api.db.slick.DatabaseConfigProvider
import seed.DataSeeder
import slick.jdbc.JdbcProfile

import scala.concurrent.Await
import scala.concurrent.duration._

trait CleanDatabase extends BeforeAndAfterAll { self: Suite with GuiceOneAppPerSuite =>

  override def beforeAll(): Unit = {
    super.beforeAll()

    val dbConfigProvider = app.injector.instanceOf[DatabaseConfigProvider]
    val dbConfig = dbConfigProvider.get[JdbcProfile]
    val db = dbConfig.db
    val profile = dbConfig.profile
    import profile.api._

    val truncateAction = DBIO.seq(
      sqlu"SET FOREIGN_KEY_CHECKS = 0",
      sqlu"TRUNCATE TABLE contracts",
      sqlu"TRUNCATE TABLE employees",
      sqlu"TRUNCATE TABLE users",
      sqlu"SET FOREIGN_KEY_CHECKS = 1"
    )

    Await.result(db.run(truncateAction), 10.seconds)

    val seeder = app.injector.instanceOf[DataSeeder]
    Await.result(seeder.seed(), 10.seconds)
  }
}
