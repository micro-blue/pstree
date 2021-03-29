package notifier;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @author hjk
 * @Description
 * @date 2021/3/29 14:24
 */
public class NormalNotifier extends Notifier {
    final Lock lock = new ReentrantLock();
    final Condition overRetry = lock.newCondition();
    private long retryCount;

    public void run() {
        //while (true) {
        lock.lock();
        try {
            while (true) {
                Long value = unResolvedQueue.peek();
                if (value == null) {
                    long i = 0;
                    for (; i < retryCount; i++) {
                        value = unResolvedQueue.peek();
                        if (value != null) {
                            doCalculate(value);
                            leftNotifier.receiveFromParent(value);
                            righNotifier.receiveFromParent(value);
                            break;
                        }

                        Long lValue = leftResolvedQueue.peek();
                        Long rValue = rightResolvedQueue.peek();
                        if (lValue != null && rValue != null) {
                            parentNotifier.receiveFromChild(lValue, rValue);
                            break;
                        }
                    }
                    if (i >= retryCount) {
                        isWaiting = true;
                        overRetry.await();
                        //break;
                    }
                } else {
                    value = unResolvedQueue.poll();
                    doCalculate(value);
                    leftNotifier.receiveFromParent(value);
                    righNotifier.receiveFromParent(value);

                }
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            lock.unlock();
        }
        // leftResolvedQueue
        //}
    }


    @Override
    public void receiveFromParent(Long val) {
        unResolvedQueue.offer(val);
        while (isWaiting) {
            lock.lock();
            try {
                isWaiting = false;
                overRetry.signal();
            } finally {
                lock.unlock();
            }
        }
    }

    @Override
    public void receiveFromChild(Long val1, Long val2) {
        //todo 合并两个值
    }
}
