package com.tngtech.jgiven.report.model;

import static com.google.common.collect.Lists.newArrayList;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.CaseFormat;
import com.google.common.base.Strings;
import com.google.common.base.Throwables;
import com.google.common.collect.Lists;
import com.tngtech.jgiven.annotation.CasesAsTable;
import com.tngtech.jgiven.annotation.Description;
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
import com.tngtech.jgiven.impl.util.WordUtil;
import com.tngtech.jgiven.report.model.StepFormatter.Formatting;

/**
 * Builds up the report model while the scenario is executed
 */
public class ReportModelBuilder implements ScenarioListener {
    private static final Logger log = LoggerFactory.getLogger( ReportModelBuilder.class );

    private ScenarioModel currentScenarioModel;
    private ScenarioCaseModel currentScenarioCase;
    private ReportModel scenarioCollectionModel;

    private Word introWord;

    private AbstractJGivenConfiguraton configuration = new DefaultConfiguration();

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

    public void addStepMethod( Method paramMethod, List<Object> arguments, InvocationMode mode ) {
        String name;
        Description description = paramMethod.getAnnotation( Description.class );
        if( description != null ) {
            name = description.value();
        } else {
            name = nameWithoutUnderlines( paramMethod );
        }

        List<Formatting<?>> formatters = getFormatters( paramMethod.getParameterAnnotations() );
        List<Word> words = new StepFormatter( name, arguments, formatters ).buildFormattedWords();

        if( introWord != null ) {
            words.add( 0, introWord );
            introWord = null;
        }

        writeStep( name, words, mode );
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

    public void writeStep( String name, List<Word> words, InvocationMode mode ) {
        getCurrentScenarioCase().addStep( name, words, mode );
    }

    private ScenarioCaseModel getCurrentScenarioCase() {
        if( currentScenarioCase == null ) {
            scenarioStarted( "An Undescribed Scenario" );
        }
        return currentScenarioCase;
    }

    @Override
    public void stepMethodInvoked( Method paramMethod, List<Object> arguments, InvocationMode mode ) {
        if( !isStepMethod( paramMethod ) )
            return;
        addStepMethod( paramMethod, arguments, mode );
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
        return scenarioCollectionModel;
    }

    @Override
    public void scenarioFailed( Throwable e ) {
        if( !currentScenarioCase.steps.isEmpty() ) {
            currentScenarioCase.steps.get( currentScenarioCase.steps.size() - 1 )
                .setStatus( StepStatus.FAILED );
        }
        setSuccess( false );
        setErrorMessage( e.getMessage() );
    }

    @Override
    public void scenarioStarted( Method method, LinkedHashMap<String, ?> arguments ) {
        readConfiguration( method.getDeclaringClass() );
        readAnnotations( method );
        setParameterNames( newArrayList( arguments.keySet() ) );

        // must come at last
        setMethodName( method.getName() );
        setArguments( toStringList( arguments.values() ) );
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
                    String[] stringArray = new String[array.length];
                    for( int i = 0; i < array.length; i++ ) {
                        stringArray[i] = array[i] + "";
                    }
                    if( tagConfig.isExplodeArray() ) {
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
