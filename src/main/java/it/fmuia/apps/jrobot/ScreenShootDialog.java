package it.fmuia.apps.jrobot;

import java.awt.AWTException;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;

import javax.swing.ImageIcon;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

public class ScreenShootDialog extends JDialog implements FocusListener
{
	private static final int DEFAULT_WIDTH = 50;

	private static final int DEFAULT_HEIGHT = 50;

	private JLabel screenShootLabel;

	private int x;

	private int y;

	private JTextField widthTextField;

	private JTextField heightTextField;

	private JPanel screenShootPanel;

	public ScreenShootDialog(JDialog parent, int x, int y)
	{
		super(parent, "Screenshoot at " + x + "," + y, true);
		this.x = x;
		this.y = y;
		this.setLayout(new BorderLayout());
		this.setBackground(Color.BLACK);
		JPanel nortPanel = new JPanel();
		JLabel widthLabel = new JLabel("Larghezza : ");
		nortPanel.add(widthLabel);
		this.widthTextField = new JTextField("" + DEFAULT_WIDTH);
		this.widthTextField.addFocusListener(this);
		nortPanel.add(widthTextField);

		JLabel heightLabel = new JLabel("Altezza : ");
		nortPanel.add(heightLabel);
		this.heightTextField = new JTextField("" + DEFAULT_HEIGHT);
		// this.heightTextField.addFocusListener(this);
		nortPanel.add(heightTextField);

		this.add(nortPanel, BorderLayout.NORTH);

		this.captureScreen(DEFAULT_WIDTH, DEFAULT_HEIGHT);
		screenShootPanel = new JPanel();
		screenShootPanel.setBackground(Color.black);
		screenShootPanel.add(screenShootLabel);
		this.add(screenShootPanel);
		this.updateDimension();
	}

	private void captureScreen(int width, int height)
	{
		System.out.println("captureScreen (" + width + "," + height + ")");
		try
		{
			Robot robot = new Robot();
			int rectX = x - (width / 2);
			int rectY = y - (height / 2);
			Image image = robot.createScreenCapture(new Rectangle(rectX, rectY, width, height));
			this.screenShootLabel = new JLabel(new ImageIcon(image));
		}
		catch (AWTException e)
		{
			JOptionPane.showMessageDialog(this, "Errore durante la creazione del dialog", "Errore", JOptionPane.ERROR_MESSAGE);
			e.printStackTrace();
		}
	}

	@Override
	public void focusGained(FocusEvent e)
	{
		// TODO Auto-generated method stub

	}

	@Override
	public void focusLost(FocusEvent e)
	{
		System.out.println("FOCUS LOST");
		screenShootPanel.remove(this.screenShootLabel);
		int width = new Integer(this.widthTextField.getText());
		int height = new Integer(this.heightTextField.getText());
		this.captureScreen(width, height);
		screenShootPanel.add(this.screenShootLabel);
		this.updateDimension();
	}

	private void updateDimension()
	{
		this.pack();
		this.pack();
		Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
		this.setLocation(dim.width / 2 - this.getSize().width / 2, dim.height / 2 - this.getSize().height / 2);
	}

	public static void main(String[] args)
	{
		JDialog d = new ScreenShootDialog(null, 674, 348);
		d.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		d.setVisible(true);
	}
}
