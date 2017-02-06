/* 
 * Copyright (c) 2016 Expedia, Inc. All rights reserved.
 */
package com.expedia.stljni;

/**
 * @author Willie Wheeler
 */
@SuppressWarnings("serial")
public class StlException extends RuntimeException {
	
	public StlException(String message) {
		super(message);
	}
}
