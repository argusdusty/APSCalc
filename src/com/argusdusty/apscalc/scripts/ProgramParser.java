package com.argusdusty.apscalc.scripts;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;

public class ProgramParser
{
	public static Script parse(String FName)
	{
		try
		{
			BufferedReader inStream = new BufferedReader(new FileReader("scripts/" + FName));
		}
		catch (FileNotFoundException e)
		{
			throw new Error("Cannot Find File: " + FName);
		}
		//TODO
		return null;
	}
}
