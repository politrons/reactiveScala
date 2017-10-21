import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global
/**
  * Created by pabloperezgarcia on 15/10/2017.
  */
object MonadTransformer extends App {


  //Attributes for this example
  sealed trait Employee {
    val id: String
  }

  final case class EmployeeWithoutDetails(id: String) extends Employee

  final case class EmployeeWithDetails(id: String, name: String, city: String, age: Int) extends Employee

  case class Company(companyName: String, employees: List[EmployeeWithoutDetails])

  //Database class to return elements
  trait AsyncDBOps {
    protected def getDetails(employeeId: String): Future[Option[EmployeeWithDetails]]

    protected def getCompany(companyName: String): Future[Option[Company]]
  }

  class DB extends AsyncDBOps {
    override def getDetails(employeeId: String): Future[Option[EmployeeWithDetails]] = Future {
      Some(EmployeeWithDetails("1", "name", "city", 36))
    }

    override def getCompany(companyName: String): Future[Option[Company]] = Future {
      Some(Company(companyName, List(EmployeeWithoutDetails("1"))))
    }
  }

  //Monad transformer
  def getEmployeeAge(employeeId: String, companyName: String): Future[Option[Int]] = {
    val db = new DB
    val eventualMaybeInt = for {
      companyOpt: Option[Company] <- db.getCompany(companyName)
      company: Company = companyOpt.getOrElse(Company("error", List()))
      if company.employees map (_.id) contains employeeId
      detailsOpt: Option[EmployeeWithDetails] <- db.getDetails(employeeId)
    } yield detailsOpt map (_.age)
    println(eventualMaybeInt)
    eventualMaybeInt
  }

  getEmployeeAge("1","Tesco")

}
