package it.fmuia.apps.jrobot;

import it.fmuia.apps.jrobot.events.RobotEvent;
import it.fmuia.apps.jrobot.events.RobotMouseEvent;
import it.fmuia.apps.jrobot.jna.GlobalHookManager;
import it.fmuia.apps.jrobot.jna.keyboard.NativeKeyboardEvent;
import it.fmuia.apps.jrobot.jna.keyboard.NativeKeyboardListener;
import it.fmuia.apps.jrobot.jna.keyboard.VirtualSpecialKeyCodes;
import it.fmuia.apps.jrobot.jna.mouse.NativeMouseActionListener;
import it.fmuia.apps.jrobot.jna.mouse.NativeMouseEvent;

import java.awt.AWTException;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.JToolBar;
import javax.swing.JTree;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeSelectionModel;

public class RegisterDialog extends JDialog implements TreeSelectionListener, NativeKeyboardListener, NativeMouseActionListener
{
	private JTextField nome;

	private JTree eventsTree;

	private boolean recording;

	private JCheckBox listenKeyBoard;

	private JCheckBox listenMouse;

	// private GlobalKeyListener globalKeylistener;

	private JButton saveButton;

	private JButton registerButton;

	private JButton stopButton;

	private JRobot robot;

	// private GlobalMouseListener globalMouseListener;

	private JButton removeNodeButton;

	private JButton takeScreenShootButton;

	private GlobalHookManager globalHookManager;

	private JButton testButton;

	private boolean altPressed;

	private boolean robotSaved;

	public RegisterDialog(JFrame frame)
	{
		super(frame, true);
		this.setTitle("Registra nuovo robot");
		this.setLayout(new BorderLayout());
		JToolBar toolbar = new JToolBar(JToolBar.HORIZONTAL);
		registerButton = new JButton("Registra");
		registerButton.addActionListener(new ActionListener()
		{

			@Override
			public void actionPerformed(ActionEvent e)
			{
				doRegister();
			}
		});
		toolbar.add(registerButton);

		stopButton = new JButton("Stop");
		this.stopButton.setEnabled(false);
		stopButton.addActionListener(new ActionListener()
		{

			@Override
			public void actionPerformed(ActionEvent e)
			{
				stopRecording();
			}
		});
		toolbar.add(stopButton);
		toolbar.addSeparator();
		saveButton = new JButton("Salva");
		saveButton.addActionListener(new ActionListener()
		{

			@Override
			public void actionPerformed(ActionEvent e)
			{
				doSaveRobot();
			}
		});
		saveButton.setEnabled(false);
		toolbar.add(saveButton);
		toolbar.addSeparator();
		this.testButton = new JButton("Test");
		this.testButton.addActionListener(new ActionListener()
		{

			@Override
			public void actionPerformed(ActionEvent e)
			{
				executeRobotTest();
			}
		});
		this.testButton.setEnabled(false);
		toolbar.add(this.testButton);
		this.add(toolbar, BorderLayout.NORTH);

		JPanel centerPanel = new JPanel(new GridLayout(1, 2));
		JPanel leftPanel = new JPanel(new BorderLayout());
		JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
		JLabel nomeLbl = new JLabel("Nome Robot : ");
		panel.add(nomeLbl);
		this.nome = new JTextField();
		this.nome.setColumns(20);
		this.nome.addFocusListener(new FocusListener()
		{

			@Override
			public void focusLost(FocusEvent e)
			{
				enableDisableSaveButton();
			}

			@Override
			public void focusGained(FocusEvent e)
			{

			}
		});
		panel.add(nome);
		leftPanel.add(panel, BorderLayout.NORTH);

		JPanel optionsPanel = new JPanel(new GridLayout(2, 1));
		optionsPanel.setBorder(BorderFactory.createTitledBorder("Opzioni"));
		leftPanel.add(optionsPanel);

		this.listenKeyBoard = new JCheckBox("Eventi Tastiera");
		this.listenKeyBoard.setSelected(true);
		optionsPanel.add(listenKeyBoard);

		this.listenMouse = new JCheckBox("Eventi Mouse");
		this.listenMouse.setSelected(true);
		optionsPanel.add(listenMouse);

		centerPanel.add(leftPanel);

		JPanel rightPanel = new JPanel(new BorderLayout());
		JToolBar treeToolbar = new JToolBar();
		this.removeNodeButton = new JButton("Cancella");
		this.removeNodeButton.setEnabled(false);
		this.removeNodeButton.addActionListener(new ActionListener()
		{

			@Override
			public void actionPerformed(ActionEvent e)
			{
				removeNode();
			}
		});
		treeToolbar.add(removeNodeButton);
		treeToolbar.addSeparator();

		this.takeScreenShootButton = new JButton("ScreenShoot");
		this.takeScreenShootButton.setToolTipText("Visualizza lo screen shoot del punto dove il mouse è stato premuto");
		this.takeScreenShootButton.setEnabled(false);
		this.takeScreenShootButton.addActionListener(new ActionListener()
		{

			@Override
			public void actionPerformed(ActionEvent e)
			{
				takeScreenShoot();

			}
		});
		treeToolbar.add(takeScreenShootButton);
		rightPanel.add(treeToolbar, BorderLayout.NORTH);
		this.eventsTree = new JTree(new DefaultMutableTreeNode("Robot"));
		this.eventsTree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
		this.eventsTree.addTreeSelectionListener(this);
		rightPanel.add(new JScrollPane(this.eventsTree));

		centerPanel.add(rightPanel);

		this.add(centerPanel);
		this.pack();
		Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
		this.setLocation(dim.width / 2 - this.getSize().width / 2, dim.height / 2 - this.getSize().height / 2);
	}

