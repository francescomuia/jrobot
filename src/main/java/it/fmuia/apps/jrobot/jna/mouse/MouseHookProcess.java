package it.fmuia.apps.jrobot.jna.mouse;

import it.fmuia.apps.jrobot.jna.jnaplatext.User32;

import com.sun.jna.platform.win32.WinDef.LRESULT;
import com.sun.jna.platform.win32.WinDef.WPARAM;
import com.sun.jna.platform.win32.WinUser.HHOOK;

public abstract class MouseHookProcess implements LowLevelMouseProc
{
	public static final int WM_MOUSEMOVE = 512;

	public static final int WM_LBUTTONDOWN = 513;

	public static final int WM_LBUTTONUP = 514;

	public static final int WM_RBUTTONDOWN = 516;

	public static final int WM_RBUTTONUP = 517;

	public static final int WM_MBUTTONDOWN = 519;

	public static final int WM_MBUTTONUP = 520;

	private HHOOK hook;

	@Override
	public LRESULT callback(int nCode, WPARAM wParam, MOUSEHOOKSTRUCT lParam)
	{
		if (nCode >= 0)
		{
			switch (wParam.intValue())
			{
			case WM_LBUTTONDOWN:
			case WM_RBUTTONDOWN:
			case WM_MBUTTONDOWN:
				this.handleMouseButtonPressed(wParam.intValue(), lParam);
				break;
			case WM_LBUTTONUP:
			case WM_RBUTTONUP:
			case WM_MBUTTONUP:
				this.handleMouseButtonReleased(wParam.intValue(), lParam);
				break;
			case WM_MOUSEMOVE:
				this.handleMouseMove(lParam);
				break;

			}
		}
		return User32.INSTANCE.CallNextHookEx(this.hook, nCode, wParam, lParam.getPointer());

	}

	public abstract void handleMouseMove(MOUSEHOOKSTRUCT lParam);

	public abstract void handleMouseButtonReleased(int button, MOUSEHOOKSTRUCT lParam);

	public abstract void handleMouseButtonPressed(int button, MOUSEHOOKSTRUCT lParam);

	public void setHHOOK(HHOOK mouseHook)
	{
		this.hook = mouseHook;
	}

}
