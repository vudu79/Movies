package ru.vodolatskii.movies.presentation.fragments

import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.test.espresso.Espresso
import androidx.test.espresso.ViewAction
import androidx.test.espresso.ViewAssertion
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.ext.junit.rules.activityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.hamcrest.Matchers
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import ru.vodolatskii.movies.R
import ru.vodolatskii.movies.presentation.MainActivity

@RunWith(AndroidJUnit4::class)
class SelectionsFragmentTest {
//    @get: Rule
//    var activityScenario = activityScenarioRule<MainActivity>()

    @Test
    fun ShouldEnabledButtonWHENTextInserted() {
        launchFragmentInContainer<SelectionsFragment>()

        Espresso.onView(ViewMatchers.withId(R.id.textedit)).perform(ViewActions.typeText("123"))
        Espresso.closeSoftKeyboard()
        Espresso.onView(ViewMatchers.withId(R.id.button_next))
            .check(ViewAssertions.matches(ViewMatchers.isEnabled()))
    }

    @Test
    fun ShouldNotBeEnabledWHENTextIsBlack() {
        launchFragmentInContainer<SelectionsFragment>()
        Espresso.onView(ViewMatchers.withId(R.id.textedit)).perform(ViewActions.typeText("werwer"))
        Espresso.onView(ViewMatchers.withId(R.id.textedit)).perform(ViewActions.clearText())
        Espresso.closeSoftKeyboard()
        Espresso.onView(ViewMatchers.withId(R.id.button_next)).check(ViewAssertions.matches(Matchers.not(ViewMatchers.isEnabled())))

    }

}