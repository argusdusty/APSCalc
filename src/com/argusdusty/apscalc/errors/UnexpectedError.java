package com.argusdusty.apscalc.errors;

public class UnexpectedError extends Error
{
	private static final long serialVersionUID = 2341336110673070637L;

	public UnexpectedError(String code)
	{
		super("Unexpected Error: " + code);
	}
}
