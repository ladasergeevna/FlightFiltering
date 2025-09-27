package com.gridnine.testing;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

public class Main {
    public static void main(String[] args) {
        List<Flight> flights = FlightBuilder.createFlights();

        // Правило 1: Вылет до текущего момента времени.
        System.out.println("\nВылеты в прошлом:");
        List<Flight> pastFlights = filterFlights(flights, new PastDepartureFilter());
        printFlights(pastFlights);

        // Правило 2: Сегменты с датой вылета раньше даты прилета
        System.out.println("\nРейс с сегментами, где дата вылета раньше даты прилета:");
        List<Flight> badSegmentsFlights = filterFlights(flights, new InvalidSegmentDatesFilter());
        printFlights(badSegmentsFlights);

        // Правило 3: Перелеты, где общее время, проведённое на земле, не превышает два часа
        System.out.println("\nПерелёты с менее чем 2 часами на земле:");
        List<Flight> groundTimeExceedsFlights = filterFlights(flights, new GroundTimeExceedsFilter(2));
        printFlights(groundTimeExceedsFlights);
    }

    private static void printFlights(List<Flight> flights) {
        for (Flight f : flights) {
            System.out.println(f);
        }
    }

    private static List<Flight> filterFlights(List<Flight> flights, FlightFilter filter) {
        return flights.stream()
                .filter(filter::testFlight)
                .collect(Collectors.toList());
    }

    static class PastDepartureFilter implements FlightFilter {
        @Override
        public boolean testFlight(Flight flight) {
            LocalDateTime now = LocalDateTime.now();
            return flight.getSegments().stream()
                    .noneMatch(s -> s.getDepartureDate().isBefore(now));
        }
    }

    static class InvalidSegmentDatesFilter implements FlightFilter {
        @Override
        public boolean testFlight(Flight flight) {
            return flight.getSegments().stream()
                    .noneMatch(s -> s.getArrivalDate().isBefore(s.getDepartureDate()));
        }
    }

    static class GroundTimeExceedsFilter implements FlightFilter {
        private final int hoursThreshold;

        public GroundTimeExceedsFilter(int hoursThreshold) {
            this.hoursThreshold = hoursThreshold;
        }

        @Override
        public boolean testFlight(Flight flight) {
            List<Segment> segments = flight.getSegments();
            if (segments.size() < 2) {
                return false;
            }
            long totalGroundTimeMinutes = 0;
            for (int i = 0; i < segments.size() - 1; i++) {
                LocalDateTime arrival = segments.get(i).getArrivalDate();
                LocalDateTime nextDeparture = segments.get(i + 1).getDepartureDate();
                long minutesBetween = java.time.Duration.between(arrival, nextDeparture).toMinutes();
                totalGroundTimeMinutes += minutesBetween;
            }
            return totalGroundTimeMinutes > hoursThreshold * 60;
        }
    }
}
