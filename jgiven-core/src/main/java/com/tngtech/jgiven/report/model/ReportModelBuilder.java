package com.tngtech.jgiven.report.model;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.CaseFormat;
import com.google.common.base.Strings;
import com.google.common.base.Throwables;
import com.google.common.collect.Lists;
import com.tngtech.jgiven.annotation.CasesAsTable;
import com.tngtech.jgiven.annotation.Description;
import com.tngtech.jgiven.annotation.ExtendedDescription;
import com.tngtech.jgiven.annotation.Format;
import com.tngtech.jgiven.annotation.Formatf;
import com.tngtech.jgiven.annotation.IsTag;
import com.tngtech.jgiven.annotation.NotImplementedYet;
import com.tngtech.jgiven.annotation.ScenarioDescription;
import com.tngtech.jgiven.config.AbstractJGivenConfiguraton;
import com.tngtech.jgiven.config.ConfigurationUtil;
import com.tngtech.jgiven.config.DefaultConfiguration;
import com.tngtech.jgiven.config.TagConfiguration;
import com.tngtech.jgiven.format.DefaultFormatter;
import com.tngtech.jgiven.format.PrintfFormatter;
import com.tngtech.jgiven.impl.intercept.InvocationMode;
import com.tngtech.jgiven.impl.intercept.ScenarioListener;
import com.tngtech.jgiven.impl.util.AssertionUtil;
import com.tngtech.jgiven.impl.util.WordUtil;
import com.tngtech.jgiven.report.model.StepFormatter.Formatting;

/**
 * Builds up the report model while the scenario is executed.
 */
public class ReportModelBuilder implements ScenarioListener {
    private static final Logger log = LoggerFactory.getLogger( ReportModelBuilder.class );

    private ScenarioModel currentScenarioModel;
    private ScenarioCaseModel currentScenarioCase;
    private ReportModel reportModel;

    private Word introWord;

    private AbstractJGivenConfiguraton configuration = new DefaultConfiguration();

    private long scenarioStartedNanos;

    public ReportModelBuilder() {
        this( new ReportModel() );
    }

    public ReportModelBuilder( ReportModel scenarioCollectionModel ) {
        setReportModel( scenarioCollectionModel );
    }

    public void setReportModel( ReportModel reportModel ) {
        this.reportModel = reportModel;
    }

    @Override
    public void scenarioStarted( String description ) {
        scenarioStartedNanos = System.nanoTime();
        String readableDescription = description;

        if( description.contains( "_" ) ) {
            readableDescription = description.replace( '_', ' ' );
        } else if( !description.contains( " " ) ) {
            readableDescription = camelCaseToReadableText( description );
        }

        currentScenarioCase = new ScenarioCaseModel();

        currentScenarioModel = reportModel.findScenarioModel( readableDescription ).orNull();

        if( currentScenarioModel == null ) {
            currentScenarioModel = new ScenarioModel();
            currentScenarioModel.className = reportModel.getClassName();
            reportModel.getScenarios().add( currentScenarioModel );
        }

        currentScenarioModel.addCase( currentScenarioCase );
        currentScenarioModel.description = readableDescription;
    }

    private String camelCaseToReadableText( String camelCase ) {
        String scenarioDescription = CaseFormat.LOWER_CAMEL.to( CaseFormat.LOWER_UNDERSCORE, camelCase ).replace( '_', ' ' );
        return WordUtil.capitalize( scenarioDescription );
    }

    public void addStepMethod( Method paramMethod, List<NamedArgument> arguments, InvocationMode mode ) {
        StepModel stepModel = new StepModel();

        Description description = paramMethod.getAnnotation( Description.class );
        if( description != null ) {
            stepModel.name = description.value();
        } else {
            stepModel.name = nameWithoutUnderlines( paramMethod );
        }

        ExtendedDescription extendedDescriptionAnnotation = paramMethod.getAnnotation( ExtendedDescription.class );
        if( extendedDescriptionAnnotation != null ) {
            stepModel.setExtendedDescription( extendedDescriptionAnnotation.value() );
        }

        List<Formatting<?>> formatters = getFormatters( paramMethod.getParameterAnnotations() );
        stepModel.words = new StepFormatter( stepModel.name, arguments, formatters ).buildFormattedWords();

        if( introWord != null ) {
            stepModel.words.add( 0, introWord );
            introWord = null;
        }

        stepModel.setStatus( mode.toStepStatus() );
        writeStep( stepModel );
    }

