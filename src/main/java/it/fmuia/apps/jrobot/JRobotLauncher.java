package it.fmuia.apps.jrobot;

import it.fmuia.apps.jrobot.events.RobotEvent;
import it.fmuia.apps.jrobot.events.RobotKeyboardEvent;
import it.fmuia.apps.jrobot.events.RobotMouseEvent;
import it.fmuia.apps.jrobot.jna.keyboard.VirtualSpecialKeyCodes;
import it.fmuia.apps.jrobot.jna.mouse.NativeMouseEvent;

import java.awt.AWTException;
import java.awt.Robot;
import java.awt.TrayIcon;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.Iterator;
import java.util.List;

import javax.swing.SwingWorker;

public class JRobotLauncher extends SwingWorker<Void, Object>
{
	private JRobot jRobot;

	private boolean interrupted;

	private Robot robot;

	private TrayIcon tray;

	public JRobotLauncher(JRobot jRobot) throws AWTException
	{
		this.jRobot = jRobot;
		robot = new Robot();
	}

	public JRobotLauncher(JRobot jRobot, TrayIcon tray) throws AWTException
	{
		this(jRobot);
		this.tray = tray;
	}

	private void displayMessage(String message)
	{
		if (tray != null)
		{
			this.tray.displayMessage("JRobot", message, TrayIcon.MessageType.INFO);
		}
		else
		{
			System.out.println(message);
		}
	}

	@Override
	protected void process(List<Object> chunks)
	{
		for (Object object : chunks)
		{
			if (object instanceof String)
			{
				displayMessage(object.toString());
			}
			else
			{
				RobotEvent event = (RobotEvent) object;
				displayMessage(event.toString());
			}
		}
	}

	@Override
	protected Void doInBackground() throws Exception
	{
		for (int i = 1; i <= 5; i++)
		{
			this.publish(this.jRobot.getName() + " si avvierà tra " + (6 - i) + " secondi");
			Thread.currentThread().sleep(1000);
		}
		Iterator<RobotEvent> events = this.jRobot.getEvents().iterator();
		while (events.hasNext() && !isInterrupted())
		{
			RobotEvent e = events.next();
			this.publish(e);
			if (e instanceof RobotKeyboardEvent)
			{
				this.handleKeyboardEvent(e);
			}
			else
			{
				this.handleMouseEvent(e);
			}
		}
		this.publish(this.jRobot.getName() + " FINITO");
		return null;
	}

	private void handleMouseEvent(RobotEvent e)
	{
		RobotMouseEvent evt = (RobotMouseEvent) e;
		NativeMouseEvent nEvt = (NativeMouseEvent) evt.getValue();
		robot.delay((int) evt.getTimeElapsed());
		robot.mouseMove(nEvt.getX(), nEvt.getY());

		if (MouseEvent.MOUSE_PRESSED == nEvt.getType())
		{
			robot.mousePress(InputEvent.getMaskForButton(nEvt.getMouseButton()));
		}
		else
		{
			robot.mouseRelease(InputEvent.getMaskForButton(nEvt.getMouseButton()));
		}
	}

	private void handleKeyboardEvent(RobotEvent e)
	{
		RobotKeyboardEvent evt = (RobotKeyboardEvent) e;
		robot.delay((int) evt.getTimeElapsed());
		if (evt.isSpecial())
		{
			if (VirtualSpecialKeyCodes.VK_RETURN.equals(VirtualSpecialKeyCodes.getByKeyCode(evt.getKeycode())))
			{
				robot.keyPress(KeyEvent.VK_ENTER);
				robot.keyRelease(KeyEvent.VK_ENTER);
			}
			else
			{
				robot.keyPress(evt.getKeycode());
				robot.keyRelease(evt.getKeycode());
			}
		}
		else
		{
			this.writeString((String) evt.getValue());
		}
	}

	private void writeString(String s)
	{
		for (int i = 0; i < s.length(); i++)
		{
			char c = s.charAt(i);
			if (Character.isUpperCase(c))
			{
				robot.keyPress(KeyEvent.VK_SHIFT);
			}
			robot.keyPress(Character.toUpperCase(c));
			robot.keyRelease(Character.toUpperCase(c));

			if (Character.isUpperCase(c))
			{
				robot.keyRelease(KeyEvent.VK_SHIFT);
			}
		}

	}

	//
	// @Override
	public boolean isInterrupted()
	{
		return interrupted;
	}

	//
	// @Override
	public void interrupt()
	{
		this.interrupted = true;
	}

	public static void main(String[] args) throws FileNotFoundException, IOException, ClassNotFoundException, AWTException
	{
		ObjectInputStream in = new ObjectInputStream(new FileInputStream("C:/Users/muia/Documents/robot2.robot"));
		JRobot robot = (JRobot) in.readObject();
		in.close();
		System.out.println(robot.getName());
		// List<JRobot> robots = new ArrayList<>();
		// robots.add(robot);
		// JRobotOptions options = JRobotOptions.getInstance();
		// ObjectOutputStream out = new ObjectOutputStream(new
		// FileOutputStream(new File(options.getRobotDirectory(),
		// JRobotOptions.ROBOT_FILE_NAME)));
		// out.writeObject(robots);
		// out.close();
	}

}
