package com.argusdusty.apscalc;

public class Test
{
	private static int total = 0;
	private static int failed = 0;
	private static int partial = 0;
	static void Tests()
	{
		total = 0; failed = 0; partial = 0;
		
		IntTests();
		FloatTests();
		NumMathTests();
		ParaTests();
		VarTests();
		SpecialParserTests();
		StructTests();
		UnivarFuncTests();
		BivarFuncTests();
		SpecialFuncTests();
		BoolFuncTests();
		MatrixFuncTests();
		TrigFuncTests();
		SolveTests();
		MSolveTests();
		ExtraTests();
		
		System.out.println("Failed " + (failed + partial) + "/" + total + " tests");
		System.out.println("Partially passed " + partial + "/" + (failed + partial) + " failed tests");
	}
	
	private static void IntTests()
	{
		test("123", "123");
		test("1,234", "1234");
		test("123,412,412", "123412412");
		test("0", "0");
		test("001234567890", "1234567890");
		test("0,00,123,452", "123452");
		test("123,", "Error: Unexpected Character: ,");
	}
	
	private static void FloatTests()
	{
		test("1.3", "1.3");
		test("1,234.5", "1234.5");
		test(".5", "0.5");
		test("001.05", "1.05");
		test("00.05", "0.05");
		test("1.2.3", "Error: Unexpected Character: .");
	}
	
	private static void NumMathTests()
	{
		test("1+2", "3");
		test("1+3+4", "8");
		test("23+46", "69");
		test("23-46", "-23");
		test("-23+46", "23");
		test("1.2+5", "6.2");
		test("5+1.3", "6.3");
		test("1,234+4,567", "5801");
		test("1,2 + 1.2", "13.2");
		test("1.2 + 4,321", "4322.2");
		test("1.2-.5", "0.7");
		test("1-1.2", "-0.2");
		test("1-3+4-5.4+6+2.3", "4.9");
		test("5.0", "5");
		test("1.2-1.2", "0.0");
		test("1*2", "2");
		test("1.2*3", "3.6");
		test("1*2*3", "6");
		test("2*3.1*4", "24.8");
		test("2*4.1+3*1.2", "11.8");
		test("2*0.5 - 2.3*4 + 5", "-3.2");
		test("2*-3", "-6");
		test("3/4", "3/4");
		test("5/10", "1/2");
		test("20/10", "2");
		test("6/10", "3/5");
		test("-6*10", "-60");
		test("6*-10", "-60");
		test("6/-10", "-3/5");
		test("-6/10", "-3/5");
		test("6*23+46", "184");
		test("6+23*2", "52");
		test("23*2-6", "40");
		test("1,234*2", "2468");
		test("2+1,234", "1236");
		test("1,234+2,345", "3579");
		test("1/10*6", "3/5");
		test("3^4", "81");
		test("4^3", "64");
		test("4^2/4", "4");
	}
	
	private static void ParaTests()
	{
		test("4-(1+2)", "1");
		test("4(1+2)", "12");
		test("4*(1+2)", "12");
		test("3^(1+2)", "27");
		test("3^(1-2)", "1/3");
		test("3^(1/2)", "3^(1/2)");
		test("4^(1/2)", "2");
	}
	
	private static void VarTests()
	{
		test("x", "x");
		test("2x", "2*x");
		test("x+y","x+y");
		test("23+x","x+23");
		test("-x+y+23", "y+23-x");
		test("x*6","6*x");
		test("-6*x+3*y", "3*y-6*x");
		test("x^2", "x^2");
		test("3*y*x^2","3*x^2*y");
		test("y^3*x^2", "x^2*y^3");
		test("(x+y)*z", "x*z+y*z");
		test("(x+y)*(z+t)", "t*x+t*y+x*z+y*z");
	}
	
	private static void SpecialParserTests()
	{
		test("x:=3;x", "3");
		test("x:=y;y:=5;x", "5");
		test("2+3;Ans", "5");
		test("evalf(2+5/2)", "4.5");
		test("evalf(1/3)", "0.333333");
		test("evalf(1/3, 7)", "0.3333333");
		test("0b101", "5");
		test("0xFF", "255");
		test("x:=5;del(x);x","x");
		test("4!", "24");
		test("3*4!", "72");
		test("3^2!", "9");
		test("(2+3)!", "120");
		test("evalf(Pi)", "3.14159");
		test("evalf(e)", "2.71828");
		test("evalf(Pi^e)", "22.4592");
	}
	
