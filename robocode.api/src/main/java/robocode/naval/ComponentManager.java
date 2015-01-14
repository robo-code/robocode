package robocode.naval;

import java.awt.Color;
import java.io.Serializable;
import java.lang.reflect.Array;
import java.nio.ByteBuffer;
import java.util.ArrayList;

import net.sf.robocode.io.Logger;
import net.sf.robocode.serialization.ISerializableHelper;
import net.sf.robocode.serialization.RbSerializer;
import robocode.naval.interfaces.IComponent;
import robocode.robotinterfaces.IComponents;

/**
 * Manages the different components that can be assigned to a ship.
 * @author Thales B.V. / Jiri Waning / Thomas Hakkers
 * @since 1.8.3.0 Alpha 1
 * @version 0.3
 */
public class ComponentManager implements Serializable, IComponents {
		
	// ======================================================================
	//  Fields
	// ======================================================================
	private static final long serialVersionUID = 6105556384436141732L;

	/**
	 * The maximum amount of components that can be added to a ship.
	 */
	public static final int MAX_COMPONENTS = 4;
	
	/**
	 * The list containing all the components.
	 */
	private ArrayList<IComponent> components;

	
	
	
	
	// ======================================================================
	//  Constructors
	// ======================================================================
	public ComponentManager() {
		this.components = new ArrayList<IComponent>(MAX_COMPONENTS);
	}
	
	// Is being used to make copies of an ComponentManager instance.
	private ComponentManager(ArrayList<IComponent> components) {
		this.components = components;
	}
		
		
	/**
	 * @returns all the components in the form of an ArrayList<IComponent>
	 */
	public ArrayList<IComponent> getComponentArrayList(){
		return components;
	}
	
	// ======================================================================
	//  Miscellaneous
	// ======================================================================
	
	/**
	 * Get a copy of the given {@code ComponentManager}.
	 * @param manager The manager that has to be copied.
	 * @return A copy of the manager.
	 */
	public static final ComponentManager copy(ComponentManager manager) {
		if (manager == null) {
			return null;
		}
		return new ComponentManager(manager.components);
	}
	
	/**
	 * {@inheritDoc}
	 */
	public int size() {
		return components.size();
	}
	
	/**
	 * Determines whether the given index is a valid index for this
	 * {@code ComponentManager}.
	 * @param index The index we want to validate.
	 * @return {@code true} when the given index is valid; {@code false} otherwise.
	 */
	public boolean isValidIndex(int index) {
		return (index >= 0 && index < components.size() && components.size() != 0);
	}
	
	/**
	 * {@inheritDoc}
	 */
	public <T extends IComponent> int indexOf(T value) {
		if (components.contains(value)) {
			return components.indexOf(value);
		}
		return -1;
	}
	
	/**
	 * Counts the amount of components that matches the given class.
	 * @param componentClass The class of the components.
	 * @return The amount of components of the specified class.
	 */
	private <T extends IComponent> int count(Class<T> componentClass) {
		int total = 0;
		
		for (IComponent component: components) {
			if (component == null) {
				continue;
			}

			// componentClass.class and its super classes
			if (componentClass.isAssignableFrom(component.getClass()) ||
				component.getClass().equals(componentClass)) {
				total++;
			}
		}
		
		return total;
	}
	
	
	
	
	
	// ======================================================================
	//  Modifiers
	// ======================================================================
	
	/**
	 * {@inheritDoc}
	 */
	public void removeComponent(int index) {
		if (isValidIndex(index)) {
			components.remove(index);
		}
		throw new IndexOutOfBoundsException();
	}
	
	/**
	 * {@inheritDoc}
	 */
	public IComponent getComponent(int index) {
		if (isValidIndex(index)) {
			return components.get(index);
		}
		throw new IndexOutOfBoundsException();
	}
	
	// ======================================================================
	//  Generic Modifiers
	// ======================================================================
	
	/**
	 * {@inheritDoc}
	 */
	public void addComponent(IComponent component) {
		if (components.size() != MAX_COMPONENTS) {
			if (!components.contains(component)) {
				components.add(component);
			} else {
				Logger.logWarning("The ship already contains the specified component.");
			}
		} else {
			Logger.logWarning("You already have the maximum permitted amount of components.\r\nWhich is " + MAX_COMPONENTS);
		}
	}
	
