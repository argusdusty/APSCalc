package com.argusdusty.apscalc.errors;

public class UnexpectedCharError extends Error
{
	private static final long serialVersionUID = 1035868544392324290L;
	
	public UnexpectedCharError(char character)
	{
		super("Unexpected Character: " + character);
	}
}
