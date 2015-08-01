package com.tngtech.jgiven.report.model;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.CaseFormat;
import com.google.common.base.Strings;
import com.google.common.base.Throwables;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.tngtech.jgiven.annotation.*;
import com.tngtech.jgiven.attachment.Attachment;
import com.tngtech.jgiven.config.AbstractJGivenConfiguraton;
import com.tngtech.jgiven.config.ConfigurationUtil;
import com.tngtech.jgiven.config.DefaultConfiguration;
import com.tngtech.jgiven.config.TagConfiguration;
import com.tngtech.jgiven.exception.JGivenWrongUsageException;
import com.tngtech.jgiven.format.DefaultFormatter;
import com.tngtech.jgiven.format.TableFormatter;
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
    private StepModel currentStep;
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
            currentScenarioModel.setClassName( reportModel.getClassName() );
            reportModel.getScenarios().add( currentScenarioModel );
        }

        currentScenarioModel.addCase( currentScenarioCase );
        currentScenarioModel.setDescription( readableDescription );
    }

    private String camelCaseToReadableText( String camelCase ) {
        String scenarioDescription = CaseFormat.LOWER_CAMEL.to( CaseFormat.LOWER_UNDERSCORE, camelCase ).replace( '_', ' ' );
        return WordUtil.capitalize( scenarioDescription );
    }

    public void addStepMethod( Method paramMethod, List<NamedArgument> arguments, InvocationMode mode ) {
        StepModel stepModel = createStepModel( paramMethod, arguments, mode );
        currentStep = stepModel;
        writeStep( stepModel );
    }

    StepModel createStepModel( Method paramMethod, List<NamedArgument> arguments, InvocationMode mode ) {
        StepModel stepModel = new StepModel();

        stepModel.name = getDescription( paramMethod );

        ExtendedDescription extendedDescriptionAnnotation = paramMethod.getAnnotation( ExtendedDescription.class );
        if( extendedDescriptionAnnotation != null ) {
            stepModel.setExtendedDescription( extendedDescriptionAnnotation.value() );
        }

        List<NamedArgument> nonHiddenArguments = filterHiddenArguments( arguments, paramMethod.getParameterAnnotations() );

        List<Formatting<?>> formatters = getFormatters( paramMethod.getParameterAnnotations() );
        stepModel.words = new StepFormatter( stepModel.name, nonHiddenArguments, formatters ).buildFormattedWords();

        if( introWord != null ) {
            stepModel.words.add( 0, introWord );
            introWord = null;
        }

        stepModel.setStatus( mode.toStepStatus() );
        return stepModel;
    }

    private String getDescription( Method paramMethod ) {
        if( paramMethod.isAnnotationPresent( Hidden.class ) ) {
            return "";
        }

        Description description = paramMethod.getAnnotation( Description.class );
        if( description != null ) {
            return description.value();
        }
        As as = paramMethod.getAnnotation( As.class );
        if( as != null ) {
            return as.value();
        }

        return nameWithoutUnderlines( paramMethod );
    }

    private List<NamedArgument> filterHiddenArguments( List<NamedArgument> arguments, Annotation[][] parameterAnnotations ) {
        List<NamedArgument> result = Lists.newArrayList();
        for( int i = 0; i < parameterAnnotations.length; i++ ) {
            if( !isHidden( parameterAnnotations[i] ) ) {
                result.add( arguments.get( i ) );
            }
        }
        return result;
    }

    @Override
    public void introWordAdded( String value ) {
        introWord = new Word();
        introWord.setIntroWord( true );
        introWord.setValue( value );
    }

    private List<Formatting<?>> getFormatters( Annotation[][] parameterAnnotations ) {
        List<Formatting<?>> res = Lists.newArrayList();
        for( Annotation[] annotations : parameterAnnotations ) {
            if( !isHidden( annotations ) ) {
                res.add( getFormatting( annotations ) );
            }
        }
        return res;
    }

    private boolean isHidden( Annotation[] annotations ) {
        for( Annotation annotation : annotations ) {
            if( annotation instanceof Hidden ) {
                return true;
            }
        }
        return false;
    }

    @SuppressWarnings( { "rawtypes", "unchecked" } )
    private Formatting<?> getFormatting( Annotation[] annotations ) {
        return getFormatting( annotations, Sets.<Class<?>>newHashSet(), null );
    }

    /**
     * Recursively searches for formatting annotations.
     * @param visitedTypes used to prevent an endless loop
     */
    private Formatting<?> getFormatting( Annotation[] annotations, Set<Class<?>> visitedTypes, Annotation originalAnnotation ) {
        for( Annotation annotation : annotations ) {
            try {
                if( annotation instanceof Format ) {
                    Format arg = (Format) annotation;
                    return new Formatting( arg.value().newInstance(), arg.args() );
                } else if( annotation instanceof Table ) {
                    Table tableAnnotation = (Table) annotation;
                    return new Formatting( new TableFormatter( tableAnnotation ) );
                } else if( annotation instanceof AnnotationFormat ) {
                    AnnotationFormat arg = (AnnotationFormat) annotation;
                    return new Formatting( new StepFormatter.AnnotationBasedFormatter( arg.value().newInstance(), originalAnnotation ) );
                } else {
                    Class<? extends Annotation> annotationType = annotation.annotationType();
                    if( !visitedTypes.contains( annotationType ) ) {
                        visitedTypes.add( annotationType );
                        Formatting<?> formatting = getFormatting( annotationType.getAnnotations(), visitedTypes, annotation );
                        if( formatting != null ) {
                            return formatting;
                        }
                    }
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
            scenarioStarted( "A Scenario" );
        }
        return currentScenarioCase;
    }

    @Override
    public void stepMethodInvoked( Method method, List<NamedArgument> arguments, InvocationMode mode ) {
        if( method.isAnnotationPresent( IntroWord.class ) ) {
            introWordAdded( getDescription( method ) );
        } else {
            addStepMethod( method, arguments, mode );
        }
    }

    public void setMethodName( String methodName ) {
        currentScenarioModel.setTestMethodName( methodName );
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

    public ReportModel getReportModel() {
        return reportModel;
    }

    @Override
    public void stepMethodFailed( Throwable t ) {
        if( !currentScenarioCase.getSteps().isEmpty() ) {
            currentScenarioCase.getStep( currentScenarioCase.getSteps().size() - 1 )
                .setStatus( StepStatus.FAILED );
        }
    }

    @Override
    public void stepMethodFinished( long durationInNanos ) {
        if( !currentScenarioCase.getSteps().isEmpty() ) {
            currentScenarioCase.getSteps().get( currentScenarioCase.getSteps().size() - 1 ).setDurationInNanos( durationInNanos );
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

        if( method.isAnnotationPresent( Description.class ) ) {
            scenarioDescription = method.getAnnotation( Description.class ).value();
        } else if( method.isAnnotationPresent( As.class ) ) {
            scenarioDescription = method.getAnnotation( As.class ).value();
        }

        scenarioStarted( scenarioDescription );

        if( method.isAnnotationPresent( NotImplementedYet.class ) || method.isAnnotationPresent( Pending.class ) ) {
            currentScenarioModel.setPending(true);
        }

        if( currentScenarioCase.getCaseNr() == 1 ) {
            addTags( method.getDeclaringClass().getAnnotations() );
            addTags( method.getAnnotations() );
        }
    }

    public void addTags( Annotation... annotations ) {
        for( Annotation annotation : annotations ) {
            List<Tag> tags = toTags( annotation );
            this.reportModel.addTags( tags );
            this.currentScenarioModel.addTags( tags );
        }
    }

    public List<Tag> toTags( Annotation annotation ) {
        Class<? extends Annotation> annotationType = annotation.annotationType();
        IsTag isTag = annotationType.getAnnotation( IsTag.class );
        TagConfiguration tagConfig;
        if( isTag != null ) {
            tagConfig = fromIsTag( isTag, annotation );
        } else {
            tagConfig = configuration.getTagConfiguration( annotationType );
        }

        if( tagConfig == null ) {
            return Collections.emptyList();
        }

        Tag tag = new Tag( tagConfig.getAnnotationType() );

        if( !Strings.isNullOrEmpty( tagConfig.getName() ) ) {
            tag.setName( tagConfig.getName() );
        }

        if( tagConfig.isPrependType() ) {
            tag.setPrependType( true );
        }

        if( !Strings.isNullOrEmpty( tagConfig.getCssClass() ) ) {
            tag.setCssClass( tagConfig.getCssClass() );
        }

        if( !Strings.isNullOrEmpty( tagConfig.getColor() ) ) {
            tag.setColor( tagConfig.getColor() );
        }

        Object value = tagConfig.getDefaultValue();
        if( !Strings.isNullOrEmpty( tagConfig.getDefaultValue() ) ) {
            tag.setValue( tagConfig.getDefaultValue() );
        }

        if( tagConfig.isIgnoreValue() ) {
            tag.setDescription( getDescriptionFromGenerator( tagConfig, annotation, value ) );
            return Arrays.asList( tag );
        }

        tag.setTags( tagConfig.getTags() );

        try {
            Method method = annotationType.getMethod( "value" );
            value = method.invoke( annotation );
            if( value != null ) {
                if( value.getClass().isArray() ) {
                    Object[] objectArray = (Object[]) value;
                    if( tagConfig.isExplodeArray() ) {
                        List<Tag> explodedTags = getExplodedTags( tag, objectArray, annotation, tagConfig );
                        return explodedTags;
                    }
                    tag.setValue( toStringList( objectArray ) );

                } else {
                    tag.setValue( String.valueOf( value ) );
                }
            }
        } catch( NoSuchMethodException ignore ) {

        } catch( Exception e ) {
            log.error( "Error while getting 'value' method of annotation " + annotation, e );
        }

        tag.setDescription( getDescriptionFromGenerator( tagConfig, annotation, value ) );
        return Arrays.asList( tag );
    }

    public TagConfiguration fromIsTag( IsTag isTag, Annotation annotation ) {

        String name = Strings.isNullOrEmpty( isTag.name() ) ? isTag.type() : isTag.name();

        return TagConfiguration.builder( annotation.annotationType() )
            .defaultValue( isTag.value() )
            .description( isTag.description() )
            .explodeArray( isTag.explodeArray() )
            .ignoreValue( isTag.ignoreValue() )
            .prependType( isTag.prependType() )
            .name( name )
            .descriptionGenerator( isTag.descriptionGenerator() )
            .cssClass( isTag.cssClass() )
            .color( isTag.color() )
            .tags( getTagNames( isTag, annotation ) )
            .build();

    }

    private List<String> getTagNames( IsTag isTag, Annotation annotation ) {
        List<Tag> tags = getTags( isTag, annotation );
        reportModel.addTags( tags );
        List<String> tagNames = Lists.newArrayList();
        for( Tag tag : tags ) {
            tagNames.add( tag.toIdString() );
        }
        return tagNames;
    }

    private List<Tag> getTags( IsTag isTag, Annotation annotation ) {
        List<Tag> allTags = Lists.newArrayList();

        for( Annotation a : annotation.annotationType().getAnnotations() ) {
            if( a.annotationType().isAnnotationPresent( IsTag.class ) ) {
                List<Tag> tags = toTags( a );
                for( Tag tag : tags ) {
                    allTags.add( tag );
                }
            }
        }

        return allTags;
    }

    private List<String> toStringList( Object[] value ) {
        Object[] array = value;
        List<String> values = Lists.newArrayList();
        for( Object v : array ) {
            values.add( String.valueOf( v ) );
        }
        return values;
    }

    private String getDescriptionFromGenerator( TagConfiguration tagConfiguration, Annotation annotation, Object value ) {
        try {
            return tagConfiguration.getDescriptionGenerator().newInstance().generateDescription( tagConfiguration, annotation, value );
        } catch( Exception e ) {
            throw new JGivenWrongUsageException( "Error while trying to generate the description for annotation " + annotation
                    + " using DescriptionGenerator class " + tagConfiguration.getDescriptionGenerator() + ": " + e.getMessage(), e );
        }
    }

    private List<Tag> getExplodedTags( Tag originalTag, Object[] values, Annotation annotation, TagConfiguration tagConfig ) {
        List<Tag> result = Lists.newArrayList();
        for( Object singleValue : values ) {
            Tag newTag = originalTag.copy();
            newTag.setValue( String.valueOf( singleValue ) );
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

    @Override
    public void attachmentAdded( Attachment attachment ) {
        currentStep.setAttachment( attachment );
    }

    @Override
    public void extendedDescriptionUpdated( String extendedDescription ) {
        currentStep.setExtendedDescription( extendedDescription );
    }

    public void setTestClass( Class<?> testClass ) {
        setClassName( testClass.getName() );
        if( testClass.isAnnotationPresent( Description.class ) ) {
            reportModel.setDescription( testClass.getAnnotation( Description.class ).value() );
        }
    }

}
