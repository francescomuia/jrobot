package it.fmuia.apps.jrobot;

import it.fmuia.apps.jrobot.events.RobotEvent;
import it.fmuia.apps.jrobot.events.RobotKeyboardEvent;
import it.fmuia.apps.jrobot.events.RobotMouseEvent;
import it.fmuia.apps.jrobot.jna.GlobalHookManager;
import it.fmuia.apps.jrobot.jna.keyboard.NativeKeyboardEvent;
import it.fmuia.apps.jrobot.jna.keyboard.NativeKeyboardListener;
import it.fmuia.apps.jrobot.jna.keyboard.VirtualSpecialKeyCodes;
import it.fmuia.apps.jrobot.jna.mouse.NativeMouseEvent;

import java.awt.AWTException;
import java.awt.Robot;
import java.awt.TrayIcon;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;

import javax.swing.JOptionPane;

import org.apache.log4j.Logger;

public class JRobotLauncherStepping implements NativeKeyboardListener
{

	Logger logger = Logger.getLogger(getClass());

	private GlobalHookManager hook;

	private SteppingLauncher thread;

	public JRobotLauncherStepping(JRobot jRobot, TrayIcon tray) throws AWTException
	{
		this.thread = new SteppingLauncher(jRobot, tray);
		this.hook = new GlobalHookManager();
		hook.addKeyboardListener(this);
	}

	class SteppingLauncher extends Thread
	{
		private JRobot jRobot;

		private Robot robot;

		private TrayIcon tray;

		private int currentEventIndex;

		public SteppingLauncher(JRobot robot, TrayIcon icon) throws AWTException
		{
			super();
			this.jRobot = robot;
			this.tray = icon;
			this.robot = new Robot();
		}

		@Override
		public void run()
		{
			synchronized (this)
			{
				try
				{
					while (!isInterrupted() && this.currentEventIndex < this.jRobot.getEvents().size())
					{
						RobotEvent event = this.jRobot.getEvents().get(this.currentEventIndex);
						this.displayMessage("EVENTO " + event);
						this.wait();
						if (event instanceof RobotKeyboardEvent)
						{
							this.handleKeyboardEvent(event);
						}
						else
						{
							this.handleMouseEvent(event);
						}
						this.currentEventIndex++;
					}
				}
				catch (InterruptedException e)
				{
					e.printStackTrace();
				}
			}
		}

		public boolean isNextLast()
		{
			return !(this.currentEventIndex + 1 < this.jRobot.getEvents().size());
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
				else if (isValidKeyCode(evt.getKeycode()))
				{
					try
					{
						robot.keyPress(evt.getKeycode());
						robot.keyRelease(evt.getKeycode());
					}
					catch (IllegalArgumentException ex)
					{
						logger.error("ERRORE DURANTE L'INSERIMENTO DI UN KEYCODE " + evt, ex);
						int choose = JOptionPane.showConfirmDialog(null, "Si è verificato un errore durante la simulazione dell'evento " + evt
								+ " Continuare ?", "Errore", JOptionPane.YES_NO_OPTION, JOptionPane.ERROR_MESSAGE);
						if (!(JOptionPane.YES_OPTION == choose))
						{
							throw new RuntimeException(ex);
						}
					}
				}
				else
				{
					logger.warn("INGNORATO KEY EVENT " + e);
				}
			}
			else
			{
				this.writeString((String) evt.getValue());
			}
		}

		private boolean isValidKeyCode(int keycode)
		{
			String text = KeyEvent.getKeyText(keycode);
			logger.debug("IS VALID KEY CODE RETUR " + text);

			return text != null && !text.isEmpty();
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
	}

	public void start()
	{

		hook.start();
		thread.start();
	}

	@Override
	public void keyPressed(NativeKeyboardEvent ev)
	{
		VirtualSpecialKeyCodes key = VirtualSpecialKeyCodes.getByKeyCode(ev.getKeyCode());
		if (VirtualSpecialKeyCodes.VK_F8.equals(key))
		{
			synchronized (thread)
			{
				boolean isLast = thread.isNextLast();
				thread.notify();
				if (isLast)
				{
					hook.interrupt();
				}
			}
		}

	}

	@Override
	public void keyReleased(NativeKeyboardEvent ev)
	{

	}

}
