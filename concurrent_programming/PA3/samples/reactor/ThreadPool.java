/**
 * A Pool where the Worker Threads are placed.
 * This pool contains an inner TaskQueue, from which the Worker Threads extract Tasks and
 * executeTask them.
 * The Thread Pool provides functionaliry for inserting Tasks into the Task Queue, starting
 * and stopping the Worker Threads.
 */
public class ThreadPool {
    protected Thread[] _pool;
    protected TaskQueue _taskQueue;
    protected volatile boolean _shouldRun;
    protected boolean _started;

    /**
     * Implementation of the Worker Thread.
     * These threads, when activated, constantly trying to extract Tasks from
     * the TaskQueue; when a Task is extracted, they executeTask it by invoking Task.executeTask();
     */
    private class WorkerThread extends Thread {
        private WorkerThread(String name) {
            //@todo setName(name);
            super(name);
        }

        public void run() {
            while (_shouldRun) {
                try {
                    Task task = _taskQueue.getTask();
                    task.executeTask();
                    System.out.println(getName() + ": executed");
                } catch (InterruptedException i) {
                    _shouldRun = false;
                } catch (TaskFailedException tf) {
                    _shouldRun = false;
                } catch (Exception e) {
                    e.printStackTrace(System.err);
                }
            }
        }
    }

    /**
     * Creates a new ThreadPool object
     * @param size the number of Worker Threads in the ThreadPool
     */
    public ThreadPool(int size) {
        _taskQueue = new TaskQueue();
        _pool = new WorkerThread[size];
        _shouldRun = true;
        _started = false;
    }

    /**
     * Starts all the Worker Threads in the ThreadPool.
     * If the Task Queue is empty, the Worker Threads will be waiting for Tasks to be entered.
     */
    public void startPool() {
        if (!_started) {
            _started = true;

            for (int i = 0; i < _pool.length; i++) {
                _pool[i] = new WorkerThread("WorkerThread_" + i);
                _pool[i].start();
            }
        }
    }

    /**
     * Causes the Worker Threads to stop
     */
    public void stopPool() {
        _shouldRun = false;

        for (int i = 0; i < _pool.length; i++) {
            _pool[i].interrupt();
        }
    }

    /**
     * Adds a Task to the ThreadPool's TaskQueue
     * @param task the Task to be added
     */
    public void addTask(Task task) {
        _taskQueue.addTask(task);
    }

    public int size() {
        int retVal = 0;

        for (int i = 0; i < _pool.length; i++) {
            WorkerThread worker = (WorkerThread) _pool[i];

            if ((worker != null) && worker.isAlive()) {
                retVal++;
            }
        }

        return retVal;
    }

    public void join(int millisec) {
        if (millisec > 0) {
            for (int i = 0; i < _pool.length; i++) {
                WorkerThread worker = (WorkerThread) _pool[i];

                if (worker != null) {
                    try {
                        worker.join(millisec);
                    } catch (InterruptedException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }
            }
        } else {
            while (!_taskQueue.isEmpty()) {
                try {
                    wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
