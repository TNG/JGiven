package com.tngtech.jgiven.report;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.List;

abstract public class AbstractCommandLineParser {
    private static final Logger log = LoggerFactory.getLogger( AbstractCommandLineParser.class );
    private Map<String, String> flagMap = new HashMap<String, String>();

    abstract public void parseArgs( List<String> flags, List<String> args );

    public Map<String, String> getFlagMap() {
        return flagMap;
    }

    public Map<String, String> parseToFlagMap( List<String> flags, List<String> args ) {
        parseArgs( flags, args );
        return getFlagMap();
    }

    public Logger log() {
        return log;
    }

    public static String getFormat( List<String> args ) {
        for( String arg : args ) {
            if( arg.startsWith( "--format=" ) ) {
                String[] formatArgs = arg.split( "=" );
                if( formatArgs.length < 2 ) {
                    log.error( "Could not split %s on delimiter %s", arg, "=" );
                } else {
                    return formatArgs[1];
                }
                break;
            }
        }
        return "";
    }
}
