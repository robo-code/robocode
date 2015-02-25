package robocode.robotinterfaces;

import robocode.naval.interfaces.IComponent;

/**
 * An interface describing the functionalities of manageable components.
 * @author Thales B.V. / Jiri Waning
 * @since 1.8.3 Alpha 1
 * @version 0.1
 */
public interface IComponents {
	
	// ======================================================================
	//  Miscellaneous
	// ======================================================================
	
//	/**
//	 * Get the amount of components.
//	 * @return The amount of components.
//	 */
//	int size();
	
	/**
	 * Get the index of the specified component.
	 * <p/>
	 * {@code -1} when the component has not available.
	 * @param value The component from whom to get the index.
	 * @return The index of the component.
	 */
	<T extends IComponent> int indexOf(T value);
	
	
	
	
	
	// ======================================================================
	//  Modifiers
	// ======================================================================
	
	/**
	 * Remove the component at the specified index.
	 * @param index The index of the component that has to be removed.
	 */
	void removeComponent(int index);
	
	/**
	 * Get the component at the specified index.
	 * @param index The index of the component.
	 * @return The component at the specified index.
	 */
	IComponent getComponent(int index);
	
	
	
	
	
	// ======================================================================
	//  Generic Modifiers
	// ======================================================================
	
	/**
	 * Add a component.
	 * @param component The component that has to be added.
	 */
	void addComponent(IComponent component);
	
	/**
	 * Add a component on the specified index.
	 * @param index The index at which the add the component.
	 * @param component The component whom has to be added.
	 */
	void addComponent(int index, IComponent component);
	
	/**
	 * Remove the specified component.
	 * @param component The component to remove.
	 */
	<T extends IComponent> void removeComponent(T component);
	
	/**
	 * Remove the components that match the specified class.
	 * @param componentClass The class of the objects to remove.
	 */
	<T extends IComponent> void removeComponents(Class<T> componentClass);
	
	/**
	 * Get the first component that matches the given class.
	 * <p/>
	 * {@code null} when there is no component of the given class.
	 * @param componentClass The class of the component.
	 * @return The first component that matches the given class.
	 */
	<T extends IComponent> T getComponent(Class<T> componentClass);
	
	/**
	 * Get an array of components that match the given class.
	 * <p/>
	 * {@code null} when there are no components matching the given class.
	 * @param componentClass The common class of the objects.
	 * @return An array of components that match the given class.
	 */
	<T extends IComponent> T[] getComponents(Class<T> componentClass);
}