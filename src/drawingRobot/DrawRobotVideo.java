package drawingRobot;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Polygon;
import java.awt.Stroke;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.DecimalFormat;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.Timer;

import utils.Pose2d;
import utils.Robot;

public class DrawRobotVideo extends JPanel implements ActionListener{

	Timer t;
	Font big = new Font("Courier New", 1, 50);
	Font small = new Font("Courier New", 1, 30);
	Font biggest = new Font("Courier New", 1, 90);
	JFrame frame;

	enum robotCase{
		waitAtStart,
		startChassis,
		finishChassis,
		startWheels,
		finishWheels,
		drawOdo,
		delayBeforeAxis,
		drawAxis,
		moveRobotBack,
		rotateRobot,
		moveForward,
		idle;
	}
	
	Robot r;
	long start = System.currentTimeMillis();
	Pose2d center;
	double scale = 4.0;
	robotCase rc;
	
	private static final DecimalFormat decfor = new DecimalFormat("0.00"); 
	
	public static void main(String[] args) {
		DrawRobotVideo drive = new DrawRobotVideo();
	}
	
	public DrawRobotVideo() {
		frame = new JFrame("VideoExplanation");
		frame.setSize(1600, 934);
		center = new Pose2d(1600/2, 934/2-17, Math.toRadians(-90));
		frame.add(this);
		
		r = new Robot(center);
		r.scale = scale;
		
		rc = robotCase.waitAtStart;
		
		start = System.currentTimeMillis();
		
		t = new Timer(5,this);
		t.start();
		
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setVisible(true);
	}
	
	
	public void paint(Graphics g) {
		super.paintComponent(g);
		
		double robotWidth = 100;
		double wheelWidth = scale*robotWidth/10;
		double wheelHeight = wheelWidth * 2;
		double wheelPosX = (scale * robotWidth - wheelHeight - wheelWidth * 0.5)/2;
		double wheelPosY = (scale * robotWidth - wheelWidth * 1.5)/2;

		Graphics2D g2 = (Graphics2D) g;
		Stroke s = new BasicStroke((float)(scale*4),BasicStroke.CAP_SQUARE,BasicStroke.JOIN_MITER,10.0f);
		g2.setStroke(s);
		g2.setColor(Color.DARK_GRAY);
		
		double m8 = 0;

		double t = (System.currentTimeMillis() - start)/(1000.0);
		switch (rc){
			case waitAtStart:
				if (t > 4) {
					start = System.currentTimeMillis();
					rc = robotCase.startChassis;
				}
				break;
			case drawOdo:
				double m5 = 1;
				if (rc == robotCase.drawOdo) {
					m5 = Math.min(2*t,1);
				}
				double radius = (scale * robotWidth - wheelWidth * 1.5)/2.0;
				g2.drawLine((int)(center.x-radius),(int)(center.y-wheelWidth/2),(int)(center.x-radius),(int)(center.y-wheelWidth/2+wheelWidth*m5));
				g2.drawLine((int)(center.x+radius),(int)(center.y-wheelWidth/2),(int)(center.x+radius),(int)(center.y-wheelWidth/2+wheelWidth*m5));
				g2.drawLine((int)(center.x-m5*wheelWidth/2),(int)(center.y+radius),(int)(center.x+m5*wheelWidth/2),(int)(center.y+radius));

			case finishWheels:
				double m4 = 1;
				if (rc == robotCase.finishWheels) {
					m4 = Math.min(t,1);
				}
				double a = 1, b = 1;
				for (int i = 0; i < 4; i ++) {
					switch(i) {
					case(0): a = 1; b = 1; break;
					case(1): a = 1; b = -1; break;
					case(2): a = -1; b = -1; break;
					case(3): a = -1; b = 1; break;
					}
					g2.drawLine((int)(center.x - (wheelPosY - wheelWidth/2) * a), (int)(center.y - (wheelPosX + wheelHeight/2) * b),(int)(center.x - (wheelPosY - wheelWidth/2) * a), (int)(center.y - (wheelPosX + wheelHeight/2 - wheelHeight * m4) * b));
					g2.drawLine((int)(center.x - (wheelPosY + wheelWidth/2) * a), (int)(center.y - (wheelPosX - wheelHeight/2) * b),(int)(center.x - (wheelPosY + wheelWidth/2 - wheelWidth * m4) * a), (int)(center.y - (wheelPosX - wheelHeight/2) * b));
				}
			case startWheels:
				double m3 = 1;
				if (rc == robotCase.startWheels) {
					m3 = Math.min(t,1);
				}
				a = 1; b = 1;
				for (int i = 0; i < 4; i ++) {
					switch(i) {
					case(0): a = 1; b = 1; break;
					case(1): a = 1; b = -1; break;
					case(2): a = -1; b = -1; break;
					case(3): a = -1; b = 1; break;
					}
					g2.drawLine((int)(center.x - (wheelPosY + wheelWidth/2) * a), (int)(center.y - (wheelPosX + wheelHeight/2) * b), (int)(center.x - (wheelPosY + wheelWidth/2 - wheelWidth * m3) * a), (int)(center.y - (wheelPosX + wheelHeight/2) * b));
					g2.drawLine((int)(center.x - (wheelPosY + wheelWidth/2) * a), (int)(center.y - (wheelPosX + wheelHeight/2) * b), (int)(center.x - (wheelPosY + wheelWidth/2) * a), (int)(center.y - (wheelPosX + wheelHeight/2 - wheelHeight*m3) * b));
				}
			case finishChassis:
				double m2 = 1;
				if (rc == robotCase.finishChassis) {
					m2 = Math.min(t/2,1);
				}
				g2.drawLine((int)(center.x - r.scale * 50), (int)(center.y + r.scale * 50), (int)(center.x - r.scale * 50 + r.scale * 100 * m2), (int)(center.y + r.scale * 50));
				g2.drawLine((int)(center.x + r.scale * 50), (int)(center.y - r.scale * 50), (int)(center.x + r.scale * 50), (int)(center.y - r.scale * 50 + r.scale * 100 * m2));
			case startChassis:
				double m1 = 1;
				if (rc == robotCase.startChassis) {
					m1 = Math.min(t/2,1);
				}
				g2.drawLine((int)(center.x - r.scale * 50), (int)(center.y - r.scale * 50), (int)(center.x - r.scale * 50), (int)(center.y - r.scale * 50 + r.scale * 100 * m1));
				g2.drawLine((int)(center.x - r.scale * 50), (int)(center.y - r.scale * 50), (int)(center.x - r.scale * 50 + r.scale * 100 * m1), (int)(center.y - r.scale * 50));
				switch(rc) {
					case drawOdo:		if (t > 0.5) {start = System.currentTimeMillis(); rc = robotCase.delayBeforeAxis;} break;
					case finishWheels:	if (t > 1) {start = System.currentTimeMillis(); rc = robotCase.drawOdo;} break;
					case startWheels:	if (t > 1) {start = System.currentTimeMillis(); rc = robotCase.finishWheels;} break;
					case finishChassis:	if (t > 2.5) {start = System.currentTimeMillis(); rc = robotCase.startWheels;} break;
					case startChassis:	if (t > 2) {start = System.currentTimeMillis(); rc = robotCase.finishChassis;} break;
				}
				break;
			case delayBeforeAxis:
				double m7 = Math.min(t/3,1);
				center.x = 800 - 500*m7;
				center.y = 450 + 150*m7;
				center.heading = Math.toRadians(-90)*(1-m7);
				r.p = center.clone();
				r.drawRobot(g2);
				if (t > 3) {
					start = System.currentTimeMillis();
					rc = robotCase.drawAxis;
				}
				break;
			case idle:
			case moveForward:
				double m10 = 1;
				if (rc == robotCase.moveForward) {
					m10 = Math.min(t/3,1);
				}
				r.p.x = center.x + 300*m10;
				r.p.y = center.y - 300*m10;
			case rotateRobot:
				double m9 = 1;
				if (rc == robotCase.rotateRobot) {
					m9 = Math.min(t/2,1);
				}
				r.p.heading = Math.toRadians(-45*m9);
				
				g2.setColor(Color.black);
				g2.setFont(big);
				double rx = (r.p.x - center.x)/30;
				double ry = (center.y - r.p.y)/30;
				double relX = Math.sqrt(rx*rx + ry*ry);
				g2.drawString("Θ = " + decfor.format(Math.toDegrees(r.p.heading*-1)), (int)131, (int)80);
				g2.drawString("X = " + decfor.format(rx), (int)131, (int)140);
				g2.drawString("Y = " + decfor.format(ry), (int)131, (int)200);
				g2.drawString("relX = " + decfor.format(relX), (int)40, (int)260);
				g2.drawString("relY = 0.00", (int)40, (int)320);

				g2.setColor(Color.red);
				g2.drawLine((int)r.p.x, (int)center.y, (int)r.p.x, (int)r.p.y);
				g2.setColor(Color.blue);
				g2.drawLine((int)center.x, (int)r.p.y, (int)r.p.x, (int)r.p.y);
				g2.setColor(Color.magenta);
				g2.drawLine((int)center.x, (int)center.y, (int)r.p.x, (int)r.p.y);
				
			case moveRobotBack:
				m8 = 1;
				if (rc == robotCase.moveRobotBack) {
					m8 = Math.min(t/3,1);
					center.x = 600 - 300*(1-m8); // 300 -> 600
					center.y = 650 - 50*(1-m8);
					r.p = center.clone();
				}
				
			case drawAxis:
				double m6 = 1;
				if (rc == robotCase.drawAxis) {
					m6 = Math.min(t/3,1);
				}
				
				double yAxisLength = 400*m6;
				double xAxisLength = 800*m6 - 400*m8;
				double size = 20;
				g2.setColor(Color.GRAY);
				g2.drawLine((int)(center.x), (int)(center.y), (int)(center.x), (int)(center.y - yAxisLength));
				Polygon p2 = new Polygon();
				p2.addPoint((int)(center.x - size), (int)(center.y - yAxisLength));
				p2.addPoint((int)(center.x + size), (int)(center.y - yAxisLength));
				p2.addPoint((int)(center.x), (int)(center.y - yAxisLength - 2 * size));
				g2.fillPolygon(p2);
				
				g2.drawLine((int)(center.x), (int)(center.y), (int)(center.x + xAxisLength), (int)(center.y));
				Polygon p1 = new Polygon();
				p1.addPoint((int)(center.x + xAxisLength), (int)(center.y - size));
				p1.addPoint((int)(center.x + xAxisLength), (int)(center.y + size));
				p1.addPoint((int)(center.x + xAxisLength + 2 * size), (int)(center.y));
				g2.fillPolygon(p1);
				
				g2.setColor(Color.black);
				g2.setFont(big);
				g2.drawString("X", (int)(center.x + xAxisLength), (int)(center.y+70));
				g2.drawString("Y", (int)(center.x - 70), (int)(center.y-yAxisLength));
				
				r.drawRobot(g2);
				
				if (rc == robotCase.moveForward && t > 3.5) {
					start = System.currentTimeMillis();
					rc = robotCase.idle;
				}
				else if (rc == robotCase.rotateRobot && t > 2) {
					start = System.currentTimeMillis();
					rc = robotCase.moveForward;
				}
				else if (rc == robotCase.moveRobotBack && t > 4) {
					start = System.currentTimeMillis();
					rc = robotCase.rotateRobot;
				}
				else if (rc == robotCase.drawAxis && t > 3) {
					start = System.currentTimeMillis();
					rc = robotCase.moveRobotBack;
				}
				break;
		}
	}
	
	@Override
	public void actionPerformed(ActionEvent arg0) {
		repaint();
	
	}

}
