package com.kuyou.rtcm.utils;

import android.content.Context;
import android.widget.Toast;

public class ToastUtil {

    private static Toast mToast;  
        public final static void showToast(String text ,Context context) {    
            if(mToast == null) {    
                mToast = Toast.makeText(context, text, Toast.LENGTH_SHORT);    
            } else {    
                mToast.setText(text);      
                mToast.setDuration(Toast.LENGTH_SHORT);    
            }    
            mToast.show();    
        }    
            
        public void cancelToast() {    
                if (mToast != null) {    
                    mToast.cancel();    
                }    
            }    
            
        public void onBackPressed() {    
                cancelToast();    
            }   
}
