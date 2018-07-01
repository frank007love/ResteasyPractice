package org.tonylin.practice.resteasy;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses({ MessageBodyTest.class, SnifferReadInterceptorTest.class, RestClientProviderTest.class , DebugReadInterceptorTest.class})
public class AllTests {

}
