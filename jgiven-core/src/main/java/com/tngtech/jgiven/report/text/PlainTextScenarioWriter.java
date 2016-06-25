package com.tngtech.jgiven.report.text;

import java.io.PrintWriter;
import java.util.List;

import org.fusesource.jansi.Ansi.Attribute;
import org.fusesource.jansi.Ansi.Color;

import com.google.common.base.Function;
import com.google.common.base.Joiner;
import com.google.common.base.Strings;
import com.google.common.collect.Iterables;
import com.tngtech.jgiven.impl.params.DefaultCaseDescriptionProvider;
import com.tngtech.jgiven.impl.util.WordUtil;
import com.tngtech.jgiven.report.model.*;

public class PlainTextScenarioWriter extends PlainTextWriter {
    protected static final String INDENT = "   ";
    public static final String NESTED_HEADING = "  ";
    public static final String NESTED_INDENT = "  ";

    protected ScenarioModel currentScenarioModel;
    protected ScenarioCaseModel currentCaseModel;
    private int maxFillWordLength;
    private boolean firstStep;

    public PlainTextScenarioWriter( PrintWriter printWriter, boolean withColor ) {
        super( printWriter, withColor );
    }

    @Override
    public void visit( ScenarioModel scenarioModel ) {
        writer.println( "\n " + bold( WordUtil.capitalize( scenarioModel.getDescription() ) ) + "\n" );
        currentScenarioModel = scenarioModel;
        firstStep = true;
    }

    @Override
    public void visitEnd( ScenarioCaseModel scenarioCase ) {
        if( !scenarioCase.isSuccess() ) {
            writer.println();
            writer.print( withColor( Color.RED, Attribute.INTENSITY_BOLD, "FAILED: " + scenarioCase.getErrorMessage() ) );
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
        writer.print( bold( "  Case " + scenarioCase.getCaseNr() + ": " ) );
        writer.println( bold( getDescriptionOrDefault( scenarioCase ) ) );
        writer.println();
    }

    public String getDescriptionOrDefault( ScenarioCaseModel scenarioCase ) {
        if( scenarioCase.hasDescription() ) {
            return scenarioCase.getDescription();
        } else {
            return DefaultCaseDescriptionProvider.defaultDescription( currentScenarioModel.getExplicitParameters(),
                scenarioCase.getExplicitArguments() );
        }
    }

    static class MaxFillWordLengthGetter extends ReportModelVisitor {
        private int maxLength = 1;

        public int getLength( ScenarioCaseModel scenarioCase ) {
            scenarioCase.accept( this );
            return maxLength;
        }

        @Override
        public void visit( StepModel stepModel ) {
            Word word = stepModel.getWords().get( 0 );
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
        printStep( stepModel, 0, false );
        firstStep = false;
    }

    private void printStep( StepModel stepModel, int depth, boolean showPassed ) {
        List<Word> words = stepModel.getWords();

        if( stepModel.isSectionTitle() ) {
            printSectionTitle( stepModel );
            return;
        }

        String introString = getIntroString( words, depth );

        int restSize = words.size();
        boolean printDataTable = false;
        if( words.size() > 1 ) {
            Word lastWord = words.get( words.size() - 1 );
            if( lastWord.isArg() && lastWord.getArgumentInfo().isDataTable() ) {
                restSize = restSize - 1;
                printDataTable = true;
            }
        }

        int introWordIndex = words.get( 0 ).isIntroWord() ? 1 : 0;
        String line = introString + joinWords( words.subList( introWordIndex, restSize ) );
        if( stepModel.isPending() ) {
            line = gray( line + " (pending)" );
        } else if( stepModel.isSkipped() ) {
            line = gray( line + " (skipped)" );
        } else if( stepModel.isFailed() ) {
            line = boldRed( line + " (failed)" );
        } else if( showPassed ) {
            line = green( line + " (passed)" );
        }

        if( !Strings.isNullOrEmpty( stepModel.getComment() ) ) {
            line = line + gray( String.format( " [%s]", stepModel.getComment() ) );
        }

        writer.println( line );

        printNestedSteps( stepModel, depth, showPassed );

        if( printDataTable ) {
            writer.println();
            printDataTable( words.get( words.size() - 1 ) );
        }
    }

    private void printSectionTitle( StepModel stepModel ) {
        if( !firstStep ) {
            writer.println();
        }
        writer.println( INDENT + bold( joinWords( stepModel.getWords() ) ) );
        writer.println();
    }

    private String getIntroString( List<Word> words, int depth ) {
        String intro;
        if( depth > 0 ) {
            intro = INDENT + String.format( "%" + maxFillWordLength + "s ", " " ) +
                    Strings.repeat( NESTED_INDENT, depth - 1 ) + NESTED_HEADING;

            if( words.get( 0 ).isIntroWord() ) {
                intro = intro + WordUtil.capitalize( words.get( 0 ).getValue() ) + " ";
            }
        } else {
            if( words.get( 0 ).isIntroWord() ) {
                intro = INDENT + String.format( "%" + maxFillWordLength + "s ", WordUtil.capitalize( words.get( 0 ).getValue() ) );
            } else {
                intro = INDENT + String.format( "%" + maxFillWordLength + "s ", " " );
            }
        }
        return intro;
    }

    private void printNestedSteps( StepModel stepModel, int depth, boolean showPassed ) {
        for( StepModel nestedStepModel : stepModel.getNestedSteps() ) {
            printStep( nestedStepModel, depth + 1, stepModel.getStatus() == StepStatus.FAILED || showPassed );
        }
    }

    private void printDataTable( Word word ) {
        PlainTextTableWriter plainTextTableWriter = new PlainTextTableWriter( writer, withColor );
        plainTextTableWriter.writeDataTable( word.getArgumentInfo().getDataTable(), INDENT + "  " );
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
