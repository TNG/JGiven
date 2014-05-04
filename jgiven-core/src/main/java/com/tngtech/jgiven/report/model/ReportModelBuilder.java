package com.tngtech.jgiven.report.model;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.CaseFormat;
import com.google.common.base.Strings;
import com.google.common.base.Throwables;
import com.google.common.collect.Lists;
import com.thoughtworks.paranamer.BytecodeReadingParanamer;
import com.thoughtworks.paranamer.ParameterNamesNotFoundException;
import com.thoughtworks.paranamer.Paranamer;
import com.tngtech.jgiven.annotation.CasesAsTable;
import com.tngtech.jgiven.annotation.Description;
import com.tngtech.jgiven.annotation.Format;
import com.tngtech.jgiven.annotation.Formatf;
import com.tngtech.jgiven.annotation.IsTag;
import com.tngtech.jgiven.annotation.NotImplementedYet;
import com.tngtech.jgiven.annotation.ScenarioDescription;
import com.tngtech.jgiven.format.DefaultFormatter;
import com.tngtech.jgiven.format.PrintfFormatter;
import com.tngtech.jgiven.impl.intercept.ScenarioListener;
import com.tngtech.jgiven.impl.util.WordUtil;
import com.tngtech.jgiven.report.model.StepFormatter.Formatting;

/**
 * Builds up the report model while the scenario is executed
 */
public class ReportModelBuilder implements ScenarioListener {
    private static final Logger log = LoggerFactory.getLogger( ReportModelBuilder.class );

    private static final Paranamer paranamer = new BytecodeReadingParanamer();

    private ScenarioModel currentScenarioModel;
    private ScenarioCaseModel currentScenarioCase;
    private ReportModel scenarioCollectionModel;

    private Word introWord;

    public ReportModelBuilder() {
        this( new ReportModel() );
    }

    public ReportModelBuilder( ReportModel scenarioCollectionModel ) {
        setModel( scenarioCollectionModel );
    }

    public void setModel( ReportModel scenarioCollectionModel ) {
        this.scenarioCollectionModel = scenarioCollectionModel;
    }

    @Override
    public void scenarioStarted( String description ) {
        String readableDescription = description;

        if( description.contains( "_" ) ) {
            readableDescription = description.replace( '_', ' ' );
        } else if( !description.contains( " " ) ) {
            readableDescription = camelCaseToReadableText( description );
        }

        currentScenarioCase = new ScenarioCaseModel();

        if( !scenarioCollectionModel.scenarios.isEmpty() ) {
            ScenarioModel scenarioModel = scenarioCollectionModel.scenarios.get( scenarioCollectionModel.scenarios.size() - 1 );
            if( scenarioModel.description.equals( readableDescription ) ) {
                currentScenarioModel = scenarioModel;
            }
        }

        if( currentScenarioModel == null ) {
            currentScenarioModel = new ScenarioModel();
            currentScenarioModel.className = scenarioCollectionModel.className;
            scenarioCollectionModel.scenarios.add( currentScenarioModel );
        }

        currentScenarioModel.addCase( currentScenarioCase );
        currentScenarioModel.description = readableDescription;
    }

    private String camelCaseToReadableText( String camelCase ) {
        String scenarioDescription = CaseFormat.LOWER_CAMEL.to( CaseFormat.LOWER_UNDERSCORE, camelCase ).replace( '_', ' ' );
        return WordUtil.capitalize( scenarioDescription );
    }

    public void addStepMethod( Method paramMethod, List<Object> arguments ) {
        String name;
        Description description = paramMethod.getAnnotation( Description.class );
        if( description != null ) {
            name = description.value();
        } else {
            name = nameWithoutUnderlines( paramMethod );
        }

        List<Formatting<?>> formatters = getFormatters( paramMethod.getParameterAnnotations() );
        List<Word> words = new StepFormatter( name, arguments, formatters ).buildFormattedWords();
        boolean notImplementedYet = paramMethod.isAnnotationPresent( NotImplementedYet.class ) ||
                paramMethod.getDeclaringClass().isAnnotationPresent( NotImplementedYet.class );

        if( introWord != null ) {
            words.add( 0, introWord );
            introWord = null;
        }

        writeStep( name, words, notImplementedYet );
    }

    @Override
    public void introWordAdded( String fillWord ) {
        introWord = new Word();
        introWord.isIntroWord = true;
        introWord.value = fillWord;
    }

    private List<Formatting<?>> getFormatters( Annotation[][] parameterAnnotations ) {
        List<Formatting<?>> res = Lists.newArrayList();
        for( Annotation[] annotations : parameterAnnotations ) {
            res.add( getFormatting( annotations ) );
        }
        return res;
    }

    @SuppressWarnings( { "rawtypes", "unchecked" } )
    private Formatting<?> getFormatting( Annotation[] annotations ) {
        for( Annotation annotation : annotations ) {
            try {
                if( annotation instanceof Format ) {
                    Format arg = (Format) annotation;
                    return new Formatting( arg.value().newInstance(), arg.args() );
                } else if( annotation instanceof Formatf ) {
                    Formatf arg = (Formatf) annotation;
                    return new Formatting( PrintfFormatter.class.newInstance(), arg.value() );
                }
            } catch( Exception e ) {
                throw Throwables.propagate( e );
            }
        }
        return null;
    }

    public void writeStep( String name, List<Word> words, boolean notImplementedYet ) {
        getCurrentScenarioCase().addStep( name, words, notImplementedYet );
    }

