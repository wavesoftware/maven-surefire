package org.apache.maven.surefire.common;

/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

import org.apache.maven.surefire.extensions.PluginExecutionContext;

import java.util.Iterator;
import java.util.ServiceLoader;

import static org.apache.maven.surefire.common.ContextStatus.*;

public final class ContextHolder<T extends PluginExecutionContext>
{
    private final Object lock;

    private final Class<T> contextType;

    private volatile T context;

    private volatile ContextStatus status;

    public ContextHolder( Class<T> contextType )
    {
        status = NOT_LOADED;
        this.contextType = contextType;
        lock = new Object();
    }

    public boolean isInitialized()
    {
        getContext();
        return status == LOADED;
    }

    public boolean isMultipleSpiContext()
    {
        return status == FAILED_WITH_MORE_INSTANCES;
    }

    /**
     * If the context is null, the default plugin context is taken.
     */
    public T getContext()
    {
        if ( status == NOT_LOADED )
        {
            synchronized ( lock )
            {
                if ( status == NOT_LOADED )
                {
                    ServiceLoader<T> spi = ServiceLoader.load( contextType );
                    Iterator<T> it = spi.iterator();

                    T currentContext = null;
                    if ( it.hasNext() )
                    {
                        currentContext = it.next();
                    }

                    if ( currentContext == null )
                    {
                        status = FAILED;
                    }
                    else if ( it.hasNext() )
                    {
                        status = FAILED_WITH_MORE_INSTANCES;
                    }
                    else
                    {
                        context = currentContext;
                        status = LOADED;
                    }
                }
            }
        }

        return context;
    }

}
