package org.phoenix.jmeter.cases;

import java.util.Map.Entry;

import org.apache.jorphan.collections.HashTree;
import org.apache.jorphan.collections.HashTreeTraverser;

 class Test123{
	public Test123(String s){
		System.out.println(s);
	}
	
	public void show(String s1){
		System.out.println("User:"+s1);
	}
}

class test3 implements HashTreeTraverser{

	@Override
	public void addNode(Object node, HashTree subTree) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void subtractNode() {
		// TODO Auto-generated method stub
		System.out.println("222222222");
	}

	@Override
	public void processPath() {
		// TODO Auto-generated method stub
		System.out.println("11111111111111");
	}
}

public class HashTreeTest {
	public static void main(String[] args) {
		HashTree ht = new HashTree();
		ht.add("1",new Test123("1---1"));
		ht.add("3",new Test123("3---3"));
		ht.add("2",new Test123("2---2"));
		

		ht.traverse(new test3());
		
		for(Entry<Object, HashTree> e:ht.entrySet()){
			System.out.println(e.getKey()+"    ");
			((Test123)e.getValue().getArray()[0]).show("123");
		}
	}
}
