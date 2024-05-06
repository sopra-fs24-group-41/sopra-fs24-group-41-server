package ch.uzh.ifi.hase.soprafs24.timer;

import java.util.Timer;
import java.util.TimerTask;

public class mockTimer extends Timer implements ownTimer {
    @Override
    public void scheduleAtFixedRate(TimerTask task, long delay, long period) {
        super.scheduleAtFixedRate(task, delay, period/10);
    }
}