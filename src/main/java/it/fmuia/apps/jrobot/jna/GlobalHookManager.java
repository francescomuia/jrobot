package it.fmuia.apps.jrobot.jna;

import it.fmuia.apps.jrobot.jna.keyboard.KeyboardHookProcess;
import it.fmuia.apps.jrobot.jna.keyboard.NativeKeyboardListener;
import it.fmuia.apps.jrobot.jna.mouse.MouseActionHookProcess;
import it.fmuia.apps.jrobot.jna.mouse.MouseHookProcess;
import it.fmuia.apps.jrobot.jna.mouse.MouseMoveHookProcess;
import it.fmuia.apps.jrobot.jna.mouse.NativeMouseActionListener;
import it.fmuia.apps.jrobot.jna.mouse.NativeMouseMotionListener;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.sun.jna.platform.win32.Kernel32;
import com.sun.jna.platform.win32.User32;
import com.sun.jna.platform.win32.WinDef.HMODULE;
import com.sun.jna.platform.win32.WinUser;
import com.sun.jna.platform.win32.WinUser.HHOOK;
import com.sun.jna.platform.win32.WinUser.MSG;

public class GlobalHookManager extends Thread
{
	private static GlobalHookManager instance;

	private final User32 lib = User32.INSTANCE;

	private final HMODULE hMod = Kernel32.INSTANCE.GetModuleHandle(null);

	private boolean interrupted = false;

	private static final Logger LOGGER = Logger.getLogger(GlobalHookManager.class.getCanonicalName());

	private List<HHOOK> keyboardHookProcess;

	private List<HHOOK> mouseHookProcess;

	private List<NativeKeyboardListener> keyboardListeners;

	private List<KeyboardHookProcess> keyboardHook = new ArrayList<KeyboardHookProcess>();

	private List<MouseHookProcess> mouseHook = new ArrayList<MouseHookProcess>();

	private List<NativeMouseMotionListener> mouseMotionListeners;

	private List<NativeMouseActionListener> mouseActionListeners;

	public GlobalHookManager()
	{
		this.keyboardListeners = new ArrayList<NativeKeyboardListener>();
		this.keyboardHookProcess = new ArrayList<HHOOK>();
		this.mouseMotionListeners = new ArrayList<NativeMouseMotionListener>();
		this.mouseActionListeners = new ArrayList<NativeMouseActionListener>();
		this.mouseHookProcess = new ArrayList<HHOOK>();

	}

	public void addKeyboardListener(NativeKeyboardListener listener)
	{
		this.keyboardListeners.add(listener);
		final KeyboardHookProcess hook = new KeyboardHookProcess(listener);
		keyboardHook.add(hook);
	}

	public void addMouseActionListener(NativeMouseActionListener listener)
	{
		this.mouseActionListeners.add(listener);
		final MouseHookProcess hook = new MouseActionHookProcess(listener);
		this.mouseHook.add(hook);
	}

	public void addMouseMotionListener(NativeMouseMotionListener listener)
	{
		this.mouseMotionListeners.add(listener);
		final MouseHookProcess hook = new MouseMoveHookProcess(listener);
		this.mouseHook.add(hook);
	}

	private void installKeyboardHook()
	{
		for (KeyboardHookProcess hook : keyboardHook)
		{
			HHOOK keyboardHook = lib.SetWindowsHookEx(WinUser.WH_KEYBOARD_LL, hook, hMod, 0);
			hook.setHHOOK(keyboardHook);
			keyboardHookProcess.add(keyboardHook);
		}
	}

	private void installMouseHook()
	{
		for (MouseHookProcess hook : mouseHook)
		{
			HHOOK mouseHook = lib.SetWindowsHookEx(WinUser.WH_MOUSE_LL, hook, hMod, 0);
			hook.setHHOOK(mouseHook);
			this.mouseHookProcess.add(mouseHook);
		}
	}

	@Override
	public synchronized void start()
	{
		if (keyboardListeners.isEmpty() && mouseMotionListeners.isEmpty() && mouseActionListeners.isEmpty())
		{
			throw new RuntimeException("NESSUN LISTENER DEFINITO");
		}
		else
		{
			super.start();
		}
	}

	@Override
	public void run()
	{
		this.installKeyboardHook();
		this.installMouseHook();
		while (!interrupted)
		{
			boolean result;
			MSG msg = new MSG();
			while ((result = lib.PeekMessage(msg, null, 0, 0, 1)))
			{
				if (!result)
				{
					LOGGER.log(Level.WARNING, "error in get message");
					break;
				}
				else
				{
					LOGGER.log(Level.INFO, "got message");
					lib.TranslateMessage(msg);
					lib.DispatchMessage(msg);
				}
			}
		}
		if (!this.keyboardHookProcess.isEmpty())
		{
			uninstallKeyboardProcess();
		}
		if (!this.mouseHook.isEmpty())
		{
			uninstallMouseProcess();
		}
	}

	private void uninstallKeyboardProcess()
	{
		for (HHOOK hook : this.keyboardHookProcess)
		{
			lib.UnhookWindowsHookEx(hook);
		}
	}

	private void uninstallMouseProcess()
	{
		for (HHOOK hook : this.mouseHookProcess)
		{
			lib.UnhookWindowsHookEx(hook);
		}
	}

	@Override
	public void interrupt()
	{
		super.interrupt();
		this.interrupted = true;
	}

	@Override
	public boolean isInterrupted()
	{
		return this.interrupted;
	}

}
