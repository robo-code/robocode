﻿#region Copyright (c) 2001, 2010 Mathew A. Nelson and Robocode contributors

// Copyright (c) 2001, 2008 Mathew A. Nelson and Robocode contributors
// All rights reserved. This program and the accompanying materials
// are made available under the terms of the Common Public License v1.0
// which accompanies this distribution, and is available at
// http://robocode.sourceforge.net/license/cpl-v10.html

#endregion

namespace robocode.robocode
{
    public static class Keys
    {
        /**
        * The first number in the range of ids used for key events.
        */
        public const int KEY_FIRST = 400;

        /**
        * The last number in the range of ids used for key events.
        */
        public const int KEY_LAST = 402;

        /**
        * The "key typed" event.  This event is generated when a character is
        * entered.  In the simplest case, it is produced by a single key press.  
        * Often, however, characters are produced by series of key presses, and 
        * the mapping from key pressed events to key typed events may be 
        * many-to-one or many-to-many.  
        */
        public const int KEY_TYPED = KEY_FIRST;

        /**
        * The "key pressed" event. This event is generated when a key
        * is pushed down.
        */
        public const int KEY_PRESSED = 1 + KEY_FIRST; //Event.KEY_PRESS

        /**
        * The "key released" event. This event is generated when a key
        * is let up.
        */
        public const int KEY_RELEASED = 2 + KEY_FIRST; //Event.KEY_RELEASE

        /* Virtual key codes. */

        public const int VK_ENTER = '\n';
        public const int VK_BACK_SPACE = '\b';
        public const int VK_TAB = '\t';
        public const int VK_CANCEL = 0x03;
        public const int VK_CLEAR = 0x0C;
        public const int VK_SHIFT = 0x10;
        public const int VK_CONTROL = 0x11;
        public const int VK_ALT = 0x12;
        public const int VK_PAUSE = 0x13;
        public const int VK_CAPS_LOCK = 0x14;
        public const int VK_ESCAPE = 0x1B;
        public const int VK_SPACE = 0x20;
        public const int VK_PAGE_UP = 0x21;
        public const int VK_PAGE_DOWN = 0x22;
        public const int VK_END = 0x23;
        public const int VK_HOME = 0x24;

        /**
        * Constant for the non-numpad <b>left</b> arrow key.
        * @see #VK_KP_LEFT
        */
        public const int VK_LEFT = 0x25;

        /**
        * Constant for the non-numpad <b>up</b> arrow key.
        * @see #VK_KP_UP
        */
        public const int VK_UP = 0x26;

        /**
        * Constant for the non-numpad <b>right</b> arrow key.
        * @see #VK_KP_RIGHT
        */
        public const int VK_RIGHT = 0x27;

        /**
        * Constant for the non-numpad <b>down</b> arrow key.
        * @see #VK_KP_DOWN
        */
        public const int VK_DOWN = 0x28;

        /**
        * Constant for the comma key, ","
        */
        public const int VK_COMMA = 0x2C;

        /**
        * Constant for the minus key, "-"
        * @since 1.2
        */
        public const int VK_MINUS = 0x2D;

        /**
        * Constant for the period key, "."
        */
        public const int VK_PERIOD = 0x2E;

        /**
        * Constant for the forward slash key, "/"
        */
        public const int VK_SLASH = 0x2F;

        /** VK_0 thru VK_9 are the same as ASCII '0' thru '9' (0x30 - 0x39) */
        public const int VK_0 = 0x30;
        public const int VK_1 = 0x31;
        public const int VK_2 = 0x32;
        public const int VK_3 = 0x33;
        public const int VK_4 = 0x34;
        public const int VK_5 = 0x35;
        public const int VK_6 = 0x36;
        public const int VK_7 = 0x37;
        public const int VK_8 = 0x38;
        public const int VK_9 = 0x39;

        /**
        * Constant for the semicolon key, ";"
        */
        public const int VK_SEMICOLON = 0x3B;

        /**
        * Constant for the equals key, "="
        */
        public const int VK_EQUALS = 0x3D;

