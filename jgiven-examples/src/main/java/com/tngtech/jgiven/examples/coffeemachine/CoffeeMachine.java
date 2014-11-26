package com.tngtech.jgiven.examples.coffeemachine;

public class CoffeeMachine {

    static final int DEFAULT_COFFEE_PRICE = 2;

    public int price = DEFAULT_COFFEE_PRICE;
    public boolean on;
    public int dollars;
    public int coffees;
    public String message;

    public boolean pressButton() {
        if( !on ) {
            return false;
        }

        if( coffees == 0 ) {
            message = "Error: No coffees left";
            return false;
        }

        if( dollars < price ) {
            message = "Error: Insufficient money";
            return false;
        }

        coffees--;
        dollars = 0;
        message = "Enjoy your coffee!";
        return true;
    }

    public void insertOneEuroCoin(int dollars) {
        this.dollars += dollars;
    }

}
