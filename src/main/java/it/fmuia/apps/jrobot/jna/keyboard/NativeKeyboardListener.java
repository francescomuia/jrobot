package it.fmuia.apps.jrobot.jna.keyboard;

public interface NativeKeyboardListener
{

	void keyPressed(NativeKeyboardEvent ev);

	void keyReleased(NativeKeyboardEvent ev);

}