        /** VK_A thru VK_Z are the same as ASCII 'A' thru 'Z' (0x41 - 0x5A) */
        public const int VK_A = 0x41;
        public const int VK_B = 0x42;
        public const int VK_C = 0x43;
        public const int VK_D = 0x44;
        public const int VK_E = 0x45;
        public const int VK_F = 0x46;
        public const int VK_G = 0x47;
        public const int VK_H = 0x48;
        public const int VK_I = 0x49;
        public const int VK_J = 0x4A;
        public const int VK_K = 0x4B;
        public const int VK_L = 0x4C;
        public const int VK_M = 0x4D;
        public const int VK_N = 0x4E;
        public const int VK_O = 0x4F;
        public const int VK_P = 0x50;
        public const int VK_Q = 0x51;
        public const int VK_R = 0x52;
        public const int VK_S = 0x53;
        public const int VK_T = 0x54;
        public const int VK_U = 0x55;
        public const int VK_V = 0x56;
        public const int VK_W = 0x57;
        public const int VK_X = 0x58;
        public const int VK_Y = 0x59;
        public const int VK_Z = 0x5A;

        /**
        * Constant for the open bracket key, "["
        */
        public const int VK_OPEN_BRACKET = 0x5B;

        /**
        * Constant for the Back slash key, "\"
        */
        public const int VK_BACK_SLASH = 0x5C;

        /**
        * Constant for the close bracket key, "]"
        */
        public const int VK_CLOSE_BRACKET = 0x5D;

        public const int VK_NUMPAD0 = 0x60;
        public const int VK_NUMPAD1 = 0x61;
        public const int VK_NUMPAD2 = 0x62;
        public const int VK_NUMPAD3 = 0x63;
        public const int VK_NUMPAD4 = 0x64;
        public const int VK_NUMPAD5 = 0x65;
        public const int VK_NUMPAD6 = 0x66;
        public const int VK_NUMPAD7 = 0x67;
        public const int VK_NUMPAD8 = 0x68;
        public const int VK_NUMPAD9 = 0x69;
        public const int VK_MULTIPLY = 0x6A;
        public const int VK_ADD = 0x6B;

        /** 
        * This constant is obsolete, and is included only for backwards
        * compatibility.
        * @see #VK_SEPARATOR
        */
        public const int VK_SEPARATER = 0x6C;

        /** 
        * Constant for the Numpad Separator key. 
        * @since 1.4
        */
        public const int VK_SEPARATOR = VK_SEPARATER;

        public const int VK_SUBTRACT = 0x6D;
        public const int VK_DECIMAL = 0x6E;
        public const int VK_DIVIDE = 0x6F;
        public const int VK_DELETE = 0x7F; /* ASCII DEL */
        public const int VK_NUM_LOCK = 0x90;
        public const int VK_SCROLL_LOCK = 0x91;

        /** Constant for the F1 function key. */
        public const int VK_F1 = 0x70;

        /** Constant for the F2 function key. */
        public const int VK_F2 = 0x71;

        /** Constant for the F3 function key. */
        public const int VK_F3 = 0x72;

        /** Constant for the F4 function key. */
        public const int VK_F4 = 0x73;

        /** Constant for the F5 function key. */
        public const int VK_F5 = 0x74;

        /** Constant for the F6 function key. */
        public const int VK_F6 = 0x75;

        /** Constant for the F7 function key. */
        public const int VK_F7 = 0x76;

        /** Constant for the F8 function key. */
        public const int VK_F8 = 0x77;

        /** Constant for the F9 function key. */
        public const int VK_F9 = 0x78;

        /** Constant for the F10 function key. */
        public const int VK_F10 = 0x79;

        /** Constant for the F11 function key. */
        public const int VK_F11 = 0x7A;

        /** Constant for the F12 function key. */
        public const int VK_F12 = 0x7B;

        /**
        * Constant for the F13 function key.
        * @since 1.2
        */
        /* F13 - F24 are used on IBM 3270 keyboard; use random range for constants. */
        public const int VK_F13 = 0xF000;

        /**
        * Constant for the F14 function key.
        * @since 1.2
        */
        public const int VK_F14 = 0xF001;

        /**
        * Constant for the F15 function key.
        * @since 1.2
        */
        public const int VK_F15 = 0xF002;

