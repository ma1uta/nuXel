package ru.sggr.nuxel

import org.scalatest.FunSuite
import BeanValidator.function2validator

/**
 * bunch of tests for ru.sggr.nuxel.NuxelService
 */
class NuxelServiceTest extends FunSuite{

  test("first simple test, read the file, get beans, count them") {
    assert(NuxelService.getInstance(getClass.getClassLoader.getResourceAsStream("test.xls")).
      extractBeans.size() == 11)
  }

  test("chain validators syntax") {
    NuxelService.getInstance(null).addValidator((x: Bean) => if (x.name contains "a") "Fault: contains 'a'" else "" ).
    addValidator(new BeanValidator {
      override def validate(bean: Bean) = ""
    })
  }

  /*test("simple validator logic check") {
    ???
  }*/

 test("Testing xslx parsing") {
   NuxelService.getInstance(getClass.getClassLoader.getResourceAsStream("modernFile.xlsx") ).extractBeans
 }

 test("Test for out of bound issue") {
   assert(NuxelService.
     getInstance(getClass.getClassLoader.getResourceAsStream("outofbound.xls") ).
     extractBeans.size == 4)

 }

 test("Not aligned data in sheet") {
   assert(NuxelService.getInstance(getClass.getClassLoader.getResourceAsStream("notaligned.xls") ).
     extractBeans.size == 4)
   println((NuxelService.getInstance(getClass.getClassLoader.getResourceAsStream("notaligned.xls") ).
     extractBeans))
 }

 test("Not aligned data in sheet... from alex") {
   assert(NuxelService.getInstance(getClass.getClassLoader.getResourceAsStream("notaligned_one_more.xls") ).
     extractBeans.size == 3)
   println((NuxelService.getInstance(getClass.getClassLoader.getResourceAsStream("notaligned_one_more.xls") ).
     extractBeans))
 }

 test("Complex file from customer with lots of unrelated data and joined cells") {
   assert(NuxelService.getInstance(getClass.getClassLoader.getResourceAsStream("customer_sample.xlsx")).extractBeans.size == 2)
 }

 test("One more complex file from customer") {
   assert(NuxelService.getInstance(getClass.getClassLoader.getResourceAsStream("one-more-order.xls")).extractBeans.size == 1)
 }

 test("Another order file format") {
   assert(NuxelService.getInstance(getClass.getClassLoader.getResourceAsStream("one_more_order_form.xls")).extractBeans.size == 1)
 }

 test("This thing crashes for some reason") {
   assert(NuxelService.getInstance(getClass.getClassLoader.getResourceAsStream("crush.xlsx")).extractBeans.size == 1)
 }

 test("Let's check the order of items in this xlsx") {
   val beans = NuxelService.getInstance(getClass.getClassLoader.getResourceAsStream("list_order_check.xlsx")).extractBeans
   println(beans)
 }


}
