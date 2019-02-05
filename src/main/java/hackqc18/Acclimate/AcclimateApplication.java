package hackqc18.Acclimate;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import hackqc18.Acclimate.alert.AlertService;
import hackqc18.Acclimate.alert.CsvAlertParser;
import hackqc18.Acclimate.alert.Geometry;
import hackqc18.Acclimate.alert.HistoAlert;
import hackqc18.Acclimate.alert.LiveAlert;
import hackqc18.Acclimate.alert.OldUserAlert;
import hackqc18.Acclimate.alert.UserAlert;
import hackqc18.Acclimate.alert.repository.HistoAlertRepository;
import hackqc18.Acclimate.alert.repository.OldUserAlertRepository;
import hackqc18.Acclimate.alert.repository.UserAlertRepository;
import hackqc18.Acclimate.authentication.VerifyToken;
import hackqc18.Acclimate.exception.AlertNotFoundException;
import hackqc18.Acclimate.exception.BadRequestException;
import hackqc18.Acclimate.exception.ResourceNotFoundException;
import hackqc18.Acclimate.exception.UserNotFoundException;
import hackqc18.Acclimate.monitoredZone.MoniZoneRepository;
import hackqc18.Acclimate.monitoredZone.MoniZoneService;
import hackqc18.Acclimate.monitoredZone.MonitoredZone;
import hackqc18.Acclimate.notifications.PushNotifServiceImpl;
import hackqc18.Acclimate.tile.TileRepository;
import hackqc18.Acclimate.tile.TileService;
import hackqc18.Acclimate.user.Karma;
import hackqc18.Acclimate.user.User;
import hackqc18.Acclimate.user.UserRepository;
import hackqc18.Acclimate.user.UserService;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.naming.OperationNotSupportedException;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling

public class AcclimateApplication {

    public static void main(String[] args) {

        //authenticateServer();

        SpringApplication.run(AcclimateApplication.class, args);
    }   


    /**
     * Admin SDK API (Firebase) - nécessaire
     * Pour authentifier le serveur d'Acclimate au serveur de Firebase.
     * Permet d'utiliser les fonctionnalités de l'Admin SDK.
     */
    private static void authenticateServer() {
    }
    
}
