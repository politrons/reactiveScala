package app.impl.scalaz


/**
  * Created by pabloperezgarcia on 15/10/2017.
  */
class MonadTransformer {



//  //Custom Monads transfomer
//
//  @Test
//  def customMonad(): Unit = {
//    val value1 = for {
//      value <- MonadTransfomer(Future {
//        "hello"
//      })
//    } yield value
//    println(value1)
//  }
//
//  type Id[+A] = A
//
//  sealed trait Action[A]
//
//  case class _Action(action: Future[String]) extends Action[Any]
//
//  type ActionMonad[A] = Free[Action, A]
//
//  def MonadTransfomer(action: Future[String]): ActionMonad[Any] = {
//    liftF[Action, Any](_Action(action)).foldMap(interpreter)
//  }
//
//  def interpreter: Action ~> Id = new (Action ~> Id) {
//    def apply[A](a: Action[A]): Id[A] = a match {
//      case _Action(action) => Await.result(action, Duration.create(5, TimeUnit.SECONDS))
//    }
//  }

}
