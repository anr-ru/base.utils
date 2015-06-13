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

import java.util.Set;

import org.springframework.aop.framework.Advised;
import org.springframework.aop.support.AopUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.core.env.Environment;

/**
 * Base class for Spring-beans. Contains some internal spring hooks for
 * manipulation with beans and context.
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
    protected ApplicationContext ctx;

    /**
     * Default name for production profile
     */
    public static final String PRODUCTION_PROFILE = "production";

    /**
     * Checking for 'Production' mode
     * 
     * @return true, if 'production' profile found
     */
    protected boolean isProdMode() {

        Set<String> profiles = getProfiles();
        return profiles.contains(PRODUCTION_PROFILE);
    }

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

    /**
     * Exctracts a bean target if it's a aop proxy
     * 
     * @param bean
     *            Original (may be proxied) bean
     * @return A target bean instance
     * 
     * @param <S>
     *            Expected bean class
     */
    @SuppressWarnings("unchecked")
    protected <S> S target(Object bean) {

        try {
            return (S) (AopUtils.isAopProxy(bean) ? ((Advised) bean).getTargetSource().getTarget() : bean);
        } catch (Exception ex) {
            throw new ApplicationException(ex);
        }
    }

    /**
     * Getting bean from context (a short-cut)
     * 
     * @param name
     *            Name of bean
     * @return Bean instance
     * 
     * @param <S>
     *            Type of bean
     */
    @SuppressWarnings("unchecked")
    protected <S> S bean(String name) {

        return (S) ctx.getBean(name);
    }

    /**
     * Returns true if specified bean exists
     * 
     * @param name
     *            Name of the bean
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
}