        /**
        * Constant for the F16 function key.
        * @since 1.2
        */
        public const int VK_F16 = 0xF003;

        /**
        * Constant for the F17 function key.
        * @since 1.2
        */
        public const int VK_F17 = 0xF004;

        /**
        * Constant for the F18 function key.
        * @since 1.2
        */
        public const int VK_F18 = 0xF005;

        /**
        * Constant for the F19 function key.
        * @since 1.2
        */
        public const int VK_F19 = 0xF006;

        /**
        * Constant for the F20 function key.
        * @since 1.2
        */
        public const int VK_F20 = 0xF007;

        /**
        * Constant for the F21 function key.
        * @since 1.2
        */
        public const int VK_F21 = 0xF008;

        /**
        * Constant for the F22 function key.
        * @since 1.2
        */
        public const int VK_F22 = 0xF009;

        /**
        * Constant for the F23 function key.
        * @since 1.2
        */
        public const int VK_F23 = 0xF00A;

        /**
        * Constant for the F24 function key.
        * @since 1.2
        */
        public const int VK_F24 = 0xF00B;

        public const int VK_PRINTSCREEN = 0x9A;
        public const int VK_INSERT = 0x9B;
        public const int VK_HELP = 0x9C;
        public const int VK_META = 0x9D;

        public const int VK_BACK_QUOTE = 0xC0;
        public const int VK_QUOTE = 0xDE;

        /**
        * Constant for the numeric keypad <b>up</b> arrow key.
        * @see #VK_UP
        * @since 1.2
        */
        public const int VK_KP_UP = 0xE0;

        /**
        * Constant for the numeric keypad <b>down</b> arrow key.
        * @see #VK_DOWN
        * @since 1.2
        */
        public const int VK_KP_DOWN = 0xE1;

        /**
        * Constant for the numeric keypad <b>left</b> arrow key.
        * @see #VK_LEFT
        * @since 1.2
        */
        public const int VK_KP_LEFT = 0xE2;

        /**
        * Constant for the numeric keypad <b>right</b> arrow key.
        * @see #VK_RIGHT
        * @since 1.2
        */
        public const int VK_KP_RIGHT = 0xE3;

        /* For European keyboards */
        /** @since 1.2 */
        public const int VK_DEAD_GRAVE = 0x80;
        /** @since 1.2 */
        public const int VK_DEAD_ACUTE = 0x81;
        /** @since 1.2 */
        public const int VK_DEAD_CIRCUMFLEX = 0x82;
        /** @since 1.2 */
        public const int VK_DEAD_TILDE = 0x83;
        /** @since 1.2 */
        public const int VK_DEAD_MACRON = 0x84;
        /** @since 1.2 */
        public const int VK_DEAD_BREVE = 0x85;
        /** @since 1.2 */
        public const int VK_DEAD_ABOVEDOT = 0x86;
        /** @since 1.2 */
        public const int VK_DEAD_DIAERESIS = 0x87;
        /** @since 1.2 */
        public const int VK_DEAD_ABOVERING = 0x88;
        /** @since 1.2 */
        public const int VK_DEAD_DOUBLEACUTE = 0x89;
        /** @since 1.2 */
        public const int VK_DEAD_CARON = 0x8a;
        /** @since 1.2 */
        public const int VK_DEAD_CEDILLA = 0x8b;
        /** @since 1.2 */
        public const int VK_DEAD_OGONEK = 0x8c;
        /** @since 1.2 */
        public const int VK_DEAD_IOTA = 0x8d;
        /** @since 1.2 */
        public const int VK_DEAD_VOICED_SOUND = 0x8e;
        /** @since 1.2 */
        public const int VK_DEAD_SEMIVOICED_SOUND = 0x8f;

        /** @since 1.2 */
        public const int VK_AMPERSAND = 0x96;
        /** @since 1.2 */
        public const int VK_ASTERISK = 0x97;
        /** @since 1.2 */
        public const int VK_QUOTEDBL = 0x98;
        /** @since 1.2 */
        public const int VK_LESS = 0x99;

