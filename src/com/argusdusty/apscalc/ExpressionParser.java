package com.argusdusty.apscalc;

import java.util.Arrays;
import java.util.ArrayList;

import com.argusdusty.apscalc.errors.UnexpectedCharError;
import com.argusdusty.apscalc.errors.UnexpectedError;
import com.argusdusty.apscalc.functions.*;
import com.argusdusty.apscalc.functions.bool.BoolFunc;
import com.argusdusty.apscalc.functions.bool.Compare;
import com.argusdusty.apscalc.types.Bool;
import com.argusdusty.apscalc.types.Complex;
import com.argusdusty.apscalc.types.EmptyExpression;
import com.argusdusty.apscalc.types.Equation;
import com.argusdusty.apscalc.types.Expression;
import com.argusdusty.apscalc.types.Float;
import com.argusdusty.apscalc.types.Int;
import com.argusdusty.apscalc.types.Num;
import com.argusdusty.apscalc.types.Power;
import com.argusdusty.apscalc.types.Prod;
import com.argusdusty.apscalc.types.Rational;
import com.argusdusty.apscalc.types.Sum;
import com.argusdusty.apscalc.types.Variable;
import com.argusdusty.apscalc.types.arrays.Expseq;
import com.argusdusty.apscalc.types.arrays.List;
import com.argusdusty.apscalc.types.arrays.Matrix;
import com.argusdusty.apscalc.types.arrays.Text;
import com.argusdusty.apscalc.types.mathconsts.MathConst;
import com.argusdusty.apscalc.utils.EvalfUtils;
import com.argusdusty.apscalc.utils.OrderedMap;
import com.argusdusty.apscalc.utils.SolverUtils;

public class ExpressionParser
{
	private static String numbers = "1234567890";
	private static String letters = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ?";
	
	public static Expression parse(String equation)
	{
		equation = removeSpace(equation);
		if (equation.length() == 0) throw new Error("Empty input!");
		if (!syntaxCheck(equation)) throw new Error("Expression failed syntax check");
		Expression e = parseExpression(equation);
		return e;
	}
	
	private static String removeSpace(String equation)
	{
		boolean instr = false; String result = ""; char temp;
		for (int i = 0; i < equation.length(); i++)
		{
			temp = equation.charAt(i);
			if (temp == '"') instr = !instr;
			else if (!instr && " \t\r\n\f".indexOf(temp) != -1) continue;
			result += temp;
		}
		return result;
	}
	
