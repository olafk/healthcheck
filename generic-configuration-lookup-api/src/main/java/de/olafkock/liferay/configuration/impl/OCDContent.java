package de.olafkock.liferay.configuration.impl;

import java.util.LinkedList;
import java.util.List;

/**
 * data transfer object
 *  
 * @author Olaf Kock
 */

public class OCDContent {
	public String id;
	public String name;
	public String description;
	public String category = "undeclared";
	public String scope = "undeclared";
	public List<ADContent> ads = new LinkedList<ADContent>();
	public String comment = "";
	public String bundle;
	public String learnMessageResource;
	public String learnMessageKey;
	
	public static class Comparator implements java.util.Comparator<OCDContent>{

		@Override
		public int compare(OCDContent o1, OCDContent o2) {
			return ("" + o1.scope + o1.category + o1.name).compareTo(
					"" + o2.scope + o2.category + o2.name);
		}
	}
}