        /** @since 1.2 */
        public const int VK_GREATER = 0xa0;
        /** @since 1.2 */
        public const int VK_BRACELEFT = 0xa1;
        /** @since 1.2 */
        public const int VK_BRACERIGHT = 0xa2;

        /**
        * Constant for the "@" key.
        * @since 1.2
        */
        public const int VK_AT = 0x0200;

        /**
        * Constant for the ":" key.
        * @since 1.2
        */
        public const int VK_COLON = 0x0201;

        /**
        * Constant for the "^" key.
        * @since 1.2
        */
        public const int VK_CIRCUMFLEX = 0x0202;

        /**
        * Constant for the "$" key.
        * @since 1.2
        */
        public const int VK_DOLLAR = 0x0203;

        /**
        * Constant for the Euro currency sign key.
        * @since 1.2
        */
        public const int VK_EURO_SIGN = 0x0204;

        /**
        * Constant for the "!" key.
        * @since 1.2
        */
        public const int VK_EXCLAMATION_MARK = 0x0205;

        /**
        * Constant for the inverted exclamation mark key.
        * @since 1.2
        */
        public const int VK_INVERTED_EXCLAMATION_MARK = 0x0206;

        /**
        * Constant for the "(" key.
        * @since 1.2
        */
        public const int VK_LEFT_PARENTHESIS = 0x0207;

        /**
        * Constant for the "#" key.
        * @since 1.2
        */
        public const int VK_NUMBER_SIGN = 0x0208;

        /**
        * Constant for the "+" key.
        * @since 1.2
        */
        public const int VK_PLUS = 0x0209;

        /**
        * Constant for the ")" key.
        * @since 1.2
        */
        public const int VK_RIGHT_PARENTHESIS = 0x020A;

        /**
        * Constant for the "_" key.
        * @since 1.2
        */
        public const int VK_UNDERSCORE = 0x020B;

        /**
        * Constant for the Microsoft Windows "Windows" key.
        * It is used for both the left and right version of the key.  
        * @see #getKeyLocation()
        * @since 1.5
        */
        public const int VK_WINDOWS = 0x020C;

        /**
        * Constant for the Microsoft Windows Context Menu key.
        * @since 1.5
        */
        public const int VK_CONTEXT_MENU = 0x020D;

        /* for input method support on Asian Keyboards */

        /* not clear what this means - listed in Microsoft Windows API */
        public const int VK_const = 0x0018;

        /** Constant for the Convert function key. */
        /* Japanese PC 106 keyboard, Japanese Solaris keyboard: henkan */
        public const int VK_CONVERT = 0x001C;

        /** Constant for the Don't Convert function key. */
        /* Japanese PC 106 keyboard: muhenkan */
        public const int VK_NONCONVERT = 0x001D;

        /** Constant for the Accept or Commit function key. */
        /* Japanese Solaris keyboard: kakutei */
        public const int VK_ACCEPT = 0x001E;

        /* not clear what this means - listed in Microsoft Windows API */
        public const int VK_MODECHANGE = 0x001F;

        /* replaced by VK_KANA_LOCK for Microsoft Windows and Solaris; 
        might still be used on other platforms */
        public const int VK_KANA = 0x0015;

        /* replaced by VK_INPUT_METHOD_ON_OFF for Microsoft Windows and Solaris; 
        might still be used for other platforms */
        public const int VK_KANJI = 0x0019;

        /**
        * Constant for the Alphanumeric function key.
        * @since 1.2
        */
        /* Japanese PC 106 keyboard: eisuu */
        public const int VK_ALPHANUMERIC = 0x00F0;

        /**
        * Constant for the Katakana function key.
        * @since 1.2
        */
        /* Japanese PC 106 keyboard: katakana */
        public const int VK_KATAKANA = 0x00F1;

        /**
        * Constant for the Hiragana function key.
        * @since 1.2
        */
        /* Japanese PC 106 keyboard: hiragana */
        public const int VK_HIRAGANA = 0x00F2;

        /**
        * Constant for the Full-Width Characters function key.
        * @since 1.2
        */
        /* Japanese PC 106 keyboard: zenkaku */
        public const int VK_FULL_WIDTH = 0x00F3;

