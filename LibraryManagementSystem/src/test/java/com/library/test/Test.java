package com.library.test;

import org.junit.platform.suite.api.SelectClasses;
import org.junit.platform.suite.api.Suite;

@Suite
@SelectClasses({
    BookTest.class,
    CDTest.class,
    LoanTest.class,
    UserAccountTest.class,
    BookFineStrategyTest.class,
    CDFineStrategyTest.class,
    EmailNotifierMockTest.class,
    LibraryServiceTest.class
})
public class Test {
}
