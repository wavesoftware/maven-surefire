package org.apache.maven.surefire.booter;
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

import org.apache.maven.surefire.report.ReporterFactory;
import org.apache.maven.surefire.report.RunListener;
import org.apache.maven.surefire.suite.RunResult;
import org.apache.maven.surefire.testset.RunOrderParameters;
import org.apache.maven.surefire.util.Randomizer;
import org.apache.maven.surefire.util.RunOrder;
import org.apache.maven.surefire.util.RunOrders;
import org.junit.Test;

import java.io.File;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

public class SurefireReflectorTest
{
    @Test
    public void testShouldCreateFactoryWithoutException()
    {
        ReporterFactory factory = new ReporterFactory() {
            @Override
            public RunListener createReporter() {
                return null;
            }

            @Override
            public RunResult close() {
                return null;
            }
        };
        ClassLoader cl = Thread.currentThread().getContextClassLoader();
        SurefireReflector reflector = new SurefireReflector( cl );
        BaseProviderFactory baseProviderFactory =
                (BaseProviderFactory) reflector.createBooterConfiguration( cl, factory, true );
        assertNotNull( baseProviderFactory.getReporterFactory() );
        assertSame( factory, baseProviderFactory.getReporterFactory() );
    }

    @Test
    public void testCreateRunOrderParameters() {
        // given
        ClassLoader cl = Thread.currentThread().getContextClassLoader();
        SurefireReflector reflector = new SurefireReflector( cl );
        RunOrders runOrders = new RunOrders( RunOrder.RANDOM );
        Randomizer randomizer = new Randomizer();
        File statisticsFile = new File( "." );
        RunOrderParameters parameters = new RunOrderParameters( runOrders, randomizer, statisticsFile );

        // when
        Object created = reflector.createRunOrderParameters( parameters );

        assertTrue( created instanceof RunOrderParameters );
    }

}
