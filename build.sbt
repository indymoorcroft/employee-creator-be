ThisBuild / scalaVersion := "2.13.14"
ThisBuild / version := "0.1.0-SNAPSHOT"

lazy val root = (project in file("."))
  .enablePlugins(PlayScala)
  .settings(
    name := "play-scala-slick-mysql",
    libraryDependencies ++= Seq(
      guice,
      filters,
      "com.typesafe.play" %% "play-json" % "2.9.4",
      "com.typesafe.play" %% "play-slick" % "5.1.0",
      "com.typesafe.play" %% "play-slick-evolutions" % "5.1.0",
      "mysql" % "mysql-connector-java" % "8.0.33",
      "org.scalatestplus.play" %% "scalatestplus-play" % "6.0.0" % Test,
      "org.scalatest" %% "scalatest" % "3.2.18" % Test,
      "org.scalatestplus" %% "selenium-4-21" % "3.2.19.0" % Test,
      "io.github.bonigarcia" % "webdrivermanager" % "5.5.1",
      "com.auth0" % "java-jwt" % "4.4.0",
      "org.mindrot" % "jbcrypt" % "0.4"
    )
  )
