# FlightFiltering
Упрощенная система для фильтрации авиаперелетов по различным правилам

## Основные компоненты
- FlightFilter - интерфейс для фильтрации перелетов
- Flight - класс для полетов
- Segment - класс для сегментов

## Имеющиеся правила фильтрации
PastDepartureFilter Исключает перелеты, содержащие сегменты с вылетом до текущего момента времени.

InvalidSegmentDatesFilter Исключает перелеты с сегментами, где дата прилета раньше даты вылета.

GroundTimeExceedsFilter Исключает перелеты, где общее время, проведенное на земле между сегментами, превышает 2 часа.

## Технологический стек
- Java 17+
- Maven
- JUnit5

## Использование
### Получение данных

`List<Flight> flights = FlightBuilder.createFlights();`
### Применение правила

`List<Flight> result = filter.filterFlights(flights, new DepartureBeforeNowRule());`

### Создание собственных правил
` static class YourFilter implements FlightFilter {
        @Override
        public boolean testFlight(Flight flight) {
         //Ваша логика
        }
    }`

###  Использование правила
`List<Flight> exampleFlights = filterFlights(flights, new YourFilter());`

## Тестирование
Unit-тесты для каждого класса правил и основного класса

### Запуск всех тестов
`mvn test`
### Запуск конкретного тестового класса
`mvn test -Dtest=testInvalidSegmentDatesFilter`

## Запуск приложения
- `$mvn clean package`
- `$java -jar target/FlightFiltering-<версия приложения>.jar`
