package app;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import model.Book;
import model.SmsMessage;
import model.Visitor;

import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class Lab5App {

    public static void main(String[] args) throws IOException {
        // Чтение JSON через Gson
        String json = new String(Files.readAllBytes(Paths.get("data/books.json")), StandardCharsets.UTF_8);
        Type listType = new TypeToken<List<Visitor>>() {}.getType();
        List<Visitor> visitors = new Gson().fromJson(json, listType);

        System.out.println("Задание 1: Посетители");
        visitors.forEach(v -> System.out.println(v.getName() + " " + v.getSurname()));
        System.out.println("Всего посетителей: " + visitors.size());

        System.out.println("\nЗадание 2: Книги без повторений");
        List<Book> uniqueBooks = visitors.stream()
                .flatMap(v -> v.getFavoriteBooks().stream())
                .distinct()
                .collect(Collectors.toList());
        uniqueBooks.forEach(b -> System.out.println(b.getName()));
        System.out.println("Всего уникальных книг: " + uniqueBooks.size());

        System.out.println("\nЗадание 3: Сортировка по году");
        uniqueBooks.stream()
                .sorted(Comparator.comparing(Book::getPublishingYear))
                .forEach(b -> System.out.println(b.getPublishingYear() + " - " + b.getName()));

        System.out.println("\nЗадание 4: Автор Jane Austen");
        boolean hasAusten = visitors.stream()
                .flatMap(v -> v.getFavoriteBooks().stream())
                .anyMatch(b -> "Jane Austen".equalsIgnoreCase(b.getAuthor()));
        System.out.println("Есть ли книги Jane Austen? " + (hasAusten ? "Да" : "Нет"));

        System.out.println("\nЗадание 5: Максимум книг у посетителя");
        int maxBooks = visitors.stream()
                .mapToInt(v -> v.getFavoriteBooks().size())
                .max()
                .orElse(0);
        System.out.println("Максимальное число книг: " + maxBooks);

        System.out.println("\nЗадание 6: SMS сообщения");
        double averageBooks = visitors.stream()
                .mapToInt(v -> v.getFavoriteBooks().size())
                .average()
                .orElse(0.0);
        System.out.println("Среднее число книг: " + averageBooks);

        List<SmsMessage> smsList = visitors.stream()
                .filter(Visitor::isSubscribed)
                .map(v -> {
                    int count = v.getFavoriteBooks().size();
                    String text;
                    if (count > averageBooks) {
                        text = "you are a bookworm";
                    } else if (count < averageBooks) {
                        text = "read more";
                    } else {
                        text = "fine";
                    }
                    return new SmsMessage(v.getPhone(), text);
                })
                .collect(Collectors.toList());

        smsList.forEach(sms -> System.out.println("Кому: " + sms.getPhoneNumber() + " | Текст: " + sms.getMessage()));
    }
}