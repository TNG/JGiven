package com.tngtech.jgiven.impl;

import java.util.Stack;

class StepOffset {

    private Stack<Integer> stepOffset = new Stack<>();

    StepOffset(){
        stepOffset.push(0);
    }

    void beganNonModelStep(){

    }

    void beganModelStep(){
        stepOffset.push(0);
    }
}
