package it.fmuia.apps.jrobot.jna.jnaplatext;

/* JNA imports. */
import com.sun.jna.Native;
import com.sun.jna.ptr.IntByReference;
import com.sun.jna.win32.StdCallLibrary;
import com.sun.jna.win32.W32APIOptions;

/**
 * Provides JNA access to Win32 functions in USER32.DLL.
 *
 * @author Michael Alyn Miller <malyn@strangeGizmo.com>
 * @version 1.0.0
 */
public interface User32 extends StdCallLibrary, com.sun.jna.platform.win32.User32
{

	/** Instance of USER32.DLL for use in accessing native functions. */
	User32 INSTANCE = (User32) Native.loadLibrary("user32", User32.class, W32APIOptions.DEFAULT_OPTIONS);

	int ToUnicodeEx(int wVirtKey, int wScanCode, byte[] lpKeyState, char[] pwszBuff, int cchBuff, int wFlags, IntByReference dwhkl);

	IntByReference GetKeyboardLayout(int dwLayout);

	short VkKeyScan(char ch);
}
