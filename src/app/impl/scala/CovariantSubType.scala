package app.impl.scala

import org.junit.Test

/**
  * Covariant subtypes is a very powerful feature, normally when you define on Java a generic type
  * You can specify if that Type extends/super from a class type.
  * Here using the character +  we can specify that the class accept a type and all subtypes of that type.
  * That gave us the the possibility to receive whatever type and subtypes
  */
class CovariantSubType {

  class Company[+T](company: T) // Adding the + specify that company is not only accept type T,
                                // but also all subtypes of T

  class BigCompany

  class SmallCompany extends BigCompany

  class startUp extends SmallCompany


  class Investor(company: Company[BigCompany])

  @Test def testCovariant(): Unit = {
    val bigInvestor:Investor = new Investor(new Company[BigCompany](new BigCompany))
    val smallInvestor:Investor=new Investor(new Company[SmallCompany](new SmallCompany))
    val startUpInvestor:Investor=new Investor(new Company[startUp](new startUp))


  }

}
