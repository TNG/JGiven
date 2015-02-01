package com.tngtech.jgiven.report.text;

import static org.fusesource.jansi.Ansi.Attribute.INTENSITY_BOLD;
import static org.fusesource.jansi.Ansi.Color.MAGENTA;

import java.io.PrintWriter;
import java.util.List;

import org.fusesource.jansi.Ansi.Attribute;
import org.fusesource.jansi.Ansi.Color;

import com.google.common.base.Function;
import com.google.common.base.Joiner;
import com.google.common.collect.Iterables;
import com.tngtech.jgiven.impl.util.WordUtil;
import com.tngtech.jgiven.report.model.*;

public class PlainTextScenarioWriter extends PlainTextWriter {
    private static final String INDENT = "   ";

    protected ScenarioModel currentScenarioModel;
    protected ScenarioCaseModel currentCaseModel;
    private int maxFillWordLength;

    public PlainTextScenarioWriter( PrintWriter printWriter, boolean withColor ) {
        super( printWriter, withColor );
    }

    @Override
    public void visit( ScenarioModel scenarioModel ) {
        writer.print( "\n" + withColor( MAGENTA, INTENSITY_BOLD, " Scenario: " ) );
        println( Color.MAGENTA, scenarioModel.getDescription() + "\n" );
        currentScenarioModel = scenarioModel;
    }

    @Override
    public void visitEnd( ScenarioCaseModel scenarioCase ) {
        if( !scenarioCase.success ) {
            writer.println();
            writer.print( "FAILED: " + scenarioCase.errorMessage );
        }
        writer.println();
    }

    @Override
    public void visit( ScenarioCaseModel scenarioCase ) {
        if( currentScenarioModel.getScenarioCases().size() > 1 ) {
            printCaseLine( scenarioCase );
        }
        maxFillWordLength = new MaxFillWordLengthGetter().getLength( scenarioCase );
        currentCaseModel = scenarioCase;
    }

    protected void printCaseLine( ScenarioCaseModel scenarioCase ) {
        writer.print( "  Case " + scenarioCase.getCaseNr() + ": " );
        List<String> arguments = scenarioCase.getExplicitArguments();
        if( !arguments.isEmpty() ) {
            List<String> parameterNames = currentScenarioModel.getExplicitParameters();
            for( int i = 0; i < arguments.size(); i++ ) {
                if( i < parameterNames.size() ) {
                    writer.print( parameterNames.get( i ) + " = " );
                }
                writer.print( arguments.get( i ) );
                if( i != arguments.size() - 1 ) {
                    writer.print( ", " );
                }
            }
        }
        writer.println();
    }

    static class MaxFillWordLengthGetter extends ReportModelVisitor {
        private int maxLength;

        public int getLength( ScenarioCaseModel scenarioCase ) {
            scenarioCase.accept( this );
            return maxLength;
        }

        @Override
        public void visit( StepModel stepModel ) {
            Word word = stepModel.words.get( 0 );
            if( word.isIntroWord() ) {
                int length = word.getValue().length();
                if( length > maxLength ) {
                    maxLength = length;
                }
            }
        }
    }

    @Override
    public void visit( StepModel stepModel ) {
        String intro = "";
        List<Word> words = stepModel.words;
        if( words.get( 0 ).isIntroWord() ) {
            intro = withColor( Color.BLUE, Attribute.INTENSITY_BOLD,
                INDENT + String.format( "%" + maxFillWordLength + "s ", WordUtil.capitalize( words.get( 0 ).getValue() ) ) );
        } else {
            intro = INDENT + words.get( 0 ).getValue() + " ";
        }

        int restSize = words.size();
        boolean printDataTable = false;
        if( words.size() > 1 ) {
            Word lastWord = words.get( words.size() - 1 );
            if( lastWord.isArg() && lastWord.getArgumentInfo().isDataTable() ) {
                restSize = restSize - 1;
                printDataTable = true;
            }

        }
        String rest = joinWords( words.subList( 1, restSize ) );

        if( stepModel.isNotImplementedYet() ) {
            rest = withColor( Color.BLACK, true, Attribute.INTENSITY_FAINT, rest + " (not implemented yet)" );
        } else if( stepModel.isSkipped() ) {
            rest = withColor( Color.BLACK, true, Attribute.INTENSITY_FAINT, rest + " (skipped)" );
        } else if( stepModel.isFailed() ) {
            rest = withColor( Color.RED, true, Attribute.INTENSITY_FAINT, rest );
            rest += withColor( Color.RED, true, Attribute.INTENSITY_BOLD, " (failed)" );
        }
        writer.println( intro + rest );

        if( printDataTable ) {
            writer.println();
            printDataTable( words.get( words.size() - 1 ) );
        }
    }

    private void printDataTable( Word word ) {
        PlainTextTableWriter plainTextTableWriter = new PlainTextTableWriter( writer, withColor );
        String indent = INDENT + "  ";
        plainTextTableWriter.writeDataTable( word.getArgumentInfo().getDataTable(), indent );
        writer.println();
    }

    private String joinWords( List<Word> words ) {
        return Joiner.on( " " ).join( Iterables.transform( words, new Function<Word, String>() {
            @Override
            public String apply( Word input ) {
                return wordToString( input );
            }
        } ) );
    }

    protected String wordToString( Word word ) {
        if( word.isArg() && !isInt( word ) ) {
            return word.getFormattedValue();
        }
        return word.getValue();
    }

    private boolean isInt( Word word ) {
        try {
            Integer.valueOf( word.getFormattedValue() );
            return true;
        } catch( NumberFormatException e ) {
            return false;
        }
    }

}
