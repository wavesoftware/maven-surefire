package org.apache.maven.surefire.util;

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

    RunOrders(List<RunOrderWithArguments> runOrders)
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
        for (RunOrderWithArguments order : withArguments)
        {
            if ( order.getRunOrder().equals( runOrder ) )
            {
                return true;
            }
        }
        return false;
    }

    public RunOrderArguments getArguments( RunOrder runOrder ) {
        for (RunOrderWithArguments order : withArguments) {
            if ( order.getRunOrder().equals( runOrder ) )
            {
                return order.getRunOrderArguments();
            }
        }
        throw new IllegalStateException( "20180524:221004 - check if contains specific run " +
                "order before using getArguments" );
    }

    public RunOrder firstAsType() {
        if (!any())
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
        for (RunOrder runOrder : runOrders)
        {
            RunOrderArguments args = new RunOrderArguments( new ArrayList<String>() );
            orderWithArguments.add( new RunOrderWithArguments( runOrder, args ) );
        }
        return orderWithArguments;
    }
}
