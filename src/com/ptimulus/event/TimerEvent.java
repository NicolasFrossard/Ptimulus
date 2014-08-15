package com.ptimulus.event;

import com.ptimulus.PtimulusService;

import java.util.Timer;
import java.util.TimerTask;

/**
 * This class implement a timer that will feed the service with regular event.
 */
public class TimerEvent implements IEvent {

    private PtimulusService ptimulusService;
    private Timer timer;

    public TimerEvent(PtimulusService ptimulusService)
    {
        this.ptimulusService = ptimulusService;
        this.timer = new Timer();
    }

    @Override
    public void startListening() {
        // Run the task each seconds
        timer.scheduleAtFixedRate(task, 0, 1000);
    }

    @Override
    public void stopListening() {
        task.cancel();
    }

    private TimerTask task = new TimerTask() {
        @Override
        public void run() {
            ptimulusService.timerTick();
        }
    };
}
