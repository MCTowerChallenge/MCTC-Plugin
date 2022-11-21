package io.github.idkahn.towerchallenge.timer;

import org.jetbrains.annotations.NotNull;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

public class Duration implements Comparable<Duration> {

    private long years;
    private long months;
    private long days;
    private long hours;
    private long minutes;
    private long seconds;
    private long nanos;

    public Duration (long years, long months, long days, long hours, long minutes, long seconds, long nanos) {
        this.years = years;
        this.months = months;
        this.days = days;
        this.hours = hours;
        this.minutes = minutes;
        this.seconds = seconds;
        this.nanos = nanos;
    }

    public Duration (Duration input) {
        this(input.years, input.months, input.days, input.hours, input.minutes, input.seconds, input.nanos);
    }

    public Duration (LocalDateTime fromDateTime, LocalDateTime toDateTime) {
        LocalDateTime tempDateTime = LocalDateTime.from( fromDateTime );

        this.years = tempDateTime.until( toDateTime, ChronoUnit.YEARS );
        tempDateTime = tempDateTime.plusYears( this.years );

        this.months = tempDateTime.until( toDateTime, ChronoUnit.MONTHS );
        tempDateTime = tempDateTime.plusMonths( this.months );

        this.days = tempDateTime.until( toDateTime, ChronoUnit.DAYS );
        tempDateTime = tempDateTime.plusDays( this.days );

        this.hours = tempDateTime.until( toDateTime, ChronoUnit.HOURS );
        tempDateTime = tempDateTime.plusHours( this.hours );

        this.minutes = tempDateTime.until( toDateTime, ChronoUnit.MINUTES );
        tempDateTime = tempDateTime.plusMinutes( this.minutes );

        this.seconds = tempDateTime.until( toDateTime, ChronoUnit.SECONDS );
        tempDateTime = tempDateTime.plusSeconds( this.seconds );

        this.nanos = tempDateTime.until( toDateTime, ChronoUnit.NANOS );

    }

    public void setFromDateTime (LocalDateTime fromDateTime, LocalDateTime toDateTime) {
        LocalDateTime tempDateTime = LocalDateTime.from( fromDateTime );

        this.years = tempDateTime.until( toDateTime, ChronoUnit.YEARS );
        tempDateTime = tempDateTime.plusYears( this.years );

        this.months = tempDateTime.until( toDateTime, ChronoUnit.MONTHS );
        tempDateTime = tempDateTime.plusMonths( this.months );

        this.days = tempDateTime.until( toDateTime, ChronoUnit.DAYS );
        tempDateTime = tempDateTime.plusDays( this.days );

        this.hours = tempDateTime.until( toDateTime, ChronoUnit.HOURS );
        tempDateTime = tempDateTime.plusHours( this.hours );

        this.minutes = tempDateTime.until( toDateTime, ChronoUnit.MINUTES );
        tempDateTime = tempDateTime.plusMinutes( this.minutes );

        this.seconds = tempDateTime.until( toDateTime, ChronoUnit.SECONDS );
        tempDateTime = tempDateTime.plusSeconds( this.seconds );

        this.nanos = tempDateTime.until( toDateTime, ChronoUnit.NANOS );

    }

    public LocalDateTime getDateTime(LocalDateTime fromDateTime) {
        LocalDateTime endDateTime = LocalDateTime.from(fromDateTime);
        endDateTime = endDateTime.plusYears(years);
        endDateTime = endDateTime.plusMonths(months);
        endDateTime = endDateTime.plusDays(days);
        endDateTime = endDateTime.plusHours(hours);
        endDateTime = endDateTime.plusMinutes(minutes);
        endDateTime = endDateTime.plusSeconds(seconds);
        endDateTime = endDateTime.plusNanos(nanos);
        return endDateTime;
    }

    public long getYears() {
        return years;
    }

    public long getMonths() {
        return months;
    }

    public long getDays() {
        return days;
    }

    public long getHours() {
        return hours;
    }

    public long getMinutes() {
        return minutes;
    }

    public long getSeconds() {
        return seconds;
    }

    public long getNanos() {
        return nanos;
    }

    public String getFormattedTime() {
        return padZeros(getHours()) +":"+ padZeros(getMinutes()) +":"+ padZeros(getSeconds());
    }

    public Long getTime() {
        return (days*24*60*60*1000000000)+(hours*60*60*1000000000)+(minutes*60*1000000000)+seconds*1000000000+nanos;
    }

    public String padZeros(long input) {
        String zeros = "00";
        return zeros.substring(Long.toString(input).length())+input;
    }

    @Override
    public String toString() {
        return getYears() + " years " + getMonths() + " months " + getDays() + " days " + padZeros(getHours()) +":"+ padZeros(getMinutes()) +":"+ padZeros(getSeconds()) +":"+getNanos();
    }

    @Override
    public int compareTo(@NotNull Duration o) {
        return Long.compare(this.getTime(), o.getTime());
    }
}
