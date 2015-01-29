package it.fmuia.apps.jrobot;

import it.fmuia.apps.jrobot.events.RobotEvent;
import it.fmuia.apps.jrobot.events.RobotEvent.Type;
import it.fmuia.apps.jrobot.events.RobotKeyboardEvent;
import it.fmuia.apps.jrobot.events.RobotMouseEvent;
import it.fmuia.apps.jrobot.jna.keyboard.NativeKeyboardEvent;
import it.fmuia.apps.jrobot.jna.keyboard.VirtualSpecialKeyCodes;
import it.fmuia.apps.jrobot.jna.mouse.NativeMouseEvent;

import java.awt.AWTException;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.imageio.ImageIO;

public class JRobot implements Serializable
{
	private static final long serialVersionUID = 1L;

	private static final int DEFAULT_WIDTH = 100;

	private static final int DEFAULT_HEIGHT = 100;

	private String name;

	private List<RobotEvent> events;

	public JRobot(String name)
	{
		this.name = name;
		this.events = Collections.synchronizedList(new ArrayList<RobotEvent>());
	}

	public void calculateTimes()
	{
		for (int i = 1; i < this.events.size() - 1; i++)
		{
			RobotEvent first = events.get(i);
			RobotEvent second = events.get(i + 1);
			first.calculateTimeElapsed(second);
		}
	}

	public void addEvent(RobotEvent e)
	{
		this.events.add(e);
	}

	public boolean isLastKeyBoardEvent()
	{

		return (!this.events.isEmpty() && this.getEvents().get(this.events.size() - 1).getType().equals(Type.KEYBOARD))
				&& !VirtualSpecialKeyCodes.isSpecialKeyCode(((RobotKeyboardEvent) this.events.get(this.events.size() - 1)).getKeycode());

	}

	public void addKeyboarEvent(NativeKeyboardEvent event)
	{

		if (!event.isSpecial() && isLastKeyBoardEvent())
		{
			RobotKeyboardEvent e = (RobotKeyboardEvent) this.events.get(this.events.size() - 1);
			e.setValue(((String) e.getValue()).concat(String.valueOf(event.getCharacters())));
		}
		else
		{
			this.addEvent(new RobotKeyboardEvent(event));
		}

	}

	private synchronized byte[] captureScreen(int x, int y, int width, int height)
	{
		System.out.println("captureScreen (" + width + "," + height + ")");
		try
		{
			Robot robot = new Robot();
			int rectX = x - (width / 2);
			int rectY = y - (height / 2);
			BufferedImage image = robot.createScreenCapture(new Rectangle(rectX, rectY, width, height));
			ByteArrayOutputStream out = new ByteArrayOutputStream();
			ImageIO.write(image, "png", out);
			out.close();

			return out.toByteArray();
		}
		catch (Exception e)
		{
			throw new RuntimeException(e);
		}

	}

	public void addMouseEvent(NativeMouseEvent event)
	{
		byte[] screen = this.captureScreen(event.getX(), event.getY(), DEFAULT_WIDTH, DEFAULT_HEIGHT);
		this.addEvent(new RobotMouseEvent(event, screen));

	}

	public String getName()
	{
		return name;
	}

	public void setName(String name)
	{
		this.name = name;
	}

	public List<RobotEvent> getEvents()
	{
		return events;
	}

	public void setEvents(List<RobotEvent> events)
	{
		this.events = events;
	}

	public JRobotLauncher launch() throws AWTException
	{
		JRobotLauncher launcher = new JRobotLauncher(this);
		launcher.execute();
		return launcher;
	}

	@Override
	public int hashCode()
	{
		final int prime = 31;
		int result = 1;
		result = prime * result + ((name == null) ? 0 : name.hashCode());
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
		JRobot other = (JRobot) obj;
		if (name == null)
		{
			if (other.name != null)
				return false;
		}
		else if (!name.equals(other.name))
			return false;
		return true;
	}

}
