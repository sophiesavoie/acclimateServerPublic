package hackqc18.Acclimate.tile;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import hackqc18.Acclimate.alert.Alert;
import hackqc18.Acclimate.monitoredZone.MonitoredZone;


/**
 * Helper class to manage the Tile calculations and operations.
 * 
 * @author Olivier L. Applin and Sophie Savoie
 *
 */
public class TileCalculator {
	
	@Autowired
	TileService tileService;

	
	/**
	 * Province of Quebec max North coordinates
	 */
	public static double QUEBEC_NORTH = 64.0;
	
	/**
	 * Province of Quebec max South coordinates
	 */
	public static double QUEBEC_SOUTH = 40.0;
	
	/**
	 * Province of Quebec max East coordinates
	 */
	public static double QUEBEC_EAST = -58.0;
	
	/**
	 * Province of Quebec max West coordinates
	 */
	public static double QUEBEC_WEST = -84.0;
	
	/**
	 * Represent the vertical delimitation of the tile used to seperate the province of 
	 * quebec into multiple sub-tiles.
	 */
	private static int QUEBEC_VERTICAL_TILE_AMOUNT = (int) (QUEBEC_NORTH - QUEBEC_SOUTH);
	
	/**
	 * Represent the horizontal delimitation of the tile used to seperate the province of 
	 * quebec into multiple sub-tiles.
	 */
	private static int QUEBEC_HORIZONTAL_TILE_AMOUNT = (int) (QUEBEC_EAST - QUEBEC_WEST);
	
	
	/**
	 * Store all the Tiles in a 2x2 grid.
	 */
	private Tile[][] tileMatrix;
	
	/**
	 * Creates a tileCalculator matrix
	 */
	public TileCalculator() {
	
		tileMatrix = 
				new Tile[(int) QUEBEC_VERTICAL_TILE_AMOUNT]
						[(int) QUEBEC_HORIZONTAL_TILE_AMOUNT];
				
		// put new tile for each matrix index
		for (int i = 0; i < QUEBEC_VERTICAL_TILE_AMOUNT - 1; i++) {
			
			for (int j = 0; j < QUEBEC_HORIZONTAL_TILE_AMOUNT; j++) {
				int index = yxToIndex(i, j);
				tileMatrix[i][j] = new Tile(index);
			}
		}
	}
	
	/**
	 * This method is used to find the index within the tileMatrix in which the coordinates fall.
	 * 
	 * @param lng
	 * @param lat
	 * @return 
	 */
	public int[] findTileMatrixIndex(double lat, double lng) {
		
		int latIndex = (int) ((lat - QUEBEC_SOUTH)) ;
		int lngIndex = (int) ((lng - QUEBEC_WEST)) ;

		return new int[]{latIndex, lngIndex};
		
	}
	
	/**
	 * This method is used to find the coordinates of a tile index
	 * 
	 * @param lat
	 * @param lng
	 * @return
	 */
	public int findTileIndexOfPoint(double lat, double lng) {
		int[] tileMatrixes = findTileMatrixIndex(lat, lng);
		return yxToIndex(tileMatrixes[0], tileMatrixes[1]);
	}
	
	/**
	 * This method is used to find the index within the tileMatrix in which the coordinates fall.
	 * 
	 * @param alert
	 * @return
	 */
	public int[] findTileMatrixIndex(Alert alert) {
		return findTileMatrixIndex(
				alert.getGeometry().getCoordinates()[0], 	// lat
				alert.getGeometry().getCoordinates()[1]);	// lng
		
	}
	
	/**
	 * Returns all the monitoredZones located in the tile
	 * 
	 * @param index
	 * @return
	 */
	public List<MonitoredZone> getZonesOfTile(int index){
		int[] indices = indexToXY(index);
		Tile tile = tileMatrix[indices[0]][indices[1]];
		
		return tileService.getZonesByTileIndex(tile.getTileIndex());
	}
	
	
	/**
	 * Returns all the monitoredZones located in the tile in which the alert falls
	 * 
	 * @param alert
	 * @return
	 */
	public List<MonitoredZone> getZones(Alert alert){
		List<MonitoredZone> mz = new ArrayList<>();
		int[] indices = findTileMatrixIndex(alert);
		int index = yxToIndex(indices[0], indices[1]);
		
		mz = tileService.getZonesByTileIndex(index);
		return mz;
		
	}
	
	/**
	 * Used to transfer from 2d matrix coordinates to linear index
	 * @param x
	 * @param y
	 * @return
	 */
	public int yxToIndex(int y, int x) {
		return y + x * (tileMatrix.length - 1);
	}
	
	/**
	 * Used to transfert from linear index to 2d matrix coordinates
	 * @param index
	 * @return
	 */
	public int[] indexToXY(int index) {
		return new int[] {
			index % (tileMatrix.length - 1),
			index / tileMatrix.length
		};
	}
	
	
	/**
	 * Retourne la liste des index des tuiles dans laquelle se trouve la monitoredZone
	 * 
	 * @return la liste des index de tuiles
	 */
	public ArrayList<Integer> findMonitoredZoneTiles(MonitoredZone mn){
		ArrayList<Integer> tiles = new ArrayList<>();
    	int[] nwMatrixIndexes;
        int[] neMatrixIndexes;
        int[] swMatrixIndexes;
        double north, south, east, west;
		
		if (mn.getGeometry().getType().equals("Polygon")) {
			double[][] coordinates = mn.getGeometry().getPolyCoordinates();
			north = coordinates[0][0];
			west = coordinates[0][1];
			south = coordinates[1][0];
			east = coordinates[1][1];
		}
		
		else {	// type cerle
			double centerLat = mn.getGeometry().getCoordinates()[0];
			double centerLng = mn.getGeometry().getCoordinates()[1];
			double degRad = mn.getRadius() / 111139;
			north = centerLat + degRad;
			south = centerLat - degRad;
			west = centerLng - degRad;
			east = centerLng + degRad;
		}
		
		nwMatrixIndexes = findTileMatrixIndex(north, west);
        neMatrixIndexes = findTileMatrixIndex(north, east);
        swMatrixIndexes = findTileMatrixIndex(south, west);
        
        for (int i = swMatrixIndexes[0]; i <= nwMatrixIndexes[0]; i++) {
        	for (int j = nwMatrixIndexes[1]; j <= neMatrixIndexes[1]; j++) {
        		if (!tiles.contains(yxToIndex(i, j))) {
        			tiles.add(yxToIndex(i, j));
        		}
        	}
        }       
        return tiles;
	}
    
}
