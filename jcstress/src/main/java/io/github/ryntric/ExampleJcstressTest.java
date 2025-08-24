package io.github.ryntric;

import org.openjdk.jcstress.annotations.Actor;
import org.openjdk.jcstress.annotations.JCStressTest;
import org.openjdk.jcstress.annotations.Mode;
import org.openjdk.jcstress.annotations.State;
import org.openjdk.jcstress.infra.results.I_Result;

/**
 * author: vbondarchuk
 * date: 8/24/25
 * time: 9:39 PM
 **/

@JCStressTest
public class ExampleJcstressTest {

    @State
    public static class ExampleState {

    }

    @Actor
    public void test(ExampleState state, I_Result result) {

    }

}