    private ScenarioCaseModel getCurrentScenarioCase() {
        if( currentScenarioCase == null ) {
            scenarioStarted( "An Undescribed Scenario" );
        }
        return currentScenarioCase;
    }

    @Override
    public void stepMethodInvoked( Method paramMethod, List<Object> arguments ) {
        if( !isStepMethod( paramMethod ) )
            return;
        addStepMethod( paramMethod, arguments );
    }

    public boolean isStepMethod( Method paramMethod ) {
        if( !Modifier.isPublic( paramMethod.getModifiers() ) )
            return false;

        return true;
    }

    public void setMethodName( String methodName ) {
        currentScenarioModel.testMethodName = methodName;
    }

    public void setArguments( List<String> arguments ) {
        currentScenarioCase.arguments = arguments;
    }

    public void setParameterNames( List<String> parameterNames ) {
        currentScenarioModel.parameterNames = parameterNames;
    }

    public void setClassName( String name ) {
        scenarioCollectionModel.className = name;
    }

    public void setSuccess( boolean b ) {
        if( !currentScenarioCase.steps.isEmpty() ) {
            currentScenarioCase.steps.get( currentScenarioCase.steps.size() - 1 ).failed = !b;
        }
        currentScenarioCase.success = b;
    }

    public void setErrorMessage( String message ) {
        currentScenarioCase.errorMessage = message;
    }

    private static String nameWithoutUnderlines( Method paramMethod ) {
        return paramMethod.getName().replace( '_', ' ' );
    }

    public ReportModel getScenarioCollectionModel() {
        return scenarioCollectionModel;
    }

    @Override
    public void scenarioFailed( Throwable e ) {
        setSuccess( false );
        setErrorMessage( e.getMessage() );
    }

    @Override
    public void scenarioSucceeded() {
        setSuccess( true );
    }

    @Override
    public void scenarioStarted( Method method, List<?> arguments ) {
        readAnnotations( method );
        readParameterNames( method );

        // must come at last
        setMethodName( method.getName() );
        setArguments( toStringList( arguments ) );
    }

    private List<String> toStringList( List<?> arguments ) {
        List<String> result = Lists.newArrayList();
        for( Object o : arguments ) {
            result.add( new DefaultFormatter<Object>().format( o ) );
        }
        return result;
    }

    private void readParameterNames( Method method ) {
        try {
            setParameterNames( Arrays.asList( paranamer.lookupParameterNames( method ) ) );
        } catch( ParameterNamesNotFoundException e ) {
            log.warn( "Could not determine parameter names for method " + method
                    + ". You should compile your source code with debug information." );
        }
    }

    private void readAnnotations( Method method ) {
        String scenarioDescription = method.getName();

        if( method.isAnnotationPresent( ScenarioDescription.class ) ) {
            scenarioDescription = method.getAnnotation( ScenarioDescription.class ).value();
        }

        scenarioStarted( scenarioDescription );

        if( method.isAnnotationPresent( CasesAsTable.class ) ) {
            currentScenarioModel.setCasesAsTable( true );
        }

        if( currentScenarioCase.caseNr == 1 ) {
            addTags( method.getDeclaringClass().getAnnotations() );
            addTags( method.getAnnotations() );
        }
    }

    private void addTags( Annotation[] annotations ) {
        for( Annotation annotation : annotations ) {
            this.currentScenarioModel.tags.addAll( toTags( annotation ) );
        }
    }

    public static List<Tag> toTags( Annotation annotation ) {
        Class<? extends Annotation> annotationType = annotation.annotationType();
        IsTag isTag = annotationType.getAnnotation( IsTag.class );
        if( isTag == null ) {
            return Collections.emptyList();
        }

        String type = annotationType.getSimpleName();

        if( !Strings.isNullOrEmpty( isTag.type() ) ) {
            type = isTag.type();
        }

        Tag tag = new Tag( type );

        if( isTag.prependType() ) {
            tag.setPrependType( true );
        }

        if( isTag.description() != null ) {
            tag.setDescription( isTag.description() );
        }

        if( !Strings.isNullOrEmpty( isTag.value() ) ) {
            tag.setValue( isTag.value() );
        }

        if( isTag.ignoreValue() ) {
            return Arrays.asList( tag );
        }

        try {
            Method method = annotationType.getMethod( "value" );
            Object value = method.invoke( annotation );
            if( value != null ) {
                if( value.getClass().isArray() ) {
                    Object[] array = (Object[]) value;
                    String[] stringArray = new String[array.length];
                    for( int i = 0; i < array.length; i++ ) {
                        stringArray[i] = array[i] + "";
                    }
                    if( isTag.explodeArray() ) {
                        return getExplodedTags( tag, stringArray );
                    }
                    tag.setValue( stringArray );
                } else {
                    tag.setValue( value + "" );
                }
            }
        } catch( NoSuchMethodException ignore ) {

        } catch( Exception e ) {
            log.error( "Error while getting 'value' method of annotation " + annotation, e );
        }

        return Arrays.asList( tag );
    }

    private static List<Tag> getExplodedTags( Tag originalTag, String[] stringArray ) {
        List<Tag> result = Lists.newArrayList();
        for( String singleValue : stringArray ) {
            Tag newTag = new Tag( originalTag.getName(), singleValue );
            newTag.setDescription( originalTag.getDescription() );
            newTag.setPrependType( originalTag.isPrependType() );
            result.add( newTag );
        }
        return result;
    }
}
