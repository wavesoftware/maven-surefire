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

import org.apache.maven.plugin.surefire.log.api.ConsoleLogger;
import org.apache.maven.plugin.surefire.log.api.NullConsoleLogger;
import org.apache.maven.plugin.surefire.report.DefaultReporterFactory;
import org.apache.maven.surefire.testset.RunOrderParameters;
import org.apache.maven.surefire.util.Randomizer;
import org.apache.maven.surefire.util.RunOrder;
import org.apache.maven.surefire.util.RunOrders;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.io.File;
import java.io.IOException;

import static org.junit.Assert.assertTrue;

/**
 * @author <a href="krzysztof.suszynski@wavesoftware.pl">Krzysztof Suszyński</a>
 * @since 2016-04-03
 */
public class CommonReflectorTest
{
    @Rule
    public TemporaryFolder temporaryFolder = new TemporaryFolder();

    @Test
    public void testCreateReportingReporterFactory() throws IOException
    {
        // given
        CommonReflector commonReflector = new CommonReflector( this.getClass().getClassLoader() );
        RunOrders runOrders = new RunOrders( RunOrder.DEFAULT );
        RunOrderParameters runOrderParameters = new RunOrderParameters(
                runOrders, null, null
        );
        ConsoleLogger consoleLogger = new NullConsoleLogger();
        File tempDir = temporaryFolder.newFolder();
        StartupReportConfiguration startupReportConfiguration = new StartupReportConfiguration(
                true, true, null, false, false, tempDir, false, null, null, false, 0, null, null, null, runOrderParameters
        );
        // when
        Object object = commonReflector.createReportingReporterFactory(
                startupReportConfiguration, consoleLogger
        );

        // then
        assertTrue( "object is instance of DefaultReporterFactory",
                object instanceof DefaultReporterFactory );
    }

    @Test
    public void testCreateReportingReporterFactoryWithRunOrderParameters() throws IOException
    {
        // given
        CommonReflector commonReflector = new CommonReflector( this.getClass().getClassLoader() );
        String seed = "123";
        Randomizer randomizer = new Randomizer( seed );
        RunOrderParameters runOrderParameters = new RunOrderParameters(
                new RunOrders( RunOrder.RANDOM ), randomizer, null
        );
        ConsoleLogger consoleLogger = new NullConsoleLogger();
        File tempDir = temporaryFolder.newFolder();
        StartupReportConfiguration startupReportConfiguration = new StartupReportConfiguration(
                true, true, null, false, false, tempDir, false, null, null, false, 0, null, "UTF-8", "failsafe",
                runOrderParameters
        );
        // when
        Object object = commonReflector.createReportingReporterFactory(
                startupReportConfiguration, consoleLogger
        );

        // then
        assertTrue( "object is instance of DefaultReporterFactory",
                object instanceof DefaultReporterFactory );
    }
}