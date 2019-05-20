/*
 * The MIT License
 *
 * Copyright 2015 Rik Schaaf aka CC007 <http://coolcat007.nl/>.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package com.github.cc007.headsplugin.legacy.utils.heads;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Rik Schaaf aka CC007 (http://coolcat007.nl/)
 */
public class HeadsCategory
{

	private List<Head> heads;

	private String categoryName;

	private int id;

	public HeadsCategory(String categoryName, int id)
	{
		this.categoryName = categoryName;
		this.id = id;
		this.heads = new ArrayList<>();
	}

	/**
	 * Get the value of categoryName
	 *
	 * @return the value of categoryName
	 */
	public String getCategoryName()
	{
		return categoryName;
	}

	/**
	 * Set the value of categoryName
	 *
	 * @param categoryName new value of categoryName
	 */
	public void setCategoryName(String categoryName)
	{
		this.categoryName = categoryName;
	}

	/**
	 * Get the value of heads
	 *
	 * @return the value of heads
	 */
	public List<Head> getList()
	{
		return heads;
	}

	/**
	 * Set the value of heads
	 *
	 * @param heads new value of heads
	 */
	public void setList(List<Head> heads)
	{
		this.heads = heads;
	}

	/**
	 * Set the value of heads
	 *
	 * @param headsCategory new value of heads
	 */
	public void setCategory(HeadsCategory headsCategory)
	{
		if (headsCategory != null) {
			this.heads = headsCategory.getList();
			this.categoryName = headsCategory.getCategoryName();
		}
		else {
			this.heads = null;
		}
	}

	/**
	 * Merge two heads lists
	 *
	 * @param heads the added heads
	 */
	public void addAllHeads(List<Head> heads)
	{
		if (heads != null) {
			this.heads.addAll(heads);
		}
	}

	/**
	 * Merge two heads lists
	 *
	 * @param headsCategory the added heads
	 */
	public void addAllHeads(HeadsCategory headsCategory)
	{
		if (headsCategory != null) {
			this.heads.addAll(headsCategory.getList());
		}
	}

	/**
	 * Check if the head exists
	 *
	 * @param name the name of the head
	 * @return true if the head exists, otherwise false
	 */
	public boolean hasHead(String name)
	{
		for (Head head : heads) {
			if (head.getName().equalsIgnoreCase(name)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Get a certain head based on name
	 *
	 * @param name the name of the head
	 * @return the head if the head exists, otherwise null
	 */
	public Head getHead(String name)
	{
		for (Head head : heads) {
			if (head.getName().equalsIgnoreCase(name)) {
				return head;
			}
		}
		return null;
	}

	/**
	 * Add a head to the list
	 *
	 * @param head new head to be added
	 */
	public void addHead(Head head)
	{
		heads.add(head);
	}

	/**
	 * Remove a head from the list
	 *
	 * @param head the head to be removed
	 */
	public void removeHead(Head head)
	{
		heads.remove(head);
	}

	/**
	 * Remove a head from the list
	 *
	 * @param name the name of the head to be removed
	 */
	public void removeHead(String name)
	{
		for (Head head : heads) {
			if (head.getName().equalsIgnoreCase(name)) {
				heads.remove(head);
				break;
			}
		}
	}

	/**
	 * Clear out this category object
	 */
	public void clear()
	{
		heads.clear();
	}

	@Override
	public String toString()
	{
		return "HeadsCategory{" + "heads=" + heads + ", categoryName=" + categoryName + '}';
	}

	/**
	 * Get the id of the category
	 *
	 * @return the id of the category
	 */
	public int getId()
	{
		return id;
	}

}
