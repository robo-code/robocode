/**
 * Copyright (c) 2001-2019 Mathew A. Nelson and Robocode contributors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * https://robocode.sourceforge.io/license/epl-v10.html
 */
// This class is based on the source code from Sun's Java 1.5.0 API for java.awt.event.KeyEvent, but
// rewritten for C# and .NET with the purpose to bridge the .NET and Java internals of Robocode.

namespace Robocode
{
#pragma warning disable 1591
    /// <exclude/>
    public static class Keys
    {
        public const int KEY_FIRST = 400;

        public const int KEY_LAST = 402;

        public const int KEY_TYPED = KEY_FIRST;

        public const int KEY_PRESSED = 1 + KEY_FIRST;

        public const int KEY_RELEASED = 2 + KEY_FIRST;

        public const int VK_ENTER = 0x0A;
        public const int VK_BACK_SPACE = 0x20;
        public const int VK_TAB = 0x08;
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

        public const int VK_LEFT = 0x25;

        public const int VK_UP = 0x26;

        public const int VK_RIGHT = 0x27;

        public const int VK_DOWN = 0x28;

        public const int VK_COMMA = 0x2C;

        public const int VK_MINUS = 0x2D;

        public const int VK_PERIOD = 0x2E;

        public const int VK_SLASH = 0x2F;

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

        public const int VK_SEMICOLON = 0x3B;

        public const int VK_EQUALS = 0x3D;

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

        public const int VK_OPEN_BRACKET = 0x5B;

        public const int VK_BACK_SLASH = 0x5C;

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

        public const int VK_SEPARATER = 0x6C;

        public const int VK_SEPARATOR = VK_SEPARATER;

        public const int VK_SUBTRACT = 0x6D;
        public const int VK_DECIMAL = 0x6E;
        public const int VK_DIVIDE = 0x6F;
        public const int VK_DELETE = 0x7F;
        public const int VK_NUM_LOCK = 0x90;
        public const int VK_SCROLL_LOCK = 0x91;

        public const int VK_F1 = 0x70;

        public const int VK_F2 = 0x71;

        public const int VK_F3 = 0x72;

        public const int VK_F4 = 0x73;

        public const int VK_F5 = 0x74;

        public const int VK_F6 = 0x75;

        public const int VK_F7 = 0x76;

        public const int VK_F8 = 0x77;

        public const int VK_F9 = 0x78;

        public const int VK_F10 = 0x79;

        public const int VK_F11 = 0x7A;

        public const int VK_F12 = 0x7B;

        public const int VK_F13 = 0xF000;

        public const int VK_F14 = 0xF001;

        public const int VK_F15 = 0xF002;

        public const int VK_F16 = 0xF003;

        public const int VK_F17 = 0xF004;

        public const int VK_F18 = 0xF005;

        public const int VK_F19 = 0xF006;

        public const int VK_F20 = 0xF007;

        public const int VK_F21 = 0xF008;

        public const int VK_F22 = 0xF009;

        public const int VK_F23 = 0xF00A;

        public const int VK_F24 = 0xF00B;

        public const int VK_PRINTSCREEN = 0x9A;
        public const int VK_INSERT = 0x9B;
        public const int VK_HELP = 0x9C;
        public const int VK_META = 0x9D;

        public const int VK_BACK_QUOTE = 0xC0;
        public const int VK_QUOTE = 0xDE;

        public const int VK_KP_UP = 0xE0;

        public const int VK_KP_DOWN = 0xE1;

        public const int VK_KP_LEFT = 0xE2;

        public const int VK_KP_RIGHT = 0xE3;

        public const int VK_DEAD_GRAVE = 0x80;
        public const int VK_DEAD_ACUTE = 0x81;
        public const int VK_DEAD_CIRCUMFLEX = 0x82;
        public const int VK_DEAD_TILDE = 0x83;
        public const int VK_DEAD_MACRON = 0x84;
        public const int VK_DEAD_BREVE = 0x85;
        public const int VK_DEAD_ABOVEDOT = 0x86;
        public const int VK_DEAD_DIAERESIS = 0x87;
        public const int VK_DEAD_ABOVERING = 0x88;
        public const int VK_DEAD_DOUBLEACUTE = 0x89;
        public const int VK_DEAD_CARON = 0x8a;
        public const int VK_DEAD_CEDILLA = 0x8b;
        public const int VK_DEAD_OGONEK = 0x8c;
        public const int VK_DEAD_IOTA = 0x8d;
        public const int VK_DEAD_VOICED_SOUND = 0x8e;
        public const int VK_DEAD_SEMIVOICED_SOUND = 0x8f;

