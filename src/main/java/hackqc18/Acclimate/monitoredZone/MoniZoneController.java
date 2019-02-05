package hackqc18.Acclimate.monitoredZone;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
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
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import hackqc18.Acclimate.alert.Geometry;
import hackqc18.Acclimate.authentication.VerifyToken;
import hackqc18.Acclimate.exception.BadRequestException;
import hackqc18.Acclimate.exception.UserNotAuthorizedException;
import hackqc18.Acclimate.exception.UserNotFoundException;
import hackqc18.Acclimate.monitoredZone.MonitoredZone;
import hackqc18.Acclimate.user.User;
import hackqc18.Acclimate.user.UserService;


/**
 * Controller class for monitored zones
 * 
 * @author Sophie Savoie
 *
 */

@RestController
@RequestMapping("api/monitoredzones")
public class MoniZoneController {
	
	@Autowired
    private MoniZoneService moniZoneService;
	@Autowired
    private UserService userService;
	
	
	/**
     * The GET method associated with the URL
     * "api/monitoredzones". It returns the list of all the existing monitored zones.
     * 
     * @return the list of monitored zones
     * @throws OperationNotSupportedException
     */
    @GetMapping
    public ArrayList<MonitoredZone> getAllMonitoredZones(
    		@RequestHeader("Authorization") String token)  
    		throws OperationNotSupportedException {
    	
    	String userId = VerifyToken.verifyIdToken(token);
    	if (!userService.existsByUId(userId))
    		throw new UserNotAuthorizedException(userId);
    	
    	return moniZoneService.findAllMoniZones();
    }
	
    /**
     * The GET method associated with the URL
     * "api/monitoredzones/{uId}", where uId is variable. It returns
     * the list of monitored zones related to the uId.
     * 
     * @param uId
     * @return the list of monitored zones
     * @throws UserNotFoundException
     * @throws OperationNotSupportedException
     */
    @GetMapping("/uid/{uId}")
    public List<MonitoredZone> getMonitoredZonesByUserId (
    		@PathVariable String uId,
    		@RequestHeader("Authorization") String token) 
    		throws UserNotFoundException, OperationNotSupportedException {
    	
    	String userId = VerifyToken.verifyIdToken(token);
    	if (!userService.existsByUId(userId) || !(uId.equals(userId)))
    		throw new UserNotAuthorizedException(userId);
    	
    	return moniZoneService.findMoniZonesByUId(uId);
    }

    
    /**
     * The GET method associated with the URL
     * "api/monitoredzones/{zoneId}", where zoneId is variable. It returns
     * the associated monitored zone.
     * 
     * @param zoneId
     * @return the monitored zone associated to the zoneId
     * @throws OperationNotSupportedException
     * @throws BadRequestException 
     */
    @GetMapping("/{zoneId}")
    public MonitoredZone getMonitoredZoneById (
    		@PathVariable String zoneId,
    		@RequestHeader("Authorization") String token) 
    		throws OperationNotSupportedException, BadRequestException {
    	
    	String uId = VerifyToken.verifyIdToken(token);
    	if (!userService.existsByUId(uId))
    		throw new UserNotAuthorizedException(uId);
    	
    	int id;
    	try {
    		id = Integer.parseInt(zoneId);
    	} catch (Exception e) {
    		throw new BadRequestException("Le id " + zoneId + " n'est pas un int");
    	}
    	
    	return moniZoneService.findMoniZoneById(id);
    	
    }
    
    /**
     * The POST method associated with the URL "api/monitoredZones". It creates a new monitoredZone.
     * 
     * @param monitoredZone
     * @param token
     * @return
     * @throws OperationNotSupportedException
     * @throws BadRequestException
     */
    @PostMapping
    public MonitoredZone addMonitoredZone(
    		@RequestBody MonitoredZone monitoredZone,
    		@RequestHeader("Authorization") String token)
            throws OperationNotSupportedException, BadRequestException {
    	
    	String uId = VerifyToken.verifyIdToken(token);
    	if (!userService.existsByUId(uId))
    		throw new UserNotAuthorizedException(uId);
    	
    	return moniZoneService.createMonitoredZone(monitoredZone, uId);
    }
    
