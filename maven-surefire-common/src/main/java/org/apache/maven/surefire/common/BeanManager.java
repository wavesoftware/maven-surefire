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

import org.apache.maven.plugin.AbstractMojoExecutionException;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.surefire.extensions.ExecutionLifeCycleListener;
import org.apache.maven.surefire.extensions.Extension;
import org.apache.maven.surefire.extensions.PluginExecutionContext;
import org.apache.maven.surefire.extensions.api.DependencyScannerFileFilter;
import org.apache.maven.surefire.extensions.api.DirectoryScannerFileFilter;
import org.apache.maven.surefire.testset.RunOrderParameters;
import org.apache.maven.surefire.util.DefaultRunOrderCalculator;
import org.apache.maven.surefire.util.RunOrderCalculator;

import java.util.ArrayList;
import java.util.Collection;
import java.util.ServiceLoader;

public final class BeanManager
    implements ExecutionLifeCycleListener
{
    private volatile DependencyScannerFileFilter dependencyScannerFileFilter;

    private volatile DirectoryScannerFileFilter directoryScannerFileFilter;

    private volatile RunOrderCalculator runOrderCalculator;

    private final Collection<ExecutionLifeCycleListener> listeners = new ArrayList<ExecutionLifeCycleListener>();

    public BeanManager()
        throws MojoExecutionException
    {
        Collection<Extension> extensions = makeCollection( ServiceLoader.load( Extension.class ) );
        for ( Extension extension : extensions )
        {
            if ( extension instanceof DependencyScannerFileFilter )
            {
                if ( dependencyScannerFileFilter != null )
                {
                    failAmbiguousExtension( extension, DependencyScannerFileFilter.class, extensions );
                }
                else
                {
                    dependencyScannerFileFilter = (DependencyScannerFileFilter) extension;
                }
            }
            else if ( extension instanceof DirectoryScannerFileFilter )
            {
                if ( directoryScannerFileFilter != null )
                {
                    failAmbiguousExtension( extension, DirectoryScannerFileFilter.class, extensions );
                }
                else
                {
                    directoryScannerFileFilter = (DirectoryScannerFileFilter) extension;
                }
            }
            else if ( extension instanceof RunOrderCalculator )
            {
                if ( runOrderCalculator != null )
                {
                    failAmbiguousExtension( extension, RunOrderCalculator.class, extensions );
                }
                else
                {
                    runOrderCalculator = (RunOrderCalculator) extension;
                }
            }

            if ( extension instanceof ExecutionLifeCycleListener )
            {
                listeners.add( (ExecutionLifeCycleListener) extension );
            }
        }

        for ( ExecutionLifeCycleListener listener : ServiceLoader.load( ExecutionLifeCycleListener.class ) )
        {
            this.listeners.add( listener );
        }
    }

    public void executionStarted( String id )
    {
        for ( ExecutionLifeCycleListener listener : listeners )
        {
            listener.executionStarted( id );
        }
    }

    public void contextValidated( String id, AbstractMojoExecutionException e )
    {
        for ( ExecutionLifeCycleListener listener : listeners )
        {
            listener.contextValidated( id, e );
        }
    }

    public void contextInitialized( PluginExecutionContext context )
    {
        for ( ExecutionLifeCycleListener listener : listeners )
        {
            listener.contextInitialized( context );
        }
    }

    public void providerExecutionStarted( String providerName, PluginExecutionContext context )
    {
        for ( ExecutionLifeCycleListener listener : listeners )
        {
            listener.providerExecutionStarted( providerName, context );
        }
    }

    public void providerExecutionFinished( String providerName, PluginExecutionContext context, Exception e )
    {
        for ( ExecutionLifeCycleListener listener : listeners )
        {
            listener.providerExecutionFinished( providerName, context, e );
        }
    }

    public void executionFinished( PluginExecutionContext context, AbstractMojoExecutionException e )
    {
        for ( ExecutionLifeCycleListener listener : listeners )
        {
            listener.executionFinished( context, e );
        }
    }

    public RunOrderCalculator createRunOrderCalculator( PluginExecutionContext context )
    {
        if ( runOrderCalculator != null )
        {
            return runOrderCalculator;
        }
        else
        {
            String runOrder = context.getRunOrder();
            String statisticsHash = context.getRunStatisticsFileNameHash();
            int threadCount = context.getThreadCount();//@todo be more smart and distinguish between class/method...
            RunOrderParameters runOrderParameters = new RunOrderParameters( runOrder, statisticsHash );
            return new DefaultRunOrderCalculator( runOrderParameters, threadCount );
        }
    }

    private static void failAmbiguousExtension( Extension extension, Class<?> type, Collection<Extension> all )
        throws MojoExecutionException
    {
        String extensions = "\n";
        for ( Extension ext : all )
        {
            extensions += ext.getClass();
            extensions += "\n";
        }
        String msg = String.format( "Too ambiguous bean %s declaration of type %s. See all beans: %s",
                                    extension, type.getName(), extensions );

        throw new MojoExecutionException( msg );
    }

    private static Collection<Extension> makeCollection( ServiceLoader<Extension> extensions )
    {
        Collection<Extension> c = new ArrayList<Extension>();
        for ( Extension extension : extensions )
        {
            c.add( extension );
        }
        return c;
    }
}
