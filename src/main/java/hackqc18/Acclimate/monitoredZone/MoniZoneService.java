package hackqc18.Acclimate.monitoredZone;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.naming.OperationNotSupportedException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.util.ReflectionUtils;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.ReflectionUtils.FieldFilter;

import hackqc18.Acclimate.alert.Alert;
import hackqc18.Acclimate.alert.Geometry;
import hackqc18.Acclimate.exception.BadRequestException;
import hackqc18.Acclimate.exception.ResourceNotFoundException;
import hackqc18.Acclimate.exception.UserNotAuthorizedException;
import hackqc18.Acclimate.exception.UserNotFoundException;
import hackqc18.Acclimate.monitoredZone.MoniZoneRepository;
import hackqc18.Acclimate.monitoredZone.MonitoredZone;
import hackqc18.Acclimate.tile.Tile;
import hackqc18.Acclimate.tile.TileService;
import hackqc18.Acclimate.user.User;
import hackqc18.Acclimate.user.UserService;


/**
 * Service class for the monitored zones
 * 
 * @author Sophie Savoie
 *
 */
@Service
public class MoniZoneService {
	
	@Autowired
	private MoniZoneRepository moniZoneRepository;
	@Autowired
	private TileService tileService;
	@Autowired
	private UserService userService;
	
	
	
	/**
	 * Returns the list of all the monitored zones contained in the database
	 * 
	 * @return la liste des monitored zones
	 */
	public ArrayList<MonitoredZone> findAllMoniZones() {
		ArrayList<MonitoredZone> listMN = new ArrayList<>();
		
		moniZoneRepository.findAll().forEach(mn -> {
                listMN.add(mn);
		});
		return listMN;
	}
	
	
	/**
	 * Returns the monitored zone corresponding to the given zoneid
	 * 
	 * @param zoneId le Id de la zone
	 * @return la monitoredZone
	 */
	public MonitoredZone findMoniZoneById(int zoneId) {
		return moniZoneRepository.findById(zoneId).orElseThrow(() -> 
			new ResourceNotFoundException("La zone associé à l'id" + zoneId + "n'existe pas"));
	}

	
	/**
	 * Returns the list of the monitored zones of the user corresponding to the uId
	 * 
	 * @param uId the user uId
	 * @return the list of monitored zones
	 */
	public List<MonitoredZone> findMoniZonesByUId(String uId) {
		
		if (!userService.existsByUId(uId))
			throw new UserNotFoundException(uId);
		
		User user = userService.findUserByUId(uId);
		
		if(!moniZoneRepository.existsByUser(user))
			new ResourceNotFoundException("La zone associé au uId " + user.getuId() + " n'existe pas");
		
		return moniZoneRepository.findByUser(user);
	}

	
	/**
	 * Creates a new monitored zone in the database
	 * 
	 * @param monitoredZone la monitoredZone à enregistrer
	 * @return monitoredZone la monitoredZone créée
	 * @throws BadRequestException 
	 */
	public MonitoredZone createMonitoredZone(MonitoredZone mn, String uId) 
			throws BadRequestException {
		MonitoredZone mz;
		
		if (!mn.getUserId().equals(uId))
			throw new UserNotAuthorizedException(uId);
		
		if (mn.getGeometry().getType().equals("Polygon") && 
				(mn.getGeometry().getPolyCoordinates() == null || mn.getGeometry().getCoordinates() != null ||
				mn.getGeometry().getPolyCoordinates()[0][0] < 40 || mn.getGeometry().getPolyCoordinates()[0][0] > 63 ||
				mn.getGeometry().getPolyCoordinates()[1][0] < 40 || mn.getGeometry().getPolyCoordinates()[1][0] > 63 ||
				mn.getGeometry().getPolyCoordinates()[0][1] < -84 || mn.getGeometry().getPolyCoordinates()[0][1] > -58 || 
				mn.getGeometry().getPolyCoordinates()[1][1] < -84 || mn.getGeometry().getPolyCoordinates()[1][1] > -58))
			throw new BadRequestException("Les coordonnées doivent être situées entre [63.0, -84.0] et [40.0, -58.0]");
		
		if (mn.getGeometry().getType().equals("Point") && (mn.getGeometry().getPolyCoordinates() != null || 
				(Integer)mn.getRadius() == null || mn.getGeometry().getCoordinates() == null))
			throw new BadRequestException("Une monitoredZone de type point doit comprendre une paire "
					+ "de coordonnées et un rayon");

		try {
			mz = moniZoneRepository.save(mn);
			
		} catch (Exception e) {
			throw new BadRequestException("Erreur lors de la création de la monitored zone");
		}
		
		MonitoredZone moniZone = mz.clone();
		
		new Thread ( () ->  {
			saveTiles(moniZone);
		}).start();

		return mz;	
	}
	
	
	/**
	 * Created the tiles related to the given monitored zone
	 * 
	 * @param tileIndexes
	 * @param mz
	 */
	public void saveTiles(MonitoredZone mz) {
		ArrayList<Integer> tileIndexes = new ArrayList <>();
		tileIndexes = tileService.findMonitoredZoneTiles(mz);
		
		tileIndexes.forEach(index -> {
			Tile tile = new Tile (index, mz);
			tileService.createTile(tile);
		});
	}
	
	
	/**
	 *  Updates the given fields of the monitored zone associated to the zoneId
	 * 
	 * @param zoneId
	 * @param fields
	 * @return the monitored zone
	 * @throws OperationNotSupportedException
	 * @throws BadRequestException 
	 */
	public MonitoredZone updateMoniZone(int zoneId, Map<String, Object> fields) 
			throws OperationNotSupportedException, BadRequestException {
		
		MonitoredZone mn = moniZoneRepository.findById(zoneId).orElseThrow(() 
				-> new ResourceNotFoundException("La zone associé à l'id" + zoneId + "n'existe pas"));
		
        fields.forEach((k, v) -> {
            Field field = ReflectionUtils.findRequiredField(MonitoredZone.class, k);

            ReflectionUtils.setField(field, mn, v);
        });
        
        try {
        	moniZoneRepository.save(mn);
        } catch (Exception e){
        	throw new BadRequestException("La zone" + zoneId + "n'a pas pu être modifiée");
        }
        
        
		return mn;
	}
	
	
	/**
	 * Updates the geometry of the monitored zone
	 * 
	 * @param zoneId
	 * @param fields
	 * @return the monitored zone
	 * @throws OperationNotSupportedException
	 * @throws BadRequestException 
	 */
	public MonitoredZone updateGeomMoniZone(int zoneId, Geometry geometry)
			throws OperationNotSupportedException, BadRequestException {
		MonitoredZone mn = moniZoneRepository.findById(zoneId).orElseThrow(() 
				-> new ResourceNotFoundException("La zone associé à l'id" + zoneId + "n'existe pas"));

		mn.setGeometry(geometry);
		
		try {
			return moniZoneRepository.save(mn);
        } catch (Exception e){
        	throw new BadRequestException("La géométrie de la zone" + zoneId + "n'a pas pu être modifiée");
        }
	}
	
