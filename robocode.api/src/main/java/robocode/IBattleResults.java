package robocode;


import java.util.List;


public interface IBattleResults extends java.io.Serializable, Comparable<IBattleResults> {
	
	String getTeamName();
	int getRank();
	double getCombinedScore();
	List<Double> getScores();
	List<String> getScoreNames();
	
	int getFirsts();
	int getSeconds();
	int getThirds();
	
	// TODO: this might be bad form (variables in an interface)
	// perhaps an abstract base class would be appropriate?
	int combinedScore = 0;
}
