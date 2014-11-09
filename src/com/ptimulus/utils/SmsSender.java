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

package com.ptimulus.utils;

import android.telephony.PhoneNumberUtils;
import android.telephony.SmsManager;
import android.util.Log;
import com.ptimulus.PtimulusService;
import com.ptimulus.log.LogEntryType;

public class SmsSender {

    private final PtimulusService service;
	private final SmsManager smsmanager;

    private String destPhoneNumber1;
    private String destPhoneNumber2;
    private String destPhoneNumber3;

	public SmsSender(PtimulusService service, String destPhoneNumber1, String destPhoneNumber2, String destPhoneNumber3) {
        this.service = service;
        this.destPhoneNumber1 = destPhoneNumber1;
        this.destPhoneNumber2 = destPhoneNumber2;
        this.destPhoneNumber3 = destPhoneNumber3;
		this.smsmanager = SmsManager.getDefault();
	}

    public void SendSMS(String message) {
        try {
            if(destPhoneNumber1 != null)
            {
                smsmanager.sendTextMessage(destPhoneNumber1, null, message, null, null);
                String log = String.format("SMS sent to %s | %s", destPhoneNumber1, message);
                service.relayLog(LogEntryType.SMS, log);
                Log.d("SMS", log);
            }
            if(destPhoneNumber2 != null)
            {
                smsmanager.sendTextMessage(destPhoneNumber2, null, message, null, null);
                String log = String.format("SMS sent to %s | %s", destPhoneNumber2, message);
                service.relayLog(LogEntryType.SMS, log);
                Log.d("SMS", log);
            }
            if(destPhoneNumber3 != null)
            {
                smsmanager.sendTextMessage(destPhoneNumber3, null, message, null, null);
                String log = String.format("SMS sent to %s | %s", destPhoneNumber3, message);
                service.relayLog(LogEntryType.SMS, log);
                Log.d("SMS", log);
            }
        }
        catch (Exception e)
        {
            Log.d("SMS", e.getMessage());
        }
    }

    public void UpdateDestination1(String destination)
    {
        destPhoneNumber1 = PhoneNumberUtils.isGlobalPhoneNumber(destination) ? destination : null;
    }

    public void UpdateDestination2(String destination)
    {
        destPhoneNumber2 = PhoneNumberUtils.isGlobalPhoneNumber(destination) ? destination : null;
    }

    public void UpdateDestination3(String destination)
    {
        destPhoneNumber3 = PhoneNumberUtils.isGlobalPhoneNumber(destination) ? destination : null;
    }
}
