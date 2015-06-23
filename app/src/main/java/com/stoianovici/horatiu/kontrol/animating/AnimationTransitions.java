package com.stoianovici.horatiu.kontrol.animating;

public class AnimationTransitions {
	
	public static double EaseIn(long tt, double bb, double cc, long d)
	{
		double s = 1.70158, t = (double)tt;
		double mn = Math.min(bb,  cc);
		double b = bb - mn;
		double c = cc - mn;
		double sign = b > 0 ? -1 : 1;
		if(b > 0)
		{
			double aux = b;
			b = c;
			c = aux;
		}
        return bb + ((c*(t/=d)*t*((s+1)*t - s) + b) * sign);
	}
	
	public static double EaseOut(long tt, double b, double c, long d)
	{
		double t = (double)tt;
		return c*((t=t/d-1)*t*t + 1) + b;
	}
}
