package com.tngtech.jgiven.examples.pancakes.test;

import com.tngtech.jgiven.Stage;
import com.tngtech.jgiven.annotation.As;
import com.tngtech.jgiven.annotation.FillerWord;
import com.tngtech.jgiven.annotation.ProvidedScenarioState;
import com.tngtech.jgiven.examples.pancakes.app.Cook;
import com.tngtech.jgiven.examples.pancakes.app.PanCakeCook;
import com.tngtech.jgiven.examples.pancakes.test.steps.ThenMeal;
import com.tngtech.jgiven.examples.pancakes.test.steps.WhenCook;
import com.tngtech.jgiven.junit.ScenarioTest;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static com.tngtech.jgiven.examples.pancakes.test.ExtendedVocabularyPanCakeTest.GivenIngredients;


public class ExtendedVocabularyPanCakeTest extends ScenarioTest<GivenIngredients, WhenCook, ThenMeal> {

    @ProvidedScenarioState
    private final Cook cook = new PanCakeCook();

    @Test
    public void a_pancake_can_be_fried_out_of_an_egg_milk_and_flour() {

        given() .some() .open_bracket().fresh().close_bracket() .ingredients().comma() .consisting_of().colon()
            .an() .egg()
            .some() .milk()
            .the() .ingredient( "flour" );

        when() .the_cook_mangles_everthing_to_a_dough().
            and() .the_cook_fries_the_dough_in_a_pan();

        then() .the_resulting_meal_is_a_pan_cake();

    }

    public static class GivenIngredients extends Stage<GivenIngredients> {

        @ProvidedScenarioState
        List<String> ingredients = new ArrayList<>();

        @FillerWord
        public GivenIngredients fresh() {
            return self();
        }

        @FillerWord
        public GivenIngredients ingredients() {
            return self();
        }

        @FillerWord
        public GivenIngredients an() {
            return self();
        }

        @FillerWord
        public GivenIngredients some() {
            return self();
        }

        @FillerWord
        public GivenIngredients the() {
            return self();
        }

        @As(",")
        @FillerWord(joinToPreviousWord = true)
        public GivenIngredients comma() {
            return self();
        }

        @As(":")
        @FillerWord(joinToPreviousWord = true)
        public GivenIngredients colon() {
            return self();
        }

        @As("(")
        @FillerWord(joinToNextWord = true)
        public GivenIngredients open_bracket() {
            return self();
        }

        @As(")")
        @FillerWord(joinToPreviousWord = true)
        public GivenIngredients close_bracket() {
            return self();
        }

        public GivenIngredients consisting_of() {
            return self();
        }

        public GivenIngredients egg() {
            return ingredient( "egg" );
        }

        public GivenIngredients milk() {
            return ingredient( "milk" );
        }

        public GivenIngredients ingredient(String ingredient ) {
            ingredients.add( ingredient );
            return self();
        }
    }

}
