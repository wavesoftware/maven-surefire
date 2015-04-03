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

import org.apache.maven.plugin.AbstractMojoExecutionException;

/**
 * Life cycle of plugin execution is split in phases:<p>
 *     <ul>
 *         <li><em>executionStarted</em> called anyway
 *         <li><em>contextValidated</em> If plugin parameters validation fails, the last phase is directly called.
 *         <li><em>contextInitialized</em>
 *         <li><em>providerExecutionStarted</em> and <em>providerExecutionFinished</em> both run for each provider.
 *         <li><em>executionFinished</em> called anyway
 *     </ul>
 * The phases <em>providerExecutionStarted</em> and <em>providerExecutionFinished</em> may be executed
 * several times depending on the number of providers.<p>
 * The plugin context is just only one instance and it is initialized in the phase <em>contextInitialized</em>.
 * The context is unmodifiable and valid until after last <em>executionFinished</em> was called.
 */
public interface ExecutionLifeCycleListener
{
    void executionStarted( String id );
    void contextValidated( String id, AbstractMojoExecutionException e );
    void contextInitialized( PluginExecutionContext context );
    void providerExecutionStarted( String providerName, PluginExecutionContext context );
    void providerExecutionFinished( String providerName, PluginExecutionContext context, Exception e );
    void executionFinished( PluginExecutionContext context, AbstractMojoExecutionException e );
}
