package com.tngtech.jgiven.impl.util;

import static java.lang.String.format;

import java.lang.annotation.Annotation;
import java.lang.reflect.*;
import java.util.Arrays;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Function;
import com.google.common.base.Optional;
import com.google.common.base.Predicate;
import com.google.common.base.Throwables;
import com.google.common.collect.FluentIterable;
import com.google.common.collect.Lists;
import com.tngtech.jgiven.exception.JGivenExecutionException;
import com.tngtech.jgiven.exception.JGivenInjectionException;
import com.tngtech.jgiven.exception.JGivenUserException;

public class ReflectionUtil {
    private static Logger log = LoggerFactory.getLogger( ReflectionUtil.class );

    /**
     * Iterates over all fields of the given class and all its super classes
     * and calls action.act() for the fields that are annotated with the given annotation.
     */
    public static void forEachField( final Object object, Class<?> clazz, final FieldPredicate predicate, final FieldAction action ) {
        forEachSuperClass( clazz, new ClassAction() {
            @Override
            public void act( Class<?> clazz ) throws Exception {
                for( Field field : clazz.getDeclaredFields() ) {
                    if( predicate.isTrue( field ) ) {
                        action.act( object, field );
                    }
                }
            }
        } );
    }

    /**
     * Iterates over all methods of the given class and all its super classes
     * and calls action.act() for the methods that are annotated with the given annotation.
     */
    public static void forEachMethod( final Object object, Class<?> clazz, final Class<? extends Annotation> annotation,
            final MethodAction action ) {
        forEachSuperClass( clazz, new ClassAction() {
            @Override
            public void act( Class<?> clazz ) throws Exception {
                for( Method method : clazz.getDeclaredMethods() ) {
                    if( method.isAnnotationPresent( annotation ) ) {
                        action.act( object, method );
                    }
                }
            }
        } );
    }

    /**
     * Iterates over all super classes of the given class (including the class itself)
     * and calls action.act() for these classes.
     */
    public static void forEachSuperClass( Class<?> clazz, ClassAction action ) {
        try {
            action.act( clazz );
            Class<?> superclass = clazz.getSuperclass();
            if( superclass != null ) {
                forEachSuperClass( superclass, action );
            }
        } catch( Exception e ) {
            throw Throwables.propagate( e );
        }

    }

    public static FieldPredicate hasAtLeastOneAnnotation( final Class<? extends Annotation>... annotation ) {
        return new FieldPredicate() {
            @Override
            public boolean isTrue( Field field ) throws Exception {
                for( Class<? extends Annotation> clazz : annotation ) {
                    if( field.isAnnotationPresent( clazz ) ) {
                        return true;
                    }
                }

                return false;
            }
        };
    }

    public static FieldPredicate allFields() {
        return new FieldPredicate() {
            @Override
            public boolean isTrue( Field field ) throws Exception {
                return true;
            }
        };
    }

    public static FieldPredicate nonStaticField() {
        return new FieldPredicate() {
            @Override
            public boolean isTrue( Field field ) throws Exception {
                return !Modifier.isStatic( field.getModifiers() );
            }
        };
    }

    public static boolean hasConstructor( Class<?> type, Class<?>... parameterTypes ) {
        try {
            type.getDeclaredConstructor( parameterTypes );
            return true;
        } catch( NoSuchMethodException e ) {
            return false;
        }
    }

    public interface FieldPredicate {
        boolean isTrue( Field field ) throws Exception;
    }

    public interface ClassAction {
        void act( Class<?> clazz ) throws Exception;
    }

    public interface FieldAction {
        void act( Object object, Field field ) throws Exception;
    }

    public interface MethodAction {
        void act( Object object, Method method ) throws Exception;
    }

    public static Optional<Method> findMethodTransitively( Class<?> clazz, String methodName ) {
        if( clazz == null ) {
            return Optional.absent();
        }

        try {
            return Optional.of( clazz.getDeclaredMethod( methodName ) );
        } catch( NoSuchMethodException e ) {
            return findMethodTransitively( clazz.getSuperclass(), methodName );
        }

    }

    public static <T> T newInstance( Class<T> type ) {
        return newInstance( type, new Class<?>[0] );
    }

    public static <T> T newInstance( Class<T> type, Class<?>[] parameterTypes, Object... parameterValues ) {
        try {
            Constructor<T> constructor = type.getDeclaredConstructor( parameterTypes );
            constructor.setAccessible( true );
            return constructor.newInstance( parameterValues );
        } catch( InstantiationException e ) {
            throw new RuntimeException( e );
        } catch( IllegalAccessException e ) {
            throw new RuntimeException( e );
        } catch( NoSuchMethodException e ) {
            throw new RuntimeException( e );
        } catch( InvocationTargetException e ) {
            throw new RuntimeException( e );
        }
    }

