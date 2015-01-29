package it.fmuia.apps.jrobot;

import it.fmuia.apps.jrobot.events.RobotEvent;

import javax.swing.tree.DefaultMutableTreeNode;

public class JRobotEventNode extends DefaultMutableTreeNode
{

	public JRobotEventNode(RobotEvent e)
	{
		super(e);
	}

}
