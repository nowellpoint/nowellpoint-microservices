package com.nowellpoint.authentication.util;

import javax.ws.rs.core.UriInfo;

public class UriInfoThreadLocal {
	
	private static final ThreadLocal<UriInfo> THREAD_LOCAL = new ThreadLocal<UriInfo>();

    public static UriInfo get() {
        return (THREAD_LOCAL.get());
    }

    public static void set(UriInfo uriInfo) {
        THREAD_LOCAL.set(uriInfo);
    }

    public static void unset() {
        THREAD_LOCAL.remove();
    }
}
