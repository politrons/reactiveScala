import rx.lang.scala.Observable

/**
  * Created by pabloperezgarcia on 26/6/16.
  */
class Filtering extends Generic {

  def filter(): Unit = {
    addHeader("Observable filter")
    Observable.from(List(1, 2, 3, 4, 5))
      .filter(isBiggerThan(2))
      .map(n => n * 10)
      .filter(isBiggerThan(20))
      .map(transformToString)
      .filter(isString)
      .subscribe(println(_))
  }

  def isBiggerThan(number:Integer): (Int) => Boolean = {
    n => n > number
  }

  def transformToString: (Int) => String = {
    n => String.valueOf(n)
  }

  def isString: (String) => Boolean = {
    s => s.isInstanceOf[String]
  }
}
