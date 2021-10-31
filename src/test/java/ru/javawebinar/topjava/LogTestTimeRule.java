package ru.javawebinar.topjava;

import org.junit.rules.TestRule;
import org.junit.runner.Description;
import org.junit.runners.model.Statement;
import org.slf4j.Logger;

import javax.naming.ldap.HasControls;

import java.util.HashMap;
import java.util.Map;

import static org.slf4j.LoggerFactory.getLogger;

public class LogTestTimeRule implements TestRule {
    private static final Logger log = getLogger(LogTestTimeRule.class);

    public static final Map<String, Long> methodDuration = new HashMap<>();

    @Override
    public Statement apply(Statement statement, Description description) {
        return new Statement() {
            @Override
            public void evaluate() throws Throwable {
                Long startTime = System.currentTimeMillis();
                try {
                    statement.evaluate();
                } finally {
                    Long endTime = System.currentTimeMillis();
                    //log.info("{} took {} ms",description.getMethodName(),endTime - startTime);
                    methodDuration.put(description.getMethodName(), endTime - startTime);
                }

            }
        };
    }

    public Map<String, Long> getMethodDuration() {
        return methodDuration;
    }

}
