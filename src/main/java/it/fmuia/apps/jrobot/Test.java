package it.fmuia.apps.jrobot;

import it.fmuia.apps.jrobot.jna.GlobalHookManager;
import it.fmuia.apps.jrobot.jna.keyboard.NativeKeyboardEvent;
import it.fmuia.apps.jrobot.jna.keyboard.NativeKeyboardListener;
import it.fmuia.apps.jrobot.jna.keyboard.VirtualSpecialKeyCodes;

import java.awt.AWTException;

public class Test implements NativeKeyboardListener
{
	public static void main(String[] args) throws AWTException
	{
		Test test = new Test();
		test.start();
	}

	class TextExecute extends Thread
	{

		private boolean finish;

		@Override
		public void run()
		{
			synchronized (this)
			{

				try
				{
					while (!isInterrupted() && !isFinish())
					{
						System.out.println("THREAD WAIT");
						this.wait();
						executeStep();
					}
				}
				catch (InterruptedException e)
				{
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}

		private void executeStep()
		{
			System.out.println("STEP EXECUTION");
		}

		private boolean isFinish()
		{
			return finish;
		}

		public void setFinish(boolean finish)
		{
			this.finish = finish;
		}

	}

	private TextExecute myThread;

	private GlobalHookManager globalHook;

	public void start()
	{
		this.globalHook = new GlobalHookManager();
		this.globalHook.addKeyboardListener(this);
		this.globalHook.start();
		this.myThread = new TextExecute();
		this.myThread.start();
	}

	@Override
	public void keyPressed(NativeKeyboardEvent ev)
	{
		if (VirtualSpecialKeyCodes.isSpecialKeyCode(ev.getKeyCode()))
		{
			VirtualSpecialKeyCodes vCode = VirtualSpecialKeyCodes.getByKeyCode(ev.getKeyCode());
			if (VirtualSpecialKeyCodes.VK_F5.equals(vCode))
			{
				synchronized (myThread)
				{
					System.out.println("PREMUTO F5");
					myThread.notify();
				}
			}
			else if (VirtualSpecialKeyCodes.VK_F8.equals(vCode))
			{
				synchronized (myThread)
				{
					System.out.println("PREMUTO F8");
					myThread.setFinish(true);
					myThread.notify();
					this.globalHook.interrupt();
				}
			}
		}

	}

	@Override
	public void keyReleased(NativeKeyboardEvent ev)
	{
		// TODO Auto-generated method stub

	}
}
