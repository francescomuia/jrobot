package it.fmuia.apps.jrobot;

import java.awt.AWTException;
import java.awt.Image;
import java.awt.Menu;
import java.awt.MenuItem;
import java.awt.PopupMenu;
import java.awt.Robot;
import java.awt.SystemTray;
import java.awt.TrayIcon;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.IOException;
import java.util.List;

import javax.imageio.ImageIO;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UIManager.LookAndFeelInfo;

public class JRobotTrayIcon
{
	private static final String ROBOT_ICON = "/images/robot.png";

	private final Image robotImage;

	private SystemTray tray = SystemTray.getSystemTray();

	private TrayIcon trayIcon = null;

	private RegisterDialog registerDialog;

	private MenuItem record;

	private MenuItem stopRecord;

	private PopupMenu popupMenu;

	private Menu openItem;

	private EditRobotDialog editDialog;

	private JRobotLauncher launcher;

	public JRobotTrayIcon() throws IOException, AWTException
	{
		robotImage = ImageIO.read(JRobotTrayIcon.class.getResourceAsStream(ROBOT_ICON));
		this.trayIcon = new TrayIcon(robotImage, "JRobot");
		this.trayIcon.setPopupMenu(this.createPopupMenu());
		trayIcon.addMouseListener(new MouseListener()
		{

			@Override
			public void mouseReleased(MouseEvent e)
			{
				if (!e.isPopupTrigger())
				{
					Robot robot;
					try
					{
						robot = new Robot();
						// RIGHT CLICK
						robot.mousePress(InputEvent.BUTTON3_MASK);
						robot.mouseRelease(InputEvent.BUTTON3_MASK);
					}
					catch (AWTException e1)
					{
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
				}

			}

			@Override
			public void mousePressed(MouseEvent e)
			{
				// TODO Auto-generated method stub

			}

			@Override
			public void mouseExited(MouseEvent e)
			{
				// TODO Auto-generated method stub

			}

			@Override
			public void mouseEntered(MouseEvent e)
			{
				// TODO Auto-generated method stub

			}

			@Override
			public void mouseClicked(MouseEvent e)
			{
				// TODO Auto-generated method stub

			}
		});
		tray.add(trayIcon);

	}

	private PopupMenu createPopupMenu()
	{
		stopRecord = new MenuItem("Stop");
		stopRecord.addActionListener(new ActionListener()
		{

			@Override
			public void actionPerformed(ActionEvent e)
			{
				stopRecording();
			}
		});
		popupMenu = new PopupMenu("JRobot");
		openItem = new Menu("Robot");
		popupMenu.add(openItem);
		JRobotDao dao = JRobotDao.getInstance();
		List<JRobot> robots = dao.getRobots();
		for (JRobot jRobot : robots)
		{
			MenuItem robotItem = this.createRobotMenuItem(jRobot);
			openItem.add(robotItem);
		}

		popupMenu.addSeparator();
		record = new MenuItem("Registra");
		record.addActionListener(new ActionListener()
		{

			@Override
			public void actionPerformed(ActionEvent e)
			{
				registerAction();
			}
		});

		popupMenu.add(record);
		popupMenu.addSeparator();
		MenuItem exit = new MenuItem("Esci");
		exit.addActionListener(new ActionListener()
		{

			@Override
			public void actionPerformed(ActionEvent e)
			{
				quitApplication();
			}
		});
		popupMenu.add(exit);

		return popupMenu;
	}

	private Menu createRobotMenuItem(final JRobot jRobot)
	{
		final Menu item = new Menu(jRobot.getName());
		MenuItem start = new MenuItem("Avvia");
		start.addActionListener(new ActionListener()
		{

			@Override
			public void actionPerformed(ActionEvent e)
			{
				launch(jRobot);
			}
		});
		item.add(start);
		MenuItem edit = new MenuItem("Modifica");
		edit.addActionListener(new ActionListener()
		{

			@Override
			public void actionPerformed(ActionEvent e)
			{
				editRobot(jRobot);
			}
		});
		item.add(edit);

		MenuItem delete = new MenuItem("Cancella");
		delete.addActionListener(new ActionListener()
		{

			@Override
			public void actionPerformed(ActionEvent e)
			{
				deleteRobot(item, jRobot);
			}
		});
		item.add(delete);
		return item;
	}

	protected void editRobot(JRobot jRobot)
	{
		editDialog = new EditRobotDialog(jRobot);
		editDialog.setVisible(true);
		editDialog = null;
	}

	protected void deleteRobot(Menu item, JRobot jRobot)
	{
		this.openItem.remove(item);
		try
		{
			JRobotDao.getInstance().deleteRobot(jRobot);
		}
		catch (Exception e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	protected void launch(JRobot jRobot)
	{
		try
		{
			launcher = new JRobotLauncher(jRobot, this.trayIcon);
			launcher.execute();

		}
		catch (AWTException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	protected void quitApplication()
	{
		if (this.registerDialog != null && this.registerDialog.isRecording())
		{
			this.registerDialog.stopRecording();
		}
		if (registerDialog != null)
		{
			registerDialog.dispose();
		}
		if (this.editDialog != null)
		{
			this.editDialog.dispose();
		}
		if (launcher != null && !launcher.isDone())
		{
			launcher.interrupt();
		}
		tray.remove(trayIcon);
	}

	private void showRegisterDialog()
	{
		registerDialog = new RegisterDialog(null);
		registerDialog.setVisible(true);
	}

	public void stopRecording()
	{
		this.registerDialog.setVisible(true);
		if (!this.registerDialog.isRecording() && this.registerDialog.isRobotSaved())
		{
			MenuItem item = this.createRobotMenuItem(this.registerDialog.getRobot());
			this.openItem.add(item);
		}
		if (!this.registerDialog.isRecording())
		{
			this.registerDialog = null;
			this.popupMenu.remove(stopRecord);
			this.popupMenu.insert(record, 2);
		}
	}

	public void registerAction()
	{
		this.showRegisterDialog();
		if (!registerDialog.isRecording())
		{
			registerDialog = null;
		}
		else
		{
			this.popupMenu.remove(this.record);
			this.popupMenu.insert(stopRecord, 2);
			this.openItem.setEnabled(false);

		}

	}

	public static void main(String[] args) throws AWTException, IllegalArgumentException, IllegalAccessException
	{

		SwingUtilities.invokeLater(new Runnable()
		{

			@Override
			public void run()
			{
				try
				{
					for (LookAndFeelInfo info : UIManager.getInstalledLookAndFeels())
					{
						if ("Nimbus".equals(info.getName()))
						{
							UIManager.setLookAndFeel(info.getClassName());
							break;
						}
					}
				}
				catch (Exception e)
				{
					// If Nimbus is not available, fall back to cross-platform
					try
					{
						UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
					}
					catch (Exception ex)
					{
						// not worth my time
					}
				}
				JRobotTrayIcon jrobot;
				try
				{

					jrobot = new JRobotTrayIcon();
				}
				catch (IOException e)
				{
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				catch (AWTException e)
				{
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});

	}
}
