package com.lebo;

public interface FileOperateCallback {
        void onSuccess();

        void onFailed(String error);
    }