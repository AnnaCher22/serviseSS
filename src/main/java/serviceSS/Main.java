package serviceSS;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.http.ResponseEntity;
import serviceSS.controller.LinkController;

import java.util.Scanner;

@SpringBootApplication
public class Main implements CommandLineRunner {

    @Autowired
    private LinkController linkController;

    public static void main(String[] args) {
        SpringApplication.run(Main.class, args);
    }

    @Override
    public void run(String... args) {
        Scanner scanner = new Scanner(System.in);
        while (true) {
            System.out.println("Выберите действие:");
            System.out.println("1. Создать короткую ссылку");
            System.out.println("2. Перейти по короткой ссылке");
            System.out.println("3. Выход");
            int choice = scanner.nextInt();
            scanner.nextLine(); // Очистка буфера

            switch (choice) {
                case 1:
                    System.out.print("Введите оригинальный URL: ");
                    String originalUrl = scanner.nextLine();
                    ResponseEntity<String> response = linkController.createLink(originalUrl);
                    System.out.println(response.getBody()); // Выводим ответ в консоль
                    break;
                case 2:
                    System.out.print("Введите короткую ссылку: ");
                    String shortUrl = scanner.nextLine();
                    ResponseEntity<String> redirectResponse = linkController.redirectToLongUrl(shortUrl);
                    System.out.println(redirectResponse.getBody());
                    break;
                case 3:
                    System.out.println("Выход из программы.");
                    scanner.close();
                    return;
                default:
                    System.out.println("Некорректный ввод. Пожалуйста, попробуйте снова.");
            }
        }
    }
}
