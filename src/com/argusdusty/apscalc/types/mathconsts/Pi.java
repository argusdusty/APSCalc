package com.argusdusty.apscalc.types.mathconsts;

import com.argusdusty.apscalc.FastMath;
import com.argusdusty.apscalc.types.Float;

public class Pi extends Real
{
	public Pi() {super("Pi");}
	public int signum() {return 1;}
	public Float decForm() {return new Float(FastMath.PI());}
	public Pi copy() {return new Pi();}
}
