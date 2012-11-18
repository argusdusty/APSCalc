package com.argusdusty.apscalc.types.mathconsts;

public class Inf extends Unique
{
	public Inf() {super("inf");}
	public int signum() {return 1;}
	public Inf copy() {return new Inf();}
}
