package it.fmuia.apps.jrobot;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JOptionPane;

public class JRobotDao
{
	private static JRobotDao instance;

	private List<JRobot> robots;

	private JRobotDao()
	{
		JRobotOptions options = JRobotOptions.getInstance();
		File robotFile = new File(options.getRobotDirectory(), JRobotOptions.ROBOT_FILE_NAME);
		if (robotFile.exists())
		{
			System.out.println("LOAD ROBOT");
			this.loadRobots();
		}
		else if (!robotFile.exists())
		{
			try
			{
				robotFile.createNewFile();
				this.robots = new ArrayList<JRobot>();
			}
			catch (IOException e)
			{
				JOptionPane.showMessageDialog(null,
						"Impossibile creare file " + JRobotOptions.ROBOT_FILE_NAME + " Nella cartella " + options.getRobotDirectory(), "Errore",
						JOptionPane.ERROR_MESSAGE);
				e.printStackTrace();
			}
		}
		else
		{
			this.robots = new ArrayList<>();
		}
	}

	public void editRobot(JRobot robot) throws Exception
	{
		int index = this.robots.indexOf(robot);
		this.robots.set(index, robot);
		this.save();
	}

	public void deleteRobot(JRobot robot) throws Exception
	{
		this.robots.remove(robot);
		this.save();
	}

	public void addRobot(JRobot robot) throws Exception
	{
		this.robots.add(robot);
		this.save();
	}

	private void save() throws Exception
	{
		ObjectOutputStream out = null;
		JRobotOptions options = JRobotOptions.getInstance();
		File robotFile = new File(options.getRobotDirectory(), JRobotOptions.ROBOT_FILE_NAME);
		try
		{
			out = new ObjectOutputStream(new FileOutputStream(robotFile));
			out.writeObject(this.robots);
		}
		catch (FileNotFoundException e)
		{
			JOptionPane.showMessageDialog(null,
					"Impossibile trovare il file " + JRobotOptions.ROBOT_FILE_NAME + " Nella cartella " + options.getRobotDirectory(), "Errore",
					JOptionPane.ERROR_MESSAGE);
			throw e;
		}
		catch (IOException e)
		{
			JOptionPane.showMessageDialog(null, "Si è verificato un errore durante il salvataggio dei robot", "Errore", JOptionPane.ERROR_MESSAGE);
			throw e;
		}
		finally
		{
			if (out != null)
			{
				try
				{
					out.close();
				}
				catch (IOException e)
				{
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}

	private void loadRobots()
	{
		JRobotOptions options = JRobotOptions.getInstance();
		ObjectInputStream in = null;
		try
		{
			in = new ObjectInputStream(new FileInputStream(new File(options.getRobotDirectory(), JRobotOptions.ROBOT_FILE_NAME)));
			this.robots = (List<JRobot>) in.readObject();

		}
		catch (FileNotFoundException e)
		{
			JOptionPane.showMessageDialog(null,
					"Impossibile trovare il file " + JRobotOptions.ROBOT_FILE_NAME + " Nella cartella " + options.getRobotDirectory(), "Errore",
					JOptionPane.ERROR_MESSAGE);
			e.printStackTrace();
		}
		catch (Exception e)
		{
			JOptionPane.showMessageDialog(null, "Si è verificato un errore durante il caricamento dei robot", "Errore", JOptionPane.ERROR_MESSAGE);
			e.printStackTrace();
		}
		finally
		{
			if (in != null)
			{
				try
				{
					in.close();
				}
				catch (IOException e)
				{
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}

	public static JRobotDao getInstance()
	{
		if (instance == null)
		{
			instance = new JRobotDao();
		}
		return instance;
	}

	public List<JRobot> getRobots()
	{
		return robots;
	}
}
