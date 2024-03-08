package service;

import repository.InMemoryMap;
import repository.Repository;
import tasks.Task;
import utils.Generator;
import utils.HistoryManager;
import utils.LinkedHashHistoryManager;
import utils.SerialGenerator;

import java.util.ArrayList;

public class Managers {
    private static final int START_ID_BY_DEFAULT = 1;

    public static TaskManager getDefault() {
        Repository<Integer, Task> tasks = new InMemoryMap<>();
        Repository<Integer, ArrayList<Integer>> subordinates = new InMemoryMap<>();
        Generator generator = new SerialGenerator(START_ID_BY_DEFAULT);
        HistoryManager history = getDefaultHistory();
        return new RegularTaskManager(tasks, subordinates, generator, history);
    }

    public static HistoryManager getDefaultHistory() {
        return new LinkedHashHistoryManager();
    }

}
