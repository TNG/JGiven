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
            .setDescription( "the format of the report. Either html5, ascii or text (default: html5)" )
            .setDefaultWith( ReportGenerator.Format.HTML5 ) // this may be a sane choice
            .build();

    /**
     * A help flag is predefined for every report and checked in the
     */
    private static ConfigOption help = new ConfigOptionBuilder( "help" )
            .setCommandLineOptionWithoutArgument(
                    new CommandLineOptionBuilder( "--help" ).setShortPrefix( "-h" ).build(),
                    true )
            .setDescription( "print this help message" )
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
            for( ConfigOption co : configList ) {
                commandLineLookup( arg, co, configList );
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
    private void commandLineLookup( String arg, ConfigOption co, List<ConfigOption> configList ) {
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
        List<ConfigOption> configOptions = Arrays.asList( format );

        for( String arg : args ) {
            configParser.commandLineLookup( arg, format, configOptions );
        }

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
