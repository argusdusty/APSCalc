package com.argusdusty.apscalc.errors;

public class ReduceError extends Error
{
	private static final long serialVersionUID = -2398101829772769051L;

	public ReduceError()
	{
		super("ReduceError");
	}
	
	public ReduceError(String message)
	{
		super(message);
	}
}
