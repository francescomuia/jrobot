package it.fmuia.apps.jrobot.jna.keyboard;

import it.fmuia.apps.jrobot.jna.jnaplatext.User32;

import java.awt.event.KeyEvent;

import com.sun.jna.platform.win32.WinDef.LRESULT;
import com.sun.jna.platform.win32.WinDef.WPARAM;
import com.sun.jna.platform.win32.WinUser;
import com.sun.jna.platform.win32.WinUser.HHOOK;
import com.sun.jna.platform.win32.WinUser.KBDLLHOOKSTRUCT;
import com.sun.jna.platform.win32.WinUser.LowLevelKeyboardProc;
import com.sun.jna.ptr.IntByReference;

public class KeyboardHookProcess implements LowLevelKeyboardProc
{
	private NativeKeyboardListener listener;

	private boolean shiftPressed;

	private boolean ctrlPressed;

	private boolean altPressed;

	private HHOOK hook;

	// private final User32 lib = User32.INSTANCE;
	public KeyboardHookProcess(NativeKeyboardListener listener)
	{
		this.listener = listener;
	}

	private NativeKeyboardEvent createEvent(int keyPressed, long time, KBDLLHOOKSTRUCT info)
	{

		NativeKeyboardEvent event = null;
		if (VirtualSpecialKeyCodes.isSpecialKeyCode(info.vkCode))
		{
			VirtualSpecialKeyCodes special = VirtualSpecialKeyCodes.getByKeyCode(info.vkCode);
			if (KeyEvent.KEY_PRESSED == keyPressed)
			{
				if (special.isShift())
				{
					this.shiftPressed = true;
				}
				else if (special.isCtrl())
				{
					this.ctrlPressed = true;
				}
				else if (special.isAlt())
				{
					this.altPressed = true;
				}

			}
			else
			{
				if (special.isShift())
				{
					this.shiftPressed = false;
				}
				else if (special.isCtrl())
				{
					this.ctrlPressed = false;
				}
				else if (special.isAlt())
				{
					this.altPressed = false;
				}
			}
			event = new NativeKeyboardEvent(special.getKeyCode(), info.time, shiftPressed, ctrlPressed, altPressed);
		}
		else
		{
			byte[] keystate = new byte[256];
			if (shiftPressed)
			{
				keystate[VirtualSpecialKeyCodes.VK_SHIFT.getKeyCode()] = -128;
				keystate[VirtualSpecialKeyCodes.VK_LSHIFT.getKeyCode()] = -128;
				keystate[VirtualSpecialKeyCodes.VK_RSHIFT.getKeyCode()] = -128;
			}
			else
			{
				keystate[VirtualSpecialKeyCodes.VK_SHIFT.getKeyCode()] = 0;
				keystate[VirtualSpecialKeyCodes.VK_LSHIFT.getKeyCode()] = 0;
				keystate[VirtualSpecialKeyCodes.VK_RSHIFT.getKeyCode()] = 0;
			}

			char[] buff = new char[1];
			IntByReference keyblayoutID = User32.INSTANCE.GetKeyboardLayout(0);
			int bufflen = buff.length;
			int res = User32.INSTANCE.ToUnicodeEx(info.vkCode, info.scanCode, keystate, buff, bufflen, 0, keyblayoutID);
			if (res > 0)
			{
				event = new NativeKeyboardEvent(info.vkCode, info.time, buff, shiftPressed, ctrlPressed, altPressed);
			}
			else
			{
				event = new NativeKeyboardEvent(info.vkCode, info.time, shiftPressed, ctrlPressed, altPressed);
			}
		}

		return event;
	}

	@Override
	public LRESULT callback(int nCode, WPARAM wParam, KBDLLHOOKSTRUCT info)
	{

		NativeKeyboardEvent ev = null;
		long ti = System.currentTimeMillis();

		if (nCode >= 0)
		{

			switch (wParam.intValue())
			{
			case WinUser.WM_KEYDOWN:
			case WinUser.WM_SYSKEYDOWN:

				ev = this.createEvent(KeyEvent.KEY_PRESSED, ti, info);
				listener.keyPressed(ev);
				break;

			case WinUser.WM_KEYUP:
			case WinUser.WM_SYSKEYUP:
				ev = this.createEvent(KeyEvent.KEY_RELEASED, ti, info);
				listener.keyReleased(ev);
				break;
			}
		}

		return User32.INSTANCE.CallNextHookEx(this.hook, nCode, wParam, info.getPointer());
		// if (nh)
		// {
		// return lib.CallNextHookEx(keyboardHook, nCode, wParam,
		// info.getPointer());
		// }
		// return new LRESULT(1);
	}

	public void setHHOOK(HHOOK keyboardHook)
	{
		this.hook = keyboardHook;
	}
}
