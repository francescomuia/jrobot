package it.fmuia.apps.jrobot.events;

import it.fmuia.apps.jrobot.jna.keyboard.NativeKeyboardEvent;
import it.fmuia.apps.jrobot.jna.keyboard.VirtualSpecialKeyCodes;

public class RobotKeyboardEvent extends RobotEvent
{

	private static final long serialVersionUID = 1L;

	private String value;

	private boolean special;

	private int keycode;

	public RobotKeyboardEvent(NativeKeyboardEvent event)
	{
		super(Type.KEYBOARD, event.getWhen());
		this.value = event.getCharacters();
		this.special = event.isSpecial();
		this.keycode = event.getKeyCode();
	}

	@Override
	public Object getValue()
	{
		return value;
	}

	public void setValue(String value)
	{
		this.value = value;
	}

	@Override
	public String toString()
	{
		if (!isSpecial())
		{
			return super.toString() + " [" + value + "] ";
		}
		else
		{
			return super.toString() + "[" + VirtualSpecialKeyCodes.getByKeyCode(keycode).name() + "]";
		}
	}

	public boolean isSpecial()
	{
		return special;
	}

	public int getKeycode()
	{
		return keycode;
	}

	@Override
	public int hashCode()
	{
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + keycode;
		result = prime * result + (special ? 1231 : 1237);
		result = prime * result + ((value == null) ? 0 : value.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj)
	{
		if (this == obj)
			return true;
		if (!super.equals(obj))
			return false;
		if (getClass() != obj.getClass())
			return false;
		RobotKeyboardEvent other = (RobotKeyboardEvent) obj;
		if (keycode != other.keycode)
			return false;
		if (special != other.special)
			return false;
		if (value == null)
		{
			if (other.value != null)
				return false;
		}
		else if (!value.equals(other.value))
			return false;
		return true;
	}

}
