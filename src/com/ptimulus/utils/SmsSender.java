package com.ptimulus.utils;

import android.telephony.SmsManager;

public class SmsSender {

	private final SmsManager smsmanager;

    private String destPhoneNumber;


	public SmsSender(String destPhoneNumber) {
        this.destPhoneNumber = destPhoneNumber;
		this.smsmanager = SmsManager.getDefault();
	}

    public void SendSMS(String message) {
        smsmanager.sendTextMessage(destPhoneNumber, null, message, null, null);
    }

    public void UpdateDestination(String destination)
    {
        destPhoneNumber = destination;
    }

}
