package org.apache.maven.surefire.extensions;

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

import org.apache.maven.surefire.testset.TestListResolver;

/**
 * The context is implemented by <em>maven-surefire-common</em> module, however the user may provide own SPI context.
 * If the SPI context is more than one, the phase <em>contextValidated</em> is called with exception and the lifecycle
 * is finished with <em>executionFinished</em> and throwing {@link org.apache.maven.plugin.MojoExecutionException}
 * in the plugin.
 */
public interface PluginExecutionContext
{
    String id();
    String phase();
    boolean isSurefirePlugin();
    boolean isFailsafePlugin();
    TestListResolver getSpecificTests();
    TestListResolver getIncludedAndExcludedTests();
    String getRunOrder();
    String getRunStatisticsFileNameHash();
    String getParallel();
    boolean getUseUnlimitedThreads();
    int getThreadsPerCpuCore();
    int getThreadCount();
    int getThreadCountSuites();
    int getThreadCountClasses();
    int getThreadCountMethods();
}
