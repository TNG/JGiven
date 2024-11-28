package com.tngtech.jgiven.exampleprojects.java17;

import java.util.function.*;

public class Java17 {

    public static boolean test(String y) {
       Predicate<String> p   = ( var x ) -> x == x;
       return p.test(y);
    }

    public static void main(String[] args) {
        System.out.println(test(args[0]));
    }

}
