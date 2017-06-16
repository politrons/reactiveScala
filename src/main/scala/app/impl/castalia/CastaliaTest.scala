package app.impl.castalia

//import castalia.Main

/**
  * Created by pabloperezgarcia on 21/04/2017.
  *
  * Castalias it´ an open source Service discovery project, it´s based in Akka actors and
  * it´ really handy to mock this sort of service discovery applications.
  * You can find the original project to contribute here
  *
  * https://github.com/scala-academy/castalia
  *
  */
//class CastaliaTest {
//
//  val serviceDiscoveryAddress = "localhost:9000"
//  val endPointManagementAddress = "localhost:9090"
//  val serviceDiscovery = finagle.Http.newService(s"$serviceDiscoveryAddress")
//  val endPointManagementService = finagle.Http.newService(s"$endPointManagementAddress")
//  val endPointManagementPath = "castalia/manager/endpoints"
//
//  val server = Main
//  server.main(Array("castalia.json"))
//
//  @Before
//  def registerEndPoints(): Unit = {
//    val url = s"http://$endPointManagementAddress/$endPointManagementPath"
//    val body =
//      """
//        |{
//        | "endpoint": "my/endpoint/$1",
//        | "responses": [
//        |     {
//        |        "ids": {
//        |           "1": "0"
//        |           },
//        |        "httpStatusCode": 200
//        |     }]
//        | }
//      """.stripMargin
//
//    val registerEndPointRequest = RequestBuilder()
//      .url(url)
//      .setHeader("Content-Type", "application/json")
//      .buildPost(utf8Buf(body))
//
//    val response: Response = Await.result(endPointManagementService(registerEndPointRequest))
//
//    assert(response.statusCode == 200)
//  }
//
//  @Test
//  def consumeEndPoint(): Unit = {
//    val request = Request(Method.Get, "/my/endpoint/0")
//    request.host = serviceDiscoveryAddress
//    val response: Response = Await.result(serviceDiscovery(request))
//    assert(response.statusCode == 200)
//  }
//}
