/*
 * Copyright 2014 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package ru.anr.base;

import org.springframework.aop.framework.Advised;
import org.springframework.aop.support.AopUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.core.env.Environment;

import java.util.Set;

/**
 * The base class for Spring beans. It contains some internal spring hooks for
 * manipulation with beans, the context and basic Spring infrastructure like profiles.
 *
 * @author Alexey Romanchuk
 * @created Oct 29, 2014
 */
public class BaseSpringParent extends BaseParent {

    /**
     * The environment injection
     */
    @Autowired
    private Environment env;

    /**
     * The context injection
     */
    @Autowired
    protected ApplicationContext ctx;

    /**
     * The default name for production profile
     */
    public static final String PRODUCTION_PROFILE = "production";

    /**
     * Checks for the 'production' mode
     *
     * @return true, if 'production' profile found among the current Spring profiles.
     */
    protected boolean isProdMode() {
        Set<String> profiles = getProfiles();
        return profiles.contains(PRODUCTION_PROFILE);
    }

    /**
     * Returns all profile names.
     *
     * @return The resulted set of current profile names
     */
    protected Set<String> getProfiles() {
        return set(env.getActiveProfiles());
    }

    /**
     * Returns a refernece to a bean from the context.
     *
     * @param name  the name of bean
     * @param clazz The bean's class
     * @param <S>   The type of object
     * @return the found bean instance
     */
    protected <S> S bean(String name, Class<S> clazz) {
        return ctx.getBean(name, clazz);
    }

    /**
     * Extracts the bean's target if it is an AOP proxy.
     *
     * @param bean the original (maybe proxied) bean
     * @param <S>  the expected bean object type
     * @return The resulted target bean instance
     */
    @SuppressWarnings("unchecked")
    protected static <S> S target(Object bean) {
        try {
            return (S) (AopUtils.isAopProxy(bean) ? ((Advised) bean).getTargetSource().getTarget() : bean);
        } catch (Exception ex) {
            throw new ApplicationException(ex);
        }
    }

    /**
     * Getting bean from context (a short-cut)
     *
     * @param name Name of bean
     * @param <S>  Type of bean
     * @return Bean instance
     */
    @SuppressWarnings("unchecked")
    protected <S> S bean(String name) {

        return (S) ctx.getBean(name);
    }

    /**
     * Returns a bean instance of the given class
     *
     * @param clazz The class of a bean
     * @param <S>   A type
     * @return A bean instance
     */
    protected <S> S bean(Class<S> clazz) {

        return ctx.getBean(clazz);
    }

    /**
     * Returns true if specified bean exists
     *
     * @param name Name of the bean
     * @return true if bean exists
     */
    protected boolean hasBean(String name) {

        return ctx.containsBean(name);
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

    /**
     * If we need to override the context source
     *
     * @param context The spring context
     */
    public void setCtx(ApplicationContext context) {
        this.ctx = context;
        this.env = context.getEnvironment();
    }
}
