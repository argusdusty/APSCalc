package com.argusdusty.apscalc.utils;

import java.util.ArrayList;

public class OrderedMap<T1, T2>
{
	public ArrayList<T1> keys;
	public ArrayList<T2> values;
	public int length;
	
	public OrderedMap()
	{
		this.keys = new ArrayList<T1>();
		this.values = new ArrayList<T2>();
		this.length = 0;
	}
	
	public String toString() {return keys.toString() + "," + values.toString();}
	
	@SuppressWarnings({"rawtypes", "unchecked"})
	public OrderedMap(OrderedMap a)
	{
		this.keys = new ArrayList<T1>(a.keys);
		this.values = new ArrayList<T2>(a.values);
		this.length = a.length;
	}
	
	public void put(T1 key, T2 val) {keys.add(key); values.add(val); length += 1;}
	public void add(T1 key, T2 val) {keys.add(key); values.add(val); length += 1;}
	public void add(int i, T1 key, T2 val) {keys.add(i, key); values.add(i, val); length += 1;}
	public int size() {return length; /*return keys.size();*/}
	
	public OrderedMap<T1, T2> getPair(int i)
	{
		OrderedMap<T1, T2> r = new OrderedMap<T1, T2>();
		r.add(keys.get(i), values.get(i));
		return r;
	}
	
	public T1 getKey(int i) {return keys.get(i);}
	public T2 getVal(int i) {return values.get(i);}
	
	public void setKey(int i, T1 key) {keys.set(i, key);}
	public void setVal(int i, T2 val) {values.set(i, val);}
	public void setPair(int i, T1 key, T2 val) {keys.set(i, key); values.set(i, val);}
	
	public T2 remove(int i) {T2 r = values.get(i); keys.remove(i); values.remove(i); length -= 1; return r;}
	public T2 pop() {T2 r = values.get(length-1); keys.remove(length-1); values.remove(length-1); length -= 1; return r;}
	
	public boolean equals(OrderedMap<T1, T2> a)
	{
		if (keys.size() != a.keys.size()) return false;
		if (values.size() != a.values.size()) return false;
		for (int i = 0; i < keys.size(); i++)
			if (keys.get(i) != a.keys.get(i)) return false;
		for (int i = 0; i < values.size(); i++)
			if (values.get(i) != a.values.get(i)) return false;
		return true;
	}
	
	public void addAll(OrderedMap<T1, T2> a) {keys.addAll(a.keys); values.addAll(a.values); length += a.length;}
}
