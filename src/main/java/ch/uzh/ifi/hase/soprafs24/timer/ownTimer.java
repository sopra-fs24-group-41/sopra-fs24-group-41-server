package ch.uzh.ifi.hase.soprafs24.timer;

import java.util.TimerTask;

public interface ownTimer {
    void cancel();

    void scheduleAtFixedRate(TimerTask task, long delay, long period);
}
