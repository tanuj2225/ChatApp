package edu.uncc.tanuj.chatapp;

import android.widget.TextView;

/**
 * Created by vinay on 11/20/2016.
 */

public class CheckFieldValidator {
    public static boolean checkField(TextView field){
        String fieldData=field.getText().toString();
        if(fieldData.equalsIgnoreCase("") && fieldData!=null){field.setError("Please fill this field");return false;}
        return true;
    }
}
