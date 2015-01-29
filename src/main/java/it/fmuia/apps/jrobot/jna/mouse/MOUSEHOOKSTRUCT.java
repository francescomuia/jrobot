package it.fmuia.apps.jrobot.jna.mouse;

import java.util.Arrays;
import java.util.List;

import com.sun.jna.Structure;
import com.sun.jna.platform.win32.BaseTSD.ULONG_PTR;
import com.sun.jna.platform.win32.WinDef.DWORD;
import com.sun.jna.platform.win32.WinDef.HWND;
import com.sun.jna.platform.win32.WinDef.POINT;

public class MOUSEHOOKSTRUCT extends Structure
{
	public static class ByReference extends MOUSEHOOKSTRUCT implements Structure.ByReference
	{
	};

	public POINT pt;

	public HWND hwnd;

	public int wHitTestCode;

	public DWORD time;

	public ULONG_PTR dwExtraInfo;

	@Override
	protected List getFieldOrder()
	{
		return Arrays.asList(new String[]
		{ "pt", "hwnd", "wHitTestCode", "time", "dwExtraInfo" });
	}

}
