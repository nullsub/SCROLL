name := "SCROLL"

scalaVersion := "2.11.6"

val scalatestVersion = "2.2.1"
val shapelessVersion = "2.2.3"
val kiamaVersion = "1.8.0"
val graphCoreVersion = "1.9.2"
val graphConstrainedVersion = "1.9.0"

version := "0.7"

libraryDependencies ++= Seq(
  "com.chuusai" %% "shapeless" % shapelessVersion,
  "com.googlecode.kiama" %% "kiama" % kiamaVersion,
  "org.scalatest" %% "scalatest" % scalatestVersion % "test",
  "com.assembla.scala-incubator" %% "graph-core" % graphCoreVersion,
  "com.assembla.scala-incubator" %% "graph-constrained" % graphConstrainedVersion
)

javacOptions in Compile ++= Seq("-source", "1.7", "-target", "1.7")

scalacOptions ++= Seq("-unchecked",
  "-deprecation",
  "-feature",
  "-language:dynamics",
  "-language:reflectiveCalls",
  "-language:postfixOps",
  "-language:implicitConversions",
  "-target:jvm-1.7")

testOptions in Test += Tests.Argument("-oD")

parallelExecution in Test := false

// its a library
mainClass := None

organization := "com.github.max-leuthaeuser"

publishTo := {
  val nexus = "https://oss.sonatype.org/"
  if (isSnapshot.value)
    Some("snapshots" at nexus + "content/repositories/snapshots")
  else
    Some("releases" at nexus + "service/local/staging/deploy/maven2")
}

publishMavenStyle := true

publishArtifact in Test := false

pomIncludeRepository := { _ => false }

pomExtra :=
  <url>https://github.com/max-leuthaeuser/SCROLL</url>
    <licenses>
      <license>
        <name>LGPL 3.0 license</name>
        <url>http://www.opensource.org/licenses/lgpl-3.0.html</url>
        <distribution>repo</distribution>
      </license>
    </licenses>
    <scm>
      <connection>scm:git:github.com/max-leuthaeuser/SCROLL.git</connection>
      <developerConnection>scm:git:git@github.com:max-leuthaeuser/SCROLL.git</developerConnection>
      <url>github.com/max-leuthaeuser/SCROLL</url>
    </scm>
    <developers>
      <developer>
        <id>max-leuthaeuser</id>
        <name>Max Leuthaeuser</name>
        <url>https://wwwdb.inf.tu-dresden.de/rosi/investigators/doctoral-students/</url>
      </developer>
    </developers>
