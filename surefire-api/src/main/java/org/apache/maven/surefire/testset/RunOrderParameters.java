package org.apache.maven.surefire.testset;

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

import org.apache.maven.surefire.util.Randomizer;
import org.apache.maven.surefire.util.RunOrder;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * @author Kristian Rosenvold
 */
public class RunOrderParameters
{
    private final RunOrder[] runOrder;

    private final File runStatisticsFile;

    private final Randomizer randomizer;

    public RunOrderParameters( @Nonnull RunOrder[] runOrder, @Nullable Randomizer randomizer,
                               @Nullable File runStatisticsFile )
    {
        this.runOrder = runOrder;
        this.randomizer = ensureRandomizer( runOrder, randomizer );
        this.runStatisticsFile = runStatisticsFile;
    }

    public RunOrderParameters( @Nullable String runOrder, @Nullable Randomizer randomizer,
                               @Nullable File runStatisticsFile )
    {
        this(
                ensureRunOrder( runOrder ),
                ensureRandomizer( ensureRunOrder( runOrder ), randomizer ),
                runStatisticsFile
        );
    }

    public RunOrderParameters( @Nullable String runOrder, @Nullable Randomizer randomizer,
                               @Nullable String runStatisticsFile )
    {
        this(
                runOrder, randomizer, ensureStatisticsFile( runStatisticsFile )
        );
    }

    public static RunOrderParameters alphabetical()
    {
        return new RunOrderParameters( new RunOrder[]{ RunOrder.ALPHABETICAL }, null, null );
    }

    public RunOrder[] getRunOrder()
    {
        return runOrder;
    }

    public File getRunStatisticsFile()
    {
        return runStatisticsFile;
    }

    public Randomizer getRandomizer()
    {
        return randomizer;
    }

    public boolean isRandomized()
    {
        return isRandomized( this.runOrder );
    }

    @Nonnull
    private static RunOrder[] ensureRunOrder( @Nullable String runOrder )
    {
        return runOrder == null ? RunOrder.DEFAULT : RunOrder.valueOfMulti( runOrder );
    }

    @Nullable
    private static File ensureStatisticsFile( @Nullable String runStatisticsFile )
    {
        return runStatisticsFile != null ? new File( runStatisticsFile ) : null;
    }

    private static boolean isRandomized( RunOrder[] runOrders )
    {
        boolean randomized = false;
        for ( RunOrder runOrder : runOrders )
        {
            if ( RunOrder.RANDOM.equals( runOrder ) )
            {
                randomized = true;
                break;
            }
        }
        return randomized;
    }

    @Nullable
    private static Randomizer ensureRandomizer( @Nonnull RunOrder[] runOrders, @Nullable Randomizer randomizer )
    {
        Randomizer result = randomizer;
        if ( isRandomized( runOrders ) )
        {
            if ( result == null )
            {
                result = new Randomizer();
            }
        }
        return result;
    }
}