        public const int VK_AMPERSAND = 0x96;
        public const int VK_ASTERISK = 0x97;
        public const int VK_QUOTEDBL = 0x98;
        public const int VK_LESS = 0x99;
        public const int VK_GREATER = 0xa0;
        public const int VK_BRACELEFT = 0xa1;
        public const int VK_BRACERIGHT = 0xa2;

        public const int VK_AT = 0x0200;
        public const int VK_COLON = 0x0201;
        public const int VK_CIRCUMFLEX = 0x0202;
        public const int VK_DOLLAR = 0x0203;
        public const int VK_EURO_SIGN = 0x0204;

        public const int VK_EXCLAMATION_MARK = 0x0205;
        public const int VK_INVERTED_EXCLAMATION_MARK = 0x0206;
        public const int VK_LEFT_PARENTHESIS = 0x0207;
        public const int VK_NUMBER_SIGN = 0x0208;
        public const int VK_PLUS = 0x0209;
        public const int VK_RIGHT_PARENTHESIS = 0x020A;
        public const int VK_UNDERSCORE = 0x020B;
        public const int VK_WINDOWS = 0x020C;
        public const int VK_CONTEXT_MENU = 0x020D;
        public const int VK_const = 0x0018;
        public const int VK_CONVERT = 0x001C;
        public const int VK_NONCONVERT = 0x001D;
        public const int VK_ACCEPT = 0x001E;
        public const int VK_MODECHANGE = 0x001F;
        public const int VK_KANA = 0x0015;
        public const int VK_KANJI = 0x0019;
        public const int VK_ALPHANUMERIC = 0x00F0;
        public const int VK_KATAKANA = 0x00F1;
        public const int VK_HIRAGANA = 0x00F2;
        public const int VK_FULL_WIDTH = 0x00F3;
        public const int VK_HALF_WIDTH = 0x00F4;
        public const int VK_ROMAN_CHARACTERS = 0x00F5;
        public const int VK_ALL_CANDIDATES = 0x0100;
        public const int VK_PREVIOUS_CANDIDATE = 0x0101;
        public const int VK_CODE_INPUT = 0x0102;
        public const int VK_JAPANESE_KATAKANA = 0x0103;
        public const int VK_JAPANESE_HIRAGANA = 0x0104;
        public const int VK_JAPANESE_ROMAN = 0x0105;
        public const int VK_KANA_LOCK = 0x0106;
        public const int VK_INPUT_METHOD_ON_OFF = 0x0107;
        public const int VK_CUT = 0xFFD1;
        public const int VK_COPY = 0xFFCD;
        public const int VK_PASTE = 0xFFCF;
        public const int VK_UNDO = 0xFFCB;
        public const int VK_AGAIN = 0xFFC9;
        public const int VK_FIND = 0xFFD0;
        public const int VK_PROPS = 0xFFCA;
        public const int VK_STOP = 0xFFC8;
        public const int VK_COMPOSE = 0xFF20;
        public const int VK_ALT_GRAPH = 0xFF7E;
        public const int VK_BEGIN = 0xFF58;
        public const int VK_UNDEFINED = 0x0;
        public const int KEY_LOCATION_UNKNOWN = 0;
        public const int KEY_LOCATION_STANDARD = 1;
        public const int KEY_LOCATION_LEFT = 2;
        public const int KEY_LOCATION_RIGHT = 3;
        public const int KEY_LOCATION_NUMPAD = 4;
        public static readonly char CHAR_UNDEFINED = (char) 0xFFFF;
        public const int MOUSE_FIRST 	= 500;
        public const int MOUSE_LAST          = 507;
        public const int MOUSE_CLICKED = MOUSE_FIRST;
        public const int MOUSE_PRESSED = 1 + MOUSE_FIRST; //Event.MOUSE_DOWN
        public const int MOUSE_RELEASED = 2 + MOUSE_FIRST; //Event.MOUSE_UP
        public const int MOUSE_MOVED = 3 + MOUSE_FIRST; //Event.MOUSE_MOVE
        public const int MOUSE_ENTERED = 4 + MOUSE_FIRST; //Event.MOUSE_ENTER
        public const int MOUSE_EXITED = 5 + MOUSE_FIRST; //Event.MOUSE_EXIT
        public const int MOUSE_DRAGGED = 6 + MOUSE_FIRST; //Event.MOUSE_DRAG
        public const int MOUSE_WHEEL = 7 + MOUSE_FIRST; 
        public const int NOBUTTON = 0;
        public const int BUTTON1 = 1;
        public const int BUTTON2 = 2;
        public const int BUTTON3 = 3;

    }
#pragma warning restore 1591
}