	private static boolean syntaxCheck(String equation)
	{
		char tempa = equation.charAt(0); char tempb; boolean instr = false;
		String allowed = letters + numbers + ".()[]{}+-*/^,=:!\"><~&|";
		if (("}])=:!").indexOf(tempa) != -1 || allowed.indexOf(tempa) == -1) throw new UnexpectedCharError(tempa);
		for (int i = 0; i < equation.length() - 1; i++)
		{
			tempa = equation.charAt(i); tempb = equation.charAt(i+1);
			if (tempa == '"') instr = !instr;
			if (instr) continue; // Anything goes in a string
			if (allowed.indexOf(tempb) == -1) throw new UnexpectedCharError(tempb);
			if (numbers.indexOf(tempa) != -1 && "[{".indexOf(tempb) != -1) throw new UnexpectedCharError(tempb);
			if (letters.indexOf(tempa) != -1 && ".{".indexOf(tempb) != -1) throw new UnexpectedCharError(tempb);
			if (tempa == '[' && ("],})*/^+:=!<>").indexOf(tempb) != -1) throw new UnexpectedCharError(tempb);
			if (tempa == ']' && ("[{(\"" + numbers + letters).indexOf(tempb) != -1) throw new UnexpectedCharError(tempb);
			if (tempa == '{' && ("],})*/^+:=!<>").indexOf(tempb) != -1) throw new UnexpectedCharError(tempb);
			if (tempa == '}' && ("[{(\"" + numbers + letters).indexOf(tempb) != -1) throw new UnexpectedCharError(tempb);
			if (tempa == '(' && ("],})*/^+:=!<>").indexOf(tempb) != -1) throw new UnexpectedCharError(tempb);
			if (tempa == ')' && ("[{(\"" + numbers + letters).indexOf(tempb) != -1) throw new UnexpectedCharError(tempb);
			if (tempa == '+' && ("-+*/^]}):=!<>&|,").indexOf(tempb) != -1) throw new UnexpectedCharError(tempb);
			if (tempa == '-' && ("-+*/^]}):=!<>&|,").indexOf(tempb) != -1) throw new UnexpectedCharError(tempb);
			if (tempa == '*' && ("+*/^]}):=!<>&|,").indexOf(tempb) != -1) throw new UnexpectedCharError(tempb);
			if (tempa == '/' && ("+*/^]}):=!<>&|,").indexOf(tempb) != -1) throw new UnexpectedCharError(tempb);
			if (tempa == '^' && ("+*/^]}):=!<>&|,").indexOf(tempb) != -1) throw new UnexpectedCharError(tempb);
			if (tempa == ',' && ("+*/^]}):=!<>&|,").indexOf(tempb) != -1) throw new UnexpectedCharError(tempb);
			if (tempa == ':' && tempb != '=') throw new UnexpectedCharError(tempb);
			if (tempa == '=' && ":]})<>&|+*/^,!".indexOf(tempb) != -1) throw new UnexpectedCharError(tempb);
			if (tempa == '!' && (".:=[{(\"" + numbers + letters).indexOf(tempb) != -1) throw new UnexpectedCharError(tempb);
			if (tempa == '<' && "+*/^]}):!<&|,".indexOf(tempb) != -1) throw new UnexpectedCharError(tempb);
			if (tempa == '>' && "+*/^]}):!<>&|,".indexOf(tempb) != -1) throw new UnexpectedCharError(tempb);
			if (tempa == '~' && "-+*/^{[]}):=!<>&|,".indexOf(tempb) != -1) throw new UnexpectedCharError(tempb);
			if (tempa == '&' && "-+*/^({[]}):=!<>&|,".indexOf(tempb) != -1) throw new UnexpectedCharError(tempb);
			if (tempa == '|' && "-+*/^({[]}):=!<>&|,".indexOf(tempb) != -1) throw new UnexpectedCharError(tempb);
		}
		if (("+-*/^[{(,:=").indexOf(equation.charAt(equation.length() - 1)) != -1)
			throw new UnexpectedCharError(equation.charAt(equation.length() - 1));
		return true;
	}

	public static Expression parseExpression(String equation)
	{
		if (APSCalc.debug) System.out.println("Parse: " + equation);
		if (equation.length() == 0) throw new UnexpectedError("epe");
		if (!validParas(equation)) throw new Error("Invalid Parantheses");
		equation = removeParas(equation);
		Expression e = new EmptyExpression(), r;
		if (equation.charAt(0) == '"' && equation.charAt(equation.length() - 1) == '"')
		{
			r = parseStr(equation.substring(1, equation.length() - 1));
			if (!r.equals(e)) return r;
		}
		if (equation.charAt(0) == '{' && equation.charAt(equation.length() - 1) == '}')
		{
			r = parseSeq(equation.substring(1, equation.length() - 1));
			if (!r.equals(e)) return r;
		}
		if (equation.charAt(0) == '[' && equation.charAt(equation.length() - 1) == ']')
		{
			r = parseLst(equation.substring(1, equation.length() - 1));
			if (!r.equals(e)) return r;
		}
		if (equation.indexOf("==") != -1 || equation.indexOf("<") != -1  || equation.indexOf(">") != -1)
		{
			r = parseComp(equation);
			if (!r.equals(e)) return r;
		}
		if (equation.indexOf('=') != -1)
		{
			r = parseEqu(equation);
			if (!r.equals(e)) return r;
		}
		if (equation.indexOf('+') != -1 || equation.indexOf('-') != -1)
		{
			r = parseSum(equation);
			if (!r.equals(e)) return r;
		}
		if (equation.indexOf('*') != -1 || equation.indexOf('/') != -1)
		{
			r = parseProd(equation);
			if (!r.equals(e)) return r;
		}
		if (equation.indexOf('^') != -1)
		{
			r = parsePow(equation);
			if (!r.equals(e)) return r;
		}
		if (equation.charAt(0) == '~' || equation.indexOf('&') != -1 || equation.indexOf('|') != -1)
		{
			r = parseBool(equation);
			if (!r.equals(e)) return r;
		}
		if (equation.charAt(equation.length() - 1) == '!')
		{
			return new Fact(parseExpression(equation.substring(0, equation.length() - 1))).simplify();
		}
		if (equation.indexOf('(') != -1)
		{
			r = parseFunc(equation);
			if (!r.equals(e)) return r;
		}
		if (numbers.indexOf(equation.charAt(0)) != -1 || equation.charAt(0) == '-' || equation.charAt(0) == '.')
		{
			r = parseNum(equation);
			if (!r.equals(e)) return r;
		}
		if (letters.indexOf(equation.charAt(0)) != -1)
		{
			r = parseVar(equation);
			if (!r.equals(e)) return r;
		}
		throw new UnexpectedError("epp");
	}

