package it.fmuia.apps.jrobot;

import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;

public class JRobotOptions
{
	private static JRobotOptions instance;

	private String robotDirectory;

	public final String ROBOT_DIR_PREF_KEY = "ROBOT_DIR";

	private Preferences preferences;

	public final static String ROBOT_FILE_NAME = "jrobot.dat";

	private JRobotOptions()
	{
		preferences = Preferences.userNodeForPackage(JRobotOptions.class);
		robotDirectory = preferences.get(ROBOT_DIR_PREF_KEY, System.getProperty("user.home"));
	}

	public String getRobotDirectory()
	{
		return robotDirectory;
	}

	public void setRobotDirectory(String robotDirectory) throws BackingStoreException
	{
		this.robotDirectory = robotDirectory;
		preferences.put(ROBOT_DIR_PREF_KEY, robotDirectory);
		preferences.sync();
	}

	public static JRobotOptions getInstance()
	{
		if (instance == null)
		{
			instance = new JRobotOptions();
		}
		return instance;
	}
}
