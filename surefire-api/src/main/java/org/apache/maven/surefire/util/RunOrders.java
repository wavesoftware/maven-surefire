package org.apache.maven.surefire.util;

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

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Represents a complete set of run orders with arguments
 *
 * @author <a href="krzysztof.suszynski@wavesoftware.pl">Krzysztof Suszyński</a>
 */
@ParametersAreNonnullByDefault
public final class RunOrders
{
    private final List<RunOrderWithArguments> withArguments;

    public RunOrders( RunOrder... runOrders )
    {
        this( withEmptyArguments( runOrders ) );
    }

    RunOrders( List<RunOrderWithArguments> runOrders )
    {
        this.withArguments = Collections.unmodifiableList( runOrders );
    }

    public Iterable<RunOrderWithArguments> getIterable()
    {
        return withArguments;
    }

    public boolean any()
    {
        return !withArguments.isEmpty();
    }

    public boolean contains( RunOrder runOrder )
    {
        for ( RunOrderWithArguments order : withArguments )
        {
            if ( order.getRunOrder().equals( runOrder ) )
            {
                return true;
            }
        }
        return false;
    }

    public RunOrderArguments getArguments( RunOrder runOrder )
    {
        for ( RunOrderWithArguments order : withArguments )
        {
            if ( order.getRunOrder().equals( runOrder ) )
            {
                return order.getRunOrderArguments();
            }
        }
        throw new IllegalStateException( "20180524:221004 - check if contains specific run "
                + "order before using getArguments" );
    }

    RunOrder firstAsType()
    {
        if ( !any() )
        {
            throw new IllegalStateException(
                    "20180524:222348 - use #any() method before invoking #firstAsType() method."
            );
        }
        return withArguments.iterator().next().getRunOrder();
    }

    private static List<RunOrderWithArguments> withEmptyArguments( RunOrder[] runOrders )
    {
        List<RunOrderWithArguments> orderWithArguments = new ArrayList<RunOrderWithArguments>();
        for ( RunOrder runOrder : runOrders )
        {
            RunOrderArguments args = new RunOrderArguments( new ArrayList<String>() );
            orderWithArguments.add( new RunOrderWithArguments( runOrder, args ) );
        }
        return orderWithArguments;
    }
}