	private static void StructTests()
	{
		// Equation
		test("2+1=3", "3=3");
		// Expseq
		test("{2, x}", "{x, 2}");
		test("{2,3}", "{2, 3}");
		test("{2, 3} + {4, 6}", "{6, 7, 8, 9}");
		test("{x, y} + {y, 2}", "{x+y, x+2, 2*y, y+2}");
		test("{2, 3} + {3, 4}", "{5, 6, 7}");
		test("{2, 3} * {4, 5}", "{8, 10, 12, 15}");
		test("{3, 4} * 3", "{9, 12}");
		test("{x, y} * {x, y}", "{x^2, x*y, y^2}");
		test("{x, y}^2", "{x^2, y^2}");
		test("{x, y}^{x, y}", "{x^x, x^y, y^x, y^y}");
		test("{x, x, y}", "{x, y}");
		// List
		test("[2, 3]", "[2, 3]");
		test("[2, x]", "[2, x]");
		test("[2, 3] + [3, 4]", "[2, 3, 3, 4]");
		test("[2, 3] * [4, 5]", "[[8, 10], [12, 15]]");
		// Complex
		test("i*i", "-1");
		test("i^2", "-1");
		test("(a+b*i)*(c+d*i)", "a*c-b*d+(a*d+b*c)*i");
		// Text
		test("\"Hello,\" + \" World!\"", "\"Hello, World!\"");
		test("2*\"Hi!\"", "\"Hi!Hi!\"");
		// Matrix
		test("Matrix([[1,2],[3,4]])", "matrix([[1, 2], [3, 4]])");
		test("Matrix([[1,2,3],[4,5]])", "Error: Invalid Matrix");
		test("Matrix([[1,2],[3,4]]) + matrix([[5,6],[7,8]])", "matrix([[6, 8], [10, 12]])");
		test("Matrix([[1,2],[3,4]]) * matrix([[5,6],[7,8]])", "matrix([[19, 22], [43, 50]])");
		test("Matrix([[1,2,3],[4,5,6]]) * matrix([[7,8],[9,10],[11,12]])", "matrix([[58, 64], [139, 154]])");
		test("Matrix([[1,2],[3,4]])^2", "matrix([[7, 10], [15, 22]])");
		test("Matrix([[0,1],[0,2]])^(1/2)", "matrix([[0, 2^(1/2)/2], [0, 2^(1/2)]])");
		test("Matrix([[0,1],[0,2]])^-1", "Error: Matrix is singular");
		test("Matrix([[1,0],[0,3]])^x", "matrix([[1, 0], [0, 3^x]])");
		test("Matrix([[1,0],[1,1]])^x", "matrix([[1, 0], [x, 1]])");
		test("Matrix([[1, 1],[1,1]])^x", "matrix([[2^x/2, 2^x/2], [2^x/2, 2^x/2]])");
	}
	
	private static void UnivarFuncTests()
	{
		// Exp
		test("exp(x)", "e^x");
		test("exp(x+y)", "e^(x+y)");
		test("exp(1.2)", "3.32012");
		// Fact
		test("fact(x)", "x!");
		test("fact(6)", "720");
		// Factor
		test("factor(2)", "2");
		test("factor(10)", "2*5");
		test("factor(2^32 - 1)", "3*5*17*257*65537");
		// Gamma
		test("gamma(2)", "1");
		test("gamma(-1.1)", "9.71481");
		test("gamma(4.5)", "11.6317");
		// Ln
		test("ln(x)", "ln(x)");
		test("ln(x^2)", "2*ln(x)");
		test("ln(2*x)", "ln(x)+ln(2)");
		test("ln(6)", "ln(2)+ln(3)");
		test("ln(1.2)", "0.182322");
		test("exp(ln(x))", "x");
		// Str
		test("str(x)", "\"x\"");
		test("2*str(123)", "\"123123\"");
		// ToInt
		test("int(10.5)", "10");
		test("int(32/3)", "10");
		test("int(e^2)", "7");
		test("int(Pi/e)", "1");
		test("int(x/y)", "int(x/y)");
	}
	
