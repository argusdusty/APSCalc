package com.argusdusty.apscalc.errors;

public class IntegrateError extends Error
{
	private static final long serialVersionUID = -7701719903611419133L;
	
	public IntegrateError() {super("No integrand found");}
}
