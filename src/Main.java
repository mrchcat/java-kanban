import service.Managers;
import service.TaskManager;

import java.util.ArrayList;

public class Main {
    public static void main(String[] args) {
        TaskManager taskManager = Managers.getDefault();
        ArrayList<Integer> arr = new ArrayList<>();
        arr.add(5);
        for (Integer integer : arr) {
            System.out.println(integer);
        }
    }
}