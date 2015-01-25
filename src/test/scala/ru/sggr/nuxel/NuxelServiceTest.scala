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

  test("simple validator logic check") {
    ???
  }

}