	public static boolean validParas(String equation)
	{
		int pLevel = 0, bLevel = 0, sLevel = 0; char temp; boolean inStr = false;
		for (int i = 0; i < equation.length(); i++)
		{
			temp = equation.charAt(i);
			if (temp == '"') inStr = !inStr;
			if (inStr) continue;
			if (temp == '(') pLevel++;
			if (temp == ')') pLevel--;
			if (temp == '[') bLevel++;
			if (temp == ']') bLevel--;
			if (temp == '{') sLevel++;
			if (temp == '}') sLevel--;
			if (pLevel < 0 || bLevel < 0 || sLevel < 0) throw new Error("Invalid Parantheses");
		}
		return (pLevel == 0) && (sLevel == 0) && (bLevel == 0) && (!inStr);
	}
	
	public static String removeParas(String equation)
	{
		int paraLevel = 0; char temp;
		if (equation.charAt(0) == '(' && equation.charAt(equation.length() - 1) == ')')
		{
			for (int i = 1; i < equation.length() - 1; i++)
			{
				temp = equation.charAt(i);
				if (temp == ')') paraLevel--;
				else if (temp == '(') paraLevel++;
				if (paraLevel < 0) return equation;
			}
			if (paraLevel == 0)
				if (equation.charAt(0) == '(' && equation.charAt(equation.length() - 1) == ')')
					return removeParas(equation.substring(1, equation.length() - 1));
		}
		return equation;
	}
	
	public static Expression parseComp(String equation)
	{
		for (int i = 0; i < equation.length() - 1; i++)
		{
			if (equation.charAt(i) == '=' && equation.charAt(i+1) == '=')
			{
				Expression l = parseExpression(equation.substring(0, i));
				Expression r = parseExpression(equation.substring(i + 2));
				return new Compare(l, r, 0).simplify();
			}
			else if (equation.charAt(i) == '<')
			{
				Expression l = parseExpression(equation.substring(0, i));
				if (equation.charAt(i + 1) == '=')
				{
					Expression r = parseExpression(equation.substring(i + 2));
					return new Compare(l, r, -1).simplify();
				}
				else if (equation.charAt(i + 1) == '>')
				{
					Expression r = parseExpression(equation.substring(i + 2));
					return new Compare(l, r, 3).simplify();
				}
				Expression r = parseExpression(equation.substring(i + 1));
				return new Compare(l, r, -2).simplify();
			}
			else if (equation.charAt(i) == '>')
			{
				Expression l = parseExpression(equation.substring(0, i));
				if (equation.charAt(i + 1) == '=')
				{
					Expression r = parseExpression(equation.substring(i + 2));
					return new Compare(l, r, 1).simplify();
				}
				Expression r = parseExpression(equation.substring(i + 1));
				return new Compare(l, r, 2).simplify();
			}
		}
		return new EmptyExpression();
	}
	
