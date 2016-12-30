package app.impl.patterns.structural

/**
  * Adapter patter use an interface to give a class a behave but inside introduce an component
  * to give the implementation an extra behave
  */
object Adapter extends App{


  trait Split{
    def splitBy(character:String):Array[String]
  }

  implicit class StringExtended(s:String) extends Split{
    override def splitBy(character: String): Array[String] = s.split(character)
  }

  "This|is|an|adapter|class".splitBy("\\|").foreach(s => println(s))


}
