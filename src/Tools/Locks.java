package Tools;

import java.util.concurrent.locks.Lock;

public record Locks(Lock taskLock, Lock writeLock, Lock centralityLock, Lock timeLock) {
}
