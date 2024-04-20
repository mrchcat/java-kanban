package ru.yandex.practicum.taskmanager;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonParseException;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;
import ru.yandex.practicum.taskmanager.service.Managers;
import ru.yandex.practicum.taskmanager.service.TaskManager;
import ru.yandex.practicum.taskmanager.tasks.Epictask;
import ru.yandex.practicum.taskmanager.tasks.Selftask;
import ru.yandex.practicum.taskmanager.tasks.Subtask;
import ru.yandex.practicum.taskmanager.tasks.Task;
import ru.yandex.practicum.taskmanager.utils.DurationAdapter;
import ru.yandex.practicum.taskmanager.utils.LocalDateTimeAdapter;
import ru.yandex.practicum.taskmanager.utils.TaskDTO;

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

    private void initiateServerApi() {
        server.createContext("/tasks", this::selfHandler);
        server.createContext("/history", this::historyHandler);
        server.createContext("/prioritized", this::prioritizedHandler);

        server.createContext("/subtasks", this::subHandler);
        server.createContext("/epics", this::epicHandler);
        server.createContext("/", this::otherHandler);
    }

    Epictask parseEpictask(InputStream inputStream) {
        InputStreamReader reader = new InputStreamReader(inputStream);
        try {
            Epictask task = gson.fromJson(reader, Epictask.class);
            if (isNull(task.getName()) || isNull(task.getDescription())) {
                return null;
            } else {
                return task;
            }
        } catch (JsonParseException | DateTimeParseException e) {
            return null;
        }
    }

    Subtask parseSubtask(InputStream inputStream) {
        InputStreamReader reader = new InputStreamReader(inputStream);
        try {
            Subtask task = gson.fromJson(reader, Subtask.class);
            if (isNull(task.getName()) ||
                    isNull(task.getDescription()) ||
                    isNull(task.getDuration()) ||
                    isNull(task.getEpicId())) {
                return null;
            } else {
                return task;
            }
        } catch (JsonParseException | DateTimeParseException e) {
            return null;
        }
    }

    private void prioritizedHandler(HttpExchange exchange) {
        try (exchange) {
            String method = exchange.getRequestMethod();
            String path = exchange.getRequestURI().getPath();
            if (method.equals("GET") && path.matches("^/prioritized/?$")) {
                List<Task> taskList = taskManager.getPrioritizedTasks();
                List<TaskDTO> dtoList = taskList.stream().map(TaskDTO::get).toList();
                sendText(exchange, gson.toJson(dtoList), 200);
            } else {
                exchange.sendResponseHeaders(405, 0);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void historyHandler(HttpExchange exchange) {
        try (exchange) {
            String method = exchange.getRequestMethod();
            String path = exchange.getRequestURI().getPath();
            if (method.equals("GET") && path.matches("^/history/?$")) {
                List<Task> taskList = taskManager.getHistory();
                List<TaskDTO> dtoList = taskList.stream().map(TaskDTO::get).toList();
                sendText(exchange, gson.toJson(dtoList), 200);
            } else {
                exchange.sendResponseHeaders(405, 0);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void otherHandler(HttpExchange exchange) {
        try (exchange) {
            exchange.sendResponseHeaders(405, 0);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void selfHandler(HttpExchange exchange) {
        String method = exchange.getRequestMethod();
        String path = exchange.getRequestURI().getPath();
        try (exchange) {
            switch (method) {
                case "GET" -> {
                    if (path.matches("^/tasks/?$")) {
                        List<Task> taskList = taskManager.getAllSelftasks();
                        List<TaskDTO> dtoList = taskList.stream().map(TaskDTO::get).toList();
                        sendText(exchange, gson.toJson(dtoList), 200);
                        return;
                    }
                    if (path.matches("^/tasks/\\d+$")) {
                        Integer id = parseId(path.substring("/tasks/".length()));
                        if (nonNull(id)) {
                            Selftask selftask = taskManager.getSelftask(id);
                            if (nonNull(selftask)) {
                                sendText(exchange, gson.toJson(TaskDTO.get(selftask)), 200);
                            } else {
                                exchange.sendResponseHeaders(404, 0);
                            }
                            return;
                        }
                    }
                    exchange.sendResponseHeaders(405, 0);
                }

                case "POST" -> {
                    if (path.matches("^/tasks/?$")) {
                        Selftask selftask = parseSelftask(exchange.getRequestBody());
                        if (isNull(selftask)) {
                            exchange.sendResponseHeaders(405, 0);
                            return;
                        }
                        Integer id = selftask.getId();
                        if (isNull(id)) {
                            selftask = taskManager.add(selftask);
                        } else {
                            selftask = taskManager.update(selftask);
                        }
                        if (nonNull(selftask)) {
                            sendText(exchange, gson.toJson(TaskDTO.get(selftask)), 201);
                            return;
                        }
                    }
                    exchange.sendResponseHeaders(406, 0);
                }

                case "DELETE" -> {
                    if (path.matches("^/tasks/\\d+$")) {
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
                default -> exchange.sendResponseHeaders(405, 0);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void epicHandler(HttpExchange exchange) {
        String method = exchange.getRequestMethod();
        String path = exchange.getRequestURI().getPath();
        try (exchange) {
            switch (method) {
                case "GET" -> {
                    if (path.matches("^/epics/?$")) {
                        List<Task> taskList = taskManager.getAllEpictasks();
                        List<TaskDTO> dtoList = taskList.stream().map(TaskDTO::get).toList();
                        sendText(exchange, gson.toJson(dtoList), 200);
                        return;
                    }
                    if (path.matches("^/epics/\\d+$")) {
                        Integer id = parseId(path.substring("/epics/".length()));
                        if (nonNull(id)) {
                            Epictask epictask = taskManager.getEpic(id);
                            if (nonNull(epictask)) {
                                sendText(exchange, gson.toJson(TaskDTO.get(epictask)), 200);
                            } else {
                                exchange.sendResponseHeaders(404, 0);
                            }
                            return;
                        }
                    }
                    exchange.sendResponseHeaders(405, 0);
                }

                case "POST" -> {
                    if (path.matches("^/epics/?$")) {
                        Epictask epictask = parseEpictask(exchange.getRequestBody());
                        if (isNull(epictask)) {
                            exchange.sendResponseHeaders(405, 0);
                            return;
                        }
                        Integer id = epictask.getId();
                        if (isNull(id)) {
                            epictask = taskManager.add(epictask);
                        } else {
                            epictask = taskManager.update(epictask);
                        }
                        if (nonNull(epictask)) {
                            sendText(exchange, gson.toJson(TaskDTO.get(epictask)), 201);
                            return;
                        }
                    }
                    exchange.sendResponseHeaders(406, 0);
                }

                case "DELETE" -> {
                    if (path.matches("^/epics/\\d+$")) {
                        Integer id = parseId(path.substring("/epics/".length()));
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
                default -> exchange.sendResponseHeaders(405, 0);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void subHandler(HttpExchange exchange) {
        String method = exchange.getRequestMethod();
        String path = exchange.getRequestURI().getPath();
        try (exchange) {
            switch (method) {
                case "GET" -> {
                    if (path.matches("^/subtasks/?$")) {
                        List<Task> taskList = taskManager.getAllSubtasks();
                        List<TaskDTO> dtoList = taskList.stream().map(TaskDTO::get).toList();
                        sendText(exchange, gson.toJson(dtoList), 200);
                        return;
                    }
                    if (path.matches("^/subtasks/\\d+$")) {
                        Integer id = parseId(path.substring("/subtasks/".length()));
                        if (nonNull(id)) {
                            Subtask subtask = taskManager.getSubtask(id);
                            if (nonNull(subtask)) {
                                sendText(exchange, gson.toJson(TaskDTO.get(subtask)), 200);
                            } else {
                                exchange.sendResponseHeaders(404, 0);
                            }
                            return;
                        }
                    }
                    exchange.sendResponseHeaders(405, 0);
                }

                case "POST" -> {
                    if (path.matches("^/subtasks/?$")) {
                        Subtask subtask = parseSubtask(exchange.getRequestBody());
                        if (isNull(subtask)) {
                            exchange.sendResponseHeaders(405, 0);
                            return;
                        }
                        Integer id = subtask.getId();
                        if (isNull(id)) {
                            subtask = taskManager.add(subtask);
                        } else {
                            subtask = taskManager.update(subtask);
                        }
                        if (nonNull(subtask)) {
                            sendText(exchange, gson.toJson(TaskDTO.get(subtask)), 201);
                            return;
                        }
                    }
                    exchange.sendResponseHeaders(406, 0);
                }

                case "DELETE" -> {
                    if (path.matches("^/subtasks/\\d+$")) {
                        Integer id = parseId(path.substring("/subtasks/".length()));
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
                default -> exchange.sendResponseHeaders(405, 0);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

}
