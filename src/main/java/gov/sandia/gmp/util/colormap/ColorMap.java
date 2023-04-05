/**
 * Copyright 2009 Sandia Corporation. Under the terms of Contract
 * DE-AC04-94AL85000 with Sandia Corporation, the U.S. Government
 * retains certain rights in this software.
 * 
 * BSD Open Source License.
 * All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * 
 *    * Redistributions of source code must retain the above copyright notice,
 *      this list of conditions and the following disclaimer.
 *    * Redistributions in binary form must reproduce the above copyright
 *      notice, this list of conditions and the following disclaimer in the
 *      documentation and/or other materials provided with the distribution.
 *    * Neither the name of Sandia National Laboratories nor the names of its
 *      contributors may be used to endorse or promote products derived from
 *      this software without specific prior written permission.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 */
package gov.sandia.gmp.util.colormap;

import java.awt.Color;
/**
* ColorMap is a interface for mapping numerical values to colors.
* The easiest way to implement getRGB(value) is simply with getColor(value).getRGB().
* validLevel indicates whether the numerical value is within a range that seems "reasonable"
* for coding into colors -- however ColorMap should provide *some* feasible color for
* *any* given value, including NaN.  defaultValue() provides a default numerical value
* within the "reasonable" range -- often the minimum value.  It must be the case that
* validLevel(defaultValue()) == true.
*
* @author James Vickers
* 
*/
public interface ColorMap 
  {
  /** Returns a color for the given value */
  public Color getColor(double value);
  
  /** Returns the RGB values for a value via
      return getColor(value).getRGB()
  */
  public int getRGB(double value);
  
  /** Returns the alpha value for a color for the given value.  This could be simply written as 
      return getRGB(value) >>> 24
      or        
      return getColor(value).getAlpha()
  */
  public int getAlpha(double value);
  
  /** Returns true if a value is "valid" (it provides a meaningful color) 
   */
  public boolean validValue(double value);
  
  public Color[] getColors();
  
  public ColorRange[] getColorRanges();
  
  public double getMinValue();
  public double getMaxValue();
  
  }
