package com.tngtech.jgiven.examples.coffeemachine;

public class CoffeeMachine {

    static final int DEFAULT_COFFEE_PRICE = 2;

    public int price = DEFAULT_COFFEE_PRICE;
    public boolean on;
    public int dollars;
    public int coffees;

    public boolean pressButton() {
        if ( on && coffees > 0 && dollars >= price ) {
            coffees--;
            dollars = 0;
            return true;
        } else {
            return false;
        }
    }

    public void insertMoney( int dollars ) {
        this.dollars += dollars;
    }

}