	public static Expression parseBool(String equation)
	{
		if (equation.charAt(0) == '~')
		{
			return new BoolFunc(parseExpression(equation.substring(1)), -1).simplify();
		}
		for (int i = 0; i < equation.length() - 1; i++)
		{
			if (equation.charAt(i) == '&')
			{
				Expression l = parseExpression(equation.substring(0, i));
				Expression r = parseExpression(equation.substring(i + 1));
				return new BoolFunc(l, r, 1).simplify();
			}
			else if (equation.charAt(i) == '|')
			{
				Expression l = parseExpression(equation.substring(0, i));
				Expression r = parseExpression(equation.substring(i + 1));
				return new BoolFunc(l, r, 2).simplify();
			}
		}
		return new EmptyExpression();
	}
	
	public static Expression parseStr(String equation)
	{
		for (int i = 0; i < equation.length(); i++)
			if (equation.charAt(i) == '"')
				return new EmptyExpression();
		return new Text(equation);
	}
	
	public static Expression parseSeq(String equation)
	{
		int paraLevel = 0, previ = 0; char temp; boolean inStr = false;
		ArrayList<Expression> v = new ArrayList<Expression>();
		for (int i = 0; i < equation.length(); i++)
		{
			temp = equation.charAt(i);
			if (temp == '\"') inStr = !inStr;
			else if (temp == '(' || temp == '{') paraLevel++;
			else if (temp == ')' || temp == '}') paraLevel--;
			if (paraLevel < 0) return new EmptyExpression();
			else if (paraLevel == 0 && temp == ',' && !inStr)
			{
				v.add(parseExpression(equation.substring(previ, i))); previ = i + 1;
			}
		}
		if (inStr) throw new Error("Unexpected end of input");
		if (v.size() == 0) v.add(parseExpression(equation));
		else v.add(parseExpression(equation.substring(previ)));
		return new Expseq(v).simplify();
	}
	
	public static Expression parseLst(String equation)
	{
		int paraLevel = 0, previ = 0; char temp; boolean inStr = false;
		ArrayList<Expression> v = new ArrayList<Expression>();
		for (int i = 0; i < equation.length(); i++)
		{
			temp = equation.charAt(i);
			if (temp == '\"') inStr = !inStr;
			else if ("([{".indexOf(temp) != -1) paraLevel++;
			else if (")]}".indexOf(temp) != -1) paraLevel--;
			if (paraLevel < 0) return new EmptyExpression();
			else if (paraLevel == 0 && temp == ',' && !inStr)
			{
				v.add(parseExpression(equation.substring(previ, i))); previ = i + 1;
			}
		}
		if (inStr) throw new Error("Unexpected end of input");
		if (v.size() == 0) v.add(parseExpression(equation));
		else v.add(parseExpression(equation.substring(previ)));
		return new List(v).simplify();
	}
	
	public static Expression parseEqu(String equation)
	{
		char temp; int paraLevel = 0; boolean inStr = false;
		for (int i = 0; i < equation.length(); i++)
		{
			temp = equation.charAt(i);
			if (temp == '\"') inStr = !inStr;
			else if ("([{".indexOf(temp) != -1) paraLevel++;
			else if (")]}".indexOf(temp) != -1) paraLevel--;
			else if (paraLevel == 0 && temp == '=' && !inStr)
			{
				if (equation.charAt(i-1) == ':')
				{
					String left = equation.substring(0, i-1);
					for (int j = 0; j < left.length(); j++)
						if (letters.indexOf(left.charAt(j)) == -1)
							throw new Error("Left-hand of an assignment must be a variable");
					Variable v = new Variable(left);
					Expression right = parseExpression(equation.substring(i+1));
					for (int j = 0; j < APSCalc.globalsP.size(); j++)
						if (APSCalc.globalsP.getKey(0).equals(left))
							throw new Error("Variable name is protected");
					APSCalc.globals.put(left, right);
					for (int j = 0; j < APSCalc.globals.size(); j++)
						if (SolverUtils.contains(APSCalc.globals.getVal(j), v))
							APSCalc.globals.setVal(j, APSCalc.globals.getVal(j).substitute(v, right));
					return right;
				}
				return new Equation(parseExpression(equation.substring(0, i)), parseExpression(equation.substring(i+1)));
			}
		}
		if (inStr) throw new Error("Unexpected end of input");
		return new EmptyExpression();
	}
	
