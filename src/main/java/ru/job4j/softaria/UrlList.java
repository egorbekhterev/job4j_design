package ru.job4j.softaria;

import java.util.List;

public record UrlList(List<String> listLost, List<String> listChanged,
                      List<String> listAdded) {

}
