enablePlugins(JavaAppPackaging)
enablePlugins(AshScriptPlugin)
enablePlugins(DockerPlugin)

name := "hnnbot"

organization := "br.net.hnn.discord"

version := "0.1"

scalaVersion := "2.13.1"

resolvers += Resolver.jcenterRepo

libraryDependencies += "net.dv8tion" % "JDA" % "4.1.1_165"

libraryDependencies += "com.sedmelluq" % "lavaplayer" % "1.3.50"

libraryDependencies += "io.github.cdimascio" % "java-dotenv" % "5.1.3"

libraryDependencies += "org.json4s" %% "json4s-jackson" % "3.7.0-M1"

libraryDependencies += "com.softwaremill.sttp.client" %% "core" % "2.0.0-RC5"

libraryDependencies += "com.softwaremill.sttp.client" %% "httpclient-backend" % "2.0.0-RC5"

libraryDependencies += "ch.qos.logback" % "logback-classic" % "1.2.3" % "runtime"

assemblyMergeStrategy in assembly := {
  case "module-info.class" => MergeStrategy.discard
  case x =>
    val oldStrategy = (assemblyMergeStrategy in assembly).value
    oldStrategy(x)
}

dockerBaseImage := "adoptopenjdk/openjdk13:alpine-slim"