	public static Expression parseSum(String equation)
	{
		Expression r = new EmptyExpression(); boolean inStr = false;
		char prevop = '+', temp; int paraLevel = 0, previ = 0; boolean set = false;
		if (equation.charAt(0) == '-') {prevop = '-'; equation = equation.substring(1);}
		if ("([{".indexOf(equation.charAt(0)) != -1) paraLevel = 1;
		else if (equation.charAt(0) == '\"') inStr = true;
		for (int i = 1; i < equation.length(); i++)
		{
			temp = equation.charAt(i);
			if (temp == '\"') inStr = !inStr;
			else if ("([{".indexOf(temp) != -1) paraLevel++;
			else if (")]}".indexOf(temp) != -1) paraLevel--;
			else if (paraLevel == 0 && (temp == '+' || temp == '-'))
			{
				if (equation.charAt(i-1) == '*' || equation.charAt(i-1) == '/' || equation.charAt(i-1) == '^') continue;
				if (prevop == '+')  r = r.add(parseExpression(equation.substring(previ, i)));
				else r = r.add(parseExpression(equation.substring(previ, i)).negate());
				set = true; previ = i + 1; prevop = temp;
			}
		}
		if (inStr) throw new Error("Unexpected end of input");
		if (set && prevop == '+') r = r.add(parseExpression(equation.substring(previ)));
		else if (set && prevop == '-') r = r.add(parseExpression(equation.substring(previ)).negate());
		else if (prevop == '-') r = r.add(parseExpression(equation).negate());
		return r;
	}
	
	public static Expression parseProd(String equation)
	{
		Expression r = new EmptyExpression(); boolean inStr = false;
		char prevop = '*', temp; int paraLevel = 0, previ = 0; boolean set = false;
		for (int i = 0; i < equation.length(); i++)
		{
			temp = equation.charAt(i);
			if (temp == '\"') inStr = !inStr;
			else if ("([{".indexOf(temp) != -1) paraLevel++;
			else if (")]}".indexOf(temp) != -1) paraLevel--;
			else if (paraLevel == 0 && (temp == '*' || temp == '/'))
			{
				if (prevop == '*') r = r.mul(parseExpression(equation.substring(previ, i)));
				else r = r.mul(parseExpression(equation.substring(previ, i)).inverse());
				set = true; previ = i + 1; prevop = temp;
			}
		}
		if (inStr) throw new Error("Unexpected end of input");
		if (set)
		{
			if (prevop == '*') r = r.mul(parseExpression(equation.substring(previ)));
			else r = r.mul(parseExpression(equation.substring(previ)).inverse());
		}
		return r;
	}
	
	private static Expression parsePow(String equation)
	{
		char temp; int paraLevel = 0; boolean inStr = false;
		for (int i = 0; i < equation.length(); i++)
		{
			temp = equation.charAt(i);
			if (temp == '\"') inStr = !inStr;
			else if ("([{".indexOf(temp) != -1) paraLevel++;
			else if (")]}".indexOf(temp) != -1) paraLevel--;
			else if (paraLevel == 0 && temp == '^')
				return parseExpression(equation.substring(0, i)).pow(parseExpression(equation.substring(i + 1)));
		}
		if (inStr) throw new Error("Unexpected end of input");
		return new EmptyExpression();
	}
	
