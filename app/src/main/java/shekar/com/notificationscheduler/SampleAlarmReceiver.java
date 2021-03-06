package shekar.com.notificationscheduler;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.v4.content.WakefulBroadcastReceiver;
import android.util.Log;

import java.util.Calendar;

/**
 * When the alarm fires, this WakefulBroadcastReceiver receives the broadcast Intent 
 * and then starts the IntentService {@code SampleSchedulingService} to do some work.
 */
public class SampleAlarmReceiver extends WakefulBroadcastReceiver {
    private static final int startTime=6;
    public static final String MEAL_TYPE = "MEAL_TYPE";
    // The app's AlarmManager, which provides access to the system alarm services.
    private AlarmManager alarmMgr;
    // The pending intent that is triggered when the alarm fires.
    private PendingIntent alarmIntent;

    @Override
    public void onReceive(Context context, Intent intent) {
        int mealType=intent.getIntExtra(MEAL_TYPE,-1);
        Log.d("onReceive====",mealType+"=");
        // BEGIN_INCLUDE(alarm_onreceive)
        /* 
         * If your receiver intent includes extras that need to be passed along to the
         * service, use setComponent() to indicate that the service should handle the
         * receiver's intent. For example:
         * 
         * ComponentName comp = new ComponentName(context.getPackageName(), 
         *      MyService.class.getName());
         *
         * // This intent passed in this call will include the wake lock extra as well as 
         * // the receiver intent contents.
         * startWakefulService(context, (intent.setComponent(comp)));
         * 
         * In this example, we simply create a new intent to deliver to the service.
         * This intent holds an extra identifying the wake lock.
         */

        ComponentName comp = new ComponentName(context.getPackageName(),
                SampleSchedulingService.class.getName());
        startWakefulService(context, (intent.setComponent(comp)));

        //Intent service = new Intent(context, SampleSchedulingService.class);

        // Start the service, keeping the device awake while it is launching.
        //startWakefulService(context, service);
        // END_INCLUDE(alarm_onreceive)
    }

    // BEGIN_INCLUDE(set_alarm)
    /**
     * Sets a repeating alarm that runs once a day at approximately 8:30 a.m. When the
     * alarm fires, the app broadcasts an Intent to this WakefulBroadcastReceiver.
     * @param context
     */
    public void setAlarm(Context context) {

        alarmMgr = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
        for (int id = 0; id < 1; id++) {
            Intent intent = new Intent("reminder");
            intent.putExtra(MEAL_TYPE,id);
            alarmIntent = PendingIntent.getBroadcast(context, id, intent,0);
            alarmMgr.set(AlarmManager.RTC_WAKEUP,
                    System.currentTimeMillis(), alarmIntent);
        }

        // Enable {@code SampleBootReceiver} to automatically restart the alarm when the
        // device is rebooted.
        ComponentName receiver = new ComponentName(context, SampleBootReceiver.class);
        PackageManager pm = context.getPackageManager();

        pm.setComponentEnabledSetting(receiver,
                PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
                PackageManager.DONT_KILL_APP);
    }

    private long getTimeInMillis(int alarmId) {
        Calendar calendar = Calendar.getInstance();
        TimeCal timeCal = new TimeCal(alarmId).invoke();
        int hour = timeCal.getHour();
        int minute = timeCal.getMinute();
        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.set(Calendar.HOUR_OF_DAY, hour);
        calendar.set(Calendar.MINUTE, minute);
        calendar.set(Calendar.SECOND, 0);
        return calendar.getTimeInMillis();
    }
    // END_INCLUDE(set_alarm)

    /**
     * Cancels the alarm.
     * @param context
     */
    // BEGIN_INCLUDE(cancel_alarm)
    public void cancelAlarm(Context context) {
        // If the alarm has been set, cancel it.
        if (alarmMgr!= null) {
            alarmMgr.cancel(alarmIntent);
        }

        // Disable {@code SampleBootReceiver} so that it doesn't automatically restart the 
        // alarm when the device is rebooted.
        ComponentName receiver = new ComponentName(context, SampleBootReceiver.class);
        PackageManager pm = context.getPackageManager();

        pm.setComponentEnabledSetting(receiver,
                PackageManager.COMPONENT_ENABLED_STATE_DISABLED,
                PackageManager.DONT_KILL_APP);
    }

    private class TimeCal {
        private int mAlarmId;
        private int mHour;
        private int mMinute;

        public TimeCal(int alarmId) {
            mAlarmId = alarmId;
        }

        public int getHour() {
            return mHour;
        }

        public int getMinute() {
            return mMinute;
        }

        public TimeCal invoke() {
            mHour = 0;
            mMinute = 0;
            switch (mAlarmId){
                case 0:
                    mHour =startTime;
                    break;
                case 1:
                    mHour =startTime+3;
                    mMinute =30;
                    break;
                case 2:
                    mHour =startTime+5;
                    mMinute =30;
                    break;
                case 3:
                    mHour =startTime+9;
                    mMinute =30;
                    break;
                case 4:
                    mHour =startTime+12;
                    mMinute =0;
                    break;
            }
            return this;
        }
    }
    // END_INCLUDE(cancel_alarm)
}
