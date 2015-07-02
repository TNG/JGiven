package com.tngtech.jgiven.tests.java8;

import com.tngtech.jgiven.Stage;

public class LambdaSteps<SELF extends LambdaSteps<SELF>> extends Stage<SELF> {

    public SELF some_lambda_step( int a, int b ) {
        methodTakingALambda( ( ) -> {} );
        return self();
    }

    private void methodTakingALambda( Runnable runnable ) {

    }
}
