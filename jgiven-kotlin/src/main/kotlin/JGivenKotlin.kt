package com.tngtech.jgiven.kotlin

import com.tngtech.jgiven.Stage
import com.tngtech.jgiven.base.ScenarioTestBase

/**
 * Annotation that can be used in a non spring kotlin project to mark stages and
 * make use of the `all-open` compiler plugin.
 */
annotation class JGivenStage

// extension attributes on testBase

val <G : Stage<G>, W : Stage<W>, T : Stage<T>> ScenarioTestBase<G, W, T>.GIVEN: G get() = given()
val <G : Stage<G>, W : Stage<W>, T : Stage<T>> ScenarioTestBase<G, W, T>.WHEN: W get() = `when`()
val <G : Stage<G>, W : Stage<W>, T : Stage<T>> ScenarioTestBase<G, W, T>.THEN: T get() = then()

// extension attributes on stage

val <X : Stage<X>> Stage<X>.AND: X get() = and()
val <X : Stage<X>> Stage<X>.WITH: X get() = with()
val <X : Stage<X>> Stage<X>.BUT: X get() = but()
val <X : Stage<X>> Stage<X>.SELF: X get() = self()!!
