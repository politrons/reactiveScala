package app.impl.scalaz

import java.util.concurrent.TimeUnit

import org.junit.Test

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration.Duration
import scala.concurrent.{Await, Future}
import scalaz.OptionT
import scalaz.std.scalaFuture._
import scalaz.syntax.monad._


/**
  * Created by pabloperezgarcia on 15/10/2017.
  *
  * OptionT is a monad transformer that give us the possibility to nested in the structure F[M[G]] --> Future[Option[Value]]
  * avoiding have to worry about value/empty of the option.
  */
class OptionTMonadTransformer {

//  /**
//    * Without monad transformer we have tp worry to get the value or create default of optional
//    *
//    * @return
//    */
//  def getEmployeeAge(employeeId: String, companyName: String): Future[Option[Int]] = {
//    val db = new DbAsync
//    val eventualMaybeInt = for {
//      companyOpt: Option[Company] <- db.getCompany(companyName)
//      company: Company = companyOpt.getOrElse(Company("error", List()))
//      if company.employees map (_.id) contains employeeId
//      detailsOpt: Option[EmployeeWithDetails] <- db.getDetails(employeeId)
//    } yield detailsOpt map (_.age)
//    eventualMaybeInt
//  }
//
//  /**
//    * Here thanks to use monad transform we can forgot about nested the option and get the value.
//    * The monad transform already do that for us. In case of dont have a value in the optional, it default behave
//    * is return None and stop the pipeline.
//    */
//  def getEmployeeAgeWithOptionT(employeeId: String, companyName: String): Future[Option[Int]] = {
//    val db = new DbAsync
//    val eventualOption = (for {
//      company <- OptionT(db.getCompany(companyName))
//      if company.employees map (_.id) contains employeeId
//      details <- OptionT(db.getDetails(employeeId))
//    } yield details.age).run
//    eventualOption
//  }
//
//  /**
//    * We can adapt also our pipeline to be used by the monad transform in case we´e not passing a Future[Option[Value]]
//    * Using pure[Future] we wrap an Option into a Future, and using liftM we wrap the value into a Option into the Future.
//    */
//  def getEmployeeAgeWithOptionTHybrid(employeeId: String, companyName: String): Future[Option[Int]] = {
//    val db = new DbHybrid
//    val eventualOption = (for {
//      company <- OptionT(db.getCompany(companyName).pure[Future])
//      if company.employees map (_.id) contains employeeId
//      details <- db.getDetails(employeeId).liftM[OptionT]
//    } yield details.age).run
//    eventualOption
//  }
//
//  /**
//    * Here in the second execution, since we dont have a compensation in case the second predicate return false we will
//    * receive a NoSuchElementException Since predicate is not satisfied and we cannot get any value for the age.
//    */
//  @Test
//  def plainForComprehension(): Unit = {
//    val result = Await.result(getEmployeeAge("1", "Tesco"), Duration.create(10, TimeUnit.SECONDS))
//    print(result)
//    val result1 = Await.result(getEmployeeAge("2", "Tesco"), Duration.create(10, TimeUnit.SECONDS))
//    print(result1)
//  }
//
//  @Test
//  def futureWithOptionT(): Unit = {
//    val result = Await.result(getEmployeeAgeWithOptionT("1", "Tesco"), Duration.create(10, TimeUnit.SECONDS))
//    println(result)
//    val result1 = Await.result(getEmployeeAgeWithOptionT("foo", "Tesco"), Duration.create(10, TimeUnit.SECONDS))
//    println(result1)
//
//  }
//
//  @Test
//  def optionAndFuture(): Unit = {
//    val result = Await.result(getEmployeeAgeWithOptionTHybrid("1", "Tesco"), Duration.create(10, TimeUnit.SECONDS))
//    println(result)
//    val result1 = Await.result(getEmployeeAgeWithOptionTHybrid("foo", "Tesco"), Duration.create(10, TimeUnit.SECONDS))
//    println(result1)
//
//  }
//
//  //Attributes for this example
//  sealed trait Employee {
//    val id: String
//  }
//
//  final case class EmployeeWithoutDetails(id: String) extends Employee
//
//  final case class EmployeeWithDetails(id: String, name: String, city: String, age: Int) extends Employee
//
//  case class Company(companyName: String, employees: List[EmployeeWithoutDetails])
//
//  //Database class to return elements
//  trait AsyncDBOps {
//    protected def getDetails(employeeId: String): Future[Option[EmployeeWithDetails]]
//
//    protected def getCompany(companyName: String): Future[Option[Company]]
//  }
//
//  trait HybridDBOps {
//    protected def getDetails(employeeId: String): Future[EmployeeWithDetails]
//
//    protected def getCompany(companyName: String): Option[Company]
//  }
//
//  class DbAsync extends AsyncDBOps {
//    override def getDetails(employeeId: String): Future[Option[EmployeeWithDetails]] =
//      Future {
//        Some(EmployeeWithDetails("1", "name", "city", 36))
//      }
//
//    override def getCompany(companyName: String): Future[Option[Company]] =
//      Future {
//        Some(Company(companyName, List(EmployeeWithoutDetails("1"))))
//      }
//  }
//
//  class DbHybrid extends HybridDBOps {
//    override def getDetails(employeeId: String): Future[EmployeeWithDetails] = Future {
//      EmployeeWithDetails("1", "name", "city", 36)
//    }
//
//    override def getCompany(companyName: String): Option[Company] = Some(Company(companyName, List(EmployeeWithoutDetails("1"))))
//  }


}
