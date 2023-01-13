package ru.job4j.ood.srp.report;

import ru.job4j.ood.srp.currency.Currency;
import ru.job4j.ood.srp.currency.InMemoryCurrencyConverter;
import ru.job4j.ood.srp.formatter.DateTimeParser;
import ru.job4j.ood.srp.model.Employee;
import ru.job4j.ood.srp.store.Store;

import java.util.Calendar;
import java.util.function.Predicate;

public class ReportEngineForAD implements Report {

    private final Store store;
    private final DateTimeParser<Calendar> dateTimeParser;
    private final InMemoryCurrencyConverter inMemoryCurrencyConverter;

    public ReportEngineForAD(Store store, DateTimeParser<Calendar> dateTimeParser, InMemoryCurrencyConverter inMemoryCurrencyConverter) {
        this.store = store;
        this.dateTimeParser = dateTimeParser;
        this.inMemoryCurrencyConverter = inMemoryCurrencyConverter;
    }

    @Override
    public String generate(Predicate<Employee> filter) {
        StringBuilder text = new StringBuilder();
        text.append("Name; Hired; Fired; Salary;")
                .append(System.lineSeparator());
        for (Employee employee : store.findBy(filter)) {
            text.append(employee.getName()).append(" ")
                    .append(dateTimeParser.parse(employee.getHired())).append(" ")
                    .append(dateTimeParser.parse(employee.getFired())).append(" ")
                    .append(inMemoryCurrencyConverter.convert(Currency.USD, employee.getSalary(), Currency.RUB))
                    .append(System.lineSeparator());
        }
        return text.toString();
    }
}