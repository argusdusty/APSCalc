package com.argusdusty.apscalc.types.mathconsts;

public class NegInf extends Unique
{
	public NegInf() {super("-inf");}
	public int signum() {return -1;}
	public NegInf copy() {return new NegInf();}
}
