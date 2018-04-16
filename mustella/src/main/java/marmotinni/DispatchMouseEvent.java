/*
 *
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */


package marmotinni;

import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.remote.RemoteWebElement;

import org.xml.sax.Attributes;

/** 
 * DispatchMouseEvent
 *
 * Dispatch mouse event
 */
public class DispatchMouseEvent extends TestStep { 

    /**
     *  Dispatch a mouse event
     */
	@Override
    protected void doStep()
    {
		
		Double x;
		Double y;
		if (hasLocal)
		{
			StringBuilder script = new StringBuilder();
			insertTargetScript(script, target);
            script.append("return target.element.getBoundingClientRect().left");
			if (TestStep.showScripts)
				System.out.println(script);
            Object any = ((JavascriptExecutor)webDriver).executeScript(script.toString());
            if (any instanceof Long)
                x = ((Long)any).doubleValue();
            else if (any instanceof Double)
                x = (Double)any;
            else
            {
                System.out.println("x is not Long or Double");
                x = 0.0;
            }
			script = new StringBuilder();
			insertTargetScript(script, target);
            script.append("return target.element.getBoundingClientRect().top");
			if (TestStep.showScripts)
				System.out.println(script);
            any = ((JavascriptExecutor)webDriver).executeScript(script.toString());
            if (any instanceof Long)
                y = ((Long)any).doubleValue();
            else if (any instanceof Double)
                y = (Double)any;
            else
            {
                System.out.println("y is not Long or Double");
                y = 0.0;
            }
			x += localX;
			y += localY;
		}
		else
		{
			x = stageX;
			y = stageY;
		}
		
		// find top-most element
		StringBuilder script = new StringBuilder();
		script.append("var all = document.all;");
		script.append("var n = all.length;");
		script.append("for(var i=n-1;i>=0;i--) { ");
		script.append("    var e = all[i];");
        script.append("    var bounds = e.getBoundingClientRect();");
        script.append("     if (" + x + " >= bounds.left && " + x + " <= bounds.right && " + y + " >= bounds.top && " + y + " <= bounds.bottom) {");
        script.append("         if (!e.id) e.id = Math.random().toString();");
        script.append("         return e;");
        script.append("     }");
		script.append("};");
		script.append("return null;");
		if (TestStep.showScripts)
			System.out.println(script);
		RemoteWebElement mouseTarget = (RemoteWebElement)((JavascriptExecutor)webDriver).executeScript(script.toString());
		//System.out.println("mouseTarget: " + mouseTarget.getTagName() + " " + mouseTarget.getAttribute("id"));
		String actualId = mouseTarget.getAttribute("id");
		
		script = new StringBuilder();
		script.append("var e = document.createEvent('MouseEvent');");
		script.append("e.initMouseEvent(\"" + type + "\", true, false, window, " + delta + ", " + x + ", " + y + ", " + localX + ", " + localY + ", " + ctrlKey + ", false, " + shiftKey + ", false, " + type.equals("mouseDown") + ", " + relatedObject + ");");
		script.append("document.getElementById('" + actualId + "').dispatchEvent(e);");
		if (TestStep.showScripts)
			System.out.println(script);
        try
        {
			((JavascriptExecutor)webDriver).executeScript(script.toString());
        }
        catch (Exception e1)
        {
            TestOutput.logResult("Exception thrown in DispatchMouseEvent.");
            testResult.doFail (e1.getMessage()); 
        }
		
    }
	
	/**
	 *  The object that should receive the mouse event
	 */
	public String target;
	
	/**
	 *  The type/name of the event
	 */
	public String type;
	
	/**
	 *  The ctrlKey property on the MouseEvent (optional)
	 */
	public boolean ctrlKey;
	
	/**
	 *  The delta property on the MouseEvent (optional)
	 */
	public int delta;
	
	private boolean hasLocal;
	private boolean hasStage;

	/**
	 *  The localX property on the MouseEvent (optional)
	 *  Either set stageX/stageY or localX/localY, but not both.
	 */
	public double localX;
	
	/**
	 *  The localY property on the MouseEvent (optional)
	 *  Either set stageX/stageY or localX/localY, but not both.
	 */
	public double localY;
	
	/**
	 *  The stageX property on the MouseEvent (optional)
	 *  Either set stageX/stageY or localX/localY, but not both.
	 */
	public double stageX;
	
	/**
	 *  The stageY property on the MouseEvent (optional)
	 *  Either set stageX/stageY or localX/localY, but not both.
	 */
	public double stageY;
	
	/**
	 *  The shiftKey property on the MouseEvent (optional)
	 */
	public boolean shiftKey;
	
	/**
	 *  The relatedObject property on the MouseEvent (optional)
	 */
	public String relatedObject;
	
	/**
	 *  customize string representation
	 */
	@Override 
	public String toString()
	{
		String s = "DispatchMouseEvent: target = ";
		s += target;
		if (hasLocal)
		{
			s += ", localX = " + Double.toString(localX);
			s += ", localY = " + Double.toString(localY);
		}
		if (hasStage)
		{
			s += ", stageX = " + Double.toString(stageX);
			s += ", stageY = " + Double.toString(stageY);
		}
		if (shiftKey)
			s += ", shiftKey = true";
		if (ctrlKey)
			s += ", ctrlKey = true";
		
		if (relatedObject != null)
			s += ", relatedObject = " + relatedObject;
		if (delta != 0)
			s += ", delta = " + Integer.toString(delta);
		return s;
	}
	
	@Override
	public void populateFromAttributes(Attributes attributes)
	{
		target = attributes.getValue("target");
		type = attributes.getValue("type");
		String value = attributes.getValue("localX");
		if (value != null)
		{
			localX = Double.parseDouble(value);
			hasLocal = true;
		}
		value = attributes.getValue("localY");
		if (value != null)
		{
			localY = Double.parseDouble(value);
			hasLocal = true;
		}
		value = attributes.getValue("stageX");
		if (value != null)
		{
			stageX = Double.parseDouble(value);
			hasStage = true;
		}
		value = attributes.getValue("stageY");
		if (value != null)
		{
			stageY = Double.parseDouble(value);
			hasStage = true;
		}
	}

}
