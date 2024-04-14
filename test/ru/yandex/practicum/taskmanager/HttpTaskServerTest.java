package ru.yandex.practicum.taskmanager;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.*;
import ru.yandex.practicum.taskmanager.service.TaskManager;
import ru.yandex.practicum.taskmanager.tasks.Selftask;
import ru.yandex.practicum.taskmanager.tasks.Task;
import ru.yandex.practicum.taskmanager.utils.DurationAdapter;
import ru.yandex.practicum.taskmanager.utils.LocalDateTimeAdapter;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class HttpTaskServerTest {
    HttpTaskServer httpTaskServer;
    TaskManager taskManager;
    HttpClient client;
    URI uri;
    Gson gson;

    @BeforeEach
    void initAndStart() {
        httpTaskServer = new HttpTaskServer("localhost", 8080);
        taskManager = httpTaskServer.getTaskManager();
        httpTaskServer.start();
        client = HttpClient.newHttpClient();
        this.gson = new GsonBuilder()
                .setPrettyPrinting()
                .serializeNulls()
                .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
                .registerTypeAdapter(Duration.class, new DurationAdapter())
                .create();
    }

    @AfterEach
    void stop() {
        httpTaskServer.stop();
    }


    @DisplayName("add one Selftasks")
    @Tag("Selftask")
    @Test
    void addNewSelftasksTest() throws IOException, InterruptedException {
        Selftask newTask = new Selftask("name",
                "decr",
                LocalDateTime.of(2024, 04, 13, 22, 12, 2),
                Duration.ofDays(1));
        uri = URI.create("http://localhost:8080/tasks");
        HttpRequest httpRequest = HttpRequest.newBuilder()
                .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(newTask)))
                .uri(uri)
                .build();
        HttpResponse<String> response = client.send(httpRequest, HttpResponse.BodyHandlers.ofString());
        assertEquals(201, response.statusCode());
        assertNotNull(response.body());
        Selftask taskFromServer = gson.fromJson(response.body(), Selftask.class);
        assertNotNull(taskFromServer.getId());
        assertEquals(newTask.getName(), taskFromServer.getName());
        assertEquals(newTask.getDescription(), taskFromServer.getDescription());
        assertEquals(newTask.getStartTime(), taskFromServer.getStartTime());
        assertEquals(newTask.getDuration(), taskFromServer.getDuration());
    }

    @DisplayName("add 3 new Selftasks and get all Selftasks")
    @Tag("Selftask")
    @Test
    void add3NewSelftasksTest() throws IOException, InterruptedException {
        Selftask task1 = new Selftask("name1",
                "decr1",
                LocalDateTime.of(2024, 04, 13, 22, 12, 2),
                Duration.ofDays(1));
        Selftask task2 = new Selftask("name2",
                "decr2",
                LocalDateTime.of(2025, 04, 13, 22, 12, 2),
                Duration.ofDays(1));
        Selftask task3 = new Selftask("name3",
                "decr3",
                LocalDateTime.of(2026, 04, 13, 22, 12, 2),
                Duration.ofDays(1));
        uri = URI.create("http://localhost:8080/tasks");
        HttpRequest httpRequestAddTask1 = HttpRequest.newBuilder()
                .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(task1)))
                .uri(uri)
                .build();
        HttpRequest httpRequestAddTask2 = HttpRequest.newBuilder()
                .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(task2)))
                .uri(uri)
                .build();
        HttpRequest httpRequestAddTask3 = HttpRequest.newBuilder()
                .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(task3)))
                .uri(uri)
                .build();
        client.send(httpRequestAddTask1, HttpResponse.BodyHandlers.ofString());
        client.send(httpRequestAddTask2, HttpResponse.BodyHandlers.ofString());
        client.send(httpRequestAddTask3, HttpResponse.BodyHandlers.ofString());

        HttpRequest httpRequestGetAllTasks = HttpRequest.newBuilder()
                .GET()
                .uri(uri)
                .build();
        HttpResponse<String> response = client.send(httpRequestGetAllTasks, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());
        TypeToken<List<Selftask>> token = new TypeToken<>() {
        };
        List<Selftask> tasks = gson.fromJson(response.body(), token.getType());
        assertEquals(3, tasks.size());

        Selftask returnedTasks1 = tasks.get(0);
        assertEquals(1, returnedTasks1.getId());
        assertEquals("name1", returnedTasks1.getName());

        Selftask returnedTasks2 = tasks.get(1);
        assertEquals(2, returnedTasks2.getId());
        assertEquals("name2", returnedTasks2.getName());

        Selftask returnedTasks3 = tasks.get(2);
        assertEquals(3, returnedTasks3.getId());
        assertEquals("name2", returnedTasks2.getName());
    }

    @DisplayName("add 3 new Selftasks and get task with id=2")
    @Tag("Selftask")
    @Test
    void add3NewSelftasksAndGetOneByIdTest() throws IOException, InterruptedException {
        Selftask task1 = new Selftask("name1",
                "decr1",
                LocalDateTime.of(2024, 04, 13, 22, 12, 2),
                Duration.ofDays(1));
        Selftask task2 = new Selftask("name2",
                "decr2",
                LocalDateTime.of(2025, 04, 13, 22, 12, 2),
                Duration.ofDays(1));
        Selftask task3 = new Selftask("name3",
                "decr3",
                LocalDateTime.of(2026, 04, 13, 22, 12, 2),
                Duration.ofDays(1));
        uri = URI.create("http://localhost:8080/tasks");
        HttpRequest httpRequestAddTask1 = HttpRequest.newBuilder()
                .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(task1)))
                .uri(uri)
                .build();
        HttpRequest httpRequestAddTask2 = HttpRequest.newBuilder()
                .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(task2)))
                .uri(uri)
                .build();
        HttpRequest httpRequestAddTask3 = HttpRequest.newBuilder()
                .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(task3)))
                .uri(uri)
                .build();
        client.send(httpRequestAddTask1, HttpResponse.BodyHandlers.ofString());
        client.send(httpRequestAddTask2, HttpResponse.BodyHandlers.ofString());
        client.send(httpRequestAddTask3, HttpResponse.BodyHandlers.ofString());

        URI uriToGetId = URI.create("http://localhost:8080/tasks/2");
        HttpRequest httpRequestGetById = HttpRequest.newBuilder()
                .GET()
                .uri(uriToGetId)
                .build();
        HttpResponse<String> response = client.send(httpRequestGetById, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());
        Selftask returnedTask = gson.fromJson(response.body(), Selftask.class);
        assertEquals(2, returnedTask.getId());
        assertEquals("name2", returnedTask.getName());
    }

    @DisplayName("add new Selftask and get task with bad id")
    @Tag("Selftask")
    @Test
    void addNewSelftaskAndGetOneByBadIdTest() throws IOException, InterruptedException {
        Selftask task1 = new Selftask("name1",
                "decr1",
                LocalDateTime.of(2024, 04, 13, 22, 12, 2),
                Duration.ofDays(1));
        uri = URI.create("http://localhost:8080/tasks");
        HttpRequest httpRequestAddTask1 = HttpRequest.newBuilder()
                .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(task1)))
                .uri(uri)
                .build();
        client.send(httpRequestAddTask1, HttpResponse.BodyHandlers.ofString());

        URI uriToGetId = URI.create("http://localhost:8080/tasks/20");
        HttpRequest httpRequestGetById = HttpRequest.newBuilder()
                .GET()
                .uri(uriToGetId)
                .build();
        HttpResponse<String> response = client.send(httpRequestGetById, HttpResponse.BodyHandlers.ofString());
        assertEquals(404, response.statusCode());
    }

    @DisplayName("try to add Selftask with intersection by time")
    @Tag("Selftask")
    @Test
    void addSelftaskWithIntersection() throws InterruptedException, IOException {
        Selftask task1 = new Selftask("name1",
                "decr1",
                LocalDateTime.of(2024, 04, 13, 22, 12, 2),
                Duration.ofDays(1));
        Selftask task2 = new Selftask("name2",
                "decr2",
                LocalDateTime.of(2024, 04, 13, 22, 12, 2),
                Duration.ofDays(1));
        uri = URI.create("http://localhost:8080/tasks");
        HttpRequest httpRequestAddTask1 = HttpRequest.newBuilder()
                .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(task1)))
                .uri(uri)
                .build();
        HttpRequest httpRequestAddTask2 = HttpRequest.newBuilder()
                .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(task2)))
                .uri(uri)
                .build();
        HttpResponse<String> response1 = client.send(httpRequestAddTask1, HttpResponse.BodyHandlers.ofString());
        HttpResponse<String> response2 = client.send(httpRequestAddTask2, HttpResponse.BodyHandlers.ofString());
        assertEquals(201, response1.statusCode());
        assertEquals(406, response2.statusCode());
    }

    @DisplayName("try to add Selftask with empty fields")
    @Tag("Selftask")
    @Test
    void addSelfTaskWithEmptyFieldsTest() throws IOException, InterruptedException {
        Selftask task1 = new Selftask(null,
                "decr1",
                LocalDateTime.of(2024, 04, 13, 22, 12, 2),
                Duration.ofDays(1));
        uri = URI.create("http://localhost:8080/tasks");
        HttpRequest httpRequestAddTask1 = HttpRequest.newBuilder()
                .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(task1)))
                .uri(uri)
                .build();
        HttpResponse<String> response1 = client.send(httpRequestAddTask1, HttpResponse.BodyHandlers.ofString());
        assertEquals(405, response1.statusCode());
    }

    @DisplayName("add new Selftask and get task with broken id")
    @Tag("Selftask")
    @Test
    void addNewSelftaskAndGetOneByBrokendTest() throws IOException, InterruptedException {
        Selftask task1 = new Selftask("name1",
                "decr1",
                LocalDateTime.of(2024, 04, 13, 22, 12, 2),
                Duration.ofDays(1));
        uri = URI.create("http://localhost:8080/tasks");
        HttpRequest httpRequestAddTask1 = HttpRequest.newBuilder()
                .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(task1)))
                .uri(uri)
                .build();
        client.send(httpRequestAddTask1, HttpResponse.BodyHandlers.ofString());

        URI uriToGetId = URI.create("http://localhost:8080/tasks/2sxax0");
        HttpRequest httpRequestGetById = HttpRequest.newBuilder()
                .GET()
                .uri(uriToGetId)
                .build();
        HttpResponse<String> response = client.send(httpRequestGetById, HttpResponse.BodyHandlers.ofString());
        assertEquals(405, response.statusCode());
    }

    @DisplayName("add new Selftask to bad URI")
    @Tag("Selftask")
    @Test
    void addToBadURITest() throws IOException, InterruptedException {
        Selftask task1 = new Selftask("name1",
                "decr1",
                LocalDateTime.of(2024, 04, 13, 22, 12, 2),
                Duration.ofDays(1));
        uri = URI.create("http://localhost:8080/task");
        HttpRequest httpRequestAddTask1 = HttpRequest.newBuilder()
                .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(task1)))
                .uri(uri)
                .build();
        HttpResponse<String> response1 = client.send(httpRequestAddTask1, HttpResponse.BodyHandlers.ofString());
        assertEquals(405, response1.statusCode());
    }

    @DisplayName("add new Selftask and update it")
    @Tag("Selftask")
    @Test
    void updateSelftaskTest() throws IOException, InterruptedException {
        Selftask task = new Selftask("name1",
                "decr1",
                LocalDateTime.of(2024, 04, 13, 22, 12, 2),
                Duration.ofDays(1));
        uri = URI.create("http://localhost:8080/tasks");
        HttpRequest httpRequestAddTask = HttpRequest.newBuilder()
                .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(task)))
                .uri(uri)
                .build();
        HttpResponse<String> response = client.send(httpRequestAddTask, HttpResponse.BodyHandlers.ofString());
        assertEquals(201, response.statusCode());
        URI uriToGetId = URI.create("http://localhost:8080/tasks/1");
        HttpRequest httpRequestGetById = HttpRequest.newBuilder()
                .GET()
                .uri(uriToGetId)
                .build();
        response = client.send(httpRequestGetById, HttpResponse.BodyHandlers.ofString());
        Selftask returnedTask = gson.fromJson(response.body(), Selftask.class);
        assertEquals(1, returnedTask.getId());
        assertEquals("name1", returnedTask.getName());

        returnedTask.setName("updatedName");
        HttpRequest httpRequestUpdateTask = HttpRequest.newBuilder()
                .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(returnedTask)))
                .uri(uri)
                .build();
        response = client.send(httpRequestUpdateTask, HttpResponse.BodyHandlers.ofString());
        Selftask updatedTask = gson.fromJson(response.body(), Selftask.class);
        assertEquals(201, response.statusCode());
        assertEquals(1, updatedTask.getId());
        assertEquals("updatedName", updatedTask.getName());
    }

    @DisplayName("add Selftasks and delete one")
    @Tag("Selftask")
    @Test
    void addSelftasksAndDeleteOneTest() throws IOException, InterruptedException {
        Selftask task1 = new Selftask("name1",
                "decr1",
                LocalDateTime.of(2024, 04, 13, 22, 12, 2),
                Duration.ofDays(1));
        Selftask task2 = new Selftask("name2",
                "decr2",
                LocalDateTime.of(2025, 04, 13, 22, 12, 2),
                Duration.ofDays(1));
        Selftask task3 = new Selftask("name3",
                "decr3",
                LocalDateTime.of(2026, 04, 13, 22, 12, 2),
                Duration.ofDays(1));
        uri = URI.create("http://localhost:8080/tasks");
        HttpRequest httpRequestAddTask1 = HttpRequest.newBuilder()
                .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(task1)))
                .uri(uri)
                .build();
        HttpRequest httpRequestAddTask2 = HttpRequest.newBuilder()
                .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(task2)))
                .uri(uri)
                .build();
        HttpRequest httpRequestAddTask3 = HttpRequest.newBuilder()
                .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(task3)))
                .uri(uri)
                .build();
        client.send(httpRequestAddTask1, HttpResponse.BodyHandlers.ofString());
        client.send(httpRequestAddTask2, HttpResponse.BodyHandlers.ofString());
        client.send(httpRequestAddTask3, HttpResponse.BodyHandlers.ofString());

        URI uriToDelete = URI.create("http://localhost:8080/tasks/2");
        HttpRequest httpRequestUpdateTask = HttpRequest.newBuilder()
                .DELETE()
                .uri(uriToDelete)
                .build();
        HttpResponse<String> response = client.send(httpRequestUpdateTask, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());

        URI uriToGetId = URI.create("http://localhost:8080/tasks/2");
        HttpRequest httpRequestGetById = HttpRequest.newBuilder()
                .GET()
                .uri(uriToGetId)
                .build();
        response = client.send(httpRequestGetById, HttpResponse.BodyHandlers.ofString());
        assertEquals(404, response.statusCode());

        uriToGetId = URI.create("http://localhost:8080/tasks/1");
        httpRequestGetById = HttpRequest.newBuilder()
                .GET()
                .uri(uriToGetId)
                .build();
        response = client.send(httpRequestGetById, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());

        uriToGetId = URI.create("http://localhost:8080/tasks/3");
        httpRequestGetById = HttpRequest.newBuilder()
                .GET()
                .uri(uriToGetId)
                .build();
        response = client.send(httpRequestGetById, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());
    }

    @DisplayName("add Selftasks and delete bad id")
    @Tag("Selftask")
    @Test
    void addSelftasksAndDeleteBadIdTest() throws IOException, InterruptedException {
        Selftask task1 = new Selftask("name1",
                "decr1",
                LocalDateTime.of(2024, 04, 13, 22, 12, 2),
                Duration.ofDays(1));
        uri = URI.create("http://localhost:8080/tasks");
        HttpRequest httpRequestAddTask1 = HttpRequest.newBuilder()
                .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(task1)))
                .uri(uri)
                .build();
        client.send(httpRequestAddTask1, HttpResponse.BodyHandlers.ofString());

        URI uriToDelete = URI.create("http://localhost:8080/tasks/20");
        HttpRequest httpRequestUpdateTask = HttpRequest.newBuilder()
                .DELETE()
                .uri(uriToDelete)
                .build();
        HttpResponse<String> response = client.send(httpRequestUpdateTask, HttpResponse.BodyHandlers.ofString());
        assertEquals(404, response.statusCode());

        uriToDelete = URI.create("http://localhost:8080/tasks/2ыф0");
        httpRequestUpdateTask = HttpRequest.newBuilder()
                .DELETE()
                .uri(uriToDelete)
                .build();
        response = client.send(httpRequestUpdateTask, HttpResponse.BodyHandlers.ofString());
        assertEquals(405, response.statusCode());

        URI uriToGetId = URI.create("http://localhost:8080/tasks/1");
        HttpRequest httpRequestGetById = HttpRequest.newBuilder()
                .GET()
                .uri(uriToGetId)
                .build();
        response = client.send(httpRequestGetById, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());
    }

    @DisplayName("get prioritized list")
    @Tag("Prioritized")
    @Test
    void getPriorotizedTest() throws IOException, InterruptedException {
        Selftask task1 = new Selftask("name1",
                "decr1",
                LocalDateTime.of(2024, 04, 13, 22, 12, 2),
                Duration.ofDays(1));
        Selftask task2 = new Selftask("name2",
                "decr2",
                LocalDateTime.of(2023, 04, 13, 22, 12, 2),
                Duration.ofDays(1));
        Selftask task3 = new Selftask("name3",
                "decr3",
                LocalDateTime.of(2025, 04, 13, 22, 12, 2),
                Duration.ofDays(1));
        Selftask task4 = new Selftask("name3",
                "decr3",
                LocalDateTime.of(2020, 04, 13, 22, 12, 2),
                Duration.ofDays(1));

        uri = URI.create("http://localhost:8080/tasks");
        HttpRequest httpRequestAddTask1 = HttpRequest.newBuilder()
                .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(task1))).uri(uri).build();
        HttpRequest httpRequestAddTask2 = HttpRequest.newBuilder()
                .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(task2))).uri(uri).build();
        HttpRequest httpRequestAddTask3 = HttpRequest.newBuilder()
                .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(task3))).uri(uri).build();
        HttpRequest httpRequestAddTask4 = HttpRequest.newBuilder()
                .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(task4))).uri(uri).build();

        client.send(httpRequestAddTask1, HttpResponse.BodyHandlers.ofString());
        client.send(httpRequestAddTask2, HttpResponse.BodyHandlers.ofString());
        client.send(httpRequestAddTask3, HttpResponse.BodyHandlers.ofString());
        client.send(httpRequestAddTask4, HttpResponse.BodyHandlers.ofString());

        URI uriPrioritezed = URI.create("http://localhost:8080/prioritized");
        HttpRequest httpRequestPrioritized = HttpRequest.newBuilder()
                .GET().uri(uriPrioritezed).build();

        HttpResponse<String> response = client.send(httpRequestPrioritized, HttpResponse.BodyHandlers.ofString());
        System.out.println(response.body());
        assertEquals(200, response.statusCode());
        TypeToken<List<Task>> taskToken = new TypeToken<>() {
        };
        List<Task> tasks = gson.fromJson(response.body(), taskToken.getType());
        assertEquals(4, tasks.size());

        assertEquals(4, tasks.get(0).getId());
        assertEquals(2, tasks.get(1).getId());
        assertEquals(1, tasks.get(2).getId());
        assertEquals(3, tasks.get(3).getId());

    }

}