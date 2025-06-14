import java.lang.Thread;

public class Employee implements Runnable{
    private String name;
    private int id;
    private int workHours;
    private boolean isWorking;

    public Employee(int id, String name) {
        this.id = id;
        this.isWorking = false;
        this.name = name;
        this.workHours = 0;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public int getWorkHours() {
        return workHours;
    }

    public void workOneHour() {workHours++;}

    public boolean getIsWorking() {
        return isWorking;
    }

    public synchronized void setIsWorking(boolean working) {
        isWorking = working;
    }

    @Override
    public void run() {
        try {
            while (DataBase.hasTasks()) {
                Task currTask = DataBase.getTask();
                if (currTask == null) {
                    Thread.sleep(500);
                    continue;
                }

//                System.out.println(name + " начинает задачу " + currTask.getName());
                setIsWorking(true);

                boolean hasTask = true;
                int personalDay = 1;
                while (hasTask && !currTask.isDone()) {
                    int currentGlobalDay = DataBase.getCurrentDay();

                    for (int hour = 1; hour <= 8 && hasTask; hour++) {
                        synchronized (System.out) {  // Синхронизация вывода
                            // Синхронизация задачи
                                if (!currTask.isDone()) {
                                    currTask.doingTaskOneHour();
                                    DataBase.workOneHour(id);
                                    System.out.println("День " + currentGlobalDay +
                                        ", час " + hour + ": " + name +
                                        " работает над " + currTask.getName() +
                                        " (" + currTask.getDoingHours() + "/" +
                                        currTask.getNeededHours() + ")");

                                } else {
                                    currTask.setCondition(2);
                                    currTask = DataBase.getTask();
                                    if (currTask == null) {
                                        hasTask = false;
                                        break;
                                    }
                                    currTask.doingTaskOneHour();
                                    DataBase.workOneHour(id);
                                    System.out.println("День " + currentGlobalDay +
                                        ", час " + hour + ": " + name +
                                        " работает над " + currTask.getName() +
                                        " (" + currTask.getDoingHours() + "/" +
                                        currTask.getNeededHours() + ")");
                                }

                        }

                        Thread.sleep(500);
                    }

                    // Переход на новый день
                    synchronized (DataBase.class) {
                        if (personalDay == DataBase.getCurrentDay()) {
                            DataBase.getAndIncrementDay();
                            System.out.println();
                        }
                        personalDay++;
                    }

                    Thread.sleep(500); // Пауза между днями
                }

//                System.out.println(name + " завершил задачу " + currTask.getName());
                if (hasTask) {
                   currTask.setCondition(2);
                }
                setIsWorking(false);
            }
//            synchronized (System.out) {
//                System.out.println(name + " отдыхает - задач больше нет");
//            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}









