	protected void doSaveRobot()
	{
		this.robot.setName(this.nome.getText());
		JRobotDao dao = JRobotDao.getInstance();
		try
		{
			dao.addRobot(robot);
			this.robotSaved = true;
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}

	}

	protected void executeRobotTest()
	{
		try
		{
			this.robot.launch();
		}
		catch (AWTException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	protected void enableDisableSaveButton()
	{
		this.saveButton.setEnabled(this.nome.getText() != null && !this.nome.getText().trim().isEmpty() && this.robot != null
				&& !this.robot.getEvents().isEmpty());
	}

	@Override
	public void valueChanged(TreeSelectionEvent e)
	{
		if (this.eventsTree.getSelectionCount() > 0)
		{
			DefaultMutableTreeNode node = (DefaultMutableTreeNode) this.eventsTree.getSelectionPath().getLastPathComponent();
			if (node.getUserObject() instanceof RobotEvent)
			{
				RobotEvent event = (RobotEvent) node.getUserObject();
				this.removeNodeButton.setEnabled(true);
				this.takeScreenShootButton.setEnabled(event.getType().equals(RobotEvent.Type.MOUSE));
			}
			else
			{
				this.removeNodeButton.setEnabled(false);
				this.takeScreenShootButton.setEnabled(false);
			}
		}
		else
		{
			this.removeNodeButton.setEnabled(false);
			this.takeScreenShootButton.setEnabled(false);
		}

	}

	protected void takeScreenShoot()
	{
		DefaultMutableTreeNode node = (DefaultMutableTreeNode) this.eventsTree.getSelectionPath().getLastPathComponent();
		RobotMouseEvent robotEvent = (RobotMouseEvent) node.getUserObject();
		NativeMouseEvent nEvent = (NativeMouseEvent) robotEvent.getValue();

		JDialog screeshootDialog = new JDialog(this, "ScreenShot a (" + nEvent.getX() + "," + nEvent.getY() + ")", true);
		screeshootDialog.add(new JLabel(new ImageIcon(robotEvent.getScreen())));
		screeshootDialog.pack();
		Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
		screeshootDialog.setLocation(dim.width / 2 - screeshootDialog.getSize().width / 2, dim.height / 2 - screeshootDialog.getSize().height / 2);
		screeshootDialog.setVisible(true);

	}

	protected void removeNode()
	{
		DefaultMutableTreeNode node = (DefaultMutableTreeNode) this.eventsTree.getSelectionPath().getLastPathComponent();
		((DefaultTreeModel) this.eventsTree.getModel()).removeNodeFromParent(node);
		RobotMouseEvent robotEvent = (RobotMouseEvent) node.getUserObject();
		this.robot.getEvents().remove(robotEvent);
		this.enableDisableSaveButton();
	}

	protected void stopRecording()
	{
		if (globalHookManager != null && globalHookManager.isAlive())
		{
			globalHookManager.interrupt();
		}
		this.globalHookManager = null;

		this.robot.calculateTimes();
		DefaultMutableTreeNode root = (DefaultMutableTreeNode) this.eventsTree.getModel().getRoot();
		DefaultTreeModel model = (DefaultTreeModel) this.eventsTree.getModel();
		for (int i = 0; i < this.robot.getEvents().size(); i++)
		{
			model.insertNodeInto(new DefaultMutableTreeNode(this.robot.getEvents().get(i)), root, i);
		}
		this.eventsTree.expandRow(0);

		this.stopButton.setEnabled(false);
		this.registerButton.setEnabled(true);
		this.testButton.setEnabled(true);
		this.recording = false;
		this.saveButton.setEnabled(this.nome.getText() != null && !this.nome.getText().trim().isEmpty());
	}

	protected void doRegister()
	{
		this.registerButton.setEnabled(false);
		this.stopButton.setEnabled(true);
		this.setRecording(true);
		if (!this.listenKeyBoard.isSelected() && !this.listenMouse.isSelected())
		{
			JOptionPane.showMessageDialog(this, "Selezionare almeno una tipologia di eventi", "Errore", JOptionPane.ERROR_MESSAGE);
		}
		else
		{
			this.globalHookManager = new GlobalHookManager();
			if (this.listenKeyBoard.isSelected())
			{
				this.globalHookManager.addKeyboardListener(this);
			}
			if (this.listenMouse.isSelected())
			{
				this.globalHookManager.addMouseActionListener(this);
			}
			this.robot = new JRobot(this.nome.getText());

			this.dispose();
			this.globalHookManager.start();
		}
	}

	public boolean isRecording()
	{
		return recording;
	}

	public void setRecording(boolean recording)
	{
		this.recording = recording;
	}

	@Override
	public void mouseActionPerformed(NativeMouseEvent event)
	{
		if (!altPressed)
		{
			this.robot.addMouseEvent(event);
		}
		else
		{
			System.out.println("IGNORE EVENT " + event);
		}
	}

	@Override
	public void keyPressed(NativeKeyboardEvent ev)
	{
		if (ev.isAltPressed() || ev.isCtrlPressed())
		{
			this.altPressed = ev.isCtrlPressed();
		}
		else
		{
			this.robot.addKeyboarEvent(ev);
		}
	}

	@Override
	public void keyReleased(NativeKeyboardEvent ev)
	{
		if (VirtualSpecialKeyCodes.isSpecialKeyCode(ev.getKeyCode()))
		{
			if (VirtualSpecialKeyCodes.getByKeyCode(ev.getKeyCode()).isCtrl())
			{
				this.altPressed = false;
			}
		}

	}

	public boolean isRobotSaved()
	{
		return robotSaved;
	}

	public JRobot getRobot()
	{
		return robot;
	}

}
