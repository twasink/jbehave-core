/*
 * (c) 2003-2004 ThoughtWorks Ltd
 *
 * See license.txt for license details
 */
package jbehave.junit;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import jbehave.core.Ensure;
import jbehave.core.mock.UsingConstraints;
import jbehave.junit.JUnitAdapter.BehavioursAdapter;
import junit.framework.Test;
import junit.framework.TestResult;


/**
 * @author <a href="mailto:dan.north@thoughtworks.com">Dan North</a>
 * @author Mauro Talevi
 */
public class JUnitAdapterBehaviour extends UsingConstraints {
    private static final List sequenceOfEvents = new ArrayList();
    
    public void setUp() {
        sequenceOfEvents.clear();
    }
    
    public static class HasSingleMethod {
        public void shouldDoSomething() throws Exception {
        }
    }
    
    public void shouldCountSingleBehaviourMethodAsTest() throws Exception {
        // setup
        JUnitAdapter.setBehaviours(new BehavioursAdapter(HasSingleMethod.class));
        Test suite = JUnitAdapter.suite();
        
        // execute
        int testCaseCount = suite.countTestCases();
        
        // verify
        ensureThat(testCaseCount, eq(1));
    }
    
    public static class HasTwoMethods {
        public void shouldDoSomething() throws Exception {
        }
        
        public void shouldDoSomethingElse() throws Exception {
        }
    }
    
    public void shouldCountMultipleBehaviourMethodsAsTests() throws Exception {
        // setup
        JUnitAdapter.setBehaviours(new BehavioursAdapter(HasTwoMethods.class));
        Test suite = JUnitAdapter.suite();
        // execute
        int testCaseCount = suite.countTestCases();
        
        // verify
        ensureThat(testCaseCount, eq(2));
    }
    
    public static class HasFailingMethod {
        public void shouldDoSomething() throws Exception {
            Ensure.impossible("should not be invoked");
        }
    }
    
    public void shouldNotExecuteBehaviourMethodsWhileCountingThem() throws Exception {
        // setup
        JUnitAdapter.setBehaviours(new BehavioursAdapter(HasFailingMethod.class));
        Test suite = JUnitAdapter.suite();
        
        // execute
        suite.countTestCases();
    }
    
    public static class SomeBehaviourClass {
        public void shouldDoSomething() throws Exception {
            sequenceOfEvents.add("shouldDoSomething");
        }
        public void shouldDoSomethingElse() throws Exception {
            sequenceOfEvents.add("shouldDoSomethingElse");
        }
    }
    
    public void shouldExecuteBehaviourMethods() throws Exception {
        // setup
        JUnitAdapter.setBehaviours(new BehavioursAdapter(SomeBehaviourClass.class));
        Test suite = JUnitAdapter.suite();
        
        TestResult testResult = new TestResult() {
            public void startTest(Test test) {
                sequenceOfEvents.add("startTest");
            }
            public void endTest(Test test) {
                sequenceOfEvents.add("endTest");
            }
        };
        
        // execute
        suite.run(testResult);
        
        // verify
        List expectedSequenceOfEvents = Arrays.asList(new String[] {
                "startTest", "shouldDoSomething", "endTest",
                "startTest", "shouldDoSomethingElse", "endTest"
        });
        Ensure.that(testResult.wasSuccessful());
        for ( Iterator i = expectedSequenceOfEvents.iterator(); i.hasNext(); ){
            String event = (String)i.next();
            ensureThat(event, isContainedIn(sequenceOfEvents));            
        }
    }
}
