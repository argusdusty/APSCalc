package com.argusdusty.apscalc.errors;

public class DerivativeError extends Error
{
	private static final long serialVersionUID = -1674126148549443349L;
	
	public DerivativeError() {super("Expression has no derivative");}
	public DerivativeError(String name) {super("Function " + name + " has no derivative");}
}
