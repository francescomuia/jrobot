package it.fmuia.apps.jrobot.jna;

import it.fmuia.apps.jrobot.jna.keyboard.NativeKeyboardEvent;
import it.fmuia.apps.jrobot.jna.keyboard.NativeKeyboardListener;

import java.awt.AWTException;
import java.awt.Robot;
import java.awt.event.KeyEvent;
import java.io.IOException;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;

public class TestGlobalHookManager
{
	public static void main(String[] args) throws AWTException, IOException
	{
		System.out.println((byte) 0xA1);
		SwingUtilities.invokeLater(new Runnable()
		{

			@Override
			public void run()
			{
				JFrame frame = new JFrame("TEST");
				frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
				frame.setVisible(true);

			}
		});
		GlobalHookManager globalHookManager = new GlobalHookManager();
		globalHookManager.addKeyboardListener(new NativeKeyboardListener()
		{

			@Override
			public void keyReleased(NativeKeyboardEvent ev)
			{
				System.out.println("KEY RELEASED [" + ev.getCharacters() + "]");
			}

			@Override
			public void keyPressed(NativeKeyboardEvent ev)
			{

			}
		});
		// globalHookManager.addMouseActionListener(new
		// NativeMouseActionListener()
		// {
		//
		// @Override
		// public void mouseActionPerformed(NativeMouseEvent event)
		// {
		// System.out.println("ACTION " + event);
		//
		// }
		// });
		// globalHookManager.addMouseMotionListener(new
		// NativeMouseMotionListener()
		// {
		//
		// @Override
		// public void mouseMoved(NativeMouseEvent event)
		// {
		// System.out.println("MOVE " + event);
		//
		// }
		// });

		// globalHookManager.start();
		Robot robot = new Robot();
		System.out.println("TEST");
		System.in.read();
		String s = System.getProperty("line.separator");
		for (int i = 0; i < s.length(); i++)
		{
			char c = s.charAt(i);
			if (Character.isUpperCase(c))
			{
				robot.keyPress(KeyEvent.VK_SHIFT);
			}

			robot.keyPress(KeyEvent.VK_ENTER);
			robot.keyRelease(Character.toUpperCase(c));

			if (Character.isUpperCase(c))
			{
				robot.keyRelease(KeyEvent.VK_SHIFT);
			}
		}

	}
}
