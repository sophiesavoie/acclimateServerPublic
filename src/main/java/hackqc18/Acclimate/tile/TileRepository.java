package hackqc18.Acclimate.tile;

import java.util.ArrayList;
import java.util.List;

import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import hackqc18.Acclimate.monitoredZone.MonitoredZone;


/**
 * Repository class for tiles
 * 
 * @author Sophie Savoie and Olivier Lepage-Applin
 *
 */
@Repository
public interface TileRepository extends CrudRepository<Tile, Integer> {
	
	List<Tile> findByTileIndex(int tileIndex);

	List<Tile> findByMonitoredZone(MonitoredZone mz);
	
	@Transactional
	void deleteAllByMonitoredZone(MonitoredZone mz);

}
