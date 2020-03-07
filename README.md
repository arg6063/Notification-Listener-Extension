# Notification-Listener-Extension
Notification-Listener-Extension for App Inventor But it has some error so try to Solve this by contributing.

Result I'm Getting from it
1. Android is Denied to give permission : 
**BIND_NOTIFICATION_LISTENER_SERVICE**
2. We are able to get **Notification access** by adding service in manifest
3. when we call **.GetNotificationByPackageName** it showing Error: 
**Attempt to invoke virtual method ‘java.lang.stringorg.json.JSONArray.toString()’ on a null object reference**
4. **GetAllNotification** Block returning Empty result may be because of permission

Also, we have to Modify Manifest (Available in the above post) using Android Studio Editor Then we able to register an app in Special Notification Access Permission.
	<service android:name="YOUR_PACKAGE_NAME_HERE"
            	 android:label="NotificationAlertAccess"
          	   android:permission="android.permission.BIND_NOTIFICATION_LISTENER_SERVICE">
      	  <intent-filter>
            	<action android:name="android.service.notification.NotificationListenerService" />
       	 </intent-filter>
    </service>
