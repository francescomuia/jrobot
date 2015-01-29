package it.fmuia.apps.jrobot.jna.keyboard;

public class NativeKeyboardEvent
{
	private int keyCode;

	private boolean shiftPressed;

	private boolean ctrlPressed;

	private boolean altPressed;

	private String characters;

	private long when;

	private boolean special;

	public NativeKeyboardEvent(int keyCode, long time, boolean shiftPressed, boolean ctrlPressed, boolean altPressed)
	{
		this.when = time;
		this.keyCode = keyCode;
		this.shiftPressed = shiftPressed;
		this.ctrlPressed = ctrlPressed;
		this.altPressed = altPressed;
		this.special = true;
	}

	public NativeKeyboardEvent(int vkCode, long time, char[] buff, boolean shiftPressed, boolean ctrlPressed, boolean altPressed)
	{
		this(vkCode, time, shiftPressed, ctrlPressed, altPressed);
		this.characters = new String(buff);
		this.special = false;
	}

	public int getKeyCode()
	{
		return keyCode;
	}

	public boolean isShiftPressed()
	{
		return shiftPressed;
	}

	public boolean isCtrlPressed()
	{
		return ctrlPressed;
	}

	public boolean isAltPressed()
	{
		return altPressed;
	}

	public String getCharacters()
	{
		return characters;
	}

	public long getWhen()
	{
		return when;
	}

	public boolean isSpecial()
	{
		return special;
	}

}