	public static Expression parseFunc(String equation)
	{
		char temp;
		if (numbers.indexOf(equation.charAt(0)) != -1) // 123(x,y) -> 123*(x,y); 1abc(x,y) -> Unexpected character
		{
			for (int i = 0; i < equation.length(); i++)
			{
				temp = equation.charAt(i);
				if (letters.indexOf(temp) != -1) throw new Error("Unexpected character: " + temp);
				else if (temp == '(')
				{
					String subeq1 = equation.substring(0,i);
					String subeq2 = equation.substring(i + 1, equation.length() - 1);
					if (subeq1.indexOf('.') != -1) return (new Float(subeq1)).mul(parseExpression(subeq2));
					else return (new Int(subeq1)).mul(parseExpression(subeq2));
				}
			}
		}
		else if (letters.indexOf(equation.charAt(0)) == -1) throw new UnexpectedCharError(equation.charAt(0));
		for (int i = 0; i < equation.length(); i++) // abc(x,y) -> abc(x,y);
		{
			ArrayList<Expression> args = new ArrayList<Expression>();
			if (!equation.endsWith(")")) {throw new UnexpectedCharError('(');}
			if (equation.charAt(i) == '(')
			{
				if (equation.substring(0, i).toLowerCase().equals("evalf"))
				{
					Expression a;
					String s = equation.substring(i + 1);
					ArrayList<String> es = splitArgs(s);
					if (es.size() != 2 && es.size() != 1)
						throw new Error("Function evalf only takes 1 or 2 argument(s)");
					if (es.size() == 1)
					{
						Settings.set_evalf(true);
						a = EvalfUtils.evalf(parseExpression(es.get(0)));
						Settings.set_evalf(false);
						return a;
					}
					Expression a2 = parseExpression(es.get(1));
					if (!(a2 instanceof Int)) throw new Error("Argument 2 of evalf must be an integer");
					int prec = ((Int) a2).value.intValue();
					int prectemp = Settings.set_precision(prec);
					if (!Settings.evalf)
					{
						Settings.set_evalf(true);
						a = EvalfUtils.evalf(parseExpression(es.get(0)));
						Settings.set_evalf(false);
					}
					else
					{
						a = EvalfUtils.evalf(parseExpression(es.get(0)));
						Settings.set_precision(prectemp);
					}
					return a;
				}
				if (equation.substring(0, i).toLowerCase().equals("del"))
				{
					String str = equation.substring(i + 1, equation.length() - 1);
					Expression r = null;
					for (int j = 0; j < APSCalc.globals.size(); j++)
						if (APSCalc.globals.getKey(j).equals(str))
							r = APSCalc.globals.remove(j);
					if (r == null) throw new Error("Variable not stored in globals");
					return r;
				}
				if (equation.substring(0, i).toLowerCase().equals("help"))
				{
					ArrayList<String> es = splitArgs(equation.substring(i + 1));
					if (es.size() != 1) throw new Error("Function help only takes 1 argument");
					for (int j = 0; j < APSCalc.help.size(); j++)
					{
						if (es.get(0).equalsIgnoreCase(APSCalc.help.get(j).get(0)))
						{
							ArrayList<String> r = APSCalc.help.get(j);
							String s = "";
							for (int k = 0; k < r.size(); k++) s += r.get(k) + ".";
							return new Text(s);
						}
					}
					return new Text("No information found.");
				}
				ArrayList<String> es = splitArgs(equation.substring(i + 1));
				for (int j = 0; j < es.size(); j++) args.add(parseExpression(es.get(j)));
				return new Function(equation.substring(0, i), args).simplify();
			}
		}
		return new EmptyExpression();
	}
	
