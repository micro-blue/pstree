package notifier;

import org.jctools.queues.SpscLinkedQueue;

/**
 * @author hjk
 * @Description
 * @date 2021/3/29 14:16
 */
public abstract class Notifier implements Runnable {
    protected volatile boolean isWaiting;
    protected SpscLinkedQueue unResolvedQueue;
    protected SpscLinkedQueue leftResolvedQueue;
    protected SpscLinkedQueue rightResolvedQueue;
    //todo 根据是否是叶子节点采取不同行为
    protected Notifier parentNotifier;
    protected Notifier leftNotifier;
    protected Notifier righNotifier;
    //所需计算的部分,标识该Calculator只需计算哪一部分
    protected int assignedPart;
    protected long tmpMergedValue;

    public abstract void receiveFromParent(Long val);
    public abstract void receiveFromChild( Long val1,Long val2);
}
