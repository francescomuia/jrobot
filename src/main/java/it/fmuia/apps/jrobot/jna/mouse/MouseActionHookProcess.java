package it.fmuia.apps.jrobot.jna.mouse;

import java.awt.event.MouseEvent;

public class MouseActionHookProcess extends MouseHookProcess
{

	private NativeMouseActionListener listener;

	public MouseActionHookProcess(NativeMouseActionListener listener)
	{
		super();
		this.listener = listener;
	}

	@Override
	public void handleMouseMove(MOUSEHOOKSTRUCT lParam)
	{
		// TODO Auto-generated method stub

	}

	private int getButton(int button)
	{
		switch (button)
		{
		case WM_LBUTTONDOWN:
		case WM_LBUTTONUP:
			return MouseEvent.BUTTON1;
		case WM_RBUTTONDOWN:
		case WM_RBUTTONUP:
			return MouseEvent.BUTTON2;
		case WM_MBUTTONDOWN:
		case WM_MBUTTONUP:
			return MouseEvent.BUTTON3;
		}
		return -1;
	}

	@Override
	public void handleMouseButtonReleased(int button, MOUSEHOOKSTRUCT lParam)
	{
		int mouseButton = this.getButton(button);
		int type = MouseEvent.MOUSE_RELEASED;
		long wheen = lParam.time.longValue();
		int x = lParam.pt.x;
		int y = lParam.pt.y;
		NativeMouseEvent event = new NativeMouseEvent(mouseButton, type, wheen, x, y);
		this.getListener().mouseActionPerformed(event);
	}

	@Override
	public void handleMouseButtonPressed(int button, MOUSEHOOKSTRUCT lParam)
	{
		int mouseButton = this.getButton(button);
		int type = MouseEvent.MOUSE_PRESSED;
		long wheen = lParam.time.longValue();
		int x = lParam.pt.x;
		int y = lParam.pt.y;
		NativeMouseEvent event = new NativeMouseEvent(mouseButton, type, wheen, x, y);
		this.getListener().mouseActionPerformed(event);
	}

	public NativeMouseActionListener getListener()
	{
		return listener;
	}

}
