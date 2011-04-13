
import sbt._

class Project(info: ProjectInfo) extends DefaultProject(info) with IdeaProject
{
  // Scala unit testing
  val scalatest = "org.scalatest" % "scalatest" % "1.3"

  val janino = "org.codehaus.janino" % "janino" % "2.5.16"

  def simplex3dJars = descendents("lib" / "simplex3d", "*.jar")
  override def unmanagedClasspath = super.unmanagedClasspath +++ simplex3dJars


}
