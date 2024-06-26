package localizer;

import utils.Pose2d;
import utils.CubicSpline;

public class ConstAccelLocalizer extends Localizer {
	public static final double fidelity = 1E-10;
	
	@Override
	public void update(CubicSpline p,int n) {//path with n subdivisions
		super.update(p,n);
		double t1 = 0;
		Pose2d last = l.get(0);
		double h1 = last.heading;
		double h = 1.0/((double)n);
		double s = 0.0001;
		double lastRX = (p.getRelX(0,-s)) * h/s;
		double lastRY = (p.getRelY(0,-s)) * h/s;
		double lastRH = (h1-p.getPose2d(-s).heading) * h/s;
		for (int i = 0; i < n; i++) {
			double t2 = ((double)i+1.0)/((double) n);
			
			double rx = p.getRelX(t2, t1);
			double ry = p.getRelY(t2, t1);
			double rh = p.getPose2d(t2).heading-h1;
			
			//derive the equations for deltaX and deltaY
			double vrx = (rx + lastRX)/2;
			double arx = 2*(rx-vrx);
			//v_x = vrx + arx*t
			double vry = (ry + lastRY)/2;
			double ary = 2*(ry-vry);
			//v_y = vry + ary*t
			double vrh = (rh + lastRH)/2;
			double arh = (rh-vrh);
			//h = h1 + vry*t + ary*t^2
			AdaptiveQuaderature x = new AdaptiveQuaderature(new double[] {vrx,arx},new double[] {h1,vrh,arh});
			AdaptiveQuaderature y = new AdaptiveQuaderature(new double[] {vry,ary},new double[] {h1,vrh,arh});
			
			Pose2d next = new Pose2d(
					last.x + x.evaluateCos(fidelity, 0, 1, 0) - y.evaluateSin(fidelity, 0, 1, 0),
					last.y + y.evaluateCos(fidelity, 0, 1, 0) + x.evaluateSin(fidelity, 0, 1, 0),
					h1 + rh
					);
			
			int xcosIndex = 0,ycosIndex = 0, xsinIndex = 0, ysinIndex = 0;
			for (double a = t1; a < t2; a += 1E-3) {
				double m = (a-t1)/(t2-t1);
				for (;x.cos.get(xcosIndex).time < m && xcosIndex < x.cos.size();xcosIndex ++) {}
				for (;x.sin.get(xsinIndex).time < m && xsinIndex < x.sin.size();xsinIndex ++) {}
				for (;y.cos.get(ycosIndex).time < m && ycosIndex < y.cos.size();ycosIndex ++) {}
				for (;y.sin.get(ysinIndex).time < m && ysinIndex < y.sin.size();ysinIndex ++) {}
				l.add(new Pose2d(
						last.x + x.cos.get(xcosIndex).val - y.sin.get(ysinIndex).val,
						last.y + y.cos.get(ycosIndex).val + x.sin.get(xsinIndex).val,
						h1 + rh
						));
				t.add(a);
			}
			
			l.add(next);
			t.add(t2);
			last = next;
			h1 = last.heading;
			lastRX = rx;
			lastRY = ry;
			lastRH = rh;
			t1 = t2;
		}
	}
	
	public static void main(String[] args) {
		CubicSpline s = new CubicSpline(new Pose2d(100,155,Math.toRadians(0)),new Pose2d(300,205,Math.toRadians(0)));
		Localizer l = new ConstAccelLocalizer();
		for (int i = 1; i < 1024; i *= 2) {
			l.update(s,i);
			System.out.println(i + ", " + l.l.get(l.l.size()-1).getDist(s.getPose2d(1)));
		}
		//This shows that arc localization is about O(h^3)
	}
}
