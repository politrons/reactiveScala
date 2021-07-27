import java.util.Date
import scala.collection.immutable

object William extends App {


  val s =
    """|photo.jpg, Warsaw, 2013-09-05 14:08:15
       |john.png, London, 2015-06-20 15:13:22
       |myFriends.png, Warsaw, 2013-09-05 14:07:13
       |Eiffel.jpg, Paris, 2015-07-23 08:03:02
       |pisatower.jpg, Paris, 2015-07-22 23:59:59
       |BOB.jpg, London, 2015-08-05 00:02:03
       |notredame.png, Paris, 2015-09-01 12:00:00
       |me.jpg, Warsaw, 2013-09-06 15:40:22
       |a.png, Warsaw, 2016-02-13 13:33:50
       |b.jpg, Warsaw, 2016-01-02 15:12:22
       |c.jpg, Warsaw, 2016-01-02 14:34:30
       |d.jpg, Warsaw, 2016-01-02 15:15:01
       |e.png, Warsaw, 2016-01-02 09:49:09
       |f.png, Warsaw, 2016-01-02 10:55:32
       |g.jpg, Warsaw, 2016-02-29 22:13:11
       |""".stripMargin

  case class OriginalPhotoName(value: String)

  case class PhotoName(value: String)

  val format = new java.text.SimpleDateFormat("yyyy-MM-dd hh:mm:ss")

  val photoInfo: Array[String] = s.split("\n")

  val cityMap: Map[String, List[(PhotoName, Date)]] = Map()

  val cities: immutable.Iterable[(OriginalPhotoName, PhotoName)] =
    photoInfo.foldLeft(cityMap)((cities, line) => {
      val photoInfo = line.split(",")
      val city = photoInfo(1)
      val cityNameAndDate: (PhotoName, Date) = (PhotoName(photoInfo(0)), format.parse(photoInfo(2)))
      val cityNamesAndDates = cities.getOrElse(city, List())
      cities + (city -> (cityNameAndDate +: cityNamesAndDates))
    }).map(element => {
      element._1 -> element._2.sortBy(_._2)
    }).flatMap(element => {
      val city = element._1
      var number = 0
      val photosInCityCharLength = element._2.length.toString.length
      element._2.foldLeft(List[(OriginalPhotoName, PhotoName)]())((photos, tuple) => {
        number = number + 1
        val zerosInFront = photosInCityCharLength - number.toString.length
        val photoNumber = "0" * zerosInFront + number
        val originalPhotoName = OriginalPhotoName(tuple._1.value)
        val photoExtension = tuple._1.value.split("\\.")(1)
        (originalPhotoName, PhotoName(s"$city$photoNumber.$photoExtension")) +: photos
      })
    })

  val orderPhotos = photoInfo.foldLeft(List[String]())((list, line) =>
    line.split(",")(0) +: list
  ).reverse
    .foldLeft(new StringBuffer())((sb, photoName) => {
      val tuple = cities.find(tuple => tuple._1.value == photoName).get
      sb.append(tuple._2.value.trim).append("\n")
    }).toString

  println(orderPhotos)
}
