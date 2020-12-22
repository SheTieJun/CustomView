package com.lebo;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;

public class UIHandler extends Handler {

        private IUIUpdateListener mUIUpdateListener;

        UIHandler(Looper looper) {
            super(looper);
        }

        public void setUIUpdateListener(IUIUpdateListener pUIUpdateListener) {
            mUIUpdateListener = pUIUpdateListener;
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            MessageDeatail detail = (MessageDeatail) msg.obj;
            if (null != mUIUpdateListener) {
                mUIUpdateListener.onUpdate(msg.what, detail);
            }
        }
    }