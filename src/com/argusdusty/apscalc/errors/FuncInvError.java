package com.argusdusty.apscalc.errors;

public class FuncInvError extends Error
{
	private static final long serialVersionUID = 9183145347833836644L;
	
	public FuncInvError() {super("No inverse found");}
	public FuncInvError(String name) {super("No inverse found: " + name);}
}
