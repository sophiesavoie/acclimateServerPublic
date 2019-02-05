package hackqc18.Acclimate.notifications;

import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.Message;

import java.util.ArrayList;

/**
 * Sending a notification via the Google FCM server.
 *
 * For a version that doesn't use Google's API to send the request to the FCM server:
 * https://stackoverflow.com/questions/46705608/firebase-cloud-messaging-rest-api-spring/51172021#51172021
 * 
 * @author Jérémi Grenier-Berthiaume
 */
public class PushNotifServiceImpl {

    /**
     * Use when the server has determined how many Alerts are situated inside
     * a single Monitored Zone of a user.
     *
     * Documentation:
     * https://firebase.google.com/docs/cloud-messaging/admin/send-messages
     *
     * @param registrationToken tokens of the devices the notif is sent to
     * @param mzName name of the Monitored Zone (can be its ID)
     * @param nbrAlertes number of alerts detected inside the MZ
     */
    public static void sendMzLiveAlertNotif(ArrayList<String> registrationToken,
                                        String mzName, int nbrAlertes) {

        if(registrationToken.size() == 0 || nbrAlertes == 0 || mzName.isEmpty())
            return;

        registrationToken.forEach(token -> {
            // See documentation on defining a message payload.
            Message message = Message.builder()
                    .putData("title", NotifType.NEW_LIVE_ALERT.getTitle())
                    .putData("body", "\"" + mzName + "\" contient " + nbrAlertes + " alertes.")
                    .putData("tag", mzName)
                    .putData("nbrAlerts", nbrAlertes+"")
                    .setToken(token)
                    .build();

            try {
                // Send a message to the device.
                String response = FirebaseMessaging.getInstance().send(message);
                // Response is a message ID string.
                System.out.println("Successfully sent message: " + response);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }
    
    /**
     * Use when the server sends a confirmed user Alert notification to the users concerned
     *
     * Documentation:
     * https://firebase.google.com/docs/cloud-messaging/admin/send-messages
     *
     * @param registrationToken tokens of the devices the notif is sent to
     * @param mzName name of the Monitored Zone (can be its ID)
     * @param nbrAlertes number of alerts detected inside the MZ
     */
    public static void sendMzUserAlertNotif(ArrayList<String> registrationToken,
                                        String mzName, int nbrAlertes) {

        if(registrationToken.size() == 0 || nbrAlertes == 0 || mzName.isEmpty())
            return;

        registrationToken.forEach(token -> {
            // See documentation on defining a message payload.
            Message message = Message.builder()
                    .putData("title", NotifType.NEW_USER_ALERT.getTitle())
                    .putData("body", "\"" + mzName + "\" contient " + nbrAlertes + " alertes.")
                    .putData("tag", mzName)
                    .putData("nbrAlerts", nbrAlertes+"")
                    .setToken(token)
                    .build();

            try {
                // Send a message to the device.
                String response = FirebaseMessaging.getInstance().send(message);
                // Response is a message ID string.
                System.out.println("Successfully sent message: " + response);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }


    /**
     * To send a "custom" notification to devices.
     * The title and the body of the message which will appear in the notif
     * need to be determined.
     *
     * If the 'tag' is the same for two notifications displayed on the same
     * device, the newer notif will overwrite the older one that has the same tag.
     *
     * Documentation:
     * https://firebase.google.com/docs/cloud-messaging/admin/send-messages
     *
     * @param registrationToken tokens of the devices the notif is sent to
     * @param title displayed title of the notif
     * @param body displayed body message of the notif
     * @param tag associated with the current notif
     */
    public static void sendCustomNotif(ArrayList<String> registrationToken,
                                       String title, String body, String tag) {

        registrationToken.forEach(token -> {
            // See documentation on defining a message payload
            Message message = Message.builder()
                    .setToken(token)
                    .putData("title", title)
                    .putData("body", body)
                    .putData("tag", "Acclimate." + tag + NotifType.CUSTOM.getTitle())
                    .build();

            try {
                // Send a message to the device.
                String response = FirebaseMessaging.getInstance().send(message);
                // Response is a message ID string.
                System.out.println("Successfully sent message: " + response);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }


    /**
     * To notify users a new version is out.
     * This one just shows the version number in the body.
     *
     * @param registrationToken tokens of the devices the notif is sent to
     * @param version version number
     */
    public static void sendUpdateVersionNotif(ArrayList<String> registrationToken,
                                        String version) {

        registrationToken.forEach(token -> {
            Message message = Message.builder()
                    .putData("title", NotifType.UPDATE.getTitle())
                    .putData("body", "v " + version + " is out.")
                    .putData("tag", "v " + version + " is out.")
                    .setToken(token)
                    .build();

            try {
                // Send a message to the device.
                String response = FirebaseMessaging.getInstance().send(message);
                // Response is a message ID string.
                System.out.println("Successfully sent message: " + response);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }
    /**
     * To notify users a new version is out.
     * This one allows you to send a short description along with the notif.
     *
     * @param registrationToken tokens of the devices the notif is sent to
     * @param body small description (body of the notif)
     */
    public static void sendUpdateBodyNotif(ArrayList<String> registrationToken,
                                              String body) {

        registrationToken.forEach(token -> {
            Message message = Message.builder()
                    .putData("title", NotifType.UPDATE.getTitle())
                    .putData("body", body)
                    .putData("tag", "frsiuyhg4iuh-" + body)
                    .setToken(token)
                    .build();

            try {
                // Send a message to the device.
                String response = FirebaseMessaging.getInstance().send(message);
                // Response is a message ID string.
                System.out.println("Successfully sent message: " + response);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }
    /**
     * To notify users a new version is out.
     * No additional information is provided aside from the title which
     * is set by default.
     *
     * @param registrationToken tokens of the devices the notif is sent to
     */
    public static void sendUpdateNotif(ArrayList<String> registrationToken) {

        registrationToken.forEach(token -> {
            Message message = Message.builder()
                    .putData("title", NotifType.UPDATE.getTitle())
                    .putData("body", "")
                    .putData("tag", "f5iuyhg4t4wiuh-")
                    .setToken(token)
                    .build();

            try {
                // Send a message to the device.
                String response = FirebaseMessaging.getInstance().send(message);
                // Response is a message ID string.
                System.out.println("Successfully sent message: " + response);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }


    /**
     * When the user has activated the GPS notifications, he receives
     * notifications of the alerts surrounding him within a certain radius.
     *
     * @param registrationToken tokens of the devices the notif is sent to
     * @param nbrAlertes amount of alerts within the prefered radius
     */
    public static void sendGpsNotif(ArrayList<String> registrationToken,
                                           int nbrAlertes) {

        registrationToken.forEach(token -> {
            Message message = Message.builder()
                    .putData("title", NotifType.GPS.getTitle())
                    .putData("body", "Il y a " + nbrAlertes + " alertes près de vous.")
                    .putData("tag", "GPS-Notifs-Acclimate")
                    .putData("nbrAlerts", nbrAlertes+"")
                    .setToken(token)
                    .build();

            try {
                // Send a message to the device.
                String response = FirebaseMessaging.getInstance().send(message);
                // Response is a message ID string.
                System.out.println("Successfully sent message: " + response);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }
}