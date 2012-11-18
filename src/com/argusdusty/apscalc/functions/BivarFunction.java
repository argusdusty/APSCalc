package com.argusdusty.apscalc.functions;

import com.argusdusty.apscalc.errors.FuncInvError;
import com.argusdusty.apscalc.types.Expression;

public abstract class BivarFunction extends AbstractFunction
{
	public Expression arg1;
	public Expression arg2;
		
	public String toString() {return name + "(" + arg1.toString() + ", " + arg2.toString() + ")";}
	
	public boolean equals(Expression e)
	{
		if (e instanceof BivarFunction && ((BivarFunction) e).name.equals(name))
			if (((BivarFunction) e).arg1.equals(arg1) && ((BivarFunction) e).arg2.equals(arg2))
				return true;
		return false;
	}
	
	public Expression finverse(Expression e) {throw new FuncInvError(name);}
}