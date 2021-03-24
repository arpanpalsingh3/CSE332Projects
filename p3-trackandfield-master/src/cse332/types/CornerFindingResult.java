package cse332.types;

// The return type for the parallel task CornerFindingTask
public class CornerFindingResult {
	private MapCorners mapCorners;
	private int totalPopulation;
	public CornerFindingResult(MapCorners mapCorners, int totalPopulation){
		this.mapCorners = mapCorners;
		this.totalPopulation = totalPopulation;
	}
	public MapCorners getMapCorners(){
		return mapCorners;
	}
	public int getTotalPopulation(){
		return totalPopulation;
	}
}
