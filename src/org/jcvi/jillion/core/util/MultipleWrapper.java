/*******************************************************************************
 * Jillion development code
 * 
 * This code may be freely distributed and modified under the
 * terms of the GNU Lesser General Public License.  This should
 * be distributed with the code.  If you do not have a copy,
 *  see:
 * 
 *          http://www.gnu.org/copyleft/lesser.html
 * 
 * 
 * Copyright for this code is held jointly by the individual authors.  These should be listed in the @author doc comments.
 * 
 * Information about Jillion can be found on its homepage
 * 
 *         http://jillion.sourceforge.net
 * 
 * Contributors:
 *     Danny Katzel - initial API and implementation
 ******************************************************************************/
/*
 * Created on Oct 28, 2009
 *
 * @author dkatzel
 */
package org.jcvi.jillion.core.util;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
/**
 * {@code MultipleWrapper} uses dymanic proxies to wrap
 * several instances of an interface.  This allows
 * all wrapped instances to be called by only a single
 * call to the wrapper.
 * @author dkatzel
 * 
 * @param <T> the interface type to wrap to make several instances appear as a single one.
 */
public final class  MultipleWrapper<T> implements InvocationHandler{
    
	private final ReturnPolicy policy;
    private final List<T> delegates = new ArrayList<T>();
	/**
     * Since methods can only return a single
     * return value, only one of the wrapped
     * methods can be returned to the caller (even though
     * they will all be called).
     * @author dkatzel
     */
    public static enum ReturnPolicy{
        /**
         * Return the first wrapped instance.
         */
        RETURN_FIRST,
        /**
         * Return the last wrapped instance.
         */
        RETURN_LAST
    }
    /**
     * Create a dynamic proxy to wrap the given delegate instances.

     * @param classType the class object of T.
     * @param policy the return policy to use on methods that return something.
     * @param delegates the list of delegates to wrap in the order in which 
     * they will be called.
     * @return a new instance of T that wraps the delegates.
     * @throws IllegalArgumentException if no delegates are given
     * @throws NullPointerException if classType ==null or policy ==null or any delegate ==null.
     * 
     * @param <T> the interface type to wrap to make several instances appear as a single one.
     * 
     */
    @SuppressWarnings("unchecked")
    public static <T> T createMultipleWrapper(Class<T> classType,ReturnPolicy policy, Iterable<? extends T> delegates){
        
        return (T) Proxy.newProxyInstance(classType.getClassLoader(), new Class<?>[]{classType}, 
                new MultipleWrapper<T>(policy,delegates));
    }
    /**
     * Convenience constructor which is the same as calling
     * {@link #createMultipleWrapper(Class, ReturnPolicy, Object...)
     * createMultipleWrapper(classType,ReturnPolicy.RETURN_FIRST,delegates)}
     * 
     * @param classType the {@link Class} of T
     * 
     * @param delegates the instances to wrap; can not be null.
     * 
     * @see #createMultipleWrapper(Class, ReturnPolicy, Object...)
     */
    public static <T> T createMultipleWrapper(Class<T> classType,Iterable<? extends T> delegates){
       return createMultipleWrapper(classType,ReturnPolicy.RETURN_FIRST,delegates);
    }
    /**
     * Convenience constructor which is the same as calling
     * {@link #createMultipleWrapper(Class, ReturnPolicy, Object...)
     * createMultipleWrapper(classType,ReturnPolicy.RETURN_FIRST,delegates)}
     * @see #createMultipleWrapper(Class, ReturnPolicy, Object...)
     * 
     * @param classType the {@link Class} of T
     * 
     * @param delegates the instances to wrap; can not be null.
     */
    @SafeVarargs
    public static <T> T createMultipleWrapper(Class<T> classType,T... delegates){
       return createMultipleWrapper(classType,ReturnPolicy.RETURN_FIRST,Arrays.asList(delegates));
    }
    /**
     * Convenience constructor which is the same as calling
     * {@link #createMultipleWrapper(Class, ReturnPolicy, Object...)
     * createMultipleWrapper(classType,ReturnPolicy.RETURN_FIRST,delegates)}
     * @see #createMultipleWrapper(Class, ReturnPolicy, Object...)
     * 
     * 
     * @param classType the {@link Class} of T
     * @param policy the {@link ReturnPolicy} to use for methods that return a value.
     * 
     * @param delegates the instances to wrap; can not be null.
     */
    @SafeVarargs
    public static <T> T createMultipleWrapper(Class<T> classType,ReturnPolicy policy,T... delegates){
       return createMultipleWrapper(classType,policy,Arrays.asList(delegates));
    }
    
    
    private MultipleWrapper(ReturnPolicy policy,Iterable<? extends T> delegates){
        if(policy==null){
            throw new NullPointerException("policy can not be null");
        }
        
        this.policy = policy;
        for(T delegate : delegates){
            if(delegate ==null){
                throw new NullPointerException("delegate can not be null");
            }
            this.delegates.add(delegate);
        }    
        if(this.delegates.size()==0){
            throw new IllegalArgumentException("must wrap at least one delegate");
        }
    }
    @Override
    public Object invoke(Object proxy, Method method, Object[] args)
            throws Throwable {
        List<Object> returns = new ArrayList<Object>(delegates.size());
        try{
	        for(T delegate :delegates){
	            returns.add(method.invoke(delegate, args));
	        }
	        if(policy == ReturnPolicy.RETURN_LAST){
	            return returns.get(returns.size()-1);
	        }
	        return returns.get(0);
        }catch(InvocationTargetException e){
        	throw e.getCause();
        }
      
    }
    
}
