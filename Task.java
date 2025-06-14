public class Task {
    private int number;
    private String name;
    private int neededHours; // <= 16
    private int condition;//0 - to do; 1 - doing; 2 - done
    private int doingHours = 0;

    public Task(int number, String name, int neededHours) {
        this.condition = 0;
        this.name = name;
        this.neededHours = neededHours;
        this.number = number;
    }

    public synchronized int getNeededHours() {
        return neededHours;
    }
    public synchronized int getNumber() {
        return number;
    }
    public synchronized String getName() {
        return name;
    }
    public synchronized int getCondition() {
        return condition;
    }
    public synchronized int getDoingHours() {
        return doingHours;
    }

    public synchronized void setCondition(int condition) {
        if (condition >=0 && condition <=2) {
            this.condition = condition;
        }
    }

    public synchronized void doingTaskOneHour() {
        if (!isDone()) {
            doingHours++;
        }
    }

    public synchronized boolean isDone() {
        return doingHours >= neededHours;
    }
}
