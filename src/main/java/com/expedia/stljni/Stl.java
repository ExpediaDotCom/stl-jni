/* 
 * Copyright (c) 2016 Expedia, Inc. All rights reserved.
 */
package com.expedia.stljni;

/**
 * @author Willie Wheeler
 */
public class Stl {

	static {
		// Loads the library libstl_driver.jnilib (OSX only). See
		// https://developer.apple.com/library/mac/documentation/Java/Conceptual/Java14Development/05-CoreJavaAPIs/CoreJavaAPIs.html
		System.loadLibrary("stl_driver");
	}

	private StlParams params;

	public Stl(StlParams params) {
		this.params = params;
	}

	public StlResult decompose(final float[] y) {
		final StlParams p = params;
		final int n = y.length;
		
		final StlResult result = new StlResult();
		result.rw = new float[n];
		result.season = new float[n];
		result.trend = new float[n];
		
		int status = stlc(y, p.np, p.ns, p.nt, p.nl, p.isdeg, p.itdeg, p.ildeg, p.nsjump, p.ntjump, p.nljump, p.no,
				p.ni, result.rw, result.season, result.trend);
		
		if (status != 0) {
			throw new StlException("STL failed: status=" + status);
		}
		
		return result;
	}

	/** Native method that we're calling */
	private native int stlc(float[] y, int np, int ns, int nt, int nl, int isdeg, int itdeg, int ildeg, int nsjump,
			int ntjump, int nljump, int no, int ni, float[] rw, float[] season, float[] trend);
}