    public static void invokeMethod( Object object, Method method, String errorDescription ) {
        log.debug( "Executing method %s of class %s", method, object.getClass() );

        makeAccessible( method, errorDescription );

        try {
            method.invoke( object );
        } catch( IllegalArgumentException e ) {
            log.debug( "Caught exception:", e );
            throw new JGivenExecutionException( "Could not execute " + toReadableString( method ) + errorDescription +
                    ", because it requires parameters. " + "Remove the parameters and try again.", e );
        } catch( IllegalAccessException e ) {
            log.debug( "Caught exception:", e );
            throw new JGivenExecutionException( "Could not execute " + toReadableString( method ) + errorDescription +
                    ", because of insuffient access rights. "
                    + "Either make the method public or disable your security manager while executing JGiven tests.", e );
        } catch( InvocationTargetException e ) {
            throw new JGivenUserException( method, errorDescription, e.getCause() );
        }
    }

    /**
     * Returns a {@link List} of objects reflecting all the non-static field values declared by the class or interface
     * represented by the given {@link Class} object and defined by the given {@link Object}. This includes
     * {@code public}, {@code protected}, default (package) access, and {@code private} fields, but excludes inherited
     * fields. The elements in the {@link List} returned are not sorted and are not in any particular order. This method
     * returns an empty {@link List} if the class or interface declares no fields, or if the given {@link Class} object
     * represents a primitive type, an array class, or void.
     *
     * @param clazz class or interface declaring fields
     * @param target instance of given {@code clazz} from which field values should be retrieved
     * @param errorDescription customizable part of logged error message
     * @return a {@link List} containing all the found field values (never {@code null})
     */
    public static List<Object> getAllNonStaticFieldValuesFrom( final Class<?> clazz, final Object target, final String errorDescription ) {
        return getAllFieldValues( target, getAllNonStaticFields( clazz ), errorDescription );
    }

    private static Function<Field, Object> getFieldValueFunction( final Object target, final String errorDescription ) {
        return new Function<Field, Object>() {
            @Override
            public Object apply( Field field ) {
                makeAccessible( field, "" );
                try {
                    return field.get( target );
                } catch( IllegalAccessException e ) {
                    log.warn(
                        format( "Not able to access field '%s'." + errorDescription, toReadableString( field ) ), e );
                    return null;
                }
            }
        };
    }

    public static List<Field> getAllNonStaticFields( Class<?> clazz ) {
        final List<Field> result = Lists.newArrayList();

        forEachField( null, clazz, nonStaticField(), new FieldAction() {
            @Override
            public void act( Object target, Field field ) throws Exception {
                result.add( field );
            }
        } );

        return result;
    }

    public static List<Object> getAllFieldValues( Object target, Iterable<Field> fields, String errorDescription ) {
        return Lists.newArrayList( FluentIterable.from( fields ).transform(
            getFieldValueFunction( target, errorDescription ) ) );
    }

    public static List<String> getAllFieldNames( Iterable<Field> fields ) {
        return FluentIterable.from( fields ).transform( new Function<Field, String>() {
            @Override
            public String apply( Field input ) {
                return input.getName();
            }
        } ).toList();
    }

    public static void setField( Field field, Object object, Object value, String errorDescription ) {
        makeAccessible( field, errorDescription );
        try {
            field.set( object, value );
        } catch( IllegalArgumentException e ) {
            log.debug( "Caught exception:", e );
            throw new JGivenInjectionException( "Could not set " + toReadableString( field ) + errorDescription +
                    " to value " + value + ": " + e.getMessage(), e );
        } catch( IllegalAccessException e ) {
            log.debug( "Caught exception:", e );
            throw new JGivenInjectionException( "Could not set " + toReadableString( field ) + errorDescription +
                    ", because of insuffient access rights. "
                    + "Either make the field public or disable your security manager while executing JGiven tests.", e );
        }
    }

    public static void makeAccessible( AccessibleObject object, String errorDescription ) {
        try {
            object.setAccessible( true );
        } catch( SecurityException e ) {
            log.debug( "Caught exception: ", e );
            log.warn( "Could not make {} accessible, trying to access it nevertheless and hoping for the best.",
                toReadableString( object ), errorDescription );
        }
    }

    public static String toReadableString( AccessibleObject object ) {
        if( object instanceof Method ) {
            Method method = (Method) object;
            return "method '" + method.getName() + "' of class '" + method.getDeclaringClass().getSimpleName() + "'";
        } else if( object instanceof Field ) {
            Field field = (Field) object;
            return "field '" + field.getName() + "' of class '" + field.getDeclaringClass().getSimpleName() + "'";
        } else if( object instanceof Constructor<?> ) {
            Constructor<?> constructor = (Constructor<?>) object;
            return "constructor '" + constructor.getName() + "' of class '" + constructor.getDeclaringClass().getSimpleName() + "'";
        }
        return null;
    }

    public static List<Method> getNonStaticMethod( Method[] declaredMethods ) {
        return FluentIterable.from( Arrays.asList( declaredMethods ) )
            .filter( new Predicate<Method>() {
                @Override
                public boolean apply( Method input ) {
                    return ( input.getModifiers() & Modifier.STATIC ) == 0;
                }
            } )
            .toList();
    }
}