	/**
	 * Deletes the monitored zone related to the given zoneId
	 * 
	 * @param zoneId le id de la monitored zone
	 * @return the response body
	 * @throws OperationNotSupportedException
	 * @throws BadRequestException 
	 */
	public ResponseEntity<?> deleteMoniZone(int zoneId, String uId) 
			throws OperationNotSupportedException, BadRequestException {
		
		MonitoredZone zone = moniZoneRepository.findById(zoneId).orElseThrow(() -> 
		new BadRequestException ("La zone " + zoneId + "n'existe pas"));
		
		if (!zone.getUserId().equals(uId))
			throw new UserNotAuthorizedException(uId);

		try {
			new Thread ( () ->  {
				tileService.deleteAllTilesOfMz(zone);
			}).start();
			moniZoneRepository.delete(zone);
		} catch (Exception e) {
			throw new BadRequestException("Erreur lors de la suppression de la zone " + zoneId);
		}

		return ResponseEntity.ok().build();
    }

	/**
	 * Deletes all the monitored zones of a given user
	 * 
	 * @param uId the user uId
	 * @return the response body
	 */
	public ResponseEntity<?> deleteAllMzOfUser(String uId) {
		if (!userService.existsByUId(uId))
			throw new UserNotFoundException(uId);
		
		User user = userService.findUserByUId(uId);
		ArrayList<MonitoredZone> mz = (ArrayList<MonitoredZone>) moniZoneRepository.findByUser(user);
		
		if (mz != null) {
			mz.forEach(zone -> {
				new Thread ( () ->  {
				try {
					new Thread ( () ->  {
						tileService.deleteAllTilesOfMz(zone);
					}).start();
					moniZoneRepository.delete(zone);
				} catch (Exception e) {
					new BadRequestException("Erreur lors de la suppression des monitored zones de: " + uId);
				}
				}).start();
			});
		}
		
        return ResponseEntity.ok().build();
	}
}
