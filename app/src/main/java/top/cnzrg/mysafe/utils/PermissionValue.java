package top.cnzrg.mysafe.utils;

import android.Manifest;

/**
 * FileName: PermissionValue
 * Author: ZRG
 * Date: 2019/4/29 14:46
 */
public class PermissionValue {
    //短信权限
    public static final String[] SMS_PERNISSION = {Manifest.permission.RECEIVE_SMS,
            Manifest.permission.RECEIVE_SMS, Manifest.permission.READ_SMS,
            Manifest.permission.RECEIVE_WAP_PUSH, Manifest.permission.RECEIVE_MMS};

    //STORAGE存储卡权限
    public static final String[] STORAGE_PERNISSION = {Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE};

    //LOCATION位置权限
    public static final String[] LOCATION_PERNISSION = {Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION};

    //CONTACTS联系人权限
    public static final String[] CONTACTS_PERNISSION = {Manifest.permission.READ_CONTACTS,
            Manifest.permission.WRITE_CONTACTS, Manifest.permission.GET_ACCOUNTS};

    //CAMERA相机权限
    public static final String[] CAMERA_PERNISSION = {Manifest.permission.CAMERA};

}
