package com.argusdusty.apscalc;

import java.math.MathContext;
import java.math.RoundingMode;

public class Settings
{
	public static final int precinc = 4;
	public static final int default_print = 6;
	public static boolean evalf = false;
	public static MathContext print = new MathContext(default_print, RoundingMode.HALF_EVEN);
	public static MathContext MC = new MathContext(default_print+4, RoundingMode.HALF_EVEN);
	
	public static void set_evalf(boolean e) {evalf = e;}
	
	public static int set_precision(int p)
	{
		int a = print.getPrecision();
		print = new MathContext(p, MC.getRoundingMode());
		MC = new MathContext(p + precinc, MC.getRoundingMode());
		return a;
	}
	
	// Used for FastMath functions, incrementing the precision at each level of evaluation to guarantee exact results
	public static void inc_prec() {MC = new MathContext(MC.getPrecision() + precinc, MC.getRoundingMode());}
	public static void dec_prec() {MC = new MathContext(MC.getPrecision() - precinc, MC.getRoundingMode());}
}
