package com.tngtech.jgiven.impl.params;

import com.google.common.collect.Lists;
import com.tngtech.java.junit.dataprovider.DataProvider;
import com.tngtech.java.junit.dataprovider.DataProviderRunner;
import com.tngtech.java.junit.dataprovider.UseDataProvider;
import junit.framework.TestCase;
import net.java.quickcheck.junit.SeedInfo;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static net.java.quickcheck.generator.CombinedGenerators.lists;
import static net.java.quickcheck.generator.PrimitiveGenerators.strings;
import static org.assertj.core.api.Assertions.assertThat;

@RunWith( value = DataProviderRunner.class )
public class DefaultCaseAsProviderTest extends TestCase {

    @Rule
    public SeedInfo seed = new SeedInfo();

    @DataProvider
    public static Iterable<String> randomStrings(){
        return lists(strings(), 20).next();
    }

    @UseDataProvider( "randomStrings" )
    @Test
    public void test( String someString ) {
        seed.restore( -5294091015527388791L );
        DefaultCaseAsProvider provider = new DefaultCaseAsProvider();
        String description = provider.as( "$1", Lists.newArrayList( "someName" ), Lists.newArrayList( someString ) );
        assertThat( description ).isEqualTo( someString );
    }
}