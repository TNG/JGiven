package com.tngtech.jgiven.impl.util;

import java.lang.annotation.Annotation;
import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.apache.log4j.Logger;

import com.google.common.base.Optional;
import com.google.common.base.Throwables;
import com.tngtech.jgiven.exception.JGivenExecutionException;
import com.tngtech.jgiven.exception.JGivenInjectionException;
import com.tngtech.jgiven.exception.JGivenUserException;

public class ReflectionUtil {
    private static Logger log = Logger.getLogger( ReflectionUtil.class );

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
                    if( field.isAnnotationPresent( clazz ) )
                        return true;
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
        if( clazz == null )
            return Optional.absent();

        try {
            return Optional.of( clazz.getDeclaredMethod( methodName ) );
        } catch( NoSuchMethodException e ) {
            return findMethodTransitively( clazz.getSuperclass(), methodName );
        }

    }

    public static Method findMethod( Class<?> testClass, String methodName ) {
        Method method = null;
        for( Method m : testClass.getMethods() ) {
            if( m.getName().equals( methodName ) ) {
                method = m;
                break;
            }
        }

        if( method == null )
            throw new RuntimeException( "Could not find method with name " + methodName );
        return method;
    }

    public static <T> T newInstance( Class<T> value ) {
        try {
            return value.newInstance();
        } catch( InstantiationException e ) {
            throw new RuntimeException( e );
        } catch( IllegalAccessException e ) {
            throw new RuntimeException( e );
        }
    }

    public static void invokeMethod( Object object, Method method, String errorDescription ) {
        log.debug( "Executing method " + method + " of class " + object.getClass() );

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

    public static void setField( Field field, Object object, Object value, String errorDescription ) {
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
            log.warn( "Could not make " + toReadableString( object ) + errorDescription
                    + " accessible, trying to executed it nevertheless and hoping for the best." );
        }
    }

    public static String toReadableString( AccessibleObject object ) {
        if( object instanceof Method ) {
            Method method = (Method) object;
            return "method '" + method.getName() + "' of class '" + method.getDeclaringClass().getSimpleName() + "'";
        } else if( object instanceof Field ) {
            Field field = (Field) object;
            return "field '" + field.getName() + "' of class '" + field.getDeclaringClass().getSimpleName() + "'";
        }
        return null;
    }

}
