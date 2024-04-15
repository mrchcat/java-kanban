package ru.yandex.practicum.taskmanager;

public class Main {
    public static void main(String[] args) {
        HttpTaskServer server = new HttpTaskServer("localhost", 8080);
        server.start();
    }
}
