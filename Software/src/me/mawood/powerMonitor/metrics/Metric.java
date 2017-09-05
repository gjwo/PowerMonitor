package me.mawood.powerMonitor.metrics;

import com.sun.org.apache.xpath.internal.SourceTree;
import me.mawood.powerMonitor.metrics.units.Unit;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.Locale;

public class Metric implements Comparable<Metric>
{
    private final double value;
    private final Instant timestamp;
    // Using generics to facilitate unit conversion later.
    private final Unit unit;

    public Metric(double value,Instant timestamp, Unit unit)
    {
        this.value = value;
        this.timestamp = timestamp;
        this.unit = unit;
    }
    public double getValue()
    {
        return value;
    }

    public Instant getTimestamp()
    {
        return timestamp;
    }

    public Unit getUnit()
    {
        return unit;
    }

    @Override
    public String toString()
    {
        final DateTimeFormatter formatter =
                DateTimeFormatter.ISO_LOCAL_DATE_TIME
                        .withLocale( Locale.UK )
                        .withZone( ZoneId.systemDefault() );
        //System.out.println(String.format("Metric: {%.03f %s at %s}", value,unit.getSymbol(), formatter.format(timestamp)));
        return String.format("%.03f %s at %s", value,unit.getSymbol(), formatter.format(timestamp));
    }
    @Override
    public int compareTo(Metric o)
    {
        return (int)(timestamp.toEpochMilli() - o.timestamp.toEpochMilli());
    }
}
