package ru.yandex.practicum.taskmanager;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonParseException;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;
import ru.yandex.practicum.taskmanager.service.Managers;
import ru.yandex.practicum.taskmanager.service.TaskManager;
import ru.yandex.practicum.taskmanager.tasks.Selftask;
import ru.yandex.practicum.taskmanager.tasks.Task;
import ru.yandex.practicum.taskmanager.utils.DurationAdapter;
import ru.yandex.practicum.taskmanager.utils.LocalDateTimeAdapter;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.List;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;

public class HttpTaskServer {
    private final String host;
    private final int port;
    private final TaskManager taskManager;
    private final Gson gson;
    private HttpServer server;

    public HttpTaskServer(String host, int port) {
        this.host = host;
        this.port = port;
        this.gson = new GsonBuilder()
                .serializeNulls()
                .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
                .registerTypeAdapter(Duration.class, new DurationAdapter())
                .create();
        taskManager = Managers.getDefault();
        try {
            this.server = HttpServer.create(new InetSocketAddress(host, port), 0);
            initiateServerApi();
        } catch (IOException e) {
            String message = String.format("Server can not be started on %s:%d. Programme finished", host, port);
            System.out.println(message);
        }
    }

    public static void main(String[] args) {
        HttpTaskServer httpTaskServer = new HttpTaskServer("localhost", 8080);
        httpTaskServer.start();
    }

    private void initiateServerApi() {
        server.createContext("/tasks", this::selfHandler);
        server.createContext("/history", this::historyHandler);
        server.createContext("/prioritized", this::prioritizedHandler);

//        server.createContext("/subtasks", new SubHandler());
//        server.createContext("/epics", new EpicHandler());
        server.createContext("/", this::otherHandler);
    }

    public TaskManager getTaskManager() {
        return taskManager;
    }

    private void sendText(HttpExchange exchange, String text, int code) throws IOException {
        exchange.getResponseHeaders().add("Content-Type", "application/json");
        byte[] reply = text.getBytes(StandardCharsets.UTF_8);
        exchange.sendResponseHeaders(code, reply.length);
        try (OutputStream os = exchange.getResponseBody()) {
            os.write(reply);
        }
    }

    private Integer parseId(String text) {
        try {
            return Integer.parseInt(text);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    Selftask parseSelftask(InputStream inputStream) {
        InputStreamReader reader = new InputStreamReader(inputStream);
        try {
            Selftask task = gson.fromJson(reader, Selftask.class);
            if (isNull(task.getName()) || isNull(task.getDescription()) || isNull(task.getDuration())) {
                return null;
            } else {
                return task;
            }
        } catch (JsonParseException | DateTimeParseException e) {
            return null;
        }
    }

    private void prioritizedHandler(HttpExchange exchange) {
        try {
            String method = exchange.getRequestMethod();
            String path = exchange.getRequestURI().getPath();
            if (method.equals("GET") && path.matches("^\\/prioritized\\/?$")) {
                List<Task> taskList = taskManager.getPrioritizedTasks();
                sendText(exchange, gson.toJson(taskList), 200);
            } else {
                exchange.sendResponseHeaders(405, 0);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            exchange.close();
        }
    }

    private void historyHandler(HttpExchange exchange) {
        try {
            String method = exchange.getRequestMethod();
            String path = exchange.getRequestURI().getPath();
            if (method.equals("GET") && path.matches("^\\/history\\/?$")) {
                List<Task> taskList = taskManager.getHistory();
                sendText(exchange, gson.toJson(taskList), 200);
            } else {
                exchange.sendResponseHeaders(405, 0);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            exchange.close();
        }
    }

    private void otherHandler(HttpExchange exchange) {
        try {
            exchange.sendResponseHeaders(405, 0);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            exchange.close();
        }
    }

    private void selfHandler(HttpExchange exchange) {
        String method = exchange.getRequestMethod();
        String path = exchange.getRequestURI().getPath();
        try {
            switch (method) {
                case "GET" -> {
                    if (path.matches("^\\/tasks\\/?$")) {
                        List<Task> taskList = taskManager.getAllSelftasks();
                        sendText(exchange, gson.toJson(taskList), 200);
                        return;
                    }
                    if (path.matches("^\\/tasks\\/\\d+$")) {
                        Integer id = parseId(path.substring("/tasks/".length()));
                        if (nonNull(id)) {
                            Task task = taskManager.get(id);
                            if (nonNull(task)) {
                                sendText(exchange, gson.toJson(task), 200);
                            } else {
                                exchange.sendResponseHeaders(404, 0);
                            }
                            return;
                        }
                    }
                    exchange.sendResponseHeaders(405, 0);
                }

                case "POST" -> {
                    if (path.matches("^\\/tasks\\/?$")) {
                        Selftask task = parseSelftask(exchange.getRequestBody());
                        if (isNull(task)) {
                            exchange.sendResponseHeaders(405, 0);
                            return;
                        }
                        Integer id = task.getId();
                        if (isNull(id)) {
                            task = taskManager.add(task);
                        } else {
                            task = taskManager.update(task);
                        }
                        if (nonNull(task)) {
                            sendText(exchange, gson.toJson(task), 201);
                            return;
                        }
                    }
                    exchange.sendResponseHeaders(406, 0);
                }

                case "DELETE" -> {
                    if (path.matches("^\\/tasks\\/\\d+$")) {
                        Integer id = parseId(path.substring("/tasks/".length()));
                        if (nonNull(id)) {
                            Task task = taskManager.delete(id);
                            if (nonNull(task)) {
                                exchange.sendResponseHeaders(200, 0);
                            } else {
                                exchange.sendResponseHeaders(404, 0);
                            }
                            return;
                        }
                    }
                    exchange.sendResponseHeaders(405, 0);
                }
                default -> {
                    exchange.sendResponseHeaders(405, 0);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            exchange.close();
        }
    }

    public void start() {
        server.start();
        String message = String.format("Server started on %s:%d", host, port);
        System.out.println(message);
    }

    public void stop() {
        server.stop(1);
        String message = String.format("Server stopped on %s:%d", host, port);
        System.out.println(message);
    }
}
