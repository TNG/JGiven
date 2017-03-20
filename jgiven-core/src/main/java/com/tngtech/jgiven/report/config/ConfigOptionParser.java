package com.tngtech.jgiven.report.config;

import com.tngtech.jgiven.report.ReportGenerator;
import com.tngtech.jgiven.report.config.converter.ToFormat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

public class ConfigOptionParser {
    private static final Logger log = LoggerFactory.getLogger( ConfigOptionParser.class );

    /**
     * A format flag has to be defined for every report because it is used to determine which report to start
     */
    private static ConfigOption format = new ConfigOptionBuilder( "format" )
            .setCommandLineOptionWithArgument(
                    new CommandLineOptionBuilder( "--format" ).setArgumentDelimiter( "=" ).setVisualPlaceholder( "format" ).build(),
                    new ToFormat() )
            .setDescription( "the format of the report. Either html5, asciidoc or text (default: html5)" )
            .setDefaultWith( ReportGenerator.Format.HTML5 ) // this may be a sane choice
            .build();

    /**
     * A help flag is predefined for every report and checked in the
     */
    private static ConfigOption help = new ConfigOptionBuilder( "help" )
            .setCommandLineOptionWithoutArgument(
                    new CommandLineOptionBuilder( "--help" ).setShortPrefix( "-h" ).build(),
                    true )
            .setDescription( "print this help message which changes depending on the --format flag" )
            .setOptional()
            .build();

    private Map<String, Object> parsedOptions = new HashMap<String, Object>();

    /**
     *
     * @param co the configuration option to search for
     * @return returns a castable object
     */
    public Object getValue( ConfigOption co ) {
        return parsedOptions.get( co.getLongName() );
    }

    private boolean hasValue( ConfigOption co ) {
        return parsedOptions.containsKey( co.getLongName() );
    }

    /**
     *
     * Parses the configuration list and tries to create a mapping of the corresponding objects from the command line, properties
     * or environment variables
     *
     * As long as the {@link com.tngtech.jgiven.report.config.converter.StringConverter} are implemented with a null as fail
     * and a working conversion the mapped objects are always in a correct state and castable to their representation
     *
     * @param configList the configuration list by which to search for the objects
     * @param args command line arguments
     * @return returns the map of config.longName keys and corresponding castable objects
     */
    public Map<String, Object> generate( List<ConfigOption> configList, String... args ) {

        // default arguments
        configList.add( 0, format );
        configList.add( 1, help );
        for( ConfigOption co : configList ) {
            if( co.hasDefault() ) {
                parsedOptions.put( co.getLongName(), co.getValue() );
            }
        }

        // command line arguments
        for( String arg : args ) {
            boolean found = false;
            for( ConfigOption co : configList ) {
                found |= commandLineLookup( arg, co, configList );
            }
            if( !found ) {
                printSuggestion( arg, configList );
            }
        }

        // checking for non-optional flags
        for( ConfigOption co : configList ) {
            if( !co.isOptional() && !parsedOptions.containsKey( co.getLongName() ) ) {
                System.err.println( "Anticipating value for non-optional flag " + co.getCommandLineOption().showFlagInfo() );
                printUsageAndExit( configList );
            }
        }

        // TODO properties
        // TODO environment

        // help
        if( this.hasValue( help ) ) {
            printUsageAndExit( configList );
        }

        return parsedOptions;
    }

    /**
     * Compares the argument with the {@link CommandLineOption} flags and inserts an object into the parsedOptions map
     * Terminates with a sane help message if a parse is unsuccessful
     *
     * @param arg the current word from the command line argument list
     * @param co the config option to look for in the argument
     * @param configList the global config list, used to create a sane help message if the parse fails
     */
    private boolean commandLineLookup( String arg, ConfigOption co, List<ConfigOption> configList ) {
        if( arg.startsWith( co.getCommandLineOption().getLongFlag() ) || ( co.getCommandLineOption().hasShortFlag() && arg
                .startsWith( co.getCommandLineOption().getShortFlag() ) ) ) {

            if( co.getCommandLineOption().hasArgument() ) {
                String[] formatArgs = arg.split( co.getCommandLineOption().getDelimiter() );

                if( formatArgs.length < 2 ) {
                    System.err.println( "Anticipated argument after " + co.getCommandLineOption().showFlagInfo() + ", terminating." );
                    printUsageAndExit( configList );
                }

                Object value = co.toObject( formatArgs[1] );

                if( value == null ) {
                    System.err
                            .println( "Parse error for flag " + co.getCommandLineOption().showFlagInfo() + " got " + formatArgs[1] );
                    printUsageAndExit( configList );
                }

                log.debug( "setting the argument value: " + co.getLongName() + " to " + value );
                parsedOptions.put( co.getLongName(), value );
            } else {
                log.debug( "setting the default value of " + co.getLongName() + " to " + co.getValue() );
                parsedOptions.put( co.getLongName(), co.getValue() );
            }
            return true;
        }

        return false;
    }

