package com.ye.registration.token;

import java.util.Calendar;
import java.util.Date;

public class TokenExpirationTime {

    private static final int expirationTime = 10 ;

    public static Date getExpirationTime (){

        Calendar calendar = Calendar.getInstance();

        calendar.setTimeInMillis(new Date().getTime());

        calendar.add(Calendar.MINUTE ,  expirationTime );

        return new Date(calendar.getTime().getTime());
    }
}