	public static Expression parseNum(String equation)
	{
		equation = equation.replaceAll(",", "");
		char temp; boolean pastDot = false;
		if (equation.startsWith("0b"))
		{
			String allowed = "01";
			for (int i = 2; i < equation.length(); i++)
				if (allowed.indexOf(equation.charAt(i)) == -1) throw new UnexpectedCharError(equation.charAt(i));
			return new Int(equation.substring(2), 2);
		}
		if (equation.startsWith("0x"))
		{
			String allowed = "0123456789aAbBcCdDeEfF";
			for (int i = 2; i < equation.length(); i++)
				if (allowed.indexOf(equation.charAt(i)) == -1) throw new UnexpectedCharError(equation.charAt(i));
			return new Int(equation.substring(2), 16);
		}
		if (equation.charAt(0) == '.') pastDot = true;
		for (int i = 1; i < equation.length(); i++)
		{
			temp = equation.charAt(i);
			if (letters.indexOf(temp) != -1)
				return parseNum(equation.substring(0, i)).mul(parseExpression(equation.substring(i)));
			if (numbers.indexOf(temp) == -1 && temp != '.')
				throw new UnexpectedCharError(temp);
			if (temp == '.') {if (pastDot) {throw new UnexpectedCharError('.');} pastDot=true;}
		}
		return (pastDot) ? new Float(equation) : new Int(equation);
	}
	
	public static Expression parseVar(String equation)
	{
		if (equation.toLowerCase().equals("ans"))
		{
			if (APSCalc.t.equals(null)) throw new Error("null");
			return APSCalc.t;
		}
		if (equation.toLowerCase().equals("endif")) throw new Error("Not in if");
		for (int i = 0; i < equation.length(); i++)
			if (letters.indexOf(equation.charAt(i)) == -1) throw new UnexpectedCharError(equation.charAt(i));
		Variable r = new Variable(equation).simplify();
		Expression result = null;
		
		for (int j = 0; j < APSCalc.globals.size(); j++)
			if (APSCalc.globals.getKey(j).equals(equation))
				result = APSCalc.globals.getVal(j);
		if (result != null) return result;
		for (int j = 0; j < APSCalc.globalsP.size(); j++)
			if (APSCalc.globalsP.getKey(j).equals(equation))
				result = APSCalc.globalsP.getVal(j);
		return (result != null) ? result : r;
	}
	
	public static void dismantle(Expression e) {dismantle(e, 0);}