        /**
        * Constant for the Half-Width Characters function key.
        * @since 1.2
        */
        /* Japanese PC 106 keyboard: hankaku */
        public const int VK_HALF_WIDTH = 0x00F4;

        /**
        * Constant for the Roman Characters function key.
        * @since 1.2
        */
        /* Japanese PC 106 keyboard: roumaji */
        public const int VK_ROMAN_CHARACTERS = 0x00F5;

        /**
        * Constant for the All Candidates function key.
        * @since 1.2
        */
        /* Japanese PC 106 keyboard - VK_CONVERT + ALT: zenkouho */
        public const int VK_ALL_CANDIDATES = 0x0100;

        /**
        * Constant for the Previous Candidate function key.
        * @since 1.2
        */
        /* Japanese PC 106 keyboard - VK_CONVERT + SHIFT: maekouho */
        public const int VK_PREVIOUS_CANDIDATE = 0x0101;

        /**
        * Constant for the Code Input function key.
        * @since 1.2
        */
        /* Japanese PC 106 keyboard - VK_ALPHANUMERIC + ALT: kanji bangou */
        public const int VK_CODE_INPUT = 0x0102;

        /**
        * Constant for the Japanese-Katakana function key.
        * This key switches to a Japanese input method and selects its Katakana input mode.
        * @since 1.2
        */
        /* Japanese Macintosh keyboard - VK_JAPANESE_HIRAGANA + SHIFT */
        public const int VK_JAPANESE_KATAKANA = 0x0103;

        /**
        * Constant for the Japanese-Hiragana function key.
        * This key switches to a Japanese input method and selects its Hiragana input mode.
        * @since 1.2
        */
        /* Japanese Macintosh keyboard */
        public const int VK_JAPANESE_HIRAGANA = 0x0104;

        /**
        * Constant for the Japanese-Roman function key.
        * This key switches to a Japanese input method and selects its Roman-Direct input mode.
        * @since 1.2
        */
        /* Japanese Macintosh keyboard */
        public const int VK_JAPANESE_ROMAN = 0x0105;

        /**
        * Constant for the locking Kana function key.
        * This key locks the keyboard into a Kana layout.
        * @since 1.3
        */
        /* Japanese PC 106 keyboard with special Windows driver - eisuu + Control; Japanese Solaris keyboard: kana */
        public const int VK_KANA_LOCK = 0x0106;

        /**
        * Constant for the input method on/off key.
        * @since 1.3
        */
        /* Japanese PC 106 keyboard: kanji. Japanese Solaris keyboard: nihongo */
        public const int VK_INPUT_METHOD_ON_OFF = 0x0107;

        /* for Sun keyboards */
        /** @since 1.2 */
        public const int VK_CUT = 0xFFD1;
        /** @since 1.2 */
        public const int VK_COPY = 0xFFCD;
        /** @since 1.2 */
        public const int VK_PASTE = 0xFFCF;
        /** @since 1.2 */
        public const int VK_UNDO = 0xFFCB;
        /** @since 1.2 */
        public const int VK_AGAIN = 0xFFC9;
        /** @since 1.2 */
        public const int VK_FIND = 0xFFD0;
        /** @since 1.2 */
        public const int VK_PROPS = 0xFFCA;
        /** @since 1.2 */
        public const int VK_STOP = 0xFFC8;

        /**
        * Constant for the Compose function key.
        * @since 1.2
        */
        public const int VK_COMPOSE = 0xFF20;

        /**
        * Constant for the AltGraph function key.
        * @since 1.2
        */
        public const int VK_ALT_GRAPH = 0xFF7E;

        /**
        * Constant for the Begin key.
        * @since 1.5
        */
        public const int VK_BEGIN = 0xFF58;

        /**
        * This value is used to indicate that the keyCode is unknown.
        * KEY_TYPED events do not have a keyCode value; this value 
        * is used instead.  
        */
        public const int VK_UNDEFINED = 0x0;

        /**
        * KEY_PRESSED and KEY_RELEASED events which do not map to a
        * valid Unicode character use this for the keyChar value.
        */

