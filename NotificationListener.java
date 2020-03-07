/**  ~~~~~
 * Created with the AppyBuilder Code Editor.
 * This is a template for basic Extension.
 * Modify this template to customize your extension.
 *
 * **** NOTE: DO NOT use a package name. 
 * **** The package name will be created for you automatically.
 * **** Adding a package name will cause a compile error
 */


import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;

import android.widget.Toast;
import android.os.IBinder;
import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;
import android.util.Base64;

import com.google.appinventor.components.annotations.*;
import com.google.appinventor.components.common.ComponentCategory;
import com.google.appinventor.components.runtime.*;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;

@DesignerComponent(version = 1, description = "Simple notification listener extension",
        category = ComponentCategory.EXTENSION,
        nonVisible = true, iconName = "http://appyBuilder.com/extensions/icons/extension.png")
@SimpleObject(external = true)
@UsesPermissions(permissionNames =
        "android.permission.BIND_NOTIFICATION_LISTENER_SERVICE")
public class NotificationListener extends NotificationListenerService implements Component {

    protected Form form;
    private JSONObject notifications = new JSONObject();
    private JSONArray filteredPackages = new JSONArray();

    protected NotificationListener(Form form) {
        this.form = form;
        this.notifications = new JSONObject();
        this.filteredPackages = new JSONArray();
    }

    @Override
    public HandlesEventDispatching getDispatchDelegate() {
        return form;
    }

    /**
     * @param container container, component will be placed in
     */
    public NotificationListener() {
        this.notifications = new JSONObject();
        this.filteredPackages = new JSONArray();
    }

    @SimpleFunction(description = "This allows for one to subscribe for a notification on a package")
    public void RegisterPackage(String packageName) {
        filteredPackages.put(packageName);
    }

    @SimpleFunction(description = "This allows for one to subscribe for a notification(s)")
    public String GetFilteredPackages(String packageName) {
        return filteredPackages.toString();
    }

    @SimpleFunction(description = "Retrieve available notifications by packageName specified in filtered Packages")
    public String GetNotificationsByPackage(String packageName) {
      	JSONArray list = notifications.optJSONArray(packageName);
        return list != null ? list.toString() : "[]";
    }

    @SimpleFunction(description = "Retrieve available notifications regardless of the package")
    public String GetAllNotifications() {
        return notifications.toString();
    }
  
    @Override
    public void onNotificationPosted(StatusBarNotification sbn, RankingMap rankingMap) {
		append(sbn);
        super.onNotificationPosted(sbn, rankingMap);
    }

    @Override
    public void onNotificationPosted(StatusBarNotification sbn) {
       	append(sbn);
        super.onNotificationPosted(sbn);
    }

  	private void append(StatusBarNotification sbn){
  		 try {
            init();
            JSONArray jsonArray = notifications.optJSONArray(sbn.getPackageName());
            if (jsonArray == null) {
                jsonArray = new JSONArray();
            }
            jsonArray.put(buildNotification(sbn));
            notifications.putOpt(sbn.getPackageName(), jsonArray);
        } catch (JSONException e) {
            e.printStackTrace();
        }    
    }
  
    private JSONObject buildNotification(StatusBarNotification sbn) {
        JSONObject notification = new JSONObject();
        try {
            notification.put("package", sbn.getPackageName());
            if (sbn.getNotification().tickerText != null) {
                notification.put("tickerText", sbn.getNotification().tickerText.toString());
            }
            if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.KITKAT) {
                return notification;
            }
            Bundle extras = sbn.getNotification().extras;
            notification.put("title", extras.getString("android.title"));
            notification.put("body", extras.getCharSequence("android.text").toString());
            notification.put("icon", getStringFromBitmap(sbn.getNotification().largeIcon));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return notification;
    }

    private String getStringFromBitmap(Bitmap bitmapPicture) {
        final int COMPRESSION_QUALITY = 100;
        String encodedImage;
        ByteArrayOutputStream byteArrayBitmapStream = new ByteArrayOutputStream();
        bitmapPicture.compress(Bitmap.CompressFormat.PNG, COMPRESSION_QUALITY,
                byteArrayBitmapStream);
        byte[] b = byteArrayBitmapStream.toByteArray();
        encodedImage = Base64.encodeToString(b, Base64.DEFAULT);
        return encodedImage;
    }

    @Override
    public void onNotificationRemoved(StatusBarNotification sbn) {
      super.onNotificationRemoved(sbn);
    }

    @Override
    public void onListenerConnected() {
        init();
        super.onListenerConnected();
    }

    @Override
    public IBinder onBind(Intent intent) {
        init();
        return super.onBind(intent);
    }

    private void init() {
        if (this.notifications == null) {
            this.notifications = new JSONObject();
            this.filteredPackages = new JSONArray();
        }
    }
}
