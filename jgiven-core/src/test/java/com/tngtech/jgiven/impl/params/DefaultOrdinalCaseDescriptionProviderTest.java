package com.tngtech.jgiven.impl.params;

import com.google.common.collect.Lists;
import com.tngtech.java.junit.dataprovider.DataProvider;
import com.tngtech.java.junit.dataprovider.DataProviderRunner;
import com.tngtech.java.junit.dataprovider.DataProviders;
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
public class DefaultOrdinalCaseDescriptionProviderTest extends TestCase {

    @Rule
    public SeedInfo seed = new SeedInfo();

    @DataProvider
    public static Object[][] randomStrings() {
        return DataProviders.testForEach( lists( strings(), 20 ).next() );
    }

    @UseDataProvider( "randomStrings" )
    @Test
    public void test( String someString ) throws Exception {
        seed.restore( -5294091015527388791L );
        DefaultOrdinalCaseDescriptionProvider provider = new DefaultOrdinalCaseDescriptionProvider();
        String description = provider.description( "$1", Lists.newArrayList( "someName" ), Lists.newArrayList( someString ) );
        assertThat( description ).isEqualTo( someString );
    }
}