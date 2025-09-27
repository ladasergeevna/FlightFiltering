package com.gridnine.testing;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;


class FlightFiltersTest {
    private Segment createSegment(LocalDateTime dep, LocalDateTime arr) {
        return new Segment(dep, arr);
    }

    private Flight createFlight(List<Segment> segments) {
        return new Flight(segments);
    }

    @Test
    public void testPastDepartureFilter() {
        Main.PastDepartureFilter filter = new Main.PastDepartureFilter();
        LocalDateTime now = LocalDateTime.now();

        Flight flightInFuture = createFlight(List.of(createSegment(now.plusHours(1), now.plusHours(2))));
        Flight flightInPast = createFlight(List.of(createSegment(now.minusHours(2), now.minusHours(1))));
        Flight flightMixed = createFlight(List.of(
                createSegment(now.plusHours(1), now.plusHours(2)),
                createSegment(now.minusHours(3), now.minusHours(2))
        ));


        assertTrue(filter.testFlight(flightInFuture));
        assertFalse(filter.testFlight(flightInPast));
        assertFalse(filter.testFlight(flightMixed));
    }

    @Test
    void testInvalidSegmentDatesFilter() {
        Main.InvalidSegmentDatesFilter filter = new Main.InvalidSegmentDatesFilter();

        LocalDateTime now = LocalDateTime.now();

        Flight validFlight = createFlight(List.of(createSegment(now.plusHours(1), now.plusHours(2))));
        Flight invalidFlight = createFlight(List.of(createSegment(now.plusHours(2), now.plusHours(1))));
        Flight mixedFlight = createFlight(List.of(
                createSegment(now.plusHours(1), now.plusHours(2)),
                createSegment(now.plusHours(3), now.plusHours(2))
        ));


        assertTrue(filter.testFlight(validFlight));
        assertFalse(filter.testFlight(invalidFlight));
        assertFalse(filter.testFlight(mixedFlight));
    }

    @Test
    void testGroundTimeExceedsFilter() {
        Main.GroundTimeExceedsFilter filter = new Main.GroundTimeExceedsFilter(2);

        LocalDateTime now = LocalDateTime.now();

        Flight noStopFlight = createFlight(List.of(createSegment(now, now.plusHours(2))));

        Flight shortGroundTimeFlight = createFlight(List.of(
                createSegment(now, now.plusHours(1)),
                createSegment(now.plusHours(1).plusMinutes(30), now.plusHours(3))
        )); // 30 минут на земле

        Flight longGroundTimeFlight = createFlight(List.of(
                createSegment(now, now.plusHours(1)),
                createSegment(now.plusHours(4), now.plusHours(6)),
                createSegment(now.plusHours(7), now.plusHours(8))
        )); // 4 часа на земле

        assertFalse(filter.testFlight(noStopFlight));
        assertFalse(filter.testFlight(shortGroundTimeFlight));
        assertTrue(filter.testFlight(longGroundTimeFlight));
    }

}
