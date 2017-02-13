package com.tngtech.jgiven.report;

import java.util.List;

public class SimpleCommandLineParser extends AbstractCommandLineParser {

    private String delimiter = "=";

    /**
     * Only two types of arguments allowed:
     *
     *  * --<flagname>=<value>
     *  * --<flagname> (implicit value set to true)
     *
     */
    public void parseArgs( List<String> flags, List<String> args ) {
        for( String arg : args ) {
            for( String flag : flags ) {
                if( arg.startsWith( flag ) ) {
                    String[] formatArgs = arg.split( delimiter );
                    if( formatArgs.length < 2 ) {
                        getFlagMap().put( arg, "true" );
                    } else {
                        getFlagMap().put( flag, formatArgs[1] );
                    }
                }
            }
        }
    }
}

