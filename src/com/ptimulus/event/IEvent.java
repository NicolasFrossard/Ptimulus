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

/**
 * Interface of a event source class. Allow to enable and disable the event source.
 */
public interface IEvent<DataType> {

    /**
     * Enable the event source.
     */
    public void startListening();

    /**
     * Disable the event source.
     */
    public void stopListening();

    /**
     * Timer tick from the service. Assumed to be 1Hz.
     */
    public void tick(int counter);

    /**
     * Age of the last measure, in milliseconds.
     * @return
     */
    public long dataAge();

    /**
     * The last know measure.
     * @return
     */
    public DataType data();

    /**
     * Tell if we have a valid data already;
     * @return
     */
    public boolean hasData();
}
