package hackqc18.Acclimate.tile;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import hackqc18.Acclimate.alert.Alert;
import hackqc18.Acclimate.alert.LiveAlert;
import hackqc18.Acclimate.alert.RssFeedParser;
import hackqc18.Acclimate.exception.BadRequestException;
import hackqc18.Acclimate.monitoredZone.MoniZoneService;
import hackqc18.Acclimate.monitoredZone.MonitoredZone;


/**
 * Main class for managing the filtering operation on the alerts. This classe is used
 * to filter incoming alerts.
 * 
 * @author Olivier L. Applin and Sophie Savoie
 *
 */
@Service
public class TileService {
	
	@Autowired
	TileRepository tileRepository;
	@Autowired
	MoniZoneService moniZoneService;
	
	private TileCalculator tileCalculator;
	
	public TileService() {
		this.tileCalculator = new TileCalculator();
	}
	
	/**
	 * Main method for filtering alert.
	 * 
	 * This method checks all the tiling done in the {@link TileCalculator} to
	 * verify if the alert falls into any {@link MonitoredZone} saved by a {@link User}.
	 * 
	 * @param alert the alert on which the filtering is done.
	 * @return A list of all Monitored zone that the alert is within.
	 */
	public ArrayList<MonitoredZone> filterAlert(Alert alert){
		
		List<MonitoredZone> zoneInTile = tileCalculator.getZones(alert);
		ArrayList<MonitoredZone> zoneWithAlert = new ArrayList<>();
		
		zoneInTile.forEach( zone -> {
			if (zone.containsAlert(alert)) {
				zoneWithAlert.add(zone);
			}
		});
		
		return zoneWithAlert;
	}


	/**
	 * Returns the list of the registrationTokens related to the user to notify
	 * 
	 * @param monitoredZone the monitoredZone that contains an alert
	 * @return the list of registrationTokens of the user
	 */
	public ArrayList<String> getUserToNotifRegTokens(MonitoredZone monitoredZone){

		 return monitoredZone.getUser().getRegistrationToken();
	}
	
	
	/**
	 * Returns the monitored zones located in the given tile index
	 * 
	 * @param tileIndex
	 * @return the list of monitored zones 
	 */
	public ArrayList<MonitoredZone> getZonesByTileIndex(int tileIndex) {
		List<Tile> tiles = tileRepository.findByTileIndex(tileIndex);
		ArrayList<MonitoredZone> mz = new ArrayList<>();
		
		tiles.forEach(tile ->{
			try {
				mz.add(tile.getMonitoredZone());
			} catch (Exception e) {
				Logger.getLogger(TileService.class.getName())
                .log(Level.WARNING, null, e);
			}
			
		});
		
		return mz;
	}
	
	
	/**
	 * Deletes all the tiles related to the given monitored zone
	 * 
	 * @param mz the monitored zone 
	 */
	public void deleteAllTilesOfMz(MonitoredZone mz) {
		
		ArrayList<Tile> tiles = (ArrayList<Tile>) tileRepository.findByMonitoredZone(mz);
		
		if (tiles != null) {
			tiles.forEach(tile -> {
				try {
					tileRepository.delete(tile);
				} catch (Exception e) {
					new BadRequestException("Erreur lors de la suppression des tuiles de la monitored zone: " +
							mz.getZoneId());
				}
			});
		}
	}
	
	
	/**
	 * Returns the tiles located in the given index
	 * 
	 * @param tileIndex
	 * @return the list of monitored zones 
	 */
	public List<Tile> getTilesByIndex(int tileIndex) {
		return tileRepository.findByTileIndex(tileIndex);
	}
	
	/**
	 * Creates a new tile in the repository
	 * 
	 * @param tile
	 */
	public void createTile(Tile tile) {
		tileRepository.save(tile);
	}
	
	/**
	 * Returns the list of tile indexes related to the given monitored zone
	 * 
	 * @param monitored zone
	 * @return list of tile indexes
	 */
	public ArrayList<Integer> findMonitoredZoneTiles(MonitoredZone mz) {
		return tileCalculator.findMonitoredZoneTiles(mz);
	}
	
	/**
	 * Returns the list of monitored zones that contains the given alert
	 * 
	 * @param alert
	 * @return
	 */
	public List<MonitoredZone> getZones(Alert alert) {
		return tileCalculator.getZones(alert);
	}
	
	/**
	 * Used to transfer from 2d matrix coordinates to linear index
	 * 
	 * @param y
	 * @param x
	 * @return
	 */
	public int yxToIndex(int y, int x) {
		return tileCalculator.yxToIndex(y, x);
	}
	
	/**
	 * This method is used to find the coordinates of a tile index
	 * 
	 * @param lat
	 * @param lng
	 * @return
	 */
	public int findTileIndexOfPoint(double lat, double lng) {
		return tileCalculator.findTileIndexOfPoint(lat, lng);
	}
	
	/**
	 * Deletes all the tiles of the given monitored zone
	 * 
	 * @param monitored zone
	 */
	public void deleteAllByMonitoredZone(MonitoredZone mz) {
		tileRepository.deleteAllByMonitoredZone(mz);
	}
	
	/**
	 * Returns all the tiles of the given monitored zone
	 * 
	 * @param monitored zone
	 * @return list of tiles
	 */
	public ArrayList<Tile> findTilesByMz(MonitoredZone mz){
		return (ArrayList<Tile>) tileRepository.findByMonitoredZone(mz);
	}
	
}
