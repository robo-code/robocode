package net.sf.robocode.peer;

/**
 * Exactly the same as BulletCommand. It just has the added bonus that you can also retrieve the component's index from this one.
 * This is needed to know which weapon is even being fired
 * @author Thales B.V./ Thomas Hakkers
 */
public class BulletCommandShip extends BulletCommand{

	private static final long serialVersionUID = -6420971602929346456L;

	private int indexComponent;
	public BulletCommandShip(double power, boolean fireAssistValid, double fireAssistAngle, int bulletId, int indexComponent) {
		super(power, fireAssistValid, fireAssistAngle, bulletId);
		this.indexComponent = indexComponent;
	}
	
	public int getIndexComponent(){
		return indexComponent;
	}

}
