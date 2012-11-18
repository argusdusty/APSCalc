package com.argusdusty.apscalc;
	
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.URISyntaxException;
import java.util.ArrayList;

import com.argusdusty.apscalc.types.Bool;
import com.argusdusty.apscalc.types.Complex;
import com.argusdusty.apscalc.types.EmptyExpression;
import com.argusdusty.apscalc.types.Expression;
import com.argusdusty.apscalc.types.mathconsts.E;
import com.argusdusty.apscalc.types.mathconsts.Indeterminate;
import com.argusdusty.apscalc.types.mathconsts.Inf;
import com.argusdusty.apscalc.types.mathconsts.Pi;
import com.argusdusty.apscalc.utils.OrderedMap;

public class APSCalc
{
	public static final boolean debug = false;
	public static final boolean tester = false;
	public static final boolean test = true;
	
	public static String releaseState = "prealpha";
	public static String releaseNum = "0.95";
	
	public static OrderedMap<String, Expression> globalsP = new OrderedMap<String, Expression>();
	public static OrderedMap<String, Expression> globals = new OrderedMap<String, Expression>();
	
	public static PrintStream out;
	public static InputStream in;
	public static Expression t;
	public static String HELP_FILE = "com/argusdusty/apscalc/resources/help.txt";
	public static ArrayList<ArrayList<String>> help = new ArrayList<ArrayList<String>>();
	
	public static void main(String[] args) throws IOException, URISyntaxException
	{
		init();
		run();
	}
	
	public static void init()
	{
		System.out.println("APSCalc " + releaseState + " " + releaseNum);
		System.out.println("By: Mark Canning");
		if (tester)
		{
			//Graph.main(args);
			out = System.out;
			in = System.in;
		}
		else
		{
			out = System.out;
			in = System.in;
		}
		
		loadConsts();
		
		try {loadHelp();}
		catch (IOException e) {out.println("Unexpected Error: mih");}
		catch (URISyntaxException e) {out.println("Unexpected Error: mij");}
		
		if (test) Test.Tests();
	}
	
	public static void run()
	{
		String equation = "";
		BufferedReader br;
		while (true)
		{
			out.print("Input: ");
			equation = "";
			br = new BufferedReader(new InputStreamReader(in));
			try
			{
				equation = br.readLine();
				if (equation.length() == 0 || equation.equals("quit")) break;
				String s = exec(equation);
	    		out.println("Output: " + s);
			}
	    	catch (Error e) {out.println("Error: " + e.getMessage()); if (debug) e.printStackTrace();}
			catch (IOException e) {out.println("Unexpected Error: mrb"); if (debug) e.printStackTrace();}
			catch (Exception e) {out.println("Unexpected Error: mer"); if (debug) e.printStackTrace();}
		}
	}
	
	public static String exec(String in)
	{
		String[] Exps = in.split(";");
		t = new EmptyExpression();
		for (int i = 0; i < Exps.length; i++) {t = ExpressionParser.parse(Exps[i]);}
		if (debug) ExpressionParser.dismantle(t);
		String s = t.toString();
		Settings.set_precision(Settings.default_print);
		return s;
	}
	
	public static void loadConsts()
	{
		globalsP = new OrderedMap<String, Expression>();
		globals = new OrderedMap<String, Expression>();
		globalsP.put("e", new E());
		globalsP.put("pi", new Pi()); globalsP.put("Pi", new Pi()); globalsP.put("PI", new Pi());
		globalsP.put("inf", new Inf()); globalsP.put("Inf", new Inf()); globalsP.put("INF", new Inf());
		globalsP.put("i", Complex.I);
		globalsP.put("true", new Bool(true)); globalsP.put("True", new Bool(true));
		globalsP.put("false", new Bool(false)); globalsP.put("False", new Bool(false));
		globalsP.put("?", new Indeterminate());
	}
	
	public static void loadHelp() throws IOException, URISyntaxException
	{
		InputStream f = ClassLoader.getSystemClassLoader().getResourceAsStream(HELP_FILE);
		BufferedReader br = new BufferedReader(new InputStreamReader(f));
		String in; String[] line; ArrayList<String> r;
		while ((in = br.readLine()) != null)
		{
			line = in.split("\\.");
			r = new ArrayList<String>();
			if (line.length == 0) continue;
			for (int i = 0; i < line.length; i++) r.add(line[i]);
			help.add(r);
		}
	}
}