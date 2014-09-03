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
