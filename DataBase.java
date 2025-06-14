import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class DataBase {
    private static Map<String, List<List<Object>>> workbook;
    private static List<Object> taskHead;
    private static List<Object> empHead;
    private static ExcelTransformer excelTransformer = new ExcelTransformer();
    private static ArrayList<Employee> arrayEmployees = new ArrayList<Employee>();
    private static ArrayList<Task> arrayTasks = new ArrayList<Task>();
    private static int globalDay = 1;
    public static final String TASKS = "Tasks";
    public static final String EMPLOYEES = "Employees";
    public static final String INPUT = "C:\\Users\\slobo\\IdeaProjects\\Threads\\src\\main\\resources\\inputData.xlsx";
    public static final String OUTPUT = "C:\\Users\\slobo\\IdeaProjects\\Threads\\src\\main\\resources\\outputData.xlsx";


    public synchronized static ArrayList<Employee> getArrayEmployees() {
        return arrayEmployees;
    }
    public synchronized static ArrayList<Task> getArrayTasks() {
        return arrayTasks;
    }

    public DataBase() {
        try {
            workbook = excelTransformer.readExcel(INPUT);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        taskHead = workbook.get(TASKS).removeFirst();
        empHead = workbook.get(EMPLOYEES).removeFirst();

        // Загрузка сотрудников
        for (List<Object> employee : workbook.get(EMPLOYEES)) {
            DataBase.addEmployee(new Employee((int) employee.get(0), String.valueOf(employee.get(1))));
        }

        // Загрузка задач
        for (List<Object> task : workbook.get(TASKS)) {
            DataBase.addTask(new Task((int)task.get(0), String.valueOf(task.get(1)), (int)task.get(2)));
        }
    }


    public static synchronized Task getTask() {
        for (Task task : arrayTasks) {
            if (task.getCondition() == 0) {
                task.setCondition(1);
                return task;
            }
        }
        return null;
    }

    public static void addEmployee(Employee employee) {
        arrayEmployees.add(employee);
    }
    public static void addTask(Task task) {
        arrayTasks.add(task);
    }

    public static synchronized boolean hasTasks() {
        for (Task task : arrayTasks) {
            if (task.getCondition() == 0) {
                return true;
            }
        }
        return false;
    }

    public static void workOneHour(int id) {
        List<Object> employee = workbook.get(EMPLOYEES).get(id-1);
        employee.set(2, (int) employee.get(2) + 1);
    }

    public static List<Object> getEmpHead() {
        return empHead;
    }
    public static List<Object> getTaskHead() {
        return taskHead;
    }
    public synchronized static int getAndIncrementDay() {
        return globalDay++;
    }
    public synchronized static int getCurrentDay() {
        return globalDay;
    }
    public static Map<String, List<List<Object>>> getWorkbook() {
        return workbook;
    }

    public static void saveToExcel(){
        try {
            excelTransformer.writeExcel(OUTPUT, workbook);
        } catch (IOException ioException){
            System.out.println("Error: " + ioException.getMessage());
        }

    }
}