	private static void BivarFuncTests()
	{
		// Gcd
		test("gcd(4, 6)", "2");
		test("gcd(4, 5)", "1");
		test("gcd(49, 21)", "7");
		test("gcd(2/3, 2)", "2/3");
		test("gcd(4/3, 2)", "2/3");
		test("gcd(4/11, 6/13)", "2/143");
		// Lcm
		test("lcm(4, 6)", "12");
		test("lcm(4, 5)", "20");
		test("lcm(49, 21)", "147");
		test("lcm(2/3, 2)", "2");
		test("lcm(4/3, 2)", "4");
		test("lcm(4/11, 6/13)", "12");
		// Log
		test("log(4, 2)", "2");
		test("log(Pi^e, Pi)", "e");
		test("log(x*y, 3)", "ln(x)/ln(3)+ln(y)/ln(3)");
		test("log(3^x, x)", "x*ln(3)/ln(x)");
		// Mod
		test("mod(5, 3)", "2");
		test("mod(Pi, e)", "Pi-e");
		test("mod(Pi^2, e)", "Pi^2-3*e");
		test("mod(5.3, 0.21)", "0.05");
		test("mod(x, y)", "x-y*int(x/y)");
	}
	
	private static void SpecialFuncTests()
	{
		// Summation
		test("sum(x^2, x, 1, 5)", "55");
		test("sum(x, x, a, b)", "1/2*(b-a+1)*(a+b)"); // 1/2*(b^2-a^2+a+b)
		test("sum(x*y, x, 0, b)", "1/2*y*(b^2+b)");
		test("sum(y, x, a, b)", "y*(b+1-a)");
		// Product
		test("prod(x, x, 0, a)", "0");
		test("prod(x, x, 1, 5)", "120");
		test("prod(x, x, 1, a)", "a!");
	}
	
	private static void BoolFuncTests()
	{
		// BoolFunc
		test("1 & 1", "1");
		test("30 & 3", "2");
		test("true & false", "false");
		test("1 | 1", "1");
		test("30 | 3", "31");
		test("true | false", "true");
		test("~1", "-2");
		test("~30", "-31");
		test("~true", "false");
		test("xor(1, 1)", "0");
		test("xor(30, 3)", "29");
		// Compare
		test("1 > 2", "false");
		test("x == x", "true");
		//test("x == y", "false"); // Think about this one
		test("2 >= 2", "true");
		test("2.3 < 3.1", "true");
		test("0.1 <= -0.23", "false");
		test("2 <> 3", "true");
		test("x <> x", "false");
	}
	
	private static void MatrixFuncTests()
	{
		// Determinant
		test("determinant(matrix([[1,2],[3,4]]))", "-2");
		test("determinant(matrix([[1],[2]]))", "Error: Invalid matrix size in function 'determinant'");
		test("determinant(matrix([[1,2,3,4,5],[6,7,8,9,10],[11,12,13,14,15],[16,17,18,19,20],[21,22,23,24,25]]))", "0");
		test("determinant(matrix([[1,1,0,0,0],[0,2,2,0,0],[0,0,3,3,0],[0,0,0,4,4],[5,0,0,0,5]]))", "240");
		// Diagonalize
		test("diagonalize(matrix([[1,1],[0,0]]))", "matrix([[-1, 1], [1, 0]])*matrix([[0, 0], [0, 1]])*matrix([[0, 1], [1, 1]])");
		// Echelon
		test("echelon(matrix([[1,2,3],[4,5,6],[7,8,9]]))", "matrix([[1, 2, 3], [0, 1, 2], [0, 0, 0]])");
		// Len
		test("len([[1,2,3],[4,5,6],[7,8]])", "3");
		test("len(\"123321\")", "6");
		test("len({3,2,2,1})", "3");
		test("len(matrix([[1,2],[3,4]]))", "2");
		// QRDec
		test("QRDec(matrix([[0,1],[1,0]]))", "matrix([[0, 1], [1, 0]])*matrix([[1, 0], [0, 1]])");
		// REchelon
		test("rechelon(matrix([[1,2,3],[4,5,6],[7,8,9]]))", "matrix([[1, 0, -1], [0, 1, 2], [0, 0, 0]])");
		// Transpose
		test("transpose(matrix([[1,1],[0,1]]))", "matrix([[1, 0], [1, 1]])");
		test("matrix([[1,2,3],[4,5,6],[7,8,9]])^T", "matrix([[1, 4, 7], [2, 5, 8], [3, 6, 9]])");
	}
	
