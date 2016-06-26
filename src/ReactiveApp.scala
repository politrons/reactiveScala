

/**
  * Created by pabloperezgarcia on 26/6/16.
  */


object ReactiveApp {

  def main(args: Array[String]) {

    val transforming = new Transforming();
    transforming.map();
    transforming.flatMap();

    val creating = new Creating();
    creating.interval();
    creating.deferObservable();

    val filtering = new Filtering();
    filtering.filter();


  }


}



