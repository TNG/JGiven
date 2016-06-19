package com.tngtech.jgiven.report.analysis;

import com.google.common.collect.Lists;
import com.google.common.collect.Multiset;
import com.google.common.collect.Sets;
import com.google.common.collect.TreeMultiset;
import com.tngtech.jgiven.impl.util.AssertionUtil;
import com.tngtech.jgiven.report.model.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Set;

/**
 * Analyzes a report model and tries to infer which step method arguments match to which case argument.
 *
 * This is done by comparing all cases of a scenario and find out which method arguments
 * match in all cases to the same parameter.
 *
 * The algorithm is rather complex, but I could not find an easier one yet.
 *
 */
public class CaseArgumentAnalyser {
    private static final Logger log = LoggerFactory.getLogger( CaseArgumentAnalyser.class );

    public void analyze( ReportModel model ) {
        for( ScenarioModel scenarioModel : model.getScenarios() ) {
            analyze( scenarioModel );
        }
    }

    static class JoinedArgs {
        final List<Word> words;

        public JoinedArgs( Word word ) {
            words = Lists.newArrayList( word );
        }
    }

    public void analyze( ScenarioModel scenarioModel ) {
        if( scenarioModel.getScenarioCases().size() == 1 ) {
            return;
        }

        if( !isStructuralIdentical( scenarioModel ) ) {
            log.debug( "Cases are structurally different, cannot create data table" );
            return;
        }
        scenarioModel.setCasesAsTable( true );

        // get all words that are arguments
        List<List<Word>> argumentWords = collectArguments( scenarioModel );
        AssertionUtil.assertFalse( argumentCountDiffer( argumentWords ), "Argument count differs" );

        // filter out arguments that are the same in all cases
        // only keep arguments that actually differ between cases
        List<List<Word>> differentArguments = getDifferentArguments( argumentWords );

        // now join arguments that are the same within each case
        List<List<JoinedArgs>> joinedArgs = joinEqualArguments( differentArguments );

        // finally we try to use the parameter names of the scenario
        List<List<String>> explicitParameterValues = getExplicitParameterValues( scenarioModel.getScenarioCases() );
        List<String> argumentNames = findArgumentNames( joinedArgs, explicitParameterValues, scenarioModel.getExplicitParameters() );

        List<List<Word>> arguments = getFirstWords( joinedArgs );

        setParameterNames( joinedArgs, argumentNames );
        scenarioModel.setDerivedParameters( argumentNames );

        for( int iCase = 0; iCase < arguments.size(); iCase++ ) {
            scenarioModel.getCase( iCase ).setDerivedArguments( getFormattedValues( arguments.get( iCase ) ) );
        }
    }

    private List<List<String>> getExplicitParameterValues( List<ScenarioCaseModel> scenarioCases ) {
        List<List<String>> explicitParameterValues = Lists.newArrayListWithExpectedSize( scenarioCases.size() );

        for( ScenarioCaseModel caseModel : scenarioCases ) {
            explicitParameterValues.add( caseModel.getExplicitArguments() );
        }

        return explicitParameterValues;
    }

    /**
     * Finds for each JoinedArgs set the best fitting name.
     * <p>
     * First it is tried to find a name from the explicitParameterNames list, by comparing the argument values
     * with the explicit case argument values. If no matching value can be found, the name of the argument is taken.
     */
    private List<String> findArgumentNames( List<List<JoinedArgs>> joinedArgs, List<List<String>> explicitParameterValues,
            List<String> explicitParameterNames ) {
        List<String> argumentNames = Lists.newArrayListWithExpectedSize( joinedArgs.get( 0 ).size() );
        Multiset<String> paramNames = TreeMultiset.create();

        arguments:
        for( int iArg = 0; iArg < joinedArgs.get( 0 ).size(); iArg++ ) {
            parameters:
            for( int iParam = 0; iParam < explicitParameterNames.size(); iParam++ ) {
                String paramName = explicitParameterNames.get( iParam );

                boolean formattedValueMatches = true;
                boolean valueMatches = true;
                for( int iCase = 0; iCase < joinedArgs.size(); iCase++ ) {
                    JoinedArgs args = joinedArgs.get( iCase ).get( iArg );

                    String parameterValue = explicitParameterValues.get( iCase ).get( iParam );

                    String formattedValue = args.words.get( 0 ).getFormattedValue();
                    if( !formattedValue.equals( parameterValue ) ) {
                        formattedValueMatches = false;
                    }

                    String value = args.words.get( 0 ).getValue();
                    if( !value.equals( parameterValue ) ) {
                        valueMatches = false;
                    }

                    if( !formattedValueMatches && !valueMatches ) {
                        continue parameters;
                    }
                }

                // on this point either all formatted values match or all values match (or both)
                argumentNames.add( paramName );
                paramNames.add( paramName );
                continue arguments;
            }

            argumentNames.add( null );
        }

        Set<String> usedNames = Sets.newHashSet();
        for( int iArg = 0; iArg < joinedArgs.get( 0 ).size(); iArg++ ) {
            String name = argumentNames.get( iArg );
            if( name == null || paramNames.count( name ) > 1 ) {
                String origName = getArgumentName( joinedArgs, iArg );
                name = findFreeName( usedNames, origName );
                argumentNames.set( iArg, name );
            }
            usedNames.add( name );

        }

        return argumentNames;
    }