	private static void TrigFuncTests()
	{
		// Sin
		test("sin(0)", "0");
		test("sin(Pi/2)", "1");
		test("sin(5*Pi)", "0");
		test("sin(2*x)", "2*sin(x)*cos(x)");
		test("sin(x+y)", "cos(y)*sin(x)+cos(x)*sin(y)");
		test("sin(1.1)", "0.891207");
		test("sin(i)", "(e/2-1/(2*e))*i");
		test("sin(a+b*i)", "sin(a)*cosh(b)+cos(a)*sinh(b)*i");
		test("sin(asin(x))", "x");
		test("sin(acos(x))", "(1-x^2)^(1/2)");
		test("sin(atan(x))", "x/(x^2+1)^(1/2)");
		// Cos
		test("cos(0)", "1");
		test("cos(Pi/2)", "0");
		test("cos(5*Pi)", "1");
		test("cos(2*x)", "cos(x)^2-sin(x)^2");
		test("cos(x+y)", "cos(x)*cos(y)-sin(x)*sin(y)");
		test("cos(1.1)", "0.453596");
		test("cos(i)", "e/2+1/(2*e)");
		test("cos(a+b*i)", "cos(a)*cosh(b)-sin(a)*sinh(b)*i");
		test("cos(asin(x))", "(1-x^2)^(1/2)");
		test("cos(acos(x))", "x");
		test("cos(atan(x))", "1/(x^2+1)^(1/2)");
		// Tan
		test("tan(1.1)", "1.96476");
		// TODO: Csc, Sec, Cot, Acos, Asin, Atan, Sinh, Cosh, Asinh, Acosh
		
	}
	
	private static void SolveTests()
	{
		test("solve(x+a=b, x)", "b-a");
		test("solve(sin(x)=1, x)", "Pi/2");
		test("solve(x^2+2=4, x)", "{2^(1/2), -2^(1/2)}");
		test("solve(2*sin(2*x)^2+sin(2*x)+5=6, x)", "{Pi/2, Pi/6}");
		test("solve(2*x+sqrt(x)=1, x)", "{1/4}");
	}
	
	private static void MSolveTests()
	{
		test("msolve([x+1=2, y+1=x], [x, y])", "{{1, 0}}");
		test("msolve([x*y+2=5, y+2=x], [x, y])", "{{-1, -3}, {1, -1}}");
		test("msolve([x+2*y+3*z=4, x+3*y+2*z=3, 3*x+2*y+z=0], [x, y, z])", "{{2/3, 1/3, 4/3}}");
	}
	
	private static void ExtraTests()
	{
		test("1/2*(b-a+1)*(a+b)", "b^2/2+a/2+b/2-a^2/2");
		test("1/2*y*(a+b)", "a*y/2+b*y/2");
		test("(a+b)+(c+d)", "a+b+c+d");
		test("1/2*(a+b+c)", "a/2+b/2+c/2");
		test("1/2a", "a/2");
	}
	
	public static String strOut(String in)
	{
		APSCalc.loadConsts();
		String s = "";
		try {s = APSCalc.exec(in);}
		catch (Error e) {s = "Error: " + e.getMessage();}
		catch (Exception e) {s = "Failure: Unexpected Error";}
		return s;
	}
	
	public static void test(String in, String out)
	{
		String s = strOut(in);
		if (!s.equals(out))
		{
			String s2 = strOut(out);
			if (s.equals(s2))
			{
				partial++;
				System.out.println("Partially passed test: " + in + " -> " + out + " Produced: " + s);
			}
			else
			{
				failed++;
				System.out.println("Failed test: " + in + " -> " + out + " Produced: " + s);
			}
		}
		else if (APSCalc.debug)
		{
			System.out.println("Passed test: " + in + " -> " + out);
		}
		total++;
	}
	
	public static void testEq(String a, String b)
	{
		String sa = strOut(a);
		String sb = strOut(b);
		if (!sa.equals(sb))
		{
			failed++;
			System.out.println("Failed test: " + a + " = " + b + " Produced: " + sa + " and " + sb);
		}
		else if (APSCalc.debug)
		{
			System.out.println("Passed test: " + a + " = " + b);
		}
		total++;
	}
}
