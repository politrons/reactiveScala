package app.impl.scala

/**
  * I have seen far by standing on the shoulders of giants.
  * This whole exercise was based on the RockJVM youtube post https://www.youtube.com/watch?v=Y5rPHZaUakg&t=487s
  */
object FunctionalMap extends App {

  trait MapF[K, V] {

    def has(key: K): Boolean

    def add(key: K, value: V): MapF[K, V]

  }

  /**
    * Factory companion object to create MapF
    */
  object MapF {

    def add[K, V](key: K, value: V): GenericMap[K, V] = {
      GenericMap(k => k == key, k =>
        if (k == key) {
          value
        } else {
          throw new IllegalAccessException
        })

    }
  }

  case class GenericMap[K, V](find: K => Boolean, get: K => V) extends MapF[K, V] {

    def has(key: K): Boolean = find(key)

    def add(key: K, value: V): GenericMap[K, V] = GenericMap[K, V](e => find(e) || e == key, k => {
      if (k == key) {
        value
      } else {
        get(k)
      }
    })

    def get(key: V): GenericMap[K, V] = get(key)

  }

  private val functionalMap: GenericMap[Int, String] = MapF add(1, "hello") add(2, "world")

  println(s"Exist:${functionalMap.has(1)}")
  println(s"Value:${functionalMap.get(1)}")


}
