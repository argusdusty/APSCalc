package com.argusdusty.apscalc.functions.list;

import java.util.ArrayList;

import com.argusdusty.apscalc.errors.DerivativeError;
import com.argusdusty.apscalc.errors.FuncInvError;
import com.argusdusty.apscalc.functions.UnivarFunction;
import com.argusdusty.apscalc.types.Expression;
import com.argusdusty.apscalc.types.Variable;
import com.argusdusty.apscalc.types.arrays.Matrix;
import com.argusdusty.apscalc.utils.MathUtils;

public class Echelon extends UnivarFunction
{

	public Echelon(Expression arg1)
	{
		this.arg1 = arg1;
		this.name = "echelon";
	}
	
	public Expression simplify()
	{
		if (arg1 instanceof Matrix)
		{
			Matrix m = ((Matrix) arg1).copy();
			ArrayList<Expression> temp;
			Expression lv;
			int lead = 0;
			for (int r = 0; r < m.m; r++)
			{
				if (lead >= m.n) break;
				int i = r;
				while(MathUtils.isZero(m.get(i, lead)))
				{
					i++;
					if (i == m.m)
					{
						i = r;
                        lead++;
                        if (lead == m.n) return m;
					}
				}
				temp = m.get(r);
				m.set(r, m.get(i));
				m.set(i, temp);
				lv = m.get(r, lead);
				for (int j = 0; j < m.n; j++)
					m.set(r, j, m.get(r, j).div(lv));
				for (int j = r; j < m.m; j++)
				{
					if (j != r)
					{
						lv = m.get(j, lead);
	                    for (int k = 0; k < m.n; k++)
	                        m.set(j, k, m.get(j, k).sub(lv.mul(m.get(r, k))));
					}
				}
				lead++;
			}
			return m;
		}
		//throw new Error("Argument of function 'echelon' must be a matrix");
		return copy();
	}
	
	public Expression derivative(Variable x) {throw new DerivativeError(name);}
	public Expression finverse(Expression e) {throw new FuncInvError(name);}
	public Expression substitute(Variable x, Expression e) {return new Echelon(arg1.substitute(x, e)).simplify();}
	public Expression copy() {return new Echelon(arg1);}
}
