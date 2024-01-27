import java.util.HashMap;
import java.util.List;

/*
    Комментарий к реализации:
    - Все типы тасков реализованы в одном классе Task за счет использования отдельного поля type типа enum Status;
    - Для генерации первичных тасков пользователем предназначены статические методы makeEpicTask, makeSubTask,
     makeStandardTask
    - Хранение тасков внутри Manager реализовано с помощью HashMap, куда кладется id и объект DeepTask, представляющий
    собой поля Task без id + массив для хранения подзадач. Наружу возвращаются таски, в которые копируется информация из
    DeepTask.
    - Т.к. в ТЗ требуется в менеджер отправлять сами объекты Task, то именно это и реализовано.
*/
public class Main {
    public static void main(String[] args) {
        Manager manager = new Manager(new HashMap<>(), 1);
        Task task1 = manager.add(Task.makeStandardTask("сходить в магазин", "хлеб, колбаса, сыр"));
        Task task2 = manager.add(Task.makeEpicTask("сходить на рыбалку", "удочки, черви, водка"));
        Task task3 = manager.add(Task.makeSubTask("купить удочки", "спиннинг", task2.id));
        Task task4 = manager.add(Task.makeSubTask("набрать червей", "не забыть опарыш", task2.id));
        Task task5 = manager.add(Task.makeSubTask("водка", "Белуга 0,7", task2.id));
        List<Task> list = manager.getAll();
        System.out.println(list);
        manager.delete(task1.id);
        manager.update(task1.id,Task.makeStandardTask(null,"хлеб, сыр и молоко",Status.IN_PROGRESS));
    }
}
