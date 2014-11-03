/**
 * 
 */
package ru.anr.base;

import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.core.env.Environment;

/**
 * Implementation of Base Service.
 * 
 * 
 * @author Alexey Romanchuk
 * @created Oct 29, 2014
 * 
 */
public class BaseSpringParent extends BaseParent {

    /**
     * Environment injection
     */
    @Autowired
    private Environment env;

    /**
     * Context injection
     */
    @Autowired
    private ApplicationContext ctx;

    /**
     * Checking all profile names
     * 
     * @return A set of current profile names
     */
    protected Set<String> getProfiles() {

        return set(env.getActiveProfiles());
    }

    /**
     * Getting bean from context (a short-cut)
     * 
     * @param name
     *            Name of bean
     * @param clazz
     *            Bean class
     * @return Bean instance
     * 
     * @param <S>
     *            Type of bean
     */
    protected <S> S bean(String name, Class<S> clazz) {

        return ctx.getBean(name, clazz);
    }

    // /////////////////////////////////////////////////////////////////////////
    // /// getters/setters
    // /////////////////////////////////////////////////////////////////////////

    /**
     * @return the env
     */
    public Environment getEnv() {

        return env;
    }
}