    /**
     * The PATCH method associated with the URL
     * "api/monitoredzones/{zoneId}", where zoneId is variable. If the
     * monitored zone with the corresponding zoneId exists, it modifies the values of
     * the given fields. 
     *
     * @param zoneId the id of the monitored zone of interest
     * @param fields the fields of the monitored zone that needs to be modified
     * @return the modified monitored zone or an empty body if it was not found
     * @throws OperationNotSupportedException
     * @throws BadRequestException 
     */
    @PatchMapping("/{zoneId}")
    public MonitoredZone updateMonitoredZone(
    		@PathVariable String zoneId, 
    		@RequestBody Map<String, Object> fields,
    		@RequestHeader("Authorization") String token) 
    		throws OperationNotSupportedException, BadRequestException {
    	
    	String userId = VerifyToken.verifyIdToken(token);
    	if (!userService.existsByUId(userId))
    		throw new UserNotAuthorizedException(userId);
    	
    	int id;
    	try {
    		id = Integer.parseInt(zoneId);
    	} catch (Exception e) {
    		throw new BadRequestException("Le id " + zoneId + " n'est pas un int");
    	}
    	
    	return moniZoneService.updateMoniZone(id, fields);
    }
    
    /**
     * The PATCH method associated with the URL
     * "api/monitoredzones/geometry/{zoneId}", where zoneId is variable. If the
     * monitored zone with the corresponding zoneId exists, it modifies the values of
     * the given geometry fields. It returns the modified monitored zone or an empty body if
     * it doesn't exist.
     *
     * @param zoneId the id of the monitored zone of interest
     * @param fields the fields of the monitored zone that needs to be modified
     * @return the modified monitored zone or an empty body if it was not found
     * @throws OperationNotSupportedException
     * @throws BadRequestException 
     */
    @PatchMapping("/geometry/{zoneId}")
    public MonitoredZone updateMoniZoneGemetry(
    		@PathVariable String zoneId, 
    		@RequestBody Geometry geometry,
    		@RequestHeader("Authorization") String token) 
    		throws OperationNotSupportedException, BadRequestException {
    	
    	String userId = VerifyToken.verifyIdToken(token);
    	if (!userService.existsByUId(userId))
    		throw new UserNotAuthorizedException(userId);
    	
    	int id;
    	try {
    		id = Integer.parseInt(zoneId);
    	} catch (Exception e) {
    		throw new BadRequestException("Le id " + zoneId + " n'est pas un int");
    	}
        
    	return moniZoneService.updateGeomMoniZone(id, geometry);
    }
    
    
    /**
     * The DELETE method associated with the URL
     * "api/monitoredzones/{zoneId}", where zoneId is variable. It deletes
     * the corresponding monitored zone from the database if it was found.
     *
     * @param zoneId the id of the monitored zone of interest
     * @return the deleted monitored zone or an empty body if it wasn't found
     * @throws OperationNotSupportedException
     * @throws BadRequestException 
     */
    @DeleteMapping("/{zoneId}")
    public ResponseEntity<?> removeMonitoredZone(
    		@PathVariable String zoneId,
    		@RequestHeader("Authorization") String token) 
    		throws OperationNotSupportedException, BadRequestException {

    	String userId = VerifyToken.verifyIdToken(token);
    	if (!userService.existsByUId(userId))
    		throw new UserNotAuthorizedException(userId);
    	
    	int id;
    	try {
    		id = Integer.parseInt(zoneId);
    	} catch (Exception e) {
    		throw new BadRequestException("Le id " + zoneId + " n'est pas un int");
    	}
    	
    	return moniZoneService.deleteMoniZone(id, userId);
    }
    
    /**
     * The DELETE method associated with the URL
     * "api/monitoredzones/all/{uId}", where uId is variable. It deletes
     * all the corresponding monitored zones related to the uId from the database if it was found.
     * 
     * @param uId
     * @param token
     * @return
     * @throws OperationNotSupportedException
     * @throws BadRequestException
     */
    @DeleteMapping("/all/{uId}")
    public ResponseEntity<?> deleteAllMzOfUser(
    		@PathVariable String uId,
    		@RequestHeader("Authorization") String token) 
    		throws OperationNotSupportedException, BadRequestException {
    	
    	String userId = VerifyToken.verifyIdToken(token);
    	if (!userService.existsByUId(userId) || !userId.equals(uId))
    		throw new UserNotAuthorizedException(userId);
    	
    	return moniZoneService.deleteAllMzOfUser(uId);
    }
}

