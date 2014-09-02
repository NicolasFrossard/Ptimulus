package com.ptimulus.event;

import com.ptimulus.PtimulusService;

import java.util.Timer;
import java.util.TimerTask;

/**
 * This class implement a timer that will feed the service with regular event.
 */
public class TimerEvent {

    private PtimulusService ptimulusService;
    private Timer timer;
    private int counter;

    public TimerEvent(PtimulusService ptimulusService)
    {
        this.ptimulusService = ptimulusService;
        this.counter = 0;
        this.timer = new Timer();
        this.timer.scheduleAtFixedRate(task, 0, 1000);
    }

    private TimerTask task = new TimerTask() {
        @Override
        public void run() {
            ptimulusService.timerTick(counter);
            counter++;
        }
    };
}