    @Override
    public void introWordAdded( String fillWord ) {
        introWord = new Word();
        introWord.setIntroWord( true );
        introWord.setValue( fillWord );
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

    public void writeStep( StepModel stepModel ) {
        getCurrentScenarioCase().addStep( stepModel );
    }

    private ScenarioCaseModel getCurrentScenarioCase() {
        if( currentScenarioCase == null ) {
            scenarioStarted( "An Undescribed Scenario" );
        }
        return currentScenarioCase;
    }

    @Override
    public void stepMethodInvoked( Method paramMethod, List<NamedArgument> arguments, InvocationMode mode ) {
        addStepMethod( paramMethod, arguments, mode );
    }

    public void setMethodName( String methodName ) {
        currentScenarioModel.testMethodName = methodName;
    }

    public void setArguments( List<String> arguments ) {
        currentScenarioCase.setExplicitArguments( arguments );
    }

    public void setParameterNames( List<String> parameterNames ) {
        currentScenarioModel.setExplicitParameters( parameterNames );
    }

    public void setClassName( String name ) {
        reportModel.setClassName( name );
    }

    public void setSuccess( boolean success ) {
        currentScenarioCase.success = success;
    }

    public void setErrorMessage( String message ) {
        currentScenarioCase.errorMessage = message;
    }

    private static String nameWithoutUnderlines( Method paramMethod ) {
        return paramMethod.getName().replace( '_', ' ' );
    }

    public ReportModel getScenarioCollectionModel() {
        return reportModel;
    }

    @Override
    public void stepMethodFailed( Throwable t ) {
        if( !currentScenarioCase.steps.isEmpty() ) {
            currentScenarioCase.steps.get( currentScenarioCase.steps.size() - 1 )
                .setStatus( StepStatus.FAILED );
        }
    }

    @Override
    public void stepMethodFinished( long durationInNanos ) {
        if( !currentScenarioCase.steps.isEmpty() ) {
            currentScenarioCase.steps.get( currentScenarioCase.steps.size() - 1 ).setDurationInNanos( durationInNanos );
        }
    }

    @Override
    public void scenarioFailed( Throwable e ) {
        setSuccess( false );
        setErrorMessage( e.getMessage() );
    }

    @Override
    public void scenarioStarted( Method method, List<NamedArgument> namedArguments ) {
        readConfiguration( method.getDeclaringClass() );
        readAnnotations( method );
        setParameterNames( getNames( namedArguments ) );

        // must come at last
        setMethodName( method.getName() );
        setArguments( toStringList( getValues( namedArguments ) ) );
    }

    private List<Object> getValues( List<NamedArgument> namedArguments ) {
        List<Object> result = Lists.newArrayList();
        for( NamedArgument a : namedArguments ) {
            result.add( a.value );
        }
        return result;
    }

    private List<String> getNames( List<NamedArgument> namedArguments ) {
        List<String> result = Lists.newArrayList();
        for( NamedArgument a : namedArguments ) {
            result.add( a.name );
        }
        return result;
    }

    private void readConfiguration( Class<?> testClass ) {
        configuration = ConfigurationUtil.getConfiguration( testClass );
    }

    private List<String> toStringList( Collection<?> arguments ) {
        List<String> result = Lists.newArrayList();
        for( Object o : arguments ) {
            result.add( new DefaultFormatter<Object>().format( o ) );
        }
        return result;
    }

    private void readAnnotations( Method method ) {
        String scenarioDescription = method.getName();

        if( method.isAnnotationPresent( ScenarioDescription.class ) ) {
            scenarioDescription = method.getAnnotation( ScenarioDescription.class ).value();
        } else if( method.isAnnotationPresent( Description.class ) ) {
            scenarioDescription = method.getAnnotation( Description.class ).value();
        }

        scenarioStarted( scenarioDescription );

        if( method.isAnnotationPresent( CasesAsTable.class ) ) {
            currentScenarioModel.setCasesAsTable( true );
        }

        if( method.isAnnotationPresent( NotImplementedYet.class ) ) {
            currentScenarioModel.notImplementedYet = true;
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

    public List<Tag> toTags( Annotation annotation ) {
        Class<? extends Annotation> annotationType = annotation.annotationType();
        IsTag isTag = annotationType.getAnnotation( IsTag.class );
        TagConfiguration tagConfig;
        if( isTag != null ) {
            tagConfig = TagConfiguration.fromIsTag( isTag );
        } else {
            tagConfig = configuration.getTagConfiguration( annotationType );
        }

        if( tagConfig == null ) {
            return Collections.emptyList();
        }

        String type = annotationType.getSimpleName();

        if( !Strings.isNullOrEmpty( tagConfig.getType() ) ) {
            type = tagConfig.getType();
        }

        Tag tag = new Tag( type );

        if( tagConfig.isPrependType() ) {
            tag.setPrependType( true );
        }

        if( tagConfig.getDescription() != null ) {
            tag.setDescription( tagConfig.getDescription() );
        }

        if( !Strings.isNullOrEmpty( tagConfig.getDefaultValue() ) ) {
            tag.setValue( tagConfig.getDefaultValue() );
        }

        if( tagConfig.isIgnoreValue() ) {
            return Arrays.asList( tag );
        }

        try {
            Method method = annotationType.getMethod( "value" );
            Object value = method.invoke( annotation );
            if( value != null ) {
                if( value.getClass().isArray() ) {
                    Object[] array = (Object[]) value;
                    List<String> values = Lists.newArrayList();
                    for( Object v : array ) {
                        values.add( String.valueOf( v ) );
                    }
                    if( tagConfig.isExplodeArray() ) {
                        return getExplodedTags( tag, values );
                    }
                    tag.setValue( values );
                } else {
                    tag.setValue( String.valueOf( value ) );
                }
            }
        } catch( NoSuchMethodException ignore ) {

        } catch( Exception e ) {
            log.error( "Error while getting 'value' method of annotation " + annotation, e );
        }

        return Arrays.asList( tag );
    }

    private static List<Tag> getExplodedTags( Tag originalTag, List<String> values ) {
        List<Tag> result = Lists.newArrayList();
        for( String singleValue : values ) {
            Tag newTag = new Tag( originalTag.getName(), singleValue );
            newTag.setDescription( originalTag.getDescription() );
            newTag.setPrependType( originalTag.isPrependType() );
            result.add( newTag );
        }
        return result;
    }

    @Override
    public void scenarioFinished() {
        AssertionUtil.assertTrue( scenarioStartedNanos > 0, "Scenario has no start time" );
        long durationInNanos = System.nanoTime() - scenarioStartedNanos;
        currentScenarioCase.setDurationInNanos( durationInNanos );
        currentScenarioModel.addDurationInNanos( durationInNanos );
    }

    public void setTestClass( Class<?> testClass ) {
        setClassName( testClass.getName() );
        if( testClass.isAnnotationPresent( Description.class ) ) {
            reportModel.setDescription( testClass.getAnnotation( Description.class ).value() );
        }
    }

}
