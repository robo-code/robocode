using System;
using System.Collections.Generic;
using System.Text;

namespace Robocode.Control.Snapshot
{
    /**
     * Interface of a debug property, which is a key-value pair.
     *
     * @author Pavel Savara (original)
     *
     * @since 1.6.2
     */
    public interface IDebugProperty
    {
        /**
         * Returns the key of the property.
         *
         * @return the key of the property.
         */
        string Key { get; }

        /**
         * Returns the value of the property.
         *
         * @return the value of the property.
         */
        string Value { get; }
    }
}
