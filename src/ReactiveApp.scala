

/**
  * Created by pabloperezgarcia on 26/6/16.
  */


object ReactiveApp {

  def main(args: Array[String]) {

    val transforming = new Transforming()
    transforming.map()
    transforming.flatMap()
    transforming.merge()
    transforming.zip()

    val creating = new Creating()
    creating.interval()
    creating.deferObservable()

    val filtering = new Filtering()
    filtering.filter()


  }


}