        /**
        * A constant indicating that the keyLocation is indeterminate
        * or not relevant.
        * <code>KEY_TYPED</code> events do not have a keyLocation; this value
        * is used instead.
        * @since 1.4
        */
        public const int KEY_LOCATION_UNKNOWN = 0;

        /**
        * A constant indicating that the key pressed or released
        * is not distinguished as the left or right version of a key,
        * and did not originate on the numeric keypad (or did not
        * originate with a virtual key corresponding to the numeric
        * keypad).
        * @since 1.4
        */
        public const int KEY_LOCATION_STANDARD = 1;

        /**
        * A constant indicating that the key pressed or released is in
        * the left key location (there is more than one possible location
        * for this key).  Example: the left shift key.
        * @since 1.4
        */
        public const int KEY_LOCATION_LEFT = 2;

        /**
        * A constant indicating that the key pressed or released is in
        * the right key location (there is more than one possible location
        * for this key).  Example: the right shift key.
        * @since 1.4
        */
        public const int KEY_LOCATION_RIGHT = 3;

        /**
        * A constant indicating that the key event originated on the
        * numeric keypad or with a virtual key corresponding to the
        * numeric keypad.
        * @since 1.4
        */
        public const int KEY_LOCATION_NUMPAD = 4;
        public static readonly char CHAR_UNDEFINED = (char) 0xFFFF;


        /**
         * The first number in the range of ids used for mouse events.
         */
        public const int MOUSE_FIRST 	= 500;

        /**
         * The last number in the range of ids used for mouse events.
         */
        public const int MOUSE_LAST          = 507;

        /**
         * The "mouse clicked" event. This <code>MouseEvent</code>
         * occurs when a mouse button is pressed and released.
         */
        public const int MOUSE_CLICKED = MOUSE_FIRST;

        /**
         * The "mouse pressed" event. This <code>MouseEvent</code>
         * occurs when a mouse button is pushed down.
         */
        public const int MOUSE_PRESSED = 1 + MOUSE_FIRST; //Event.MOUSE_DOWN

        /**
         * The "mouse released" event. This <code>MouseEvent</code>
         * occurs when a mouse button is let up.
         */
        public const int MOUSE_RELEASED = 2 + MOUSE_FIRST; //Event.MOUSE_UP

        /**
         * The "mouse moved" event. This <code>MouseEvent</code>
         * occurs when the mouse position changes.
         */
        public const int MOUSE_MOVED = 3 + MOUSE_FIRST; //Event.MOUSE_MOVE

        /**
         * The "mouse entered" event. This <code>MouseEvent</code>
         * occurs when the mouse cursor enters the unobscured part of component's
         * geometry. 
         */
        public const int MOUSE_ENTERED = 4 + MOUSE_FIRST; //Event.MOUSE_ENTER

        /**
         * The "mouse exited" event. This <code>MouseEvent</code>
         * occurs when the mouse cursor exits the unobscured part of component's
         * geometry.
         */
        public const int MOUSE_EXITED = 5 + MOUSE_FIRST; //Event.MOUSE_EXIT

        /**
         * The "mouse dragged" event. This <code>MouseEvent</code>
         * occurs when the mouse position changes while a mouse button is pressed.
         */
        public const int MOUSE_DRAGGED = 6 + MOUSE_FIRST; //Event.MOUSE_DRAG

        /**
         * The "mouse wheel" event.  This is the only <code>MouseWheelEvent</code>.
         * It occurs when a mouse equipped with a wheel has its wheel rotated.
         * @since 1.4
         */
        public const int MOUSE_WHEEL = 7 + MOUSE_FIRST; 

        /**
         * Indicates no mouse buttons; used by {@link #getButton}. 
         * @since 1.4
         */ 
        public const int NOBUTTON = 0;

        /**
         * Indicates mouse button #1; used by {@link #getButton}.
         * @since 1.4
         */ 
        public const int BUTTON1 = 1;

        /**
         * Indicates mouse button #2; used by {@link #getButton}.
         * @since 1.4
         */ 
        public const int BUTTON2 = 2;

        /**
         * Indicates mouse button #3; used by {@link #getButton}.
         * @since 1.4
         */ 
        public const int BUTTON3 = 3;

    }
}