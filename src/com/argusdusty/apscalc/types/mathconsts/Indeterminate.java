package com.argusdusty.apscalc.types.mathconsts;

public class Indeterminate extends Unique
{
	public Indeterminate() {super("?");}
	public int signum() {return 0;}
	public Indeterminate copy() {return new Indeterminate();}
}