	/**
	 * {@inheritDoc}
	 */
	public void addComponent(int index, IComponent component) {
		if (!(index >= 0 && index < MAX_COMPONENTS)) {
			throw new Error("The index of the component that had to be added at the " +
					"specified index of " + index + " was out of the maximum permitted " +
					" range of 0 to " + MAX_COMPONENTS + ".");
		}

		while (index >= components.size()) {
			components.add(null);
		}
		components.add(index, component);
	}
	
	/**
	 * {@inheritDoc}
	 */
	public <T extends IComponent> void removeComponent(T component) {
		if (components.contains(component)) {
			components.remove(component);
		}
	}
	
	/**
	 * {@inheritDoc}
	 */
	public <T extends IComponent> void removeComponents(Class<T> componentClass) {
		for (IComponent component: components) {
			if (component == null) {
				continue;
			}
			
			// componentClass.class and its super classes
			if (componentClass.isAssignableFrom(component.getClass()) ||
				component.getClass().equals(componentClass)) {
				components.remove(component);
			}
		}
	}
	
	/**
	 * {@inheritDoc}
	 */
	public <T extends IComponent> T getComponent(Class<T> componentClass) {
		for (IComponent component: components) {
			if (component == null) {
				continue;
			}
			
			// componentClass.class and its super classes
			if (componentClass.isAssignableFrom(component.getClass()) ||
				component.getClass().equals(componentClass)) {
				@SuppressWarnings("unchecked") // Object ==> T
				T obj = (T)component;
				return obj;
			}
		}
		return null;
	}
	
	/**
	 * {@inheritDoc}
	 */
	public <T extends IComponent> T[] getComponents(Class<T> componentClass) {
		@SuppressWarnings("unchecked") // Object ==> Array
		T[] components = (T[])Array.newInstance(componentClass, count(componentClass));
		
		// componentClass.class and its super classes
		ArrayList<T> list = new ArrayList<T>();
		for (IComponent component: this.components) {
			if (component == null) {
				continue;
			}
			
			if (componentClass.isAssignableFrom(component.getClass()) ||
				component.getClass().equals(componentClass)) {
				@SuppressWarnings("unchecked")
				T obj = (T)component;
				list.add(obj);
			}
		}
		
		list.toArray(components);
		return components;
	}
	
	
	/**
	 * Set the color of the component at the specified index.
	 * @param index The index of the component.
	 * @param color The color that the component should have.
	 */
	public void setColor(int index, Color color) {
		if (isValidIndex(index)) {
			components.get(index).setColor(color);
		}
	}
	
	/**
	 * Get the color of the component at the specified index.
	 * @param index The index of the component.
	 * @return The color of the component.
	 */
	public Color getColor(int index) {
		// By default the return value will be white.
		Color color = Color.WHITE;
		
		// When the index is valid we get the color of the component itself.
		if (isValidIndex(index)) {
			color = components.get(index).getColor();
		}
		
		return color;
	}

	static ISerializableHelper createHiddenSerializer() {
		return new SerializableHelper();
	}

	private static class SerializableHelper implements ISerializableHelper {
		public int sizeOf(RbSerializer serializer, Object object) {
			int size = 0;
			ComponentManager obj = (ComponentManager) object;
			for(IComponent component : obj.components){
				size += serializer.sizeOf(component.getSerializeType(), component);
			}
			
			return RbSerializer.SIZEOF_TYPEINFO + RbSerializer.SIZEOF_INT + size ;
		}
		

		public void serialize(RbSerializer serializer, ByteBuffer buffer, Object object) {
			ComponentManager obj = (ComponentManager) object;
			serializer.serialize(buffer, obj.size());
			for(IComponent component : obj.components){
				serializer.serialize(buffer, component.getSerializeType(), object);
			}
		}

		public Object deserialize(RbSerializer serializer, ByteBuffer buffer) {
			ArrayList<IComponent> components = new ArrayList<IComponent>(ComponentManager.MAX_COMPONENTS);
			int size = serializer.deserializeInt(buffer);
			for(int i = 0; i < size; ++i){
				ComponentBase component = (ComponentBase)serializer.deserializeAny(buffer);
				components.add(component);
			}
			return new ComponentManager(components);
		}
	}
}