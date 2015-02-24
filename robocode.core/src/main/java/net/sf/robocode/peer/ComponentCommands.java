package net.sf.robocode.peer;

import java.nio.ByteBuffer;

import net.sf.robocode.serialization.ISerializableHelper;
import net.sf.robocode.serialization.RbSerializer;

/**
 * Class that keeps all the Commands that concern Components together. 
 * @author Thales B.V. / Thomas Hakkers
 *
 */
public class ComponentCommands {
	private double turnRemaining;
	private boolean isAdjustForBodyTurn;
	
	public ComponentCommands(double turnRemaining, boolean isAdjustForBodyTurn){
		this.turnRemaining = turnRemaining;
		this.isAdjustForBodyTurn = isAdjustForBodyTurn;
	}
	
	public double getTurnRemaining(){
		return turnRemaining;
	}
	
	public void setTurnRemaining(double turnRemaining){
		this.turnRemaining = turnRemaining;
	}
	
	public boolean isAdjustForBodyTurn(){
		return isAdjustForBodyTurn;
	}
	
	public void setAdjustForBodyTurn(boolean independent){
		isAdjustForBodyTurn = independent;
	}
	
	static ISerializableHelper createHiddenSerializer() {
		return new SerializableHelper();
	}

	private static class SerializableHelper implements ISerializableHelper {
		public int sizeOf(RbSerializer serializer, Object object) {
			return RbSerializer.SIZEOF_TYPEINFO + RbSerializer.SIZEOF_DOUBLE + RbSerializer.SIZEOF_BOOL;
		}

		public void serialize(RbSerializer serializer, ByteBuffer buffer, Object object) {
			ComponentCommands obj = (ComponentCommands) object;

			serializer.serialize(buffer, obj.turnRemaining);
			serializer.serialize(buffer, obj.isAdjustForBodyTurn);
		}

		public Object deserialize(RbSerializer serializer, ByteBuffer buffer) {
			double turnRemaining = serializer.deserializeDouble(buffer);
			boolean isAdjustForBodyTurn = serializer.deserializeBoolean(buffer);

			return new ComponentCommands(turnRemaining, isAdjustForBodyTurn);
		}
	}
}
