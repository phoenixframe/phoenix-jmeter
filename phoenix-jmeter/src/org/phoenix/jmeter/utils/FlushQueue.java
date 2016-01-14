package org.phoenix.jmeter.utils;

import java.util.HashMap;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * 
 * @author mengfeiyang
 *
 */
public class FlushQueue {
	private LinkedBlockingQueue<String> linkedQueue;
	private HashMap<String,Object> hashMap = new HashMap<String,Object>();
	private int size;
	private volatile static FlushQueue singleton;  
	private FlushQueue(){}
	
	public static FlushQueue getInstance() {
		if (singleton == null) {
			synchronized (FlushQueue.class) {
				if (singleton == null) {
					singleton = new FlushQueue();
				}
			}
		}
		return singleton;
	}

	public void releaseQueue(){
		linkedQueue.clear();
		hashMap.clear();
	}
	
	public HashMap<String,Object> getHashMap(){
		return hashMap;
	}
	
	public void setSize(int size){
		this.size = size;
	}
	
	public int getSize(){
		return size;
	}
	
	public void setLinkedQueue(LinkedBlockingQueue<String> linkedQueue){
		this.linkedQueue = linkedQueue;
	}
	
	public synchronized void offer(String s){
		if(linkedQueue.size() == size) linkedQueue.poll();
		linkedQueue.offer(s);
	}
	
	public String take(){
		try {
			return linkedQueue.take();
		} catch (InterruptedException e) {
			return null;
		}
	}
	
	public String peek(){
		return linkedQueue.peek();
	}
	
	public String queueString(){
		return linkedQueue.toString();
	}
}
