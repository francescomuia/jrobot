package it.fmuia.apps.jrobot.events;

import it.fmuia.apps.jrobot.jna.mouse.NativeMouseEvent;

import java.awt.event.MouseEvent;

public class RobotMouseEvent extends RobotEvent
{

	private static final long serialVersionUID = 1L;

	private NativeMouseEvent value;

	private byte[] screen;

	public RobotMouseEvent(NativeMouseEvent e, byte[] screen)
	{
		super(Type.MOUSE, e.getWheen());
		this.value = e;
		this.screen = screen;
	}

	@Override
	public Object getValue()
	{
		return value;
	}

	@Override
	public String toString()
	{
		StringBuilder sb = new StringBuilder(super.toString());
		if (MouseEvent.MOUSE_PRESSED == value.getType())
		{
			sb.append("PREMUTO BOTTONE");
		}
		else
		{
			sb.append("RILASCIATO BOTTONE");
		}
		if (MouseEvent.BUTTON1 == value.getMouseButton())
		{
			sb.append(" 1 ");
		}
		else if (MouseEvent.BUTTON2 == value.getMouseButton())
		{
			sb.append(" 2 ");
		}
		else
		{
			sb.append(" 3 ");
		}
		sb.append(" A (");
		sb.append(value.getX());
		sb.append(",");
		sb.append(value.getY());
		sb.append(")");
		return sb.toString();
	}

	public byte[] getScreen()
	{
		return screen;
	}

	@Override
	public int hashCode()
	{
		final int prime = 31;
		int result = super.hashCode();
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
		RobotMouseEvent other = (RobotMouseEvent) obj;
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