    /**
     * Prints a suggestion to stderr for the argument based on the levenshtein distance metric
     *
     * @param arg the argument which could not be assigned to a flag
     * @param co the {@link ConfigOption} List where every flag is stored
     */
    private void printSuggestion( String arg, List<ConfigOption> co ) {
        List<ConfigOption> sortedList = new ArrayList<ConfigOption>( co );
        Collections.sort( sortedList, new ConfigOptionLevenshteinDistance( arg ) );
        System.err.println( "Parse error for argument \"" + arg + "\", did you mean " + sortedList.get( 0 ).getCommandLineOption()
                .showFlagInfo() + "? Ignoring for now." );

    }

    /**
     * Levenshtein Distance is defined as the amount of steps to be done, until we can form a word into another word
     * A step is a substitution, addition and removal of a character
     */
    private class ConfigOptionLevenshteinDistance implements Comparator<ConfigOption> {

        private String arg;

        ConfigOptionLevenshteinDistance( String arg ) {
            this.arg = arg;
        }

        public int compare( ConfigOption a, ConfigOption b ) {
            String[] formatArgsA = arg.split( a.getCommandLineOption().getDelimiter() );
            String[] formatArgsB = arg.split( b.getCommandLineOption().getDelimiter() );

            double distLongA = distance( a.getCommandLineOption().getLongFlag(), formatArgsA[0] );
            double distLongB = distance( b.getCommandLineOption().getLongFlag(), formatArgsB[0] );

            return distLongA < distLongB ? -1 : 1;
        }

        // blatantly adapted from wikipedia (https://en.wikipedia.org/wiki/Levenshtein_distance#Iterative_with_two_matrix_rows)
        private int distance( String a, String b ) {

            // degenerate cases
            if( a.equals( b ) )
                return 0;
            if( a.length() == 0 )
                return b.length();
            if( b.length() == 0 )
                return a.length();

            // create two work vectors of integer distances
            int[] v0 = new int[b.length() + 1];
            int[] v1 = new int[b.length() + 1];

            // initialize v0 (the previous row of distances)
            // this row is A[0][i]: edit distance for an empty s
            // the distance is just the number of characters to delete from t
            for( int i = 0; i < v0.length; i++ )
                v0[i] = i;

            for( int i = 0; i < a.length(); i++ ) {
                // calculate v1 (current row distances) from the previous row v0

                // first element of v1 is A[i+1][0]
                //   edit distance is delete (i+1) chars from s to match empty t
                v1[0] = i + 1;

                // use formula to fill in the rest of the row
                for( int j = 0; j < b.length(); j++ ) {
                    int cost = ( a.charAt( i ) == b.charAt( j ) ) ? 0 : 1;
                    v1[j + 1] = Math.min( Math.min( v1[j] + 1, v0[j + 1] + 1 ), v0[j] + cost );
                }

                // copy v1 (current row) to v0 (previous row) for next iteration
                System.arraycopy( v1, 0, v0, 0, v0.length );
            }

            return v1[b.length()];
        }
    }

    /**
     * Terminates  with a help message if the parse is not successful
     *
     * @param args command line arguments to
     * @return the format in a correct state
     */
    public static ReportGenerator.Format getFormat( String... args ) {
        ConfigOptionParser configParser = new ConfigOptionParser();
        List<ConfigOption> configOptions = Arrays.asList( format, help );

        for( ConfigOption co : configOptions ) {
            if( co.hasDefault() ) {
                configParser.parsedOptions.put( co.getLongName(), co.getValue() );
            }
        }

        for( String arg : args ) {
            configParser.commandLineLookup( arg, format, configOptions );
        }

        // TODO properties
        // TODO environment

        if( !configParser.hasValue( format ) ) {
            configParser.printUsageAndExit( configOptions );
        }
        return (ReportGenerator.Format) configParser.getValue( format );
    }

    /**
     *
     * Creates a help message based on the descriptions of the {@link ConfigOption} and terminates
     *
     * @param configOptions the configuration options of the report
     */
    public void printUsageAndExit( List<ConfigOption> configOptions ) {
        System.err.println( "Options: " );
        for( ConfigOption co : configOptions ) {
            System.err.printf( "  %-40s %s\n", co.getCommandLineOption().showFlagInfo(), co.getEnhancedDescription() );
        }
        System.exit( 1 );
    }

}
