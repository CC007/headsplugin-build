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
public class HeadsCategories
{

	private List<HeadsCategory> categories;

	public HeadsCategories()
	{
		this.categories = new ArrayList<>();
	}

	/**
	 * Get the value of categories
	 *
	 * @return the value of categories
	 */
	public List<HeadsCategory> getList()
	{
		return categories;
	}

	/**
	 * Set the value of categories
	 *
	 * @param categories new value of categories
	 */
	public void setList(List<HeadsCategory> categories)
	{
		this.categories = categories;
	}

	/**
	 * Set the value of categories
	 *
	 * @param categories new value of categories
	 */
	public void setCategories(HeadsCategories categories)
	{
		if (categories != null) {
			this.categories = categories.getList();
		}
		else {
			this.categories = null;
		}
	}

	/**
	 * Merge two categories
	 *
	 * @param categories the added categories
	 */
	public void addAllCategories(List<HeadsCategory> categories)
	{
		if (categories != null) {
			this.categories.addAll(categories);
		}
	}

	/**
	 * Merge two categories
	 *
	 * @param categories the added categories
	 */
	public void addAllCategories(HeadsCategories categories)
	{
		if (categories != null) {
			this.categories.addAll(categories.getList());
		}
	}

	/**
	 * Check if the category exists by category name
	 *
	 * @param categoryName the name of the category
	 * @return true if the category exists, otherwise false
	 */
	public boolean hasCategory(String categoryName)
	{
		for (HeadsCategory category : categories) {
			if (category.getCategoryName().equalsIgnoreCase(categoryName)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Check if the category exists by id
	 *
	 * @param id the id of the category
	 * @return true if the category exists, otherwise false
	 */
	public boolean hasCategory(int id)
	{
		for (HeadsCategory category : categories) {
			if (category.getId() == id) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Get a certain category based on categoryName
	 *
	 * @param categoryName the name of the category
	 * @return the category if the category exists, otherwise null
	 */
	public HeadsCategory getCategory(String categoryName)
	{
		for (HeadsCategory category : categories) {
			if (category.getCategoryName().equalsIgnoreCase(categoryName)) {
				return category;
			}
		}
		return null;
	}

	/**
	 * Get a certain category based on id
	 *
	 * @param id the id of the category
	 * @return the category if the category exists, otherwise null
	 */
	public HeadsCategory getCategory(int id)
	{
		for (HeadsCategory category : categories) {
			if (category.getId() == id) {
				return category;
			}
		}
		return null;
	}

	/**
	 * Add a categories to the list
	 *
	 * @param category new category to be added
	 */
	public void addCategory(HeadsCategory category)
	{
		for (HeadsCategory existingCategory : categories) {
			if (existingCategory.getCategoryName().equalsIgnoreCase(category.getCategoryName())) {
				existingCategory.setCategory(category);
				return;
			}
		}
		categories.add(category);
	}

	/**
	 * Add a categories to the list
	 *
	 * @param categoryName name of new category to be added
	 * @param id           id of new category to be added
	 */
	public void addCategory(String categoryName, int id)
	{
		this.addCategory(new HeadsCategory(categoryName, id));
	}

	/**
	 * Remove a category from the list
	 *
	 * @param category the category to be removed
	 */
	public void removeCategory(HeadsCategory category)
	{
		categories.remove(category);
	}

	/**
	 * Remove a category from the list by name
	 *
	 * @param categoryName the name of the category to be removed
	 */
	public void removeCategory(String categoryName)
	{
		for (HeadsCategory category : categories) {
			if (category.getCategoryName().equalsIgnoreCase(categoryName)) {
				categories.remove(category);
				break;
			}
		}
	}

	/**
	 * Remove a category from the list by id
	 *
	 * @param id the id of the category to be removed
	 */
	public void removeCategory(int id)
	{
		for (HeadsCategory category : categories) {
			if (category.getId() == id) {
				categories.remove(category);
				break;
			}
		}
	}

	/**
	 * Clear out this categories object
	 */
	public void clear()
	{
		categories.clear();
	}

	@Override
	public String toString()
	{
		return "HeadsCategories{" + "categories=" + categories + '}';
	}

}