	private static void dismantle(Expression e, int i)
	{
		char[] shifts = new char[i*4];
		Arrays.fill(shifts, ' ');
		String tabs = new String(shifts);
		if (e instanceof Float) System.out.println(tabs + "FLOAT: " + ((Float) e).toString());
		else if (e instanceof AbstractFunction)
		{
			if (e instanceof Function)
			{
				ArrayList<Expression> v = new ArrayList<Expression>(((Function) e).args);
				System.out.println(tabs + "FUNCTION(" + v.size() + "):");
				System.out.println(tabs + "    NAME: " + ((Function) e).name);
				for (int j = 0; j < v.size(); j++) {dismantle(v.get(j), i+1);}
			}
			else if (e instanceof UnivarFunction)
			{
				System.out.println(tabs + "FUNCTION(1):\n" + tabs + "    NAME: " + ((UnivarFunction) e).name);
				dismantle(((UnivarFunction) e).arg1, i+1);
			}
			else if (e instanceof BivarFunction)
			{
				System.out.println(tabs + "FUNCTION(2):\n" + tabs + "    NAME: " + ((BivarFunction) e).name);
				dismantle(((BivarFunction) e).arg1, i+1);
				dismantle(((BivarFunction) e).arg2, i+1);
			}
			else if (e instanceof Summation)
			{
				System.out.println(tabs + "FUNCTION(4):\n" + tabs + "    NAME: sum");
				dismantle(((Summation) e).arg1, i+1);
				dismantle(((Summation) e).arg2, i+1);
				dismantle(((Summation) e).arg3, i+1);
				dismantle(((Summation) e).arg4, i+1);
			}
			else if (e instanceof Product)
			{
				System.out.println(tabs + "FUNCTION(4):\n" + tabs + "    NAME: prod");
				dismantle(((Product) e).arg1, i+1);
				dismantle(((Product) e).arg2, i+1);
				dismantle(((Product) e).arg3, i+1);
				dismantle(((Product) e).arg4, i+1);
			}
		}
		else if (e instanceof Int) System.out.println(tabs + "INT: " + ((Int) e));
		else if (e instanceof Power)
		{
			System.out.println(tabs + "POWER:");
			dismantle(((Power) e).base, i+1); dismantle(((Power) e).exp, i+1);
		}
		else if (e instanceof Equation)
		{
			System.out.println(tabs + "EQUATION:");
			dismantle(((Equation) e).left, i+1); dismantle(((Equation) e).right, i+1);
		}
		else if (e instanceof Prod)
		{
			OrderedMap<Expression, Num> v = new OrderedMap<Expression, Num>(((Prod) e).args);
			System.out.println(tabs + "PROD(" + v.size() + "):");
			for (int j = 0; j < v.size(); j++) {dismantle(v.getKey(j), i+1); dismantle(v.getVal(j), i+1);}
		}
		else if (e instanceof Rational)
		{
			System.out.println(tabs + "RATIONAL:");
			System.out.println(tabs + "    INT: " + ((Rational) e).num);
			System.out.println(tabs + "    INT: " + ((Rational) e).denom);
		}
		else if (e instanceof Sum)
		{
			OrderedMap<Expression, Num> v = new OrderedMap<Expression, Num>(((Sum) e).args);
			System.out.println(tabs + "SUM(" + v.size() + "):");
			for (int j = 0; j < v.size(); j++) {dismantle(v.getKey(j), i+1); dismantle(v.getVal(j), i+1);}
		}
		else if (e instanceof Variable) System.out.println(tabs + "VARIABLE: " + ((Variable) e));
		else if (e instanceof Expseq)
		{
			ArrayList<Expression> v = new ArrayList<Expression>(((Expseq) e).args);
			System.out.println(tabs + "EXPSEQ(" + v.size() + "):");
			for (int j = 0; j < v.size(); j++) {dismantle(v.get(j), i+1);}
		}
		else if (e instanceof List)
		{
			ArrayList<Expression> v = new ArrayList<Expression>(((List) e).args);
			System.out.println(tabs + "LIST(" + v.size() + "):");
			for (int j = 0; j < v.size(); j++) {dismantle(v.get(j), i+1);}
		}
		else if (e instanceof Matrix)
		{
			ArrayList<ArrayList<Expression>> v = new ArrayList<ArrayList<Expression>>(((Matrix) e).args);
			System.out.println(tabs + "MATRIX(" + v.size() + "):");
			for (int j = 0; j < v.size(); j++) {dismantle(new List(v.get(j)), i+1);}
		}
		else if (e instanceof Text) System.out.println(tabs + "TEXT: " + ((Text) e).text);
		else if (e instanceof MathConst) System.out.println(tabs + "MATHCONST: " + ((MathConst) e).name);
		else if (e instanceof Bool) System.out.println(tabs + "BOOL: " + ((Bool) e).val);
		else if (e instanceof Complex)
		{
			System.out.println(tabs + "COMPLEX:");
			dismantle(((Complex) e).real, i+1); dismantle(((Complex) e).imag, i+1);
		}
		else if (e instanceof EmptyExpression) System.out.println(tabs + "EMPTYEXP");
		else {if (APSCalc.debug) System.out.println(e.getClass()); throw new UnexpectedError("dpc");}
	}
	
	public static ArrayList<String> splitArgs(String equation)
	{
		boolean inStr = false;
		int paraLevel = 0, previ = 0; char temp;
		ArrayList<String> v = new ArrayList<String>();
		for (int i = 0; i < equation.length(); i++)
		{
			temp = equation.charAt(i);
			if (temp == '\"') inStr = !inStr;
			else if ("([{".indexOf(temp) != -1) paraLevel++;
			else if (")]}".indexOf(temp) != -1) paraLevel--;
			else if (paraLevel == 0 && temp == ',') {v.add(equation.substring(previ, i)); previ = i + 1;}
		}
		v.add(equation.substring(previ, equation.length() - 1));
		return v;
	}
}