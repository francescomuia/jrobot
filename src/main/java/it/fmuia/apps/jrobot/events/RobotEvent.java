package it.fmuia.apps.jrobot.events;

import java.io.Serializable;

public abstract class RobotEvent implements Serializable
{
	public enum Type
	{
		KEYBOARD,
		MOUSE
	}

	private Type type;

	private long time;

	private long timeElapsed;

	public abstract Object getValue();

	public RobotEvent(Type type, long time)
	{
		this.type = type;
		this.time = time;
	}

	public void calculateTimeElapsed(long fromDate)
	{
		if (fromDate > this.time)
		{
			this.timeElapsed = fromDate - this.time;
		}
		else
		{
			this.timeElapsed = this.time - fromDate;
		}
		System.out.println(this.getValue() + " at time [" + this.time + "] from [" + fromDate + "] timeElapsed [" + timeElapsed + "] sec ["
				+ (timeElapsed / 1000) + "]");
	}

	public void calculateTimeElapsed(RobotEvent event)
	{
		this.calculateTimeElapsed(event.time);
	}

	public Type getType()
	{
		return type;
	}

	public long getTime()
	{
		return time;
	}

	public long getTimeElapsed()
	{
		return timeElapsed;
	}

	@Override
	public String toString()
	{
		StringBuilder sb = new StringBuilder();
		if (timeElapsed > 0)
		{
			sb.append("Dopo ");
			sb.append(this.timeElapsed / 1000);
			sb.append(" sec. ");
		}
		switch (type)
		{
		case KEYBOARD:
			sb.append("INSERITO TESTO");
			break;
		default:

			break;
		}
		return sb.toString();
	}

	@Override
	public int hashCode()
	{
		final int prime = 31;
		int result = 1;
		result = prime * result + (int) (time ^ (time >>> 32));
		result = prime * result + (int) (timeElapsed ^ (timeElapsed >>> 32));
		result = prime * result + ((type == null) ? 0 : type.hashCode());
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
		RobotEvent other = (RobotEvent) obj;
		if (time != other.time)
			return false;
		if (timeElapsed != other.timeElapsed)
			return false;
		if (type != other.type)
			return false;
		return true;
	}
}