    private String getArgumentName( List<List<JoinedArgs>> joinedArgs, int iArg ) {
        return joinedArgs.get( 0 ).get( iArg ).words.get( 0 ).getArgumentInfo().getArgumentName();
    }

    private String findFreeName( Set<String> usedNames, String origName ) {
        String name = origName;
        int counter = 2;
        while( usedNames.contains( name ) ) {
            name = origName + counter;
            counter++;
        }
        usedNames.add( name );
        return name;
    }

    private List<List<Word>> getFirstWords( List<List<JoinedArgs>> joinedArgs ) {
        List<List<Word>> result = Lists.newArrayList();
        for( int i = 0; i < joinedArgs.size(); i++ ) {
            result.add( Lists.<Word>newArrayList() );
        }

        for( int i = 0; i < joinedArgs.size(); i++ ) {
            for( int j = 0; j < joinedArgs.get( i ).size(); j++ ) {
                result.get( i ).add( joinedArgs.get( i ).get( j ).words.get( 0 ) );
            }
        }

        return result;
    }

    List<List<JoinedArgs>> joinEqualArguments( List<List<Word>> differentArguments ) {
        List<List<JoinedArgs>> joined = Lists.newArrayList();
        for( int i = 0; i < differentArguments.size(); i++ ) {
            joined.add( Lists.<JoinedArgs>newArrayList() );
        }

        if( differentArguments.get( 0 ).isEmpty() ) {
            return joined;
        }

        for( int iCase = 0; iCase < differentArguments.size(); iCase++ ) {
            joined.get( iCase ).add( new JoinedArgs( differentArguments.get( iCase ).get( 0 ) ) );
        }

        int numberOfArgs = differentArguments.get( 0 ).size();

        outer:
        for( int i = 1; i < numberOfArgs; i++ ) {
            inner:
            for( int j = 0; j < joined.get( 0 ).size(); j++ ) {

                for( int iCase = 0; iCase < differentArguments.size(); iCase++ ) {
                    Word newWord = differentArguments.get( iCase ).get( i );
                    Word joinedWord = joined.get( iCase ).get( j ).words.get( 0 );

                    if( !newWord.getFormattedValue().equals( joinedWord.getFormattedValue() ) ) {
                        continue inner;
                    }
                }

                for( int iCase = 0; iCase < differentArguments.size(); iCase++ ) {
                    joined.get( iCase ).get( j ).words.add( differentArguments.get( iCase ).get( i ) );
                }

                continue outer;
            }

            for( int iCase = 0; iCase < differentArguments.size(); iCase++ ) {
                joined.get( iCase ).add( new JoinedArgs( differentArguments.get( iCase ).get( i ) ) );
            }
        }

        return joined;
    }

    /**
     * A scenario model is structural identical if all cases have exactly the same
     * steps, except for values of step arguments.
     * <p>
     * This is implemented by comparing all cases with the first one
     */
    private boolean isStructuralIdentical( ScenarioModel scenarioModel ) {
        ScenarioCaseModel firstCase = scenarioModel.getScenarioCases().get( 0 );

        for( int iCase = 1; iCase < scenarioModel.getScenarioCases().size(); iCase++ ) {
            ScenarioCaseModel caseModel = scenarioModel.getScenarioCases().get( iCase );
            if( stepsAreDifferent( firstCase, caseModel ) ) {
                return false;
            }
        }

        return true;
    }

    private boolean stepsAreDifferent( ScenarioCaseModel firstCase, ScenarioCaseModel caseModel ) {
        if( firstCase.getSteps().size() != caseModel.getSteps().size() ) {
            return true;
        }

        for( int iStep = 0; iStep < firstCase.getSteps().size(); iStep++ ) {
            StepModel firstStep = firstCase.getStep( iStep );
            StepModel stepModel = caseModel.getStep( iStep );

            if( firstStep.getWords().size() != stepModel.getWords().size() ) {
                return true;
            }

            if( attachmentsAreStructurallyDifferent( firstStep.getAttachments(), stepModel.getAttachments() ) ) {
                return true;
            }

            if( wordsAreDifferent( firstStep, stepModel ) ) {
                return true;
            }
        }
        return false;
    }

