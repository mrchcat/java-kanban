package ru.yandex.practicum.taskmanager;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import org.junit.jupiter.api.*;
import ru.yandex.practicum.taskmanager.service.TaskManager;
import ru.yandex.practicum.taskmanager.tasks.Epictask;
import ru.yandex.practicum.taskmanager.tasks.Selftask;
import ru.yandex.practicum.taskmanager.tasks.Status;
import ru.yandex.practicum.taskmanager.tasks.Subtask;
import ru.yandex.practicum.taskmanager.utils.DurationAdapter;
import ru.yandex.practicum.taskmanager.utils.LocalDateTimeAdapter;
import ru.yandex.practicum.taskmanager.utils.TaskDTO;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class HttpTaskServerTest {
    HttpTaskServer httpTaskServer;
    TaskManager taskManager;
    HttpClient client;
    URI uri;
    Gson gson;

    @BeforeEach
    void initAndStart() throws IOException {
        Path path = Path.of("src/ru/yandex/practicum/taskmanager/repository/history.csv");
        Files.deleteIfExists(path);
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
                LocalDateTime.of(2024, 4, 13, 22, 12, 2),
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
                LocalDateTime.of(2024, 4, 13, 22, 12, 2),
                Duration.ofDays(1));
        Selftask task2 = new Selftask("name2",
                "decr2",
                LocalDateTime.of(2025, 4, 13, 22, 12, 2),
                Duration.ofDays(1));
        Selftask task3 = new Selftask("name3",
                "decr3",
                LocalDateTime.of(2026, 4, 13, 22, 12, 2),
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
                LocalDateTime.of(2024, 4, 13, 22, 12, 2),
                Duration.ofDays(1));
        Selftask task2 = new Selftask("name2",
                "decr2",
                LocalDateTime.of(2025, 4, 13, 22, 12, 2),
                Duration.ofDays(1));
        Selftask task3 = new Selftask("name3",
                "decr3",
                LocalDateTime.of(2026, 4, 13, 22, 12, 2),
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
                LocalDateTime.of(2024, 4, 13, 22, 12, 2),
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
                LocalDateTime.of(2024, 4, 13, 22, 12, 2),
                Duration.ofDays(1));
        Selftask task2 = new Selftask("name2",
                "decr2",
                LocalDateTime.of(2024, 4, 13, 22, 12, 2),
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
                LocalDateTime.of(2024, 4, 13, 22, 12, 2),
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
                LocalDateTime.of(2024, 4, 13, 22, 12, 2),
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
                LocalDateTime.of(2024, 4, 13, 22, 12, 2),
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
                LocalDateTime.of(2024, 4, 13, 22, 12, 2),
                Duration.ofDays(1));
        Selftask task2 = new Selftask("name2",
                "decr2",
                LocalDateTime.of(2025, 4, 13, 22, 12, 2),
                Duration.ofDays(1));
        Selftask task3 = new Selftask("name3",
                "decr3",
                LocalDateTime.of(2026, 4, 13, 22, 12, 2),
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
                LocalDateTime.of(2024, 4, 13, 22, 12, 2),
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

    @DisplayName("add Selftasks from String")
    @Tag("Selftask")
    @Test
    void addSelftaskFromStringTest() throws IOException, InterruptedException {
        URI uri = URI.create("http://localhost:8080/tasks");
        String s1 = "{" +
                "\"name\":\"name1\"," +
                "\"description\":\"decr1\"," +
                "\"duration\":\"PT24H\"}";
        HttpRequest httpRequestAddTask = HttpRequest.newBuilder()
                .POST(HttpRequest.BodyPublishers.ofString(s1))
                .uri(uri)
                .build();
        HttpResponse<String> response = client.send(httpRequestAddTask, HttpResponse.BodyHandlers.ofString());
        assertEquals(201, response.statusCode());
        System.out.println(response.body());
        Selftask task = gson.fromJson(response.body(), Selftask.class);
        System.out.println(task);
        assertEquals("name1", task.getName());
        assertEquals("decr1", task.getDescription());
        assertEquals(Status.NEW, task.getStatus());
        assertNull(task.getStartTime());
        assertEquals(Duration.parse("PT24H"), task.getDuration());
    }

    @DisplayName("add Selftasks from String")
    @Tag("Selftask")
    @Test
    void addSelftaskFromString2Test() throws IOException, InterruptedException {
        URI uri = URI.create("http://localhost:8080/tasks");
        String s1 = "{" +
                "\"name\":\"name1\"," +
                "\"description\":\"decr1\"," +
                "\"status\":\"DONE\"," +
                "\"startTime\":\"2024-04-13T22:12:02\"," +
                "\"duration\":\"PT24H\"}";
        HttpRequest httpRequestAddTask = HttpRequest.newBuilder()
                .POST(HttpRequest.BodyPublishers.ofString(s1))
                .uri(uri)
                .build();
        HttpResponse<String> response = client.send(httpRequestAddTask, HttpResponse.BodyHandlers.ofString());
        assertEquals(201, response.statusCode());
        System.out.println(response.body());
        Selftask task = gson.fromJson(response.body(), Selftask.class);
        System.out.println(task);
        assertEquals("name1", task.getName());
        assertEquals("decr1", task.getDescription());
        assertEquals(Status.NEW, task.getStatus());
        assertEquals(LocalDateTime.parse("2024-04-13T22:12:02", DateTimeFormatter.ISO_LOCAL_DATE_TIME),
                task.getStartTime());
        assertEquals(Duration.parse("PT24H"), task.getDuration());
    }


    @DisplayName("add Selftasks from broken String")
    @Tag("Selftask")
    @Test
    void addSelftaskFromBrokenStringTest() throws IOException, InterruptedException {
        URI uri = URI.create("http://localhost:8080/tasks");
        String s1 = "{" +
                "\"name\":\"name1\"," +
                "\"description\":\"decr1\"," +
                "\"startTime\":\"20240413T221202\"," +
                "\"duration\":\"PT24H\"}";
        HttpRequest httpRequestAddTask = HttpRequest.newBuilder()
                .POST(HttpRequest.BodyPublishers.ofString(s1))
                .uri(uri)
                .build();
        HttpResponse<String> response = client.send(httpRequestAddTask, HttpResponse.BodyHandlers.ofString());
        assertEquals(405, response.statusCode());
    }

    @DisplayName("add Selftasks from broken String")
    @Tag("Selftask")
    @Test
    void addSelftaskFromBrokenString2Test() throws IOException, InterruptedException {
        URI uri = URI.create("http://localhost:8080/tasks");
        String s1 = "{" +
                "\"name\":\"name1\"," +
                "\"description\":\"decr1\"," +
                "}";
        HttpRequest httpRequestAddTask = HttpRequest.newBuilder()
                .POST(HttpRequest.BodyPublishers.ofString(s1))
                .uri(uri)
                .build();
        HttpResponse<String> response = client.send(httpRequestAddTask, HttpResponse.BodyHandlers.ofString());
        assertEquals(405, response.statusCode());
    }

    @DisplayName("get prioritized list")
    @Tag("Prioritized")
    @Test
    void getPriorotizedTest() throws IOException, InterruptedException {
        Selftask task1 = new Selftask("name1",
                "decr1",
                LocalDateTime.of(2024, 4, 13, 22, 12, 2),
                Duration.ofDays(1));
        Selftask task2 = new Selftask("name2",
                "decr2",
                LocalDateTime.of(2023, 4, 13, 22, 12, 2),
                Duration.ofDays(1));
        Selftask task3 = new Selftask("name3",
                "decr3",
                LocalDateTime.of(2025, 4, 13, 22, 12, 2),
                Duration.ofDays(1));
        Selftask task4 = new Selftask("name3",
                "decr3",
                LocalDateTime.of(2020, 4, 13, 22, 12, 2),
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
        assertEquals(200, response.statusCode());
        TypeToken<List<TaskDTO>> taskToken = new TypeToken<>() {
        };
        List<TaskDTO> tasks = gson.fromJson(response.body(), taskToken.getType());
        assertEquals(4, tasks.size());

        assertEquals(4, tasks.get(0).id);
        assertEquals(2, tasks.get(1).id);
        assertEquals(1, tasks.get(2).id);
        assertEquals(3, tasks.get(3).id);
    }

    @DisplayName("get history list")
    @Tag("History")
    @Test
    void getHistoryTest() throws IOException, InterruptedException {
        Selftask task1 = new Selftask("name1", "decr1",
                LocalDateTime.of(2024, 4, 13, 22, 12, 2), Duration.ofDays(1));
        Selftask task2 = new Selftask("name2", "decr2",
                LocalDateTime.of(2023, 4, 13, 22, 12, 2), Duration.ofDays(1));
        Selftask task3 = new Selftask("name3", "decr3",
                LocalDateTime.of(2025, 4, 13, 22, 12, 2), Duration.ofDays(1));
        Selftask task4 = new Selftask("name4", "decr4",
                LocalDateTime.of(2020, 4, 13, 22, 12, 2), Duration.ofDays(1));

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

        URI uriToGetId2 = URI.create("http://localhost:8080/tasks/2");
        URI uriToGetId3 = URI.create("http://localhost:8080/tasks/3");
        HttpRequest httpRequestGetById2 = HttpRequest.newBuilder().GET().uri(uriToGetId2).build();
        HttpRequest httpRequestGetById3 = HttpRequest.newBuilder().GET().uri(uriToGetId3).build();

        client.send(httpRequestGetById2, HttpResponse.BodyHandlers.ofString());
        client.send(httpRequestGetById3, HttpResponse.BodyHandlers.ofString());

        URI uriHistory = URI.create("http://localhost:8080/history");
        HttpRequest httpRequestHistory = HttpRequest.newBuilder().GET().uri(uriHistory).build();

        HttpResponse<String> response = client.send(httpRequestHistory, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());
        TypeToken<List<TaskDTO>> taskToken = new TypeToken<>() {
        };
        List<TaskDTO> tasks = gson.fromJson(response.body(), taskToken.getType());
        assertEquals(2, tasks.size());
        assertEquals(3, tasks.get(0).id);
        assertEquals(2, tasks.get(1).id);
    }

    @DisplayName("add 3 new Epictasks and get all Epictasks")
    @Tag("Epictask")
    @Test
    void add3NewEpictasksTest() throws IOException, InterruptedException {
        Epictask task1 = new Epictask("name1", "decr1", null, null);
        Epictask task2 = new Epictask("name2", "decr2", null, null);
        Epictask task3 = new Epictask("name3", "decr3", null, null);
        uri = URI.create("http://localhost:8080/epics");
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
        TypeToken<List<Epictask>> token = new TypeToken<>() {
        };
        List<Epictask> tasks = gson.fromJson(response.body(), token.getType());
        assertEquals(3, tasks.size());

        Epictask returnedTasks1 = tasks.get(0);
        assertEquals(1, returnedTasks1.getId());
        assertEquals("name1", returnedTasks1.getName());

        Epictask returnedTasks2 = tasks.get(1);
        assertEquals(2, returnedTasks2.getId());
        assertEquals("name2", returnedTasks2.getName());

        Epictask returnedTasks3 = tasks.get(2);
        assertEquals(3, returnedTasks3.getId());
        assertEquals("name3", returnedTasks3.getName());
    }

    @DisplayName("add 3 new Epictasks and get one by id")
    @Tag("Epictask")
    @Test
    void add3NewEpictasksAndGetOneByIdTest() throws IOException, InterruptedException {
        Epictask task1 = new Epictask("name1", "decr1", null, null);
        Epictask task2 = new Epictask("name2", "decr2", null, null);
        Epictask task3 = new Epictask("name3", "decr3", null, null);
        uri = URI.create("http://localhost:8080/epics");
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

        URI uriToGetId = URI.create("http://localhost:8080/epics/2");
        HttpRequest httpRequestGetById = HttpRequest.newBuilder()
                .GET()
                .uri(uriToGetId)
                .build();
        HttpResponse<String> response = client.send(httpRequestGetById, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());
        Epictask task = gson.fromJson(response.body(), Epictask.class);
        assertEquals(2, task.getId());
    }


    @DisplayName("add 3 new Epictasks and delete one by id")
    @Tag("Epictask")
    @Test
    void add3NewEpictasksAndDeleteOneByIdTest() throws IOException, InterruptedException {
        Epictask task1 = new Epictask("name1", "decr1", null, null);
        Epictask task2 = new Epictask("name2", "decr2", null, null);
        Epictask task3 = new Epictask("name3", "decr3", null, null);
        uri = URI.create("http://localhost:8080/epics");
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

        URI uriDeleteById = URI.create("http://localhost:8080/epics/2");
        HttpRequest httpDeleteById = HttpRequest.newBuilder()
                .DELETE()
                .uri(uriDeleteById)
                .build();
        HttpResponse<String> response = client.send(httpDeleteById, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());

        URI uriToGetId = URI.create("http://localhost:8080/epics/2");
        HttpRequest httpRequestGetById = HttpRequest.newBuilder()
                .GET()
                .uri(uriToGetId)
                .build();
        response = client.send(httpRequestGetById, HttpResponse.BodyHandlers.ofString());
        assertEquals(404, response.statusCode());
    }

    @DisplayName("add 3 new Subtasks and get all Subtasks")
    @Tag("Subtasks")
    @Test
    void add3NewSubtasksTest() throws IOException, InterruptedException {
        Epictask epictask = new Epictask("name1", "decr1", null, null);
        URI uriEpic = URI.create("http://localhost:8080/epics");
        HttpRequest httpRequestAddEpic = HttpRequest.newBuilder()
                .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(epictask)))
                .uri(uriEpic)
                .build();
        HttpResponse<String> response = client.send(httpRequestAddEpic, HttpResponse.BodyHandlers.ofString());
        assertEquals(201, response.statusCode());
        epictask = gson.fromJson(response.body(), Epictask.class);
        int epicId = epictask.getId();

        URI uriSub = URI.create("http://localhost:8080/subtasks");
        Subtask subtask1 = new Subtask("name2", "decr2", null, Duration.ofDays(1), epicId);
        Subtask subtask2 = new Subtask("name3", "decr3", null, Duration.ofDays(1), epicId);
        Subtask subtask3 = new Subtask("name4", "decr4", null, Duration.ofDays(1), epicId);

        HttpRequest httpRequestAddSub1 = HttpRequest.newBuilder()
                .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(subtask1)))
                .uri(uriSub)
                .build();
        HttpRequest httpRequestAddSub2 = HttpRequest.newBuilder()
                .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(subtask2)))
                .uri(uriSub)
                .build();
        HttpRequest httpRequestAddSub3 = HttpRequest.newBuilder()
                .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(subtask3)))
                .uri(uriSub)
                .build();

        client.send(httpRequestAddSub1, HttpResponse.BodyHandlers.ofString());
        client.send(httpRequestAddSub2, HttpResponse.BodyHandlers.ofString());
        client.send(httpRequestAddSub3, HttpResponse.BodyHandlers.ofString());

        HttpRequest httpRequestGetAllSubTasks = HttpRequest.newBuilder()
                .GET()
                .uri(uriSub)
                .build();
        response = client.send(httpRequestGetAllSubTasks, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());
        TypeToken<List<Subtask>> token = new TypeToken<>() {
        };
        List<Subtask> tasks = gson.fromJson(response.body(), token.getType());
        System.out.println(tasks);
        assertEquals(3, tasks.size());

        Subtask returnedTasks1 = tasks.get(0);
        assertEquals(2, returnedTasks1.getId());
        assertEquals("name2", returnedTasks1.getName());

        Subtask returnedTasks2 = tasks.get(1);
        assertEquals(3, returnedTasks2.getId());
        assertEquals("name3", returnedTasks2.getName());

        Subtask returnedTasks3 = tasks.get(2);
        assertEquals(4, returnedTasks3.getId());
        assertEquals("name4", returnedTasks3.getName());
    }

    @DisplayName("add 3 new Subtasks and get one by Id")
    @Tag("Subtasks")
    @Test
    void add3NewSubtasksAndGetOneByIdTest() throws IOException, InterruptedException {
        Epictask epictask = new Epictask("name1", "decr1", null, null);
        URI uriEpic = URI.create("http://localhost:8080/epics");
        HttpRequest httpRequestAddEpic = HttpRequest.newBuilder()
                .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(epictask)))
                .uri(uriEpic)
                .build();
        HttpResponse<String> response = client.send(httpRequestAddEpic, HttpResponse.BodyHandlers.ofString());
        assertEquals(201, response.statusCode());
        epictask = gson.fromJson(response.body(), Epictask.class);
        int epicId = epictask.getId();

        URI uriSub = URI.create("http://localhost:8080/subtasks");
        Subtask subtask1 = new Subtask("name2", "decr2", null, Duration.ofDays(1), epicId);
        Subtask subtask2 = new Subtask("name3", "decr3", null, Duration.ofDays(1), epicId);
        Subtask subtask3 = new Subtask("name4", "decr4", null, Duration.ofDays(1), epicId);

        HttpRequest httpRequestAddSub1 = HttpRequest.newBuilder()
                .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(subtask1)))
                .uri(uriSub)
                .build();
        HttpRequest httpRequestAddSub2 = HttpRequest.newBuilder()
                .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(subtask2)))
                .uri(uriSub)
                .build();
        HttpRequest httpRequestAddSub3 = HttpRequest.newBuilder()
                .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(subtask3)))
                .uri(uriSub)
                .build();

        client.send(httpRequestAddSub1, HttpResponse.BodyHandlers.ofString());
        client.send(httpRequestAddSub2, HttpResponse.BodyHandlers.ofString());
        client.send(httpRequestAddSub3, HttpResponse.BodyHandlers.ofString());

        URI uriSubById2 = URI.create("http://localhost:8080/subtasks/2");
        HttpRequest httpRequestGetSub2 = HttpRequest.newBuilder()
                .GET()
                .uri(uriSubById2)
                .build();
        response = client.send(httpRequestGetSub2, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());
        Subtask returnedTask = gson.fromJson(response.body(), Subtask.class);
        assertEquals(2, returnedTask.getId());
        assertEquals("name2", returnedTask.getName());
    }

    @DisplayName("add 3 new Subtasks and delete one by Id")
    @Tag("Subtasks")
    @Test
    void add3NewSubtasksAndDeleteOneByIdTest() throws IOException, InterruptedException {
        Epictask epictask = new Epictask("name1", "decr1", null, null);
        URI uriEpic = URI.create("http://localhost:8080/epics");
        HttpRequest httpRequestAddEpic = HttpRequest.newBuilder()
                .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(epictask)))
                .uri(uriEpic)
                .build();
        HttpResponse<String> response = client.send(httpRequestAddEpic, HttpResponse.BodyHandlers.ofString());
        assertEquals(201, response.statusCode());
        epictask = gson.fromJson(response.body(), Epictask.class);
        int epicId = epictask.getId();

        URI uriSub = URI.create("http://localhost:8080/subtasks");
        Subtask subtask1 = new Subtask("name2", "decr2", null, Duration.ofDays(1), epicId);
        Subtask subtask2 = new Subtask("name3", "decr3", null, Duration.ofDays(1), epicId);
        Subtask subtask3 = new Subtask("name4", "decr4", null, Duration.ofDays(1), epicId);

        HttpRequest httpRequestAddSub1 = HttpRequest.newBuilder()
                .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(subtask1)))
                .uri(uriSub)
                .build();
        HttpRequest httpRequestAddSub2 = HttpRequest.newBuilder()
                .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(subtask2)))
                .uri(uriSub)
                .build();
        HttpRequest httpRequestAddSub3 = HttpRequest.newBuilder()
                .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(subtask3)))
                .uri(uriSub)
                .build();

        client.send(httpRequestAddSub1, HttpResponse.BodyHandlers.ofString());
        client.send(httpRequestAddSub2, HttpResponse.BodyHandlers.ofString());
        client.send(httpRequestAddSub3, HttpResponse.BodyHandlers.ofString());

        URI uriSubById2 = URI.create("http://localhost:8080/subtasks/2");
        HttpRequest httpRequestDeleteSub2 = HttpRequest.newBuilder()
                .DELETE()
                .uri(uriSubById2)
                .build();
        response = client.send(httpRequestDeleteSub2, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());

        HttpRequest httpRequestGetSub2 = HttpRequest.newBuilder()
                .GET()
                .uri(uriSubById2)
                .build();
        response = client.send(httpRequestGetSub2, HttpResponse.BodyHandlers.ofString());
        assertEquals(404, response.statusCode());
    }
}