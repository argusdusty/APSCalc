package com.argusdusty.apscalc.types.mathconsts;

import com.argusdusty.apscalc.FastMath;
import com.argusdusty.apscalc.types.Float;

public class E extends Real
{
	public E() {super("e");}
	public int signum() {return 1;}
	public Float decForm() {return new Float(FastMath.E());}
	public E copy() {return new E();}
}
