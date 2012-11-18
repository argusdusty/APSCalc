package com.argusdusty.apscalc.functions;

import com.argusdusty.apscalc.types.Expression;

public abstract class UnivarFunction extends AbstractFunction
{
	public Expression arg1;
			
	public String toString() {return name + "(" + arg1.toString() + ")";}
	
	public boolean equals(Expression e)
	{
		if (e instanceof UnivarFunction && ((UnivarFunction) e).name.equals(name))
		{
			if (((UnivarFunction) e).arg1.equals(arg1)) return true;
		}
		return false;
	}
}
