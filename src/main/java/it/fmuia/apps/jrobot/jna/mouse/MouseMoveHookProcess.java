package it.fmuia.apps.jrobot.jna.mouse;

import java.awt.event.MouseEvent;

public class MouseMoveHookProcess extends MouseHookProcess
{

	private NativeMouseMotionListener listener;

	public MouseMoveHookProcess(NativeMouseMotionListener listener)
	{
		super();
		this.listener = listener;
	}

	@Override
	public void handleMouseMove(MOUSEHOOKSTRUCT lParam)
	{
		int type = MouseEvent.MOUSE_MOVED;
		long wheen = lParam.time.longValue();
		int x = lParam.pt.x;
		int y = lParam.pt.y;
		NativeMouseEvent event = new NativeMouseEvent(type, wheen, x, y);
		this.getListener().mouseMoved(event);
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

	}

	@Override
	public void handleMouseButtonPressed(int button, MOUSEHOOKSTRUCT lParam)
	{

	}

	public NativeMouseMotionListener getListener()
	{
		return listener;
	}

}