    /**
     * Attachments are only structurally different if one step has an inline attachment
     * and the other step either has no inline attachment or the inline attachment is
     * different.
     */
    boolean attachmentsAreStructurallyDifferent( List<AttachmentModel> firstAttachments, List<AttachmentModel> otherAttachments ) {
        if( firstAttachments.size() != otherAttachments.size() ) {
            return true;
        }

        for( int i = 0; i < firstAttachments.size(); i++ ) {
            if( attachmentIsStructurallyDifferent( firstAttachments.get( i ), otherAttachments.get( i ) ) ) {
                return true;
            }
        }
        return false;
    }

    boolean attachmentIsStructurallyDifferent( AttachmentModel firstAttachment, AttachmentModel otherAttachment ) {
        if( isInlineAttachment( firstAttachment ) != isInlineAttachment( otherAttachment ) ) {
            return true;
        }

        if( isInlineAttachment( firstAttachment ) ) {
            return !firstAttachment.getValue().equals( otherAttachment.getValue() );
        }

        return false;
    }

    private boolean isInlineAttachment( AttachmentModel attachmentModel ) {
        return attachmentModel != null && attachmentModel.isShowDirectly();
    }

    private boolean wordsAreDifferent( StepModel firstStep, StepModel stepModel ) {
        for( int iWord = 0; iWord < firstStep.getWords().size(); iWord++ ) {
            Word firstWord = firstStep.getWord( iWord );
            Word word = stepModel.getWord( iWord );

            if( firstWord.isArg() != word.isArg() ) {
                return true;
            }

            if( !firstWord.isArg() && !firstWord.getValue().equals( word.getValue() ) ) {
                return true;
            }

            if( firstWord.isArg() && firstWord.isDataTable()
                    && !firstWord.getArgumentInfo().getDataTable().equals( word.getArgumentInfo().getDataTable() ) ) {
                return true;
            }

        }
        return false;
    }

    private void setParameterNames( List<List<JoinedArgs>> differentArguments, List<String> argumentNames ) {
        AssertionUtil.assertTrue( argumentNames.size() == differentArguments.get( 0 ).size(), "Number of argument names is wrong" );

        for( int iArg = 0; iArg < argumentNames.size(); iArg++ ) {
            for( int iCase = 0; iCase < differentArguments.size(); iCase++ ) {
                for( Word word : differentArguments.get( iCase ).get( iArg ).words ) {
                    word.getArgumentInfo().setParameterName( argumentNames.get( iArg ) );
                }
            }
        }
    }

    private List<String> getFormattedValues( List<Word> words ) {
        List<String> formattedValues = Lists.newArrayListWithExpectedSize( words.size() );
        for( Word word : words ) {
            formattedValues.add( word.getFormattedValue() );
        }
        return formattedValues;
    }

    /**
     * Returns a list with argument words that are not equal in all cases
     */
    List<List<Word>> getDifferentArguments( List<List<Word>> argumentWords ) {
        List<List<Word>> result = Lists.newArrayList();
        for( int i = 0; i < argumentWords.size(); i++ ) {
            result.add( Lists.<Word>newArrayList() );
        }

        int nWords = argumentWords.get( 0 ).size();

        for( int iWord = 0; iWord < nWords; iWord++ ) {
            Word wordOfFirstCase = argumentWords.get( 0 ).get( iWord );

            // data tables have equal here, otherwise
            // the cases would be structurally different
            if( wordOfFirstCase.isDataTable() ) {
                continue;
            }

            boolean different = false;
            for( int iCase = 1; iCase < argumentWords.size(); iCase++ ) {
                Word wordOfCase = argumentWords.get( iCase ).get( iWord );
                if( !wordOfCase.getFormattedValue().equals( wordOfFirstCase.getFormattedValue() ) ) {
                    different = true;
                    break;
                }

            }
            if( different ) {
                for( int iCase = 0; iCase < argumentWords.size(); iCase++ ) {
                    result.get( iCase ).add( argumentWords.get( iCase ).get( iWord ) );
                }
            }
        }

        return result;
    }

    private List<List<Word>> collectArguments( ScenarioModel scenarioModel ) {
        List<List<Word>> argumentWords = Lists.newArrayList();

        for( ScenarioCaseModel scenarioCaseModel : scenarioModel.getScenarioCases() ) {
            argumentWords.add( findArgumentWords( scenarioCaseModel ) );
        }
        return argumentWords;
    }

    private boolean argumentCountDiffer( List<List<Word>> argumentWords ) {
        int nArgs = argumentWords.get( 0 ).size();

        for( int i = 1; i < argumentWords.size(); i++ ) {
            if( argumentWords.get( i ).size() != nArgs ) {
                return true;
            }
        }
        return false;
    }

    private List<Word> findArgumentWords( ScenarioCaseModel scenarioCaseModel ) {
        List<Word> arguments = Lists.newArrayList();
        for( StepModel step : scenarioCaseModel.getSteps() ) {
            for( Word word : step.getWords() ) {
                if( word.isArg() ) {
                    arguments.add( word );
                }
            }
        }
        return arguments;
    }
}
