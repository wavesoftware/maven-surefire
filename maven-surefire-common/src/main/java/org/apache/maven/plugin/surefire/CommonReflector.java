package org.apache.maven.plugin.surefire;

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

import java.io.File;
import java.lang.reflect.Constructor;

import org.apache.maven.plugin.surefire.report.DefaultReporterFactory;
import org.apache.maven.surefire.booter.SurefireReflector;
import org.apache.maven.surefire.testset.RunOrderParameters;
import org.apache.maven.surefire.util.ReflectionUtils;
import org.apache.maven.surefire.util.SurefireReflectionException;

import javax.annotation.Nonnull;

/**
 * @author Kristian Rosenvold
 */
public class CommonReflector
{
    private final ClassLoader surefireClassLoader;

    private final Class<?> startupReportConfigurationClass;

    private final Class<?> runOrderParametersClass;

    private final SurefireReflector surefireReflector;

    public CommonReflector( @Nonnull ClassLoader surefireClassLoader )
    {
        this.surefireClassLoader = surefireClassLoader;
        this.surefireReflector = new SurefireReflector( surefireClassLoader );

        try
        {
            runOrderParametersClass = surefireClassLoader.loadClass( RunOrderParameters.class.getName() );
            startupReportConfigurationClass = surefireClassLoader
                    .loadClass( StartupReportConfiguration.class.getName() );
        }
        catch ( ClassNotFoundException e )
        {
            throw new SurefireReflectionException( e );
        }
    }

    public Object createReportingReporterFactory( @Nonnull StartupReportConfiguration startupReportConfiguration )
    {
        Class<?>[] args = new Class[]{ this.startupReportConfigurationClass };
        Object src = createStartupReportConfiguration( startupReportConfiguration );
        Object[] params = new Object[]{ src };
        return ReflectionUtils.instantiateObject( DefaultReporterFactory.class.getName(), args, params,
                                                  surefireClassLoader );
    }

    private Object createStartupReportConfiguration( @Nonnull StartupReportConfiguration reporterConfiguration )
    {
        Constructor<?> constructor = ReflectionUtils.getConstructor( startupReportConfigurationClass,
                                                                     boolean.class, boolean.class,
                                                                     String.class, boolean.class, boolean.class,
                                                                     File.class, boolean.class, String.class,
                                                                     String.class, boolean.class, int.class,
                                                                     String.class, String.class,
                                                                     runOrderParametersClass
                );
        Object runOrderParameters = surefireReflector.createRunOrderParameters(
                reporterConfiguration.getRunOrderParameters()
        );

        //noinspection BooleanConstructorCall
        Object[] params = { reporterConfiguration.isUseFile(), reporterConfiguration.isPrintSummary(),
            reporterConfiguration.getReportFormat(), reporterConfiguration.isRedirectTestOutputToFile(),
            reporterConfiguration.isDisableXmlReport(), reporterConfiguration.getReportsDirectory(),
            reporterConfiguration.isTrimStackTrace(), reporterConfiguration.getReportNameSuffix(),
            reporterConfiguration.getConfigurationHash(), reporterConfiguration.isRequiresRunHistory(),
            reporterConfiguration.getRerunFailingTestsCount(), reporterConfiguration.getXsdSchemaLocation(),
            reporterConfiguration.getPluginName(), runOrderParameters
        };
        return ReflectionUtils.newInstance( constructor, params );
    }

}
