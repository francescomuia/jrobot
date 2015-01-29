package it.fmuia.apps.jrobot.jna.mouse;

import java.io.Serializable;

public class NativeMouseEvent implements Serializable
{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private int mouseButton;

	private int type;

	private long wheen;

	private int x;

	private int y;

	public NativeMouseEvent(int mouseButton, int type, long wheen, int x, int y)
	{
		this.mouseButton = mouseButton;
		this.type = type;
		this.wheen = wheen;
		this.x = x;
		this.y = y;
	}

	public NativeMouseEvent(int type, long wheen, int x, int y)
	{
		this.type = type;
		this.wheen = wheen;
		this.x = x;
		this.y = y;
	}

	public int getMouseButton()
	{
		return mouseButton;
	}

	public int getType()
	{
		return type;
	}

	public long getWheen()
	{
		return wheen;
	}

	public int getX()
	{
		return x;
	}

	public int getY()
	{
		return y;
	}

	@Override
	public String toString()
	{
		return "NativeMouseEvent [x=" + x + ", y=" + y + ", mouseButton=" + mouseButton + ", type=" + type + ", wheen=" + wheen + "]";
	}

	@Override
	public int hashCode()
	{
		final int prime = 31;
		int result = 1;
		result = prime * result + mouseButton;
		result = prime * result + type;
		result = prime * result + (int) (wheen ^ (wheen >>> 32));
		result = prime * result + x;
		result = prime * result + y;
		return result;
	}

	@Override
	public boolean equals(Object obj)
	{
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		NativeMouseEvent other = (NativeMouseEvent) obj;
		if (mouseButton != other.mouseButton)
			return false;
		if (type != other.type)
			return false;
		if (wheen != other.wheen)
			return false;
		if (x != other.x)
			return false;
		if (y != other.y)
			return false;
		return true;
	}

}
