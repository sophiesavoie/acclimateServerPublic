package hackqc18.Acclimate.alert;

import java.util.ArrayList;
import java.util.Map;
import javax.naming.OperationNotSupportedException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import hackqc18.Acclimate.authentication.VerifyToken;
import hackqc18.Acclimate.exception.AlertNotFoundException;
import hackqc18.Acclimate.exception.BadRequestException;
import hackqc18.Acclimate.exception.UserNotAuthorizedException;
import hackqc18.Acclimate.user.UserService;

// IMPORTANT !!!
// TODO: Make sure that all input validations are done on the server side because
// hacking clients is a peace of cake compare to hacking the server!!!

/**
 * Controller class for alerts.
 * 
 * @author Normand Desmarais and Sophie Savoie
 */
// The @RestController annotation informs Spring that this class is
// a REST controller. In the background, Spring does all the magic
// to redirect related HTTP requests to this class.
//
// @RestController also invokes @ResponseBody for the class.
// The @ResponseBody annotation informs Spring that the object returned
// by methods must be translated into JSON format. Optionally,
// we could also support XML format if need be, both at the same time
// (the request header "Content-Type" would indicate whether to
// use "application/json" or "text/xml").
//
// The @RequestMapping annotation defines the base URL managed by this
// class. All requests starting with "{site-URL}/api/user/alerts" will be
// redirected to this class.
@RestController
@RequestMapping("api/alerts")
public class AlertController {

    // The @Autowired annotation informs Spring to instantiate the variable
    // AlertService with the singleton (unique single instance) instance
    // of the class AlertService.
    @Autowired
    private AlertService alertService;
    @Autowired
    private UserService userService;

    /**
     * The GET method associated with the URL "api/alerts/{alertType}". It
     * retrieves and returns the collection of user alerts. Optional filter
     * parameters could be provided to limit the number of alerts to a given
     * region.
     *
     * @param north the northern latitude (default: 90)
     * @param south the southern latitude (default: -90)
     * @param east the eastern longitude (default: 180)
     * @param west the western longitude (default: -180)
     * @return a list of alerts in JSON format
     * @throws OperationNotSupportedException
     */
    @GetMapping("/{alertType}")
    public ArrayList<Alert> getAllAlerts(
            @PathVariable String alertType,
            @RequestParam(defaultValue = "90") double north,
            @RequestParam(defaultValue = "-90") double south,
            @RequestParam(defaultValue = "180") double east,
            @RequestParam(defaultValue = "-180") double west) 
            throws OperationNotSupportedException {
    	
        return alertService.findAllAlerts(alertType, north, south, east, west);
    }


    /**
     * The GET method associated with the URL
     * "api/alerts/{alertType}/{alertId}", where alertId is variable. It returns
     * the associated alert.
     *
     * @param alertId the id of the alert of interest
     * @return the alert or empty if not found
     * @throws OperationNotSupportedException
     * @throws AlertNotFoundException
     */
    @GetMapping("/{alertType}/{alertId}")
    public Alert getAlert(
            @PathVariable String alertType,
            @PathVariable String alertId) 
            throws AlertNotFoundException, OperationNotSupportedException {
    	
        return alertService.findAlertById(alertType, alertId);
    }


    /**
     * The POST method associated with the URL "api/alerts/user". It
     * creates a new userAlert in the database.
     * 
     * @param userAlert the userAlert sent in the request
     * @param token the idToken used to check if the user is authorised to do this request
     * @return the newly created user alert
     * @throws OperationNotSupportedException
     * @throws BadRequestException
     */
    @PostMapping("/user")
    public UserAlert addAlert(
            @RequestBody UserAlert userAlert,
    		@RequestHeader("Authorization") String token) 
    		throws OperationNotSupportedException, BadRequestException {
    	
    	String uId = VerifyToken.verifyIdToken(token);
    	if (!userService.existsByUId(uId) || !userAlert.getUser().getuId().equals(uId))
    		throw new UserNotAuthorizedException(uId);
    	
    	return alertService.createUserAlert(userAlert);
    }
    
    
    /**
     * This POST method is not used. A new alert is always created, even tought another alert exists nearby.
     * 
    
    @PostMapping
    public ResponseEntity<Alert> addAlert(
            @PathVariable String alertType,
            @RequestBody AlertStub alertStub) throws OperationNotSupportedException {
        Alert alert = alertService.createAlert(alertType, alertStub);
        if (alert.getCount() == 1) { // new alert
            URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                    .path("/{alertId}").buildAndExpand(alert.getId()).toUri();
            return ResponseEntity.created(location).body(alert);
        } else { // added +1 to the count of an existing alert
            return ResponseEntity.ok(alert);
        }
    }
    **/


    /**
     * The PATCH method associated with the URL
     * "api/alerts/{alertType}/{alertId}", where alertId is variable. If the
     * alert with the corresponding alertId exists, it modifies its given fields
     * in parameters and returns the updated alert.
     * 
     * @param alertId
     * @param fields
     * @param token
     * @return
     * @throws OperationNotSupportedException
     * @throws AlertNotFoundException
     * @throws BadRequestException
     */
    @PatchMapping("/user/{alertId}")
    public UserAlert updateUserAlert(
            @PathVariable String alertId,
            @RequestBody Map<String, Object> fields,
            @RequestHeader("Authorization") String token) 
            throws OperationNotSupportedException, AlertNotFoundException, BadRequestException {
    	
    	String uId = VerifyToken.verifyIdToken(token);
    	if (!userService.existsByUId(uId))
    		throw new UserNotAuthorizedException(uId);	
    	
        return alertService.updateUserAlert(alertId, fields);
    }
    
    /**
     * The PATCH method associated with the URL
     * "api/monitoredzone/geometry/{zoneId}", where zoneId is variable. If the
     * monitored zone with the corresponding zoneId exists, it modifies the values of
     * the given geometry fields. It returns the modified alert.
     *
     * @param zoneId the id of the monitored zone of interest
     * @param fields the fields of the monitored zone that needs to be modified
     * @return the modified monitored zone
     * @throws OperationNotSupportedException
     * @throws BadRequestException 
     * @throws AlertNotFoundException 
     */
    @PatchMapping("/user/geometry/{alertId}")
    public UserAlert updateAlertGemetry(
    		@PathVariable String alertId, 
    		@RequestBody Geometry geometry,
    		@RequestHeader("Authorization") String token) 
    		throws OperationNotSupportedException, AlertNotFoundException, BadRequestException {
    	
    	String userId = VerifyToken.verifyIdToken(token);
    	if (!userService.existsByUId(userId))
    		throw new UserNotAuthorizedException(userId);
        
    	return alertService.updateAlertGeometry(alertId, geometry);
    }


    /**
     * The DELETE method associated with the URL
     * "api/alerts/{alertType}/{alertId}", where alertId is variable. It deletes
     * the corresponding alert from the database if it is found.
     *
     * @param alertId the id of the alert of interest
     * @return the deleted alert
     * @throws OperationNotSupportedException
     */
    @DeleteMapping("/user/{alertId}")
    public ResponseEntity<?> removeAlert(
            @PathVariable String alertId, 
            @RequestHeader("Authorization") String token) 
            throws OperationNotSupportedException {
    	
    	String uId = VerifyToken.verifyIdToken(token);
    	if (!userService.existsByUId(uId))
    		throw new UserNotAuthorizedException(uId);
    	
        return alertService.deleteUserAlert(alertId, uId);
    }
}
