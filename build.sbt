name := "rasterfun"

version := "0.1"

scalaVersion := "2.9.1"

// Repositories
resolvers += "Guicefruit Repository" at "http://guiceyfruit.googlecode.com/svn/repo/releases"

resolvers += "Sonatype Public" at "https://oss.sonatype.org/content/groups/public/"


// Inversion of control:
// libraryDependencies += "com.google.inject" % "guice" % "3.0"


// Logging:
libraryDependencies += "org.slf4j" % "slf4j-api" % "1.6.4"

libraryDependencies += "org.slf4j" % "slf4j-log4j12" % "1.6.4"

libraryDependencies += "log4j" % "log4j" % "1.2.16"


// Testing:
libraryDependencies += "com.novocode" % "junit-interface" % "0.10-M1" % "test"

// libraryDependencies += "org.scalatest" %% "scalatest" % "1.7.2" % "test"


// Bytecode generation
libraryDependencies += "org.codehaus.janino" % "janino" % "2.6.1"


// Config files:
libraryDependencies += "org.yaml" % "snakeyaml" % "1.11-SNAPSHOT"


// Layout
libraryDependencies += "com.miglayout" % "miglayout" % "3.7.4"  // classifier "swing"
