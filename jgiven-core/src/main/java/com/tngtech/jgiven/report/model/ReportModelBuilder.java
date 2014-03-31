package com.tngtech.jgiven.report.model;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.LocalVariableTableParameterNameDiscoverer;

import com.google.common.base.CaseFormat;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.tngtech.jgiven.annotation.Format;
import com.tngtech.jgiven.annotation.Formatf;
import com.tngtech.jgiven.annotation.IsTag;
import com.tngtech.jgiven.annotation.NotImplementedYet;
import com.tngtech.jgiven.annotation.ScenarioDescription;
import com.tngtech.jgiven.format.PrintfFormatter;
import com.tngtech.jgiven.format.StepFormatter;
import com.tngtech.jgiven.format.StepFormatter.Formatting;
import com.tngtech.jgiven.impl.intercept.ScenarioListener;
import com.tngtech.jgiven.impl.util.WordUtil;

/**
 * Builds up the report model while the scenario is executed
 */
public class ReportModelBuilder implements ScenarioListener {
    private static final Logger log = LoggerFactory.getLogger( ReportModelBuilder.class );

    private static final LocalVariableTableParameterNameDiscoverer discoverer = new LocalVariableTableParameterNameDiscoverer();

    private ScenarioModel lastScenarioModel;
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
                lastScenarioModel = scenarioModel;
            }
        }

        if( lastScenarioModel == null ) {
            lastScenarioModel = new ScenarioModel();
            lastScenarioModel.className = scenarioCollectionModel.className;
            scenarioCollectionModel.scenarios.add( lastScenarioModel );
        }

        lastScenarioModel.addCase( currentScenarioCase );
        lastScenarioModel.description = readableDescription;
    }

    private String camelCaseToReadableText( String camelCase ) {
        String scenarioDescription = CaseFormat.LOWER_CAMEL.to( CaseFormat.LOWER_UNDERSCORE, camelCase ).replace( '_', ' ' );
        return WordUtil.capitalize( scenarioDescription );
    }

    public void writeScenarioCase( ScenarioCaseModel scenarioCase ) {
        currentScenarioCase = scenarioCase;
    }

    public void addStepMethod( Method paramMethod, List<Object> arguments ) {
        String name = nameWithoutUnderlines( paramMethod );

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
                log.error( e.getMessage(), e );
                return null;
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
        lastScenarioModel.testMethodName = methodName;
    }

    public void addAnnotation( Tag tag ) {
        lastScenarioModel.addTag( tag );
    }

    public void setCaseNr( int caseNr ) {
        currentScenarioCase.caseNr = caseNr;
    }

    public void setArguments( List<String> arguments ) {
        currentScenarioCase.arguments = arguments;
    }

    public void setParameterNames( List<String> parameterNames ) {
        lastScenarioModel.parameterNames = parameterNames;
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

    public ScenarioModel getCurrentScenarioModel() {
        return lastScenarioModel;
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
            result.add( "" + o );
        }
        return result;
    }

    private void readParameterNames( Method method ) {
        setParameterNames( Arrays.asList( discoverer.getParameterNames( method ) ) );
    }

    static class ScenarioAnnotations {
        Set<String> tags = Sets.newHashSet();
        String description = "";
    }

    private void readAnnotations( Method method ) {

        ScenarioAnnotations scenarioAnnotations = new ScenarioAnnotations();
        scenarioAnnotations.description = method.getName();

        if( method.isAnnotationPresent( ScenarioDescription.class ) ) {
            scenarioAnnotations.description = method.getAnnotation( ScenarioDescription.class ).value();
        }

        scenarioStarted( scenarioAnnotations.description );

        if( currentScenarioCase.caseNr == 1 ) {
            addTags( scenarioAnnotations, method.getDeclaringClass().getAnnotations() );
            addTags( scenarioAnnotations, method.getAnnotations() );
        }
    }

    private void addTags( ScenarioAnnotations scenarioAnnotations, Annotation[] annotations ) {
        for( Annotation annotation : annotations ) {
            this.lastScenarioModel.tags.addAll( toTags( annotation ) );
        }
    }

    public static List<Tag> toTags( Annotation annotation ) {

        Class<? extends Annotation> annotationType = annotation.annotationType();
        IsTag isTag = annotationType.getAnnotation( IsTag.class );
        if( isTag == null ) {
            return Collections.emptyList();
        }

        Tag tag = new Tag();
        tag.name = annotationType.getSimpleName();

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
                        return getExplodedTags( annotationType.getSimpleName(), stringArray );
                    } else {
                        tag.value = stringArray;
                    }
                } else {
                    tag.value = value + "";
                }
            }
        } catch( NoSuchMethodException ignore ) {

        } catch( Exception e ) {
            log.error( "Error while getting 'value' method of annotation " + annotation, e );
        }

        return Arrays.asList( tag );
    }

    private static List<Tag> getExplodedTags( String tagName, String[] stringArray ) {
        List<Tag> result = Lists.newArrayList();
        for( String singleValue : stringArray ) {
            Tag tag = new Tag();
            tag.name = tagName;
            tag.value = singleValue;
            result.add( tag );
        }
        return result;
    }
}
