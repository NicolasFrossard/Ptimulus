/*
 * Copyright (C) 2014 Ptimulus
 * http://www.ptimulus.eu
 * 
 * This file is part of Ptimulus.
 * 
 * Ptimulus is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * Ptimulus is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with Ptimulus.  If not, see <http://www.gnu.org/licenses/>.
 * 
 */

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
