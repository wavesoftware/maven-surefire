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

import junit.framework.TestCase;
import org.apache.maven.plugin.surefire.report.DefaultReporterFactory;
import org.apache.maven.shared.utils.io.FileUtils;
import org.apache.maven.surefire.testset.RunOrderParameters;
import org.apache.maven.surefire.util.Randomizer;
import org.apache.maven.surefire.util.RunOrder;
import org.apache.maven.surefire.util.RunOrders;

import java.io.File;
import java.io.IOException;

/**
 * @author <a href="krzysztof.suszynski@wavesoftware.pl">Krzysztof Suszyński</a>
 * @since 2016-04-03
 */
public class CommonReflectorTest extends TestCase
{
    private File tempDir;

    @Override
    protected void setUp() throws Exception
    {
        tempDir = createTempDirectory();
    }

    @Override
    protected void tearDown() throws Exception
    {
        FileUtils.deleteDirectory( tempDir );
    }

    public void testCreateReportingReporterFactory()
    {
        // given
        CommonReflector commonReflector = new CommonReflector( this.getClass().getClassLoader() );
        StartupReportConfiguration startupReportConfiguration = new StartupReportConfiguration(
                true, true, null, false, false, tempDir, false, null, null, false, 0, null, null, null
        );
        // when
        Object object = commonReflector.createReportingReporterFactory( startupReportConfiguration );

        // then
        assertTrue( "object is instance of DefaultReporterFactory", object instanceof DefaultReporterFactory );
    }

    public void testCreateReportingReporterFactoryWithRunOrderParameters()
    {
        // given
        CommonReflector commonReflector = new CommonReflector( this.getClass().getClassLoader() );
        String seed = "123";
        Randomizer randomizer = new Randomizer( seed );
        RunOrderParameters runOrderParameters = new RunOrderParameters(
                new RunOrders( RunOrder.RANDOM ), randomizer, null
        );
        StartupReportConfiguration startupReportConfiguration = new StartupReportConfiguration(
                true, true, null, false, false, tempDir, false, null, null, false, 0, null, null,
                runOrderParameters
        );
        // when
        Object object = commonReflector.createReportingReporterFactory( startupReportConfiguration );

        // then
        assertTrue( "object is instance of DefaultReporterFactory", object instanceof DefaultReporterFactory );
    }

    private static File createTempDirectory() throws IOException
    {
        final File temp;

        temp = File.createTempFile("temp", Long.toString(System.nanoTime()));

        if(!(temp.delete()))
        {
            throw new IOException("Could not delete temp file: " + temp.getAbsolutePath());
        }

        if(!(temp.mkdir()))
        {
            throw new IOException("Could not create temp directory: " + temp.getAbsolutePath());
        }

        return (temp);
    }
}