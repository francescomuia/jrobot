package it.fmuia.apps.jrobot;

import it.fmuia.apps.jrobot.events.RobotEvent;
import it.fmuia.apps.jrobot.events.RobotMouseEvent;
import it.fmuia.apps.jrobot.jna.mouse.NativeMouseEvent;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JToolBar;
import javax.swing.JTree;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeSelectionModel;

public class EditRobotDialog extends JDialog implements TreeSelectionListener
{
	private JTree eventsTree;

	private JRobot robot;

	private JButton removeNodeButton;

	private JButton takeScreenShootButton;

	private JButton saveButton;

	public EditRobotDialog(JRobot robot)
	{
		super();
		this.robot = robot;
		this.setTitle("Modifica robot " + robot.getName());
		this.setModal(true);
		this.setLayout(new BorderLayout());
		this.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		JToolBar treeToolbar = new JToolBar();
		this.saveButton = new JButton("Salva");
		this.saveButton.addActionListener(new ActionListener()
		{

			@Override
			public void actionPerformed(ActionEvent e)
			{
				doSave();
			}
		});
		treeToolbar.add(saveButton);
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
		this.add(treeToolbar, BorderLayout.NORTH);

		this.eventsTree = new JTree(new DefaultMutableTreeNode(robot.getName()));
		this.eventsTree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
		this.eventsTree.addTreeSelectionListener(this);
		DefaultMutableTreeNode root = (DefaultMutableTreeNode) this.eventsTree.getModel().getRoot();
		DefaultTreeModel model = (DefaultTreeModel) this.eventsTree.getModel();
		for (int i = 0; i < this.robot.getEvents().size(); i++)
		{
			model.insertNodeInto(new DefaultMutableTreeNode(this.robot.getEvents().get(i)), root, i);
		}
		this.eventsTree.expandRow(0);
		this.add(new JScrollPane(eventsTree));
		this.pack();
		Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
		this.setLocation(dim.width / 2 - this.getSize().width / 2, dim.height / 2 - this.getSize().height / 2);
	}

	protected void doSave()
	{
		try
		{
			JRobotDao.getInstance().editRobot(robot);
			JOptionPane.showMessageDialog(this, "Salvataggio completato.");
		}
		catch (Exception e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
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
		RobotEvent robotEvent = (RobotEvent) node.getUserObject();
		this.robot.getEvents().remove(robotEvent);
		this.enableDisableSaveButton();
	}

	private void enableDisableSaveButton()
	{
		this.saveButton.setEnabled(this.robot.getEvents() != null && !this.robot.getEvents().isEmpty());
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
}